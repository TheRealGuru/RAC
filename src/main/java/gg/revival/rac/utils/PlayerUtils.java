package gg.revival.rac.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PlayerUtils {

    /**
     * Returns true if the player is inside the given material (feet level)
     * @param player
     * @param material
     * @return
     */
    public static boolean isInBlock(Player player, Material material) {
        return player.getLocation().getBlock().getType().equals(material);
    }

    /**
     * Returns true if the player is standing on top of the given material (under feet)
     * @param player
     * @param material
     * @return
     */
    public static boolean isStandingOnBlock(Player player, Material material) {
        Location location = player.getLocation().clone();
        location.subtract(0, 1.0, 0);
        return location.getBlock().getType().equals(material);
    }

    /**
     * Returns true if the player is standing on a block
     * @param player
     * @return
     */
    public static boolean isOnGround(Player player) {
        if(!player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR))
            return true;

        Location location = player.getLocation().clone();
        location.setY(location.getY() - 0.5);

        if(!location.getBlock().getType().equals(Material.AIR))
            return true;

        location = player.getLocation().clone();
        location.setY(location.getY() + 0.5);

        return !location.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR);
    }

    /**
     * Returns true if the given block can be stood inside of
     * @param block
     * @return
     */
    public static boolean canStandWithin(Block block) {
        if(block.getType().equals(Material.SAND) || block.getType().equals(Material.GRAVEL)) return false;

        if(!block.getType().isSolid() ||
                block.getType().name().toLowerCase().contains("door") ||
                block.getType().name().toLowerCase().contains("fence") ||
                block.getType().name().toLowerCase().contains("bars") ||
                block.getType().name().toLowerCase().contains("sign")) return false;

        return true;
    }

    /**
     * Returns true if the given player is hovering directly over water blocks
     * @param player
     * @return
     */
    public static boolean isHoveringOverWater(Player player) {
        Location location = player.getLocation();
        Location below = location.getBlock().getRelative(BlockFace.DOWN).getLocation();

        for(BlockFace directions : BlockFace.values()) {
            if(directions.equals(BlockFace.UP) || directions.equals(BlockFace.DOWN)) continue;

            if(!location.getBlock().getRelative(directions).getType().equals(Material.AIR)) return false;
            if(!below.getBlock().getRelative(directions).getType().equals(Material.WATER) && !below.getBlock().getRelative(directions).getType().equals(Material.STATIONARY_WATER)) return false;
        }

        return true;
    }

    /**
     * Returns true if the given location is submerged in water
     * @param location
     * @return
     */
    public static boolean isFullyInWater(Location location) {
        double fixedX = MathUtils.getFixedXAxis(location.getX());
        Location fixedXWithZ = new Location(location.getWorld(), fixedX, location.getY(), location.getBlockZ());
        Location fixedXWithY = new Location(location.getWorld(), fixedX, location.getBlockY(), location.getZ());

        return fixedXWithZ.getBlock().isLiquid() || fixedXWithY.getBlock().isLiquid();
    }

    /**
     * Returns the client packet ping (can be inaccurate and spoofed sadly)
     * @param player
     * @return
     */
    public static int getPing(Player player) {
        return ((CraftPlayer)player).getHandle().ping;
    }

}
