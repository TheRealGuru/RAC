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
    public static List<Block> getSurroundingBlocks(Block block) {
        List<Block> blocks = Lists.newArrayList();

        for(BlockFace faces : BlockFace.values())
            blocks.add(block.getRelative(faces));

        return blocks;
    }

    /**
     * Returns true if the given location is nearby any slab blocks
     * @param location
     * @return
     */
    public static boolean nearbySlabBlocks(Location location) {
        for (Block blocks : getSurroundingBlocks(location.getBlock()))

            if(blocks.getType().equals(Material.STEP) ||
                    blocks.getType().equals(Material.DOUBLE_STEP) ||
                    blocks.getType().equals(Material.WOOD_STEP) ||
                    blocks.getType().equals(Material.WOOD_DOUBLE_STEP) ||
                    blocks.getType().equals(Material.STONE_SLAB2) ||
                    blocks.getType().equals(Material.DOUBLE_STONE_SLAB2)) return true;

        return false;
    }

}
