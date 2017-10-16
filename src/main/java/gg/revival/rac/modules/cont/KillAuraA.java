package gg.revival.rac.modules.cont;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.collect.Maps;
import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.packets.events.PacketUseEntityEvent;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.MathUtils;
import gg.revival.rac.utils.Permissions;
import gg.revival.rac.utils.PlayerUtils;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class KillAuraA extends Check implements Listener {

    @Getter private Map<UUID, Map.Entry<Integer, Long>> auraTicks = Maps.newConcurrentMap();

    public KillAuraA(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(auraTicks.containsKey(player.getUniqueId()))
            auraTicks.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPacketUseEntity(PacketUseEntityEvent event) {
        if(!isEnabled()) return;

        if(!event.getAction().equals(EnumWrappers.EntityUseAction.ATTACK)) return;
        if(!(event.getAttacked() instanceof LivingEntity)) return;

        Player attacker = event.getAttacker();
        LivingEntity attacked = (LivingEntity)event.getAttacked();

        // Player has bypass
        if(attacker.hasPermission(Permissions.CHECK_BYPASS)) return;

        int flags = 0;
        long time = System.currentTimeMillis();

        if(auraTicks.containsKey(attacker.getUniqueId())) {
            flags = auraTicks.get(attacker.getUniqueId()).getKey();
            time = auraTicks.get(attacker.getUniqueId()).getValue();
        }

        double offset = Math.abs(MathUtils.getAimbotOffset(attacker.getLocation(), attacker.getEyeHeight(), attacked));
        double offsetLimit = 600.0;

        if(attacker.getVelocity().length() > 0.08)
            offsetLimit += 200.0;

        int ping = PlayerUtils.getPing(attacker);

        if(ping >= 100 && ping < 200)
            offsetLimit += 200.0;
        else if(ping >= 200 && ping < 250)
            offsetLimit += 225.0;
        else if(ping >= 250 && ping < 300)
            offsetLimit += 250.0;
        else if(ping >= 300 && ping < 350)
            offsetLimit += 275.0;
        else if(ping >= 350 && ping < 400)
            offsetLimit += 300.0;
        else if(ping >= 400 && ping < 450)
            offsetLimit += 325.0;
        else if(ping > 450)
            return;

        if(offset > (offsetLimit * 4.0))
            flags += 10;
        else if(offset > (offsetLimit * 3.0))
            flags += 8;
        else if(offset > (offsetLimit * 2.0))
            flags += 6;
        else if(offset > offsetLimit)
            flags += 4;

        if(auraTicks.containsKey(attacker.getUniqueId()) && (System.currentTimeMillis() - time) > 60000L) {
            flags = 0;
            time = System.currentTimeMillis();
        }

        if(flags >= getRac().getCfg().getAuraARequiredFlags()) {
            addViolation(attacker.getUniqueId(),
                    new Violation("[A] " + attacker.getName() + " attempted to attack an entity outside their field of view (" + Math.abs(offset - offsetLimit) + " over the limit)" + ", Ping: " + ping + "ms"),
                    false);

            verbose(Arrays.asList(attacker.getName() + " attempted to attack an entity outside their field of view",
                    Math.abs(offset - offsetLimit) + "pxs over the limit, Flags: " + flags + ", Ping: " + ping + "ms"));
        }

        auraTicks.put(attacker.getUniqueId(), new AbstractMap.SimpleEntry<>(flags, time));
    }
}
