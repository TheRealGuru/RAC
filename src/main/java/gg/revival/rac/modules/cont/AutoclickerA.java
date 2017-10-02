package gg.revival.rac.modules.cont;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.packets.events.PacketUseEntityEvent;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.Permissions;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AutoclickerA extends Check implements Listener {

    @Getter private Map<UUID, List<Long>> clicks = Maps.newConcurrentMap();

    public AutoclickerA(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onPacketUseEntity(PacketUseEntityEvent event) {
        if(!isEnabled()) return;

        if(!event.getAction().equals(EnumWrappers.EntityUseAction.ATTACK)) return;
        if(!(event.getAttacked() instanceof LivingEntity)) return;

        Player attacker = event.getAttacker();

        // Player has bypass
        if(attacker.hasPermission(Permissions.CHECK_BYPASS)) return;

        long time = System.currentTimeMillis();
        int cps;

        if(clicks.containsKey(attacker.getUniqueId())) {
            clicks.get(attacker.getUniqueId()).add(time);
        } else {
            List<Long> newClicksList = Lists.newArrayList();
            newClicksList.add(time);

            clicks.put(attacker.getUniqueId(), newClicksList);
        }

        List<Long> toRemove = Lists.newArrayList();

        for(long recentClicks : clicks.get(attacker.getUniqueId())) {
            if((System.currentTimeMillis() - recentClicks) >= 1000L)
                toRemove.add(recentClicks);
        }

        for(long removed : toRemove)
            clicks.get(attacker.getUniqueId()).remove(removed);

        cps = clicks.get(attacker.getUniqueId()).size();

        if(cps >= getRac().getCfg().autoclickerCpsThreshold)
            addViolation(attacker.getUniqueId(), new Violation("[A] " + attacker.getName() + " is clicking at " + cps + "CPS"), false);
    }

}
