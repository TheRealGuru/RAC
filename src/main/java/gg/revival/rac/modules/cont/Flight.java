package gg.revival.rac.modules.cont;

import com.google.common.collect.Maps;
import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.players.ACPlayer;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.LocationUtils;
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

import java.util.Map;
import java.util.UUID;

public class Flight extends Check implements Listener {

    public Flight(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @Getter private Map<UUID, Long> flyingTicks = Maps.newConcurrentMap();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(flyingTicks.containsKey(player.getUniqueId()))
            flyingTicks.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!isEnabled()) return;

        Player player = event.getPlayer();
        Location from = event.getFrom(), to = event.getTo();
        final UUID uuid = player.getUniqueId();

        // Player has bypass
        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        // Player most likely just moved their camera
        if(from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) return;

        // Player isn't in survival
        if(!player.getGameMode().equals(GameMode.SURVIVAL)) return;

        // Player is flying somehow
        if(player.isFlying()) return;

        // Player is riding some form of vehicle
        if(player.getVehicle() != null) return;

        // Player is in a block that would modify their physics
        if(PlayerUtils.isInBlock(player, Material.WATER) ||
                PlayerUtils.isInBlock(player, Material.STATIONARY_WATER) ||
                PlayerUtils.isInBlock(player, Material.LAVA) ||
                PlayerUtils.isInBlock(player, Material.STATIONARY_LAVA) ||
                PlayerUtils.isInBlock(player, Material.WEB)) return;

        // Player is nearby blocks
        if(LocationUtils.isNearBlocks(player.getLocation())) {
            if(flyingTicks.containsKey(player.getUniqueId()))
                flyingTicks.remove(player.getUniqueId());

            return;
        }

        ACPlayer acPlayer = getRac().getPlayerManager().getPlayerByUUID(player.getUniqueId());

        // Player has recently bounced on a slime block
        if((System.currentTimeMillis() - acPlayer.getRecentBounce()) <= 2000L) return;

        if(to.getY() < from.getY()) {
            if(flyingTicks.containsKey(player.getUniqueId()))
                flyingTicks.remove(player.getUniqueId());

            return;
        }

        long time = System.currentTimeMillis();

        if(flyingTicks.containsKey(player.getUniqueId()))
            time = flyingTicks.get(player.getUniqueId());

        final long timeDifference = System.currentTimeMillis() - time;

        if(timeDifference > getRac().getCfg().getFlightMaxMS()) {
            flyingTicks.remove(player.getUniqueId());

            addViolation(player.getUniqueId(), new Violation(player.getName() + " has been hovering above the ground for " + timeDifference + "ms"), false);

            event.setCancelled(true);
            player.teleport(from);

            return;
        }

        flyingTicks.put(uuid, time);
    }
}
