package gg.revival.rac.patches.landboat;

import gg.revival.rac.RAC;
import gg.revival.rac.utils.BlockUtils;
import gg.revival.rac.utils.Permissions;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class BoatPatchTask extends BukkitRunnable {

    @Getter private RAC rac;

    public BoatPatchTask(RAC rac) {
        this.rac = rac;
    }

    @Override
    public void run() {
        if(!rac.getCfg().isPatchBoats()) return;

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getVehicle() == null || !(player.getVehicle() instanceof Boat)) continue;

            if(player.hasPermission(Permissions.CHECK_BYPASS)) continue;

            boolean onLand = true;

            for(Block nearbyBlocks : BlockUtils.getNearbyBlocks(player.getLocation().clone().subtract(0, 1, 0), 1)) {
                if(!nearbyBlocks.getType().equals(Material.WATER) && !nearbyBlocks.getType().equals(Material.STATIONARY_WATER)) continue;

                onLand = false;

                break;
            }

            if(onLand) {
                Entity vehicle = player.getVehicle();

                vehicle.getLocation().getWorld().dropItem(vehicle.getLocation(), new ItemStack(Material.BOAT));
                vehicle.eject();
                vehicle.remove();
            }
        }
    }

}
