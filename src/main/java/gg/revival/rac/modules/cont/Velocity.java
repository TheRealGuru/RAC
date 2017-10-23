package gg.revival.rac.modules.cont;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.packets.events.PacketPlayerEvent;
import gg.revival.rac.players.ACPlayer;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.BlockUtils;
import gg.revival.rac.utils.Permissions;
import gg.revival.rac.utils.PlayerUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Velocity extends Check implements Listener {

    @Getter private Map<UUID, Map.Entry<Integer, Long>> velocityFlags = Maps.newConcurrentMap();
    @Getter private Map<UUID, Double> velocityHeights = Maps.newConcurrentMap();
    @Getter private Map<UUID, Long> lastUpdate = Maps.newConcurrentMap();

    public Velocity(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);

        if(!isEnabled()) return;

        new BukkitRunnable() {
            public void run() {
                performUpdate();
            }
        }.runTaskTimer(getRac(), 0L, 20L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(velocityFlags.containsKey(player.getUniqueId()))
            velocityFlags.remove(player.getUniqueId());

        if(lastUpdate.containsKey(player.getUniqueId()))
            lastUpdate.remove(player.getUniqueId());

        if(velocityHeights.containsKey(player.getUniqueId()))
            velocityHeights.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(!isEnabled()) return;

        Player player = event.getEntity();

        // Player was probably deathbanned or kicked on death
        if(player == null) return;

        // Player has bypass
        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        if(velocityFlags.containsKey(player.getUniqueId()))
            velocityFlags.remove(player.getUniqueId());

        if(lastUpdate.containsKey(player.getUniqueId()))
            lastUpdate.remove(player.getUniqueId());

        if(velocityHeights.containsKey(player.getUniqueId()))
            velocityHeights.remove(player.getUniqueId());

        ACPlayer acPlayer = getRac().getPlayerManager().getPlayerByUUID(player.getUniqueId());

        if(acPlayer == null) return;

        acPlayer.setRecentAttack(0L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!isEnabled()) return;

        if(event.isCancelled()) return;

        if(!(event.getEntity() instanceof Player)) return;

        // Make sure the player is not attacking himself or pearling
        if(event.getEntity().getUniqueId().equals(event.getDamager().getUniqueId()) || !event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) return;

        Player player = (Player)event.getEntity();

        // Player has bypass
        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        // Player is on a vehicle
        if(player.getVehicle() != null) return;

        // Player is flying
        if(player.isFlying()) return;

        Location location = player.getLocation().clone();
        location.add(0, player.getEyeHeight() + 1.0, 0.0);

        for(Block nearbyBlocks : BlockUtils.getNearbyBlocks(location, 1)) {
            if(nearbyBlocks == null || nearbyBlocks.getType().equals(Material.AIR) || !nearbyBlocks.getType().isSolid()) continue;
            return; // Player is standing near blocks which could possible prevent their knockback
        }

        velocityHeights.put(player.getUniqueId(), player.getLocation().getY());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!isEnabled()) return;

        Player player = event.getPlayer();
        Location to = event.getTo();

        if(!velocityHeights.containsKey(player.getUniqueId())) return;

        // If true it appears they've taken some form of upwards velocity
        if(to.getY() > velocityHeights.get(player.getUniqueId()))
            velocityHeights.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerPacket(PacketPlayerEvent event) {
        if(!isEnabled()) return;

        Player player = event.getPlayer();
        lastUpdate.put(player.getUniqueId(), System.currentTimeMillis());
    }

    private void performUpdate() {
        if(!isEnabled()) return;

        List<UUID> toRemove = Lists.newArrayList();

        for(UUID uuid : velocityHeights.keySet()) {
            if(Bukkit.getPlayer(uuid) == null) continue;

            Player player = Bukkit.getPlayer(uuid);
            ACPlayer acPlayer = getRac().getPlayerManager().getPlayerByUUID(player.getUniqueId());

            int flags = 0;
            long time = System.currentTimeMillis();

            if(velocityFlags.containsKey(player.getUniqueId())) {
                flags = velocityFlags.get(player.getUniqueId()).getKey();
                time = velocityFlags.get(player.getUniqueId()).getValue();

                if((System.currentTimeMillis() - time) >= 5000L) {
                    flags = 0;
                    time = System.currentTimeMillis();
                }
            }

            if(System.currentTimeMillis() >= (acPlayer.getRecentAttack() + 1000L)) {
                toRemove.add(player.getUniqueId());

                if(System.currentTimeMillis() - lastUpdate.get(uuid) < 60L) { // Check to make sure the player isn't lagging out
                    ++flags;
                    time = System.currentTimeMillis();
                } else {
                    flags = 0;
                }
            }

            if(flags >= getRac().getCfg().getVelocityFlags()) {
                flags = 0;

                addViolation(uuid, new Violation(player.getName() + " is not taking any upwards velocity changes"), false);

                verbose(Arrays.asList(player.getName() + " is not taking any upwards velocity changes",
                        "Flags: " + flags + ", Ping: " + PlayerUtils.getPing(player) + ", Time since last packet: " + (System.currentTimeMillis() - lastUpdate.get(uuid) + "ms" +
                        ", Time since last attack: " + (System.currentTimeMillis() - acPlayer.getRecentAttack()) + "ms")));
            }

            velocityFlags.put(player.getUniqueId(), new AbstractMap.SimpleEntry<>(flags, time));
        }

        if(!toRemove.isEmpty()) {
            for(UUID removed : toRemove) {
                if(!velocityHeights.containsKey(removed)) continue;
                velocityHeights.remove(removed);
            }
        }
    }
}
