package gg.revival.rac.tasks;

import gg.revival.rac.RAC;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

public class NotificationQueueTask extends BukkitRunnable implements Runnable {

    @Getter private RAC rac;

    public NotificationQueueTask(RAC rac) {
        this.rac = rac;
    }

    @Override
    public void run() {
        if(rac.getNotifications().getMessages().isEmpty()) return;
        rac.getNotifications().sendMessage();
    }

}
