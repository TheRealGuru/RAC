package gg.revival.rac.patches.payload;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.StreamSerializer;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import gg.revival.rac.RAC;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class CustomPayloadPatch {

    @Getter private RAC rac;
    @Getter private Map<UUID, Long> packetUsage = Maps.newConcurrentMap();

    public CustomPayloadPatch(RAC rac) {
        this.rac = rac;
    }

    /**
     * Performs observation on packet event
     * @param event
     */
    public void observe(PacketEvent event) {
        Player player = event.getPlayer();
        long lastPacket = -1L;

        if(packetUsage.containsKey(player.getUniqueId()))
            lastPacket = packetUsage.get(player.getUniqueId());

        if(lastPacket == -2L) {
            event.setCancelled(true);
            return;
        }

        String name = event.getPacket().getStrings().readSafely(0);

        if(!name.equals("MC|BSign") && !name.equals("MC|BEdit") && !name.equals("REGISTER")) return;

        try {
            if(name.equals("REGISTER")) {
                checkChannels(event);
            } else {
                if(System.currentTimeMillis() <= (lastPacket + 100L))
                    packetUsage.put(player.getUniqueId(), System.currentTimeMillis());
                else
                    throw new IOException("Packet flood");

                checkNbtTags(event);
            }
        } catch (Throwable ex) {
            packetUsage.put(player.getUniqueId(), -2L);

            Bukkit.broadcastMessage(rac.getNotifications().getPrefix() + ChatColor.RED + player.getName() + " has been automatically removed from the network for exploiting");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + player.getName() + " [RAC] Exploiting");
            rac.getLog().log(Level.WARNING, player.getName() + " tried spamming CustomPayload packets: " + ex.getMessage());

            event.setCancelled(true);
        }
    }

    /**
     * Checks to see if the given packet event is performing malicious NBT changes to an item
     * @param event
     * @throws IOException
     */
    @SuppressWarnings("deprecation")
    private void checkNbtTags(PacketEvent event) throws IOException {
        PacketContainer container = event.getPacket();
        ByteBuf buffer = container.getSpecificModifier(ByteBuf.class).read(0).copy();

        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);

        try (DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(bytes))) {
            ItemStack itemStack = StreamSerializer.getDefault().deserializeItemStack(inputStream);

            if (itemStack == null)
                throw new IOException("Unable to deserialize ItemStack");

            NbtCompound root = (NbtCompound) NbtFactory.fromItemTag(itemStack);

            if (root == null) {
                throw new IOException("No NBT tag?!");
            } else if (!root.containsKey("pages")) {
                throw new IOException("No 'pages' NBT compound was found");
            } else {
                NbtList<String> pages = root.getList("pages");
                if (pages.size() > 50)
                    throw new IOException("Too many pages");
            }
        } finally {
            buffer.release();
        }
    }

    /**
     * Checks to see if the given packet event is performing malicious activities with Packet channels
     * @param event
     * @throws Exception
     */
    private void checkChannels(PacketEvent event) throws Exception {
        int channelsSize = event.getPlayer().getListeningPluginChannels().size();

        PacketContainer container = event.getPacket();
        ByteBuf buffer = container.getSpecificModifier(ByteBuf.class).read(0).copy();

        try {
            for (int i = 0; i < buffer.toString(Charsets.UTF_8).split("\0").length; i++)
                if (++channelsSize > 124)
                    throw new IOException("Too much channels");
        } finally {
            buffer.release();
        }
    }

}
