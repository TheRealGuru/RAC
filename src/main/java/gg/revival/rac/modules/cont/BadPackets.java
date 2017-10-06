package gg.revival.rac.modules.cont;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.packets.events.PacketPlayerEvent;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.MathUtils;
import gg.revival.rac.utils.Permissions;
import gg.revival.rac.utils.PlayerUtils;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BadPackets extends Check implements Listener {

    @Getter private Map<UUID, Integer> timerTicks = Maps.newConcurrentMap();
    @Getter private Map<UUID, Long> lastTimer = Maps.newConcurrentMap();
    @Getter private Map<UUID, List<Long>> sentPackets = Maps.newConcurrentMap();

    public BadPackets(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(timerTicks.containsKey(player.getUniqueId()))
            timerTicks.remove(player.getUniqueId());

        if(lastTimer.containsKey(player.getUniqueId()))
            lastTimer.remove(player.getUniqueId());

        if(sentPackets.containsKey(player.getUniqueId()))
            sentPackets.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPacketPlayer(PacketPlayerEvent event) {
        if(!isEnabled()) return;

        Player player = event.getPlayer();

        // Player has bypass
        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        int flags = 0;

        if(timerTicks.containsKey(player.getUniqueId()))
            flags = timerTicks.get(player.getUniqueId());

        if(lastTimer.containsKey(player.getUniqueId())) {
            long recentTimer = lastTimer.get(player.getUniqueId());
            long difference = System.currentTimeMillis() - recentTimer;
            List<Long> packets = Lists.newArrayList();

            if(sentPackets.containsKey(player.getUniqueId()))
                packets = sentPackets.get(player.getUniqueId());

            packets.add(difference);

            if(packets.size() == 20) {
                boolean influx = true;

                for(long packet : packets) {
                    if(packet > 1L) continue;
                    influx = false;
                    break;
                }

                long average = MathUtils.averageLong(packets);

                if(average < 48L && influx)
                    flags++;
                else
                    flags = 0;

                sentPackets.remove(player.getUniqueId());
            }

            else {
                sentPackets.put(player.getUniqueId(), packets);
            }
        }

        if(flags > 4) {
            addViolation(player.getUniqueId(), new Violation(player.getName() + " sent bad packets (" + flags + "), Ping: " + PlayerUtils.getPing(player) + "ms"), true);
            flags = 0;
        }

        lastTimer.put(player.getUniqueId(), System.currentTimeMillis());
        timerTicks.put(player.getUniqueId(), flags);
    }
}
