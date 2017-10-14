package gg.revival.rac.modules.cont;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.Permissions;
import lombok.Getter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.material.Gate;
import org.bukkit.material.MaterialData;
import org.bukkit.material.TrapDoor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Phase extends Check implements Listener {

    @Getter private final ImmutableList<Material> phaseBlocks = ImmutableList.of(Material.FENCE, Material.TRAP_DOOR, Material.FENCE_GATE, Material.IRON_BARDING, Material.THIN_GLASS,
            Material.NETHER_FENCE, Material.COBBLE_WALL, Material.ANVIL, Material.CHEST, Material.TRAPPED_CHEST, Material.HOPPER, Material.STAINED_GLASS_PANE, Material.IRON_TRAPDOOR,
            Material.ACACIA_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.SPRUCE_FENCE_GATE,
            Material.ACACIA_FENCE, Material.BIRCH_FENCE, Material.DARK_OAK_FENCE, Material.JUNGLE_FENCE, Material.SPRUCE_FENCE);

    @Getter private Set<UUID> recentlyChecked = Sets.newConcurrentHashSet();
    @Getter private Map<UUID, Location> recentDamageLocation = Maps.newConcurrentMap();

    public Phase(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(recentlyChecked.contains(player.getUniqueId()))
            recentlyChecked.remove(player.getUniqueId());

        if(recentDamageLocation.containsKey(player.getUniqueId()))
            recentDamageLocation.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!isEnabled()) return;

        Player player = event.getPlayer();
        Location from = event.getFrom(), to = event.getTo();
        final UUID uuid = player.getUniqueId();

        // Player has bypass
        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        // Player just moved their camera or jumped
        if(from.getX() == to.getX() && from.getZ() == to.getZ()) return;

        // Player is flying
        if(player.isFlying()) return;

        // Player is in a vehicle
        if(player.getVehicle() != null) return;

        // Player has been recently checked
        if(recentlyChecked.contains(uuid)) return;

        // The block the player is moving towards is valid
        if(to.getBlock() == null || to.getBlock().getType().equals(Material.AIR)) return;

        // Here we're getting the difference of the players location to the center of the block they're moving to
        double centerX = to.getBlockX() + 0.5, centerY = to.getBlockY() + 0.5, centerZ = to.getBlockZ() + 0.5;
        double xDifference = Math.abs(player.getLocation().getX() - centerX), yDifference = Math.abs(player.getLocation().getY() - centerY), zDifference = Math.abs(player.getLocation().getZ() - centerZ);

        // Check to see if the player is in the middle of a block
        if(xDifference <= getRac().getCfg().getSkipPhaseThreshold() && yDifference <= getRac().getCfg().getSkipPhaseThreshold() && zDifference <= getRac().getCfg().getSkipPhaseThreshold()) {
            // Check if the player is in the middle of a phase block
            if(phaseBlocks.contains(from.getBlock().getType())) {
                boolean skipCheck = false;

                BlockState fromBlockState = from.getBlock().getState();
                MaterialData fromData = fromBlockState.getData();

                // We don't want the phase check to fire if they're in the middle of open fencegates
                if(fromData instanceof Gate) {
                    Gate gate = (Gate) fromData;

                    if(gate.isOpen()) skipCheck = true;
                }

                // Same for trap doors
                if(fromData instanceof TrapDoor) {
                    TrapDoor trapDoor = (TrapDoor) fromData;

                    if(trapDoor.isOpen()) skipCheck = true;
                }

                // If they aren't, add a violation to the fucking cocksucker
                if(!skipCheck) {
                    addViolation(uuid, new Violation("[A] " + player.getName() + " moved through a " + WordUtils.capitalize(from.getBlock().getType().name().replace("_", " ").toLowerCase())), true);

                    if(getViolations().get(uuid).size() > getVlNotify()) {
                        event.setCancelled(true);
                        player.teleport(from);
                    }

                    return;
                }
            }

            if(phaseBlocks.contains(to.getBlock().getType())) {
                boolean skipCheck = false;

                BlockState toBlockState = to.getBlock().getState();
                MaterialData toData = toBlockState.getData();

                if(toData instanceof Gate) {
                    Gate gate = (Gate) toData;

                    if(gate.isOpen()) skipCheck = true;
                }

                if(toData instanceof TrapDoor) {
                    TrapDoor trapDoor = (TrapDoor) toData;

                    if(trapDoor.isOpen()) skipCheck = true;
                }

                if(!skipCheck) {
                    addViolation(uuid, new Violation("[A] " + player.getName() + " tried to move through a " + WordUtils.capitalize(to.getBlock().getType().name().replace("_", " ").toLowerCase())), true);

                    if(getViolations().get(uuid).size() > getVlNotify()) {
                        event.setCancelled(true);
                        player.teleport(from);
                    }

                    return;
                }
            }
        }

        recentlyChecked.add(player.getUniqueId());

        new BukkitRunnable() {
            public void run() {
                recentlyChecked.remove(uuid);
            }
        }.runTaskLaterAsynchronously(getRac(), 5L);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(!isEnabled()) return;

        if(!(event.getEntity() instanceof Player)) return;
        if(!event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)) return;

        Player player = (Player)event.getEntity();
        final UUID uuid = player.getUniqueId();
        Location storedLocation = null;
        boolean teleported = false;

        if(player.isFlying()) return;

        if(!player.getGameMode().equals(GameMode.SURVIVAL)) return;

        if(player.getVehicle() != null) return;

        if(recentDamageLocation.containsKey(player.getUniqueId()))
            storedLocation = recentDamageLocation.get(player.getUniqueId());

        if(storedLocation != null) {
            double distance = storedLocation.distance(player.getLocation());

            if(distance >= getRac().getCfg().getBlockPhaseDistance()) {
                player.teleport(storedLocation);
                addViolation(uuid, new Violation("[B] " + player.getName() + " moved " + distance + " blocks while taking suffocation damage"), true);
                teleported = true;
            }
        }

        if(!teleported && !recentDamageLocation.containsKey(uuid)) {
            recentDamageLocation.put(uuid, player.getLocation());

            new BukkitRunnable() {
                public void run() {
                    recentDamageLocation.remove(uuid);
                }
            }.runTaskLaterAsynchronously(getRac(), 20L);
        }
    }

}
