package gg.revival.rac.packets.events;

import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketSwingArmEvent extends Event {

    @Getter public Player player;
    @Getter public PacketEvent event;
    private static HandlerList handlers = new HandlerList();

    public PacketSwingArmEvent(PacketEvent event, Player player) {
        this.player = player;
        this.event = event;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
