package gg.revival.rac.utils;

import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.List;

public class BlockUtils {

    /**
     * Returns a List containing surrounding blocks
     * @param block
     * @return
     */
    public static List<Block> getSurroundingBlocks(Block block, boolean cardinal) {
        List<Block> blocks = Lists.newArrayList();

        for(BlockFace faces : BlockFace.values()) {
            if(!cardinal)
                if(!faces.equals(BlockFace.NORTH) && !faces.equals(BlockFace.EAST) && !faces.equals(BlockFace.WEST) && !faces.equals(BlockFace.SOUTH)) continue;

            blocks.add(block.getRelative(faces));
        }

        return blocks;
    }

    public static List<Block> getNearbyBlocks(Location location, int distance) {
        List<Block> result = Lists.newArrayList();
        int x1 = location.getBlockX() - distance, x2 = location.getBlockX() + distance;
        int y1 = location.getBlockY() - distance, y2 = location.getBlockY() + distance;
        int z1 = location.getBlockZ() - distance, z2 = location.getBlockZ() + distance;

        for(int x = x1; x < x2; x++) {
            for(int y = y1; y < y2; y++) {
                for(int z = z1; z < z2; z++) {
                    Location nearbyBlockLocation = new Location(location.getWorld(), x, y, z);

                    if(nearbyBlockLocation.getBlock() == null || nearbyBlockLocation.getBlock().getType().equals(Material.AIR)) continue;

                    result.add(nearbyBlockLocation.getBlock());
                }
            }
        }

        return result;
    }

    /**
     * Returns true if the given location is nearby any slab blocks
     * @param location
     * @return
     */
    public static boolean nearbySlabBlocks(Location location) {
        for (Block blocks : getSurroundingBlocks(location.getBlock(), true))

            if(blocks.getType().equals(Material.STEP) ||
                    blocks.getType().equals(Material.DOUBLE_STEP) ||
                    blocks.getType().equals(Material.WOOD_STEP) ||
                    blocks.getType().equals(Material.WOOD_DOUBLE_STEP) ||
                    blocks.getType().equals(Material.STONE_SLAB2) ||
                    blocks.getType().equals(Material.DOUBLE_STONE_SLAB2)) return true;

        return false;
    }

}
