package gg.revival.rac.learning.cont;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.revival.rac.RAC;
import gg.revival.rac.learning.DataSet;
import gg.revival.rac.learning.DataSetType;
import gg.revival.rac.utils.Permissions;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class ReachLearning extends DataSet implements Listener {

    // Sprint distances are usually when the player is just holding down W with sprint toggled without w-tapping
    @Getter private Map<UUID, List<Double>> sprintDistances = Maps.newConcurrentMap();

    // Walk distances are usually caused by w-tapping and it's expected for these to be higher than sprint values
    @Getter private Map<UUID, List<Double>> walkDistances = Maps.newConcurrentMap();

    public ReachLearning(RAC rac, DataSetType type) {
        super(rac, type);

        Bukkit.getPluginManager().registerEvents(this, getRac());

        new BukkitRunnable() {
            public void run() {
                if(getTrackedPlayers().isEmpty()) return;

                for(UUID uuid : getTrackedPlayers().keySet()) {
                    if(Bukkit.getPlayer(uuid) == null) continue;

                    Player player = Bukkit.getPlayer(uuid);

                    if(walkDistances.containsKey(player.getUniqueId()) && sprintDistances.containsKey(player.getUniqueId())) {
                        getRac().getNotifications().createMessage(ChatColor.YELLOW + "[Learning Progress] " + ChatColor.BLUE + "[" + player.getName() + "] " + ChatColor.WHITE + ": " + ChatColor.RED + "[Reach] " + ChatColor.RESET +
                                "Walking: " + walkDistances.get(player.getUniqueId()).size() + "/" + getTrackedPlayers().get(player.getUniqueId()) + " " +
                                "Sprinting: " + sprintDistances.get(player.getUniqueId()).size() + "/" + getTrackedPlayers().get(player.getUniqueId()));
                    }
                }
            }
        }.runTaskTimer(getRac(), 0L, 30 * 20L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(getTrackedPlayers().containsKey(player.getUniqueId())) {
            sprintDistances.remove(player.getUniqueId());
            walkDistances.remove(player.getUniqueId());

            getTrackedPlayers().remove(player.getUniqueId());

            getRac().getLog().log(Level.WARNING, player.getName() + " logged out while their data was being collected... Dumping all information");
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) return;

        Player player = (Player)event.getDamager();
        Entity entity = event.getEntity();

        // Player isn't being tracked
        if(!getTrackedPlayers().containsKey(player.getUniqueId())) return;

        if(!sprintDistances.containsKey(player.getUniqueId()))
            sprintDistances.put(player.getUniqueId(), Lists.newArrayList());

        if(!walkDistances.containsKey(player.getUniqueId()))
            walkDistances.put(player.getUniqueId(), Lists.newArrayList());

        // Player is in creative which has further reach than Survival
        if(!player.getGameMode().equals(GameMode.SURVIVAL)) return;

        // Player is flying which means their velocity could be higher than possible in vanilla survival
        if(player.isFlying()) return;

        double distance = player.getEyeLocation().distance(entity.getLocation());

        if(distance < 3.5) return; // We really do not need anything under this value

        if(player.isSprinting()) {
            if(sprintDistances.get(player.getUniqueId()).size() < getTrackedPlayers().get(player.getUniqueId()))
                sprintDistances.get(player.getUniqueId()).add(distance);
        } else {
            if(walkDistances.get(player.getUniqueId()).size() < getTrackedPlayers().get(player.getUniqueId()))
                walkDistances.get(player.getUniqueId()).add(distance);
        }

        if(sprintDistances.get(player.getUniqueId()).size() >= getTrackedPlayers().get(player.getUniqueId()) &&
                walkDistances.get(player.getUniqueId()).size() >= getTrackedPlayers().get(player.getUniqueId())) {

            for(Player players : Bukkit.getOnlinePlayers()) {
                if(!players.hasPermission(Permissions.LEARNING_ACCESS)) continue;

                players.sendMessage(getRac().getNotifications().getPrefix() + ChatColor.YELLOW + "Reach learning completed for " + ChatColor.BLUE + player.getName() + ChatColor.YELLOW + ". Walk Dist: " +
                ChatColor.WHITE + getAverageWalkDistance(player.getUniqueId()) + ", Sprint Dist: " + getAverageSprintDistance(player.getUniqueId()));
            }

            getRac().getLog().log("Learning: " + getType().toString() + ", " + player.getName() + "'s Average Walking Reach Distance: " + getAverageWalkDistance(player.getUniqueId()) +
            ", Average Sprinting Reach Distance: " + getAverageSprintDistance(player.getUniqueId()));

            sprintDistances.remove(player.getUniqueId());
            walkDistances.remove(player.getUniqueId());
            getTrackedPlayers().remove(player.getUniqueId());
        }
    }

    /**
     * Returns the average sprinting distance for the given UUID
     * @param uuid
     * @return
     */
    private double getAverageSprintDistance(UUID uuid) {
        double distance = 0.0;

        if(!sprintDistances.containsKey(uuid)) return distance;

        for(double distances : sprintDistances.get(uuid))
            distance += distances;

        return distance / sprintDistances.get(uuid).size();
    }

    /**
     * Returns the average walking distance for the given UUID
     * @param uuid
     * @return
     */
    private double getAverageWalkDistance(UUID uuid) {
        double distance = 0.0;

        if(!walkDistances.containsKey(uuid)) return distance;

        for(double distances : walkDistances.get(uuid))
            distance += distances;

        return distance / walkDistances.get(uuid).size();
    }

}
