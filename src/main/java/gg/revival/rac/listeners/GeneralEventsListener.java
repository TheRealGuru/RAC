package gg.revival.rac.listeners;

import gg.revival.rac.RAC;
import gg.revival.rac.players.ACPlayer;
import lombok.Getter;
import me.gong.mcleaks.MCLeaksAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;
import java.util.UUID;

public class GeneralEventsListener implements Listener {

    @Getter private RAC rac;

    public GeneralEventsListener(RAC rac) {
        this.rac = rac;
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        if(!rac.getCfg().pingLeaksApi) return;

        final MCLeaksAPI.Result result = rac.getMcleaksApi().checkAccount(uuid);

        if(result.isMCLeaks())
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.RED + "Your account has been compromised" + "\n" + ChatColor.WHITE + "You will no longer be allowed to log in with this account");
        else
            rac.getLog().log("'" + uuid.toString() + "' account is not listed on MCLeaks website");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ACPlayer acPlayer = new ACPlayer(player.getUniqueId(), player.getName());
        rac.getPlayerManager().getPlayers().add(acPlayer);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ACPlayer acPlayer = rac.getPlayerManager().getPlayerByUUID(player.getUniqueId());
        rac.getPlayerManager().getPlayers().remove(acPlayer);
    }
}
