package gg.revival.rac.modules.cont;

import gg.revival.rac.RAC;
import gg.revival.rac.modules.Cheat;
import gg.revival.rac.modules.Check;
import gg.revival.rac.modules.Violation;
import gg.revival.rac.punishments.ActionType;
import gg.revival.rac.utils.BlockUtils;
import gg.revival.rac.utils.Permissions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;

public class BedLeave extends Check implements Listener {

    public BedLeave(RAC rac, String name, Cheat cheat, ActionType action, int vlNotify, int vlAction, int vlExpire, boolean enabled) {
        super(rac, name, cheat, action, vlNotify, vlAction, vlExpire, enabled);
    }

    @EventHandler
    public void onPlayerLeaveBed(PlayerBedLeaveEvent event) {
        if(!isEnabled()) return;

        Player player = event.getPlayer();
        Location location = player.getLocation();

        if(player.hasPermission(Permissions.CHECK_BYPASS)) return;

        for(Block block : BlockUtils.getNearbyBlocks(location, 8)) {
            if(block.getType().equals(Material.BED) || block.getType().equals(Material.BED_BLOCK)) return;
        }

        addViolation(player.getUniqueId(), new Violation(player.getName() + " teleported after leaving a bed"), true);
    }
}
