package gg.revival.rac.modules.cont;

import com.google.common.collect.Sets;
import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.Permissions;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BlockGlitch extends Check implements Listener {

    @Getter private Set<UUID> recentBreaks = Sets.newConcurrentHashSet();

    @Getter private final List<Material> clickables = Arrays.asList(Material.ANVIL, Material.BEACON, Material.BED_BLOCK, Material.BED, Material.BREWING_STAND, Material.BURNING_FURNACE, Material.CAULDRON,
            Material.CHEST, Material.TRAPPED_CHEST, Material.DIODE, Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON, Material.DROPPER, Material.ENCHANTMENT_TABLE, Material.ENDER_CHEST, Material.FENCE_GATE,
            Material.FURNACE, Material.HOPPER, Material.ITEM_FRAME, Material.JUKEBOX, Material.LEVER, Material.NOTE_BLOCK, Material.REDSTONE_COMPARATOR, Material.REDSTONE_COMPARATOR_OFF,
            Material.REDSTONE_COMPARATOR_ON, Material.TRAP_DOOR, Material.WOODEN_DOOR, Material.WORKBENCH, Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.WOOD_DOOR, Material.STONE_PLATE,
            Material.WOOD_PLATE, Material.IRON_PLATE, Material.GOLD_PLATE, Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.DARK_OAK_DOOR, Material.JUNGLE_DOOR, Material.SPRUCE_DOOR,
            Material.ACACIA_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.SPRUCE_FENCE_GATE);

    public BlockGlitch(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        Block block = event.getBlock();

        if(!event.isCancelled()) return;

        if(!block.getType().isSolid()) return;

        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        if(recentBreaks.contains(player.getUniqueId())) return;

        recentBreaks.add(uuid);

        new BukkitRunnable() {
            public void run() {
                recentBreaks.remove(uuid);
            }
        }.runTaskLater(getRac(), 10L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) return;

        Player player = (Player)event.getDamager();

        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        if(recentBreaks.contains(player.getUniqueId())) {
            addViolation(player.getUniqueId(), new Violation(player.getName() + " is attempting to glitch through blocks"), false);

            verbose(player.getName() + " attempted to attack a " + event.getEntity().getType().name());

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block block = event.getClickedBlock();

        if(event.isCancelled()) return;

        if(!action.equals(Action.RIGHT_CLICK_BLOCK)) return;

        if(block == null || block.getType().equals(Material.AIR)) return;

        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        if(!clickables.contains(block.getType())) return;

        if(recentBreaks.contains(player.getUniqueId())) {
            addViolation(player.getUniqueId(), new Violation(player.getName() + " is attempting to glitch through blocks"), false);

            verbose(player.getName() + " attempted to interact with a " + block.getType().name());

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractWithEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();

        if(event.isCancelled()) return;

        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        if(recentBreaks.contains(player.getUniqueId())) {
            addViolation(player.getUniqueId(), new Violation(player.getName() + " is attempting to glitch through blocks"), false);

            verbose(player.getName() + " attempted to interact with a " + event.getRightClicked().getType().name());

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerEnterBed(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();

        if(event.isCancelled()) return;

        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        if(recentBreaks.contains(player.getUniqueId())) {
            addViolation(player.getUniqueId(), new Violation(player.getName() + " is attempting to glitch through blocks"), false);

            verbose(player.getName() + " attempted to enter a bed");

            event.setCancelled(true);
        }
    }
}
