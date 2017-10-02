package gg.revival.rac.utils;

import com.google.common.collect.Queues;
import gg.revival.rac.RAC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Queue;

public class Notification {

    @Getter private RAC rac;

    @Getter public final String prefix = ChatColor.DARK_RED + "<RAC>" + ChatColor.RESET + " ";
    @Getter public Queue<String> messages = Queues.newConcurrentLinkedQueue();

    public Notification(RAC rac) {
        this.rac = rac;
    }

    public void createMessage(String message) {
        messages.add(prefix + message);
    }

    /**
     * Sends the newest message in the queue to all players with the proper permission
     */
    public void sendMessage() {
        String message = messages.poll();

        for(Player players : Bukkit.getOnlinePlayers()) {
            if(!players.hasPermission(Permissions.NOTIFICATIONS_VIEW)) continue;
            players.sendMessage(message);
        }
    }

}
