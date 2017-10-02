package gg.revival.rac.packets.events;

import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketUseEntityEvent extends Event {

    @Getter public EnumWrappers.EntityUseAction action;
    @Getter public Player attacker;
    @Getter public Entity attacked;
    private static HandlerList handlers = new HandlerList();

    public PacketUseEntityEvent(EnumWrappers.EntityUseAction action, Player attacker, Entity attacked) {
        this.action = action;
        this.attacker = attacker;
        this.attacked = attacked;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
