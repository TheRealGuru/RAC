package gg.revival.rac.patches.payload;

import gg.revival.rac.RAC;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PayloadPatchListener implements Listener {

    @Getter private RAC rac;

    public PayloadPatchListener(RAC rac) {
        this.rac = rac;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(rac.getPatchManager().getCustomPayloadPatch().getPacketUsage().containsKey(player.getUniqueId()))
            rac.getPatchManager().getCustomPayloadPatch().getPacketUsage().remove(player.getUniqueId());
    }
}
