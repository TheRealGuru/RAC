package gg.revival.rac.modules;

import com.google.common.collect.Maps;
import gg.revival.rac.RAC;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.Permissions;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Check {

    @Getter RAC rac;
    @Getter String name;
    @Getter Cheat cheat;
    @Getter ActionType action;
    @Getter Map<UUID, List<Violation>> violations;
    @Getter int vlNotify, vlAction, vlExpire;
    @Getter boolean enabled;

    public Check(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        this.rac = rac;
        this.name = name;
        this.cheat = cheat;
        this.action = action;
        this.violations = Maps.newConcurrentMap();
        this.vlNotify = vlNotify;
        this.vlAction = vlAction;
        this.vlExpire = vlExpire;
        this.enabled = enabled;
    }

    /**
     * Adds a violation to the given UUID for this check
     * @param uuid Player UUID
     * @param violation New Violation w/ Info
     * @param important Should this notification be sent without meeting the notifyvl
     */
    public void addViolation(UUID uuid, Violation violation, boolean important) {
        List<Violation> vl;

        if(violations.containsKey(uuid))
            vl = violations.get(uuid);
        else
            vl = new ArrayList<>();

        vl.add(violation);
        violations.put(uuid, vl);

        int vlCount = vl.size();

        if(vlCount >= vlNotify || important) {
            rac.getNotifications().createMessage(violation.getInformation() + " VL: " + vlCount);
            rac.getLog().log(violation.getInformation() + " VL: " + vlCount);
        }

        if(vlCount >= vlAction) {
            new BukkitRunnable() {
                public void run() {
                    if(action.equals(ActionType.BAN)) {
                        if(Bukkit.getPlayer(uuid) != null) {
                            Bukkit.broadcastMessage(rac.getNotifications().getPrefix() + ChatColor.RED + Bukkit.getPlayer(uuid).getName() + " has been automatically removed from the network for cheating");

                            if(!Bukkit.getPlayer(uuid).hasPermission(Permissions.CHECK_BYPASS)) {
                                rac.getLog().log("'" + uuid.toString() + "' (" + Bukkit.getPlayer(uuid).getName() + ") was banned for: " + cheat.toString().toLowerCase().replace("_", " "));
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + Bukkit.getPlayer(uuid).getName() + " [RAC] Modified client");
                            }
                        }
                    }

                    if(action.equals(ActionType.KICK)) {
                        if(Bukkit.getPlayer(uuid) != null) {
                            rac.getNotifications().createMessage(ChatColor.RED + Bukkit.getPlayer(uuid).getName() + " has been kicked for suspicious activity");

                            if(!Bukkit.getPlayer(uuid).hasPermission(Permissions.CHECK_BYPASS)) {
                                rac.getLog().log("'" + uuid.toString() + "' (" + Bukkit.getPlayer(uuid).getName() + ") was kicked for: " + cheat.toString().toLowerCase().replace("_", " "));
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + Bukkit.getPlayer(uuid).getName() + " Suspicious movement");
                            }
                        }
                    }
                }
            }.runTask(getRac());
        }

        new BukkitRunnable() {
            public void run() {
                if(violations.containsKey(uuid))
                    violations.get(uuid).remove(violation);
            }
        }.runTaskLaterAsynchronously(rac, vlExpire * 20L);
    }

}
