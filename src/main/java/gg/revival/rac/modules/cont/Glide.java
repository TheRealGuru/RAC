package gg.revival.rac.modules.cont;

import com.google.common.collect.Maps;
import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.LocationUtils;
import gg.revival.rac.utils.Permissions;
import gg.revival.rac.utils.PlayerUtils;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class Glide extends Check implements Listener {

    @Getter private Map<UUID, Long> glideTicks = Maps.newConcurrentMap();

    public Glide(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(glideTicks.containsKey(player.getUniqueId()))
            glideTicks.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!isEnabled()) return;

        Player player = event.getPlayer();
        Location from = event.getFrom(), to = event.getTo();

        // Player just moved camera
        if(from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) return;

        // Player is moving up, not down
        if(from.getY() < to.getY()) return;

        // Player has bypass
        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        // Player is flying or not in survival
        if(player.isFlying() || !player.getGameMode().equals(GameMode.SURVIVAL)) return;

        // Player is in a vehicle
        if(player.getVehicle() != null) return;

        // Player is in a cobweb
        if(PlayerUtils.isInBlock(player, Material.WEB)) return;

        // Player is sliding down from a cobweb
        for(int i = 0; i < 3; i++)
            if(player.getLocation().clone().add(0, i, 0).getBlock().getType().equals(Material.WEB)) return;

        // Player is near blocks
        if(LocationUtils.isNearBlocks(player.getLocation())) {
            if(glideTicks.containsKey(player.getUniqueId()))
                glideTicks.remove(player.getUniqueId());

            return;
        }

        double offsetY = from.getY() - to.getY();

        if(offsetY <= 0.0 || offsetY > 0.16) {
            if(glideTicks.containsKey(player.getUniqueId()))
                glideTicks.remove(player.getUniqueId());

            return;
        }

        long time = System.currentTimeMillis();

        if(glideTicks.containsKey(player.getUniqueId()))
            time = glideTicks.get(player.getUniqueId());

        long difference = System.currentTimeMillis() - time;

        if(difference > 1000L) {
            glideTicks.remove(player.getUniqueId());

            addViolation(player.getUniqueId(), new Violation(player.getName() + " is falling slower than expected Ping: " + PlayerUtils.getPing(player)), false);

            verbose(Arrays.asList(player.getName() + " is falling slower than expected",
                    "Difference: " + difference + "ms, Offset-Y: " + offsetY + ", Ping: " + PlayerUtils.getPing(player) + "ms"));

            event.setCancelled(true);
            player.teleport(from);

            return;
        }

        glideTicks.put(player.getUniqueId(), time);
    }

}
