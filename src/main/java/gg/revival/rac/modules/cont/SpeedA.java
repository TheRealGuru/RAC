package gg.revival.rac.modules.cont;

import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.players.ACPlayer;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.BlockUtils;
import gg.revival.rac.utils.MathUtils;
import gg.revival.rac.utils.Permissions;
import gg.revival.rac.utils.PlayerUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpeedA extends Check implements Listener {

    @Getter private Map<UUID, Map.Entry<Integer, Long>> speedTicks = new ConcurrentHashMap<>();
    @Getter private Map<UUID, Map.Entry<Integer, Long>> fastTicks = new ConcurrentHashMap<>();

    public SpeedA(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!isEnabled()) return;

        Player player = event.getPlayer();
        Location from = event.getFrom(), to = event.getTo();

        // Player has bypass
        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        // Player just jumped or moved camera
        if(from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) return;

        // Player is flying
        if(player.isFlying()) return;

        // Player isn't in survival
        if(!player.getGameMode().equals(GameMode.SURVIVAL)) return;

        ACPlayer acPlayer = getRac().getPlayerManager().getPlayerByUUID(player.getUniqueId());

        // Player has taken velocity recently
        if((System.currentTimeMillis() - acPlayer.getRecentVelocity()) < 1000L) return;

        int flags = 0, fastFlags = 0;
        long time = System.currentTimeMillis();

        if(speedTicks.containsKey(player.getUniqueId())) {
            flags = speedTicks.get(player.getUniqueId()).getKey();
            time = speedTicks.get(player.getUniqueId()).getValue();
        }

        if(fastTicks.containsKey(player.getUniqueId())) {
            double offset = MathUtils.offset(MathUtils.getHorizontalVector(from.toVector()), MathUtils.getHorizontalVector(to.toVector()));
            double offsetLimit;

            if(PlayerUtils.isOnGround(player) && player.getVehicle() == null)
                offsetLimit = 0.33;
            else
                offsetLimit = 0.4;

            if(BlockUtils.nearbySlabBlocks(player.getLocation()))
                offsetLimit += 0.05;

            Location eyeLocation = player.getEyeLocation().clone();

            if(!eyeLocation.add(0, 1.0, 0).getBlock().getType().equals(Material.AIR) && !PlayerUtils.canStandWithin(eyeLocation.getBlock()))
                offsetLimit = 1.0;
            else
                offsetLimit = 0.75;

            float currentWalkSpeed = player.getWalkSpeed();

            offsetLimit += (currentWalkSpeed > 0.2F) ? (currentWalkSpeed * 10.0f * 0.33f) : 0.0f;

            for(PotionEffect effects : player.getActivePotionEffects()) {
                if(!effects.getType().equals(PotionEffectType.SPEED)) continue;

                if(PlayerUtils.isOnGround(player))
                    offsetLimit += 0.06 * (effects.getAmplifier() + 1);
                else
                    offsetLimit += 0.02 * (effects.getAmplifier() + 1);
            }

            if(offset > offsetLimit)
                fastFlags = fastTicks.get(player.getUniqueId()).getKey() + 1;
            else
                fastFlags = 0;
        }

        if(fastFlags > 6) {
            fastFlags = 0;
            flags++;
        }

        if(speedTicks.containsKey(player.getUniqueId()) && (System.currentTimeMillis() - time) >= 60000L) {
            flags = 0;
            time = System.currentTimeMillis();
        }

        if(flags >= 2) {
            event.setCancelled(true);
            player.teleport(from);
            addViolation(player.getUniqueId(), new Violation("[A] " + player.getName() + " moved quicker than expected (" + flags + ")"), false);
            flags = 0;
        }

        speedTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<>(flags, time));
        fastTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<>(fastFlags, System.currentTimeMillis()));
    }

}
