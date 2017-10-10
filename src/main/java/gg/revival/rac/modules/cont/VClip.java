package gg.revival.rac.modules.cont;

import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.BlockUtils;
import gg.revival.rac.utils.Permissions;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class VClip extends Check implements Listener {

    public VClip(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!isEnabled()) return;

        Player player = event.getPlayer();
        Location from = event.getFrom(), to = event.getTo();

        // Player has bypass
        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        // Player is flying
        if(player.isFlying()) return;

        // Player isn't in survival
        if(!player.getGameMode().equals(GameMode.SURVIVAL)) return;

        // Player is in a vehicle
        if(player.getVehicle() != null) return;

        double yDifference = Math.abs(to.getY() - from.getY());

        // Player moved up a slab
        if(yDifference < 0.5) return;

        // Player is running up stairs
        for(Block nearbyBlocks : BlockUtils.getNearbyBlocks(to, 2)) {
            if(nearbyBlocks.getType() == null || !nearbyBlocks.getType().name().toLowerCase().contains("_stairs")) continue;
            return;
        }

        int clippedBlocks = 0;

        for(int y = (int)Math.round(yDifference), i = 0; i < y; i++) {
            Location location = (yDifference < -0.5) ? to.clone().add(0.0, i, 0.0) : from.clone().add(0.0, i, 0.0);

            Block block = location.getBlock();

            if(block == null || block.getType().equals(Material.AIR) ||
                    !block.getType().isSolid() && !block.getType().isBlock()) continue;

            clippedBlocks++;
        }

        if(clippedBlocks > 0) {
            event.setCancelled(true);
            player.teleport(from);
            addViolation(player.getUniqueId(), new Violation(player.getName() + " tried moving vertically through " + clippedBlocks + " blocks"), true);
        }
    }
}
