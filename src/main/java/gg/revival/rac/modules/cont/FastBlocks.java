package gg.revival.rac.modules.cont;

import com.google.common.collect.Maps;
import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.Permissions;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

public class FastBlocks extends Check implements Listener {

    @Getter private Map<UUID, Long> blockPlaces = Maps.newConcurrentMap();

    public FastBlocks(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(blockPlaces.containsKey(player.getUniqueId()))
            blockPlaces.remove(player.getUniqueId());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!isEnabled()) return;

        Player player = event.getPlayer();

        // Player has bypass
        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        // Player is not in survival
        if(!player.getGameMode().equals(GameMode.SURVIVAL)) return;

        // First block player has placed since they logged in
        if(!blockPlaces.containsKey(player.getUniqueId())) {
            blockPlaces.put(player.getUniqueId(), System.currentTimeMillis());
            return;
        }

        long time = blockPlaces.get(player.getUniqueId());
        long difference = System.currentTimeMillis() - time;

        if(difference <= 95L) {
            addViolation(player.getUniqueId(), new Violation(player.getName() + " placed blocks quicker than expected (" + difference + "ms)"), false);
            event.setCancelled(true);
        }

        blockPlaces.put(player.getUniqueId(), System.currentTimeMillis());
    }

}
