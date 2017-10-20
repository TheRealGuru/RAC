package gg.revival.rac.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import gg.revival.rac.RAC;
import gg.revival.rac.packets.events.PacketPlayerEvent;
import gg.revival.rac.packets.events.PacketSwingArmEvent;
import gg.revival.rac.packets.events.PacketUseEntityEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PacketManager {

    @Getter private RAC rac;

    public PacketManager(RAC rac) {
        this.rac = rac;

        rac.getProtocolManager().addPacketListener(new PacketAdapter(rac, ListenerPriority.HIGHEST, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();
                Entity entity = null;

                if(player == null) return;

                EnumWrappers.EntityUseAction packetType = packet.getEntityUseActions().read(0);
                int entityId = packet.getIntegers().read(0);

                for(Entity worldEntities : player.getWorld().getLivingEntities()) {
                    if(worldEntities.getEntityId() == entityId)
                        entity = worldEntities;
                }

                if(entity == null) return;

                Bukkit.getServer().getPluginManager().callEvent(new PacketUseEntityEvent(packetType, player, entity));
            }
        });

        rac.getProtocolManager().addPacketListener(new PacketAdapter(rac, ListenerPriority.HIGHEST, PacketType.Play.Client.ARM_ANIMATION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();

                if(player == null) return;

                Bukkit.getServer().getPluginManager().callEvent(new PacketSwingArmEvent(event, player));
            }
        });

        rac.getProtocolManager().addPacketListener(new PacketAdapter(rac, ListenerPriority.HIGHEST, PacketType.Play.Client.FLYING) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();

                if(player == null) return;

                Location location = player.getLocation();

                Bukkit.getServer().getPluginManager().callEvent(
                        new PacketPlayerEvent(player, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), PacketPlayerType.FLYING));
            }
        });

        rac.getProtocolManager().addPacketListener(new PacketAdapter(rac, ListenerPriority.HIGHEST, PacketType.Play.Client.POSITION_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();

                if(player == null) return;

                Location location = player.getLocation();

                Bukkit.getServer().getPluginManager().callEvent(
                        new PacketPlayerEvent(player, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), PacketPlayerType.POSLOOK));
            }
        });

        rac.getProtocolManager().addPacketListener(new PacketAdapter(rac, ListenerPriority.HIGHEST, PacketType.Play.Client.LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();

                if(player == null) return;

                Location location = player.getLocation();

                Bukkit.getServer().getPluginManager().callEvent(
                        new PacketPlayerEvent(player, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), PacketPlayerType.LOOK));
            }
        });

        rac.getProtocolManager().addPacketListener(new PacketAdapter(rac, ListenerPriority.HIGHEST, PacketType.Play.Client.POSITION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();

                if(player == null) return;

                Location location = player.getLocation();

                Bukkit.getServer().getPluginManager().callEvent(
                        new PacketPlayerEvent(player, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), PacketPlayerType.POSITION));
            }
        });

        rac.getProtocolManager().addPacketListener(new PacketAdapter(rac, ListenerPriority.HIGHEST, PacketType.Play.Client.CUSTOM_PAYLOAD) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                rac.getPatchManager().getCustomPayloadPatch().observe(event);
            }
        });
    }

}
