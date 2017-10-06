package gg.revival.rac.modules.cont;

import com.google.common.collect.Maps;
import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.players.ACPlayer;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.LocationUtils;
import gg.revival.rac.utils.MathUtils;
import gg.revival.rac.utils.Permissions;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;

public class Ascension extends Check implements Listener {

    @Getter private Map<UUID, Map.Entry<Long, Double>> ascensionTicks = Maps.newConcurrentMap();

    public Ascension(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(ascensionTicks.containsKey(player.getUniqueId()))
            ascensionTicks.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!isEnabled()) return;

        Player player = event.getPlayer();
        Location from = event.getFrom(), to = event.getTo();

        // Player just moved camera
        if(from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) return;

        // Player is falling
        if(from.getY() >= to.getY()) return;

        // Player has bypass
        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        // Player is flying or not in survival
        if(player.isFlying() || !player.getGameMode().equals(GameMode.SURVIVAL)) return;

        // Player is in vehicle
        if(player.getVehicle() != null) return;

        ACPlayer acPlayer = getRac().getPlayerManager().getPlayerByUUID(player.getUniqueId());

        // Player has been recently attacked
        if((System.currentTimeMillis() - acPlayer.getRecentAttack()) < 1000L) return;

        // Player has recently bounced on a slime block
        if((System.currentTimeMillis() - acPlayer.getRecentBounce()) <= 2000L) return;

        long time = System.currentTimeMillis();
        double distance = 0.0;
        double limit = 0.5;
        double offsetY = MathUtils.offset(MathUtils.getVerticalVector(from.toVector()), MathUtils.getVerticalVector(to.toVector()));

        if(ascensionTicks.containsKey(player.getUniqueId())) {
            time = ascensionTicks.get(player.getUniqueId()).getKey();
            distance = ascensionTicks.get(player.getUniqueId()).getValue();
        }

        long difference = System.currentTimeMillis() - time;

        if(offsetY > 0.0)
            distance += offsetY;

        if(LocationUtils.isNearBlocks(player.getLocation()) ||
                LocationUtils.isNearBlocks(player.getLocation().clone().subtract(0, 1, 0)))
            distance = 0.0;

        if(player.hasPotionEffect(PotionEffectType.JUMP)) {
            for(PotionEffect effect : player.getActivePotionEffects()) {
                if(!effect.getType().equals(PotionEffectType.JUMP)) continue;

                int level = effect.getAmplifier() + 1;
                limit += Math.pow(level + 4.2, 2.0) / 16.0;
                break;
            }
        }

        if(distance > limit) {
            if(difference > 500L) {
                addViolation(player.getUniqueId(), new Violation(player.getName() + " is ascending up " + Math.round(distance) + " blocks"), false);

                event.setCancelled(true);
                player.teleport(from);

                time = System.currentTimeMillis();
            }
        }

        else
            time = System.currentTimeMillis();

        ascensionTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<>(time, distance));
    }
}
