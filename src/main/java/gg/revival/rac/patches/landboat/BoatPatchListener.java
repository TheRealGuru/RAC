package gg.revival.rac.patches.landboat;

import gg.revival.rac.RAC;
import gg.revival.rac.utils.Permissions;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BoatPatchListener implements Listener {

    @Getter private RAC rac;

    public BoatPatchListener(RAC rac) {
        this.rac = rac;
    }

    @EventHandler
    public void onPlayerAttemptBoat(PlayerInteractEvent event) {
        if(!rac.getCfg().isPatchBoats()) return;

        if(event.isCancelled()) return;

        Player player = event.getPlayer();
        Action action = event.getAction();

        // Player has bypass
        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        // Wasn't a placing interaction with the water
        if(!action.equals(Action.RIGHT_CLICK_BLOCK)) return;

        // Player is not placing a boat
        if(player.getItemInHand() == null || !player.getItemInHand().getType().equals(Material.BOAT)) return;

        // Player is clicking nothing for some reason
        if(event.getClickedBlock() == null || event.getClickedBlock().getType().equals(Material.AIR)) return;

        Block block = event.getClickedBlock();

        // Player is indeed placing the boat in water
        if(block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER)) return;

        // Player is placing the boat in water, but they clicked the floor of the lake/ocean
        if(block.getRelative(BlockFace.UP).getType().equals(Material.WATER) || block.getRelative(BlockFace.UP).getType().equals(Material.STATIONARY_WATER)) return;

        event.setCancelled(true);
        player.updateInventory();
    }

}
