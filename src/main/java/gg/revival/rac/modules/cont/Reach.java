package gg.revival.rac.modules.cont;

import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.Permissions;
import gg.revival.rac.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Reach extends Check implements Listener {

    public Reach(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!isEnabled()) return;

        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();

        if(!(damager instanceof Player)) return;

        Player playerDamager = (Player)damager;

        if(playerDamager.hasPermission(Permissions.CHECK_BYPASS)) return;

        if(playerDamager.isFlying()) return;

        if(!playerDamager.getGameMode().equals(GameMode.SURVIVAL)) return;

        double maxReach = 3.5;

        if(playerDamager.isSprinting())
            maxReach = 4.0;

        maxReach += playerDamager.getVelocity().length() * 4.5;

        if(playerDamager.getVelocity().length() > 0.08)
            maxReach += playerDamager.getVelocity().length();

        double distance = playerDamager.getEyeLocation().distance(damaged.getLocation());
        int ping = PlayerUtils.getPing(playerDamager);

        if(ping >= 100 && ping < 200)
            maxReach += 0.2;
        else if(ping >= 200 && ping < 250)
            maxReach += 0.4;
        else if(ping >= 250 && ping < 300)
            maxReach += 0.6;
        else if(ping >= 300 && ping < 350)
            maxReach += 0.8;
        else if(ping > 350 && ping < 400)
            maxReach += 1.0;

        double yDifference = Math.abs(playerDamager.getLocation().getY() - damaged.getLocation().getY());
        maxReach += (yDifference / 4.0);

        if(distance > maxReach)
            addViolation(playerDamager.getUniqueId(), new Violation(playerDamager.getName() + " attacked an entity at a distance of " + Math.round(distance) + ", Ping: " + ping + "ms"), false);
    }
}
