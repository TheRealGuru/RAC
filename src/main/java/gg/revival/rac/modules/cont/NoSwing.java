package gg.revival.rac.modules.cont;

import com.google.common.collect.Maps;
import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.packets.events.PacketSwingArmEvent;
import gg.revival.rac.punishments.ActionType;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class NoSwing extends Check implements Listener {

    @Getter private Map<UUID, Long> recentArmSwing = Maps.newConcurrentMap();

    public NoSwing(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onArmSwing(PacketSwingArmEvent event) {
        recentArmSwing.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!isEnabled()) return;

        if(!(event.getDamager() instanceof Player)) return;

        Player player = (Player)event.getDamager();

        new BukkitRunnable() {
            public void run() {
                if(!recentArmSwing.containsKey(player.getUniqueId()) || (System.currentTimeMillis() - recentArmSwing.get(player.getUniqueId()) > 1500L))
                    addViolation(player.getUniqueId(), new Violation(player.getName() + " attacked an entity without swinging their arm"), false);
            }
        }.runTaskLater(getRac(), 10L);
    }
}
