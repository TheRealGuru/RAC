package gg.revival.rac.listeners;

import gg.revival.rac.RAC;
import gg.revival.rac.players.ACPlayer;
import gg.revival.rac.utils.Permissions;
import gg.revival.rac.utils.PlayerUtils;
import lombok.Getter;
import me.gong.mcleaks.MCLeaksAPI;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;

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
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.RED + "Your account has been compromised" + "\n" +
                    ChatColor.WHITE + "You will no longer be allowed to log in with this account");
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
    public void onAttack(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) return;

        Player player = (Player)event.getEntity();
        ACPlayer acPlayer = rac.getPlayerManager().getPlayerByUUID(player.getUniqueId());

        acPlayer.setRecentAttack(System.currentTimeMillis());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;

        Inventory otherInventory = event.getInventory(), clickedInventory = event.getClickedInventory();

        if(otherInventory != null && otherInventory.getName() != null && (otherInventory.getName().startsWith(ChatColor.BOLD + "Player: ") || otherInventory.getName().startsWith(ChatColor.BOLD + "Check: ")))
            event.setCancelled(true);

        if(clickedInventory != null && clickedInventory.getName() != null && (clickedInventory.getName().startsWith(ChatColor.BOLD + "Player: ") || clickedInventory.getName().startsWith(ChatColor.BOLD + "Check: ")))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom(), to = event.getTo();

        // Player only moved camera
        if(from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) return;

        // Player has bypass
        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        // Player is flying or not in survival
        if(player.isFlying() || !player.getGameMode().equals(GameMode.SURVIVAL)) return;

        // Player is in vehicle
        if(player.getVehicle() != null) return;

        if(PlayerUtils.isStandingOnBlock(player, Material.SLIME_BLOCK)) {
            ACPlayer acPlayer = rac.getPlayerManager().getPlayerByUUID(player.getUniqueId());
            acPlayer.setRecentBounce(System.currentTimeMillis());
        }
    }
}
