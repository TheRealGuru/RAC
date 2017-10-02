package gg.revival.rac.modules.cont;

import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.players.ACPlayer;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.Permissions;
import gg.revival.rac.utils.PlayerUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

public class Step extends Check implements Listener {

    public Step(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!isEnabled()) return;

        Player player = event.getPlayer();
        Location from = event.getFrom(), to = event.getTo();

        // Player bypasses
        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        // Player moved camera only
        if(from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) return;

        // Player is going down, can't step up
        if(to.getY() < from.getY()) return;

        // Player is flying
        if(player.isFlying()) return;

        // Player is not in survival
        if(!player.getGameMode().equals(GameMode.SURVIVAL)) return;

        // Player has jump boost, best just leave the smart guy alone
        if(player.hasPotionEffect(PotionEffectType.JUMP)) return;

        ACPlayer acPlayer = getRac().getPlayerManager().getPlayerByUUID(player.getUniqueId());

        // Player has taken velocity recently
        if((System.currentTimeMillis() - acPlayer.getRecentVelocity()) <= 200L) return;

        // Player is not on the ground
        if(!PlayerUtils.isOnGround(player)) return;

        double yDifference = to.getY() - from.getY();

        // Player moved up higher than possible in vanilla
        if(yDifference > 0.9) {
            event.setCancelled(true);
            player.teleport(from);
            addViolation(player.getUniqueId(), new Violation(player.getName() + " stepped up " + Math.round(yDifference) + " blocks"), false);
        }
    }
}
