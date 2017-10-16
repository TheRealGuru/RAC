package gg.revival.rac.modules.cont;

import com.google.common.collect.Maps;
import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.Permissions;
import gg.revival.rac.utils.PlayerUtils;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class NoFall extends Check implements Listener {

    @Getter private Map<UUID, Map.Entry<Long, Integer>> nofallTicks = Maps.newConcurrentMap();
    @Getter private Map<UUID, Double> fallDistances = Maps.newConcurrentMap();

    public NoFall(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(nofallTicks.containsKey(player.getUniqueId()))
            nofallTicks.remove(player.getUniqueId());

        if(fallDistances.containsKey(player.getUniqueId()))
            fallDistances.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!isEnabled()) return;

        Player player = event.getPlayer();
        Location from = event.getFrom(), to = event.getTo();

        // Player moved camera
        if(from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) return;

        // Player is moving up
        if(from.getY() < to.getY()) return;

        // Player has bypass
        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        // Player is flying or not in survival
        if(player.isFlying() || !player.getGameMode().equals(GameMode.SURVIVAL)) return;

        // Player is in a vehicle
        if(player.getVehicle() != null) return;

        // Player is on respawn screen
        if(player.isDead() || player.getHealth() <= 0.0) return;

        // Player is in a liquid
        if(PlayerUtils.isInBlock(player, Material.WATER) ||
                PlayerUtils.isInBlock(player, Material.STATIONARY_WATER) ||
                PlayerUtils.isInBlock(player, Material.LAVA) ||
                PlayerUtils.isInBlock(player, Material.STATIONARY_LAVA)) return;

        // Player is on a climbable
        if(PlayerUtils.isOnClimbable(player)) return;

        double distance = 0.0;

        if(!PlayerUtils.isOnGround(player) && to.getY() < from.getY()) {
            if(fallDistances.containsKey(player.getUniqueId()))
                distance = fallDistances.get(player.getUniqueId());

            distance += from.getY() - to.getY();
        }

        fallDistances.put(player.getUniqueId(), distance);

        if(distance < 3.0) return;

        long time = System.currentTimeMillis();
        int flags = 0;

        if(nofallTicks.containsKey(player.getUniqueId())) {
            time = nofallTicks.get(player.getUniqueId()).getKey();
            flags = nofallTicks.get(player.getUniqueId()).getValue();
        }

        if(PlayerUtils.isOnGround(player) || player.getFallDistance() == 0.0f)
            flags++;
        else
            flags = 0;

        if(nofallTicks.containsKey(player.getUniqueId()) && (System.currentTimeMillis() - time) > 10000L) {
            time = System.currentTimeMillis();
            flags = 0;
        }

        if(flags >= 3) {
            flags = 0;

            fallDistances.put(player.getUniqueId(), 0.0);

            addViolation(player.getUniqueId(), new Violation(player.getName() + " didn't take damage after falling"), false);

            verbose(Arrays.asList(player.getName() + " didn't take damage after falling",
                    "Flags: " + flags));
        }

        nofallTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<>(time, flags));
    }

}
