package gg.revival.rac.modules.cont;

import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.Permissions;
import gg.revival.rac.utils.PlayerUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

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

        double maxReach = 4.5;

        maxReach += playerDamager.getVelocity().length() * 4.5;

        if(playerDamager.getVelocity().length() > 0.08)
            maxReach += playerDamager.getVelocity().length();

        // If a player has speed > 2, (classes usually) the player can flag reach by 1.7 chasing a player
        if(playerDamager.hasPotionEffect(PotionEffectType.SPEED)) {
            for(PotionEffect effects : playerDamager.getActivePotionEffects()) {
                if(!effects.getType().equals(PotionEffectType.SPEED) || effects.getAmplifier() <= 2) continue;

                for(int i = 2; i < effects.getAmplifier(); i++)
                    maxReach += 1.0;
            }
        }

        double distance = playerDamager.getEyeLocation().distance(damaged.getLocation());
        int ping = PlayerUtils.getPing(playerDamager);

        if(ping >= 100 && ping < 200)
            maxReach += 0.3;
        else if(ping >= 200 && ping < 250)
            maxReach += 0.5;
        else if(ping >= 250 && ping < 300)
            maxReach += 0.7;
        else if(ping >= 300 && ping < 350)
            maxReach += 0.9;
        else if(ping > 350 && ping < 400)
            maxReach += 1.1;
        else if(ping >= 400 && ping < 450)
            maxReach += 1.3;
        else if(ping >= 450)
            return;

        double yDifference = Math.abs(playerDamager.getLocation().getY() - damaged.getLocation().getY());
        maxReach += (yDifference / 4.0);

        if(distance > maxReach) {
            addViolation(playerDamager.getUniqueId(), new Violation(playerDamager.getName() + " attacked an entity at a distance of " + distance + ", Ping: " + ping + "ms"), false);

            verbose(Arrays.asList(playerDamager.getName() + " attacked an entity at a distance of " + distance,
                    "Max reach: " + maxReach + ", Ping: " + ping + "ms"));
        }
    }
}
