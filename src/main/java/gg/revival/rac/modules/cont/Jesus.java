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
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

public class Jesus extends Check implements Listener {

    @Getter private Map<UUID, Long> jesusTicks = Maps.newConcurrentMap();

    public Jesus(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(jesusTicks.containsKey(player.getUniqueId()))
            jesusTicks.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!isEnabled()) return;

        Player player = event.getPlayer();
        Location from = event.getFrom(), to = event.getTo();

        // Player just moved camera or jumped
        if(from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) return;

        // Player has bypass
        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        // Player is flying or not in survival (creative/spec)
        if(player.isFlying() || !player.getGameMode().equals(GameMode.SURVIVAL)) return;

        // Player is probably in a boat
        if(player.getVehicle() != null) return;

        // Player is standing on a boat
        for(Entity nearbyEntities : player.getNearbyEntities(2, 2, 2)) {
            if(!(nearbyEntities instanceof Boat)) continue;

            return;
        }

        long time = System.currentTimeMillis();

        if(jesusTicks.containsKey(player.getUniqueId()))
            time = jesusTicks.get(player.getUniqueId());

        long difference = System.currentTimeMillis() - time;

        // Last check was over 5 seconds ago, start a new one
        if(difference > 5000L) {
            time = System.currentTimeMillis();
            jesusTicks.put(player.getUniqueId(), time);
            return;
        }

        // Player is in water
        if(PlayerUtils.isInBlock(player, Material.WATER) || PlayerUtils.isInBlock(player, Material.STATIONARY_WATER)) return;

        // Player is in water
        if(PlayerUtils.isFullyInWater(player.getLocation())) return;

        // Player is not hovering over water
        if(!PlayerUtils.isHoveringOverWater(player)) return;

        if(difference > 500L) {
            addViolation(player.getUniqueId(), new Violation(player.getName() + " has been hovering above water for " + difference + "ms"), false);

            event.setCancelled(true);
            return;
        }

        jesusTicks.put(player.getUniqueId(), System.currentTimeMillis());
    }

}
