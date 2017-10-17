package gg.revival.rac.modules.cont;

import com.google.common.collect.Maps;
import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.packets.events.PacketUseEntityEvent;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.Permissions;
import gg.revival.rac.utils.PlayerUtils;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class KillAuraB extends Check implements Listener {

    @Getter private Map<UUID, Map.Entry<UUID, Long>> lastAttacks = Maps.newConcurrentMap();

    public KillAuraB(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(lastAttacks.containsKey(player.getUniqueId()))
            lastAttacks.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPacketUseEntity(PacketUseEntityEvent event) {
        Player player = event.getAttacker();
        Entity entity = event.getAttacked();
        UUID lastAttackedId = null;
        long lastAttackTime = 0L;

        // Player has bypass
        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        // Player is in a non-survival gamemode
        if(!player.getGameMode().equals(GameMode.SURVIVAL)) return;

        // Player is flying
        if(player.isFlying()) return;

        // Player is in a vehicle, leave the big guy alone
        if(player.getVehicle() != null) return;

        if(lastAttacks.containsKey(player.getUniqueId())) {
            lastAttackedId = lastAttacks.get(player.getUniqueId()).getKey();
            lastAttackTime = lastAttacks.get(player.getUniqueId()).getValue();
        }

        long difference = System.currentTimeMillis() - lastAttackTime;

        if(difference < getRac().getCfg().getAuraBInterval()) {
            if(lastAttackedId != null && lastAttackedId.equals(entity.getUniqueId())) {
                addViolation(player.getUniqueId(),
                        new Violation(player.getName() + " attempted to attack multiple entities quicker than possible (" + difference + "ms), Ping: " + PlayerUtils.getPing(player)),
                        false);

                verbose(Arrays.asList(player.getName() + " attempted to attack a " + entity.getType().name() + " quicker than possible",
                        "Time: " + difference + "ms, Ping: " + PlayerUtils.getPing(player)));
            }
        }

        lastAttacks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<>(entity.getUniqueId(), System.currentTimeMillis()));
    }

}
