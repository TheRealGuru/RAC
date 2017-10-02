package gg.revival.rac.modules.cont;

import com.google.common.collect.Maps;
import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.Permissions;
import lombok.Getter;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;

public class Regen extends Check implements Listener {

    @Getter private Map<UUID, Map.Entry<Integer, Long>> healTicks = Maps.newConcurrentMap();
    @Getter private Map<UUID, Long> recentHeal = Maps.newConcurrentMap();

    public Regen(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(healTicks.containsKey(player.getUniqueId()))
            healTicks.remove(player.getUniqueId());

        if(recentHeal.containsKey(player.getUniqueId()))
            recentHeal.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerRegen(EntityRegainHealthEvent event) {
        if(!isEnabled()) return;

        if(!(event.getEntity() instanceof Player)) return;
        if(!event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) return;

        Player player = (Player)event.getEntity();

        // Player has bypass
        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        // Player is not in survival
        if(!player.getGameMode().equals(GameMode.SURVIVAL)) return;

        // Health is managed quicker in this difficulty
        if(player.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)) return;

        int flags = 0;
        long time = System.currentTimeMillis();

        if(healTicks.containsKey(player.getUniqueId())) {
            flags = healTicks.get(player.getUniqueId()).getKey();
            time = healTicks.get(player.getUniqueId()).getValue();
        }

        if(recentHeal.containsKey(player.getUniqueId())) {
            long lastHeal = recentHeal.get(player.getUniqueId());

            if((System.currentTimeMillis() - lastHeal) < 3000L) {
                addViolation(player.getUniqueId(), new Violation(player.getName() + " healed faster than possible (" + (System.currentTimeMillis() - lastHeal) + "ms)"), true);
                event.setCancelled(true);
            }

            recentHeal.remove(player.getUniqueId());
        }

        if(healTicks.containsKey(player.getUniqueId()) && (System.currentTimeMillis() - time) > 60000L) {
            flags = 0;
            time = System.currentTimeMillis();
        }

        recentHeal.put(player.getUniqueId(), System.currentTimeMillis());
        healTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<>(flags, time));
    }
}
