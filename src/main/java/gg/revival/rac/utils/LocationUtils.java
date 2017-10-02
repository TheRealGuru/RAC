package gg.revival.rac.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class LocationUtils {

    /**
     * Returns true if the given location is near any blocks
     * @param location
     * @return
     */
    public static boolean isNearBlocks(Location location) {
        for(Block block : BlockUtils.getSurroundingBlocks(location.getBlock())) {
            if(block.getType() == null || block.getType().equals(Material.AIR)) continue;
            return true;
        }

        Location lowerBlock = location.clone().subtract(0, -0.5, 0);

        return lowerBlock != null && !lowerBlock.getBlock().getType().equals(Material.AIR);
    }
}
