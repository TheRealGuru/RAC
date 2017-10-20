package gg.revival.rac.modules.cont;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
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

import java.util.Map;

public class VClip extends Check implements Listener {

    private final ImmutableList<Material> exemptBlocks = ImmutableList.of(Material.WALL_SIGN, Material.SIGN, Material.SIGN_POST, Material.WOOD_PLATE, Material.STONE_PLATE, Material.IRON_PLATE, Material.GOLD_PLATE,
            Material.CAKE_BLOCK, Material.FENCE, Material.STEP, Material.AIR, Material.SLIME_BLOCK, Material.DAYLIGHT_DETECTOR, Material.DAYLIGHT_DETECTOR_INVERTED);

    private final ImmutableList<String> exemptBlockVariants = ImmutableList.of("_FENCE", "FENCE_", "_STAIRS", "_STEP", "PISTON_");

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

        // Player is running up stairs/slabs
        for(Block nearbyBlocks : BlockUtils.getNearbyBlocks(to, 1)) {
            if(exemptBlocks.contains(nearbyBlocks.getType())) return;

            for(String variantNames : exemptBlockVariants)
                if(nearbyBlocks.getType().name().contains(variantNames)) return;
        }

        int clippedBlocks = 0;
        Map<Material, Integer> clippedBlockData = Maps.newHashMap();

        for(int y = (int)Math.round(yDifference), i = 0; i < y; i++) {
            Location location = (yDifference < -0.5) ? to.clone().add(0.0, i, 0.0) : from.clone().add(0.0, i, 0.0);

            Block block = location.getBlock();

            if(!block.getType().isSolid()) continue;

            if(exemptBlocks.contains(block.getType())) continue;

            boolean isExempt = false;

            for(String variantNames : exemptBlockVariants) {
                if(block.getType().name().contains(variantNames)) {
                    isExempt = true;
                    break;
                }
            }

            if(isExempt) continue;

            if(clippedBlockData.containsKey(block.getType()))
                clippedBlockData.put(block.getType(), clippedBlockData.get(block.getType()) + 1);
            else
                clippedBlockData.put(block.getType(), 1);

            clippedBlocks++;
        }

        if(clippedBlocks > 0) {
            addViolation(player.getUniqueId(), new Violation(player.getName() + " tried moving vertically through " + clippedBlocks + " blocks"), true);

            verbose(player.getName() + " tried moving vertically though " + clippedBlocks + " blocks");
            for(Material clippedBlock : clippedBlockData.keySet())
                verbose("Type: " + clippedBlock.name() + ", Amt: " + clippedBlockData.get(clippedBlock));

            event.setCancelled(true);
            player.teleport(from);
        }
    }
}
