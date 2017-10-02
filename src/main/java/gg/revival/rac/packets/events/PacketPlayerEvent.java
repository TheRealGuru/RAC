package gg.revival.rac.packets.events;

import gg.revival.rac.packets.PacketPlayerType;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketPlayerEvent extends Event {

    @Getter Player player;
    @Getter double x, y, z, yaw;
    @Getter float pitch;
    @Getter PacketPlayerType type;
    private static HandlerList handlers = new HandlerList();

    public PacketPlayerEvent(Player player, double x, double y, double z, double yaw, float pitch, PacketPlayerType packetType) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.type = packetType;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
