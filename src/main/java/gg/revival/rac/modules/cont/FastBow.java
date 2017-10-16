package gg.revival.rac.modules.cont;

import com.google.common.collect.Maps;
import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.PlayerUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class FastBow extends Check implements Listener {

    @Getter private Map<UUID, Long> recentBowPull = Maps.newConcurrentMap();

    public FastBow(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(recentBowPull.containsKey(player.getUniqueId()))
            recentBowPull.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!isEnabled()) return;

        Player player = event.getPlayer();

        if(player.getItemInHand() == null || !player.getItemInHand().getType().equals(Material.BOW)) return;

        recentBowPull.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onProjLaunch(ProjectileLaunchEvent event) {
        if(!isEnabled()) return;

        if(!(event.getEntity() instanceof Arrow)) return;

        Arrow arrow = (Arrow)event.getEntity();

        if(!(arrow.getShooter() instanceof Player)) return;

        Player player = (Player)arrow.getShooter();
        long time = System.currentTimeMillis(), limit = 300L;
        int ping = PlayerUtils.getPing(player);
        double power = arrow.getVelocity().length();

        if(ping >= 400)
            limit = 150L;

        if(power > 2.5 && time < limit) {
            addViolation(player.getUniqueId(), new Violation(player.getName() + " is firing a bow too fast, Ping: " + ping + "ms"), false);

            verbose(Arrays.asList(player.getName() + " attempted to fire a bow too fast",
                    "Power: " + power + ", Limit: " + limit + ", Time: " + time + ", Ping: " + ping + "ms"));

            event.setCancelled(true);
        }
    }
}
