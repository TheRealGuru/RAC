package gg.revival.rac.modules.cont;

import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.players.ACPlayer;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.Permissions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class KillAuraB extends Check implements Listener {

    public KillAuraB(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if(!isEnabled()) return;

        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();

        if(event.isCancelled()) return;

        if(!(damager instanceof Player) || !(damaged instanceof LivingEntity)) return;

        Player player = (Player)damager;

        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        ACPlayer acPlayer = getRac().getPlayerManager().getPlayerByUUID(player.getUniqueId());

        if((System.currentTimeMillis() - acPlayer.getRecentAttack()) <= 10L)
            addViolation(damager.getUniqueId(), new Violation("[B] " + damager.getName() + " attacked multiple entities very quickly (" + (System.currentTimeMillis() - acPlayer.getRecentAttack()) + "ms)"), false);

        acPlayer.setRecentAttack(System.currentTimeMillis());
    }
}
