package gg.revival.rac;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import gg.revival.rac.commands.CommandManager;
import gg.revival.rac.commands.RACCommandExecutor;
import gg.revival.rac.listeners.GeneralEventsListener;
import gg.revival.rac.modules.CheckManager;
import gg.revival.rac.packets.PacketManager;
import gg.revival.rac.patches.PatchManager;
import gg.revival.rac.players.PlayerManager;
import gg.revival.rac.tasks.NotificationQueueTask;
import gg.revival.rac.utils.Config;
import gg.revival.rac.utils.Log;
import gg.revival.rac.utils.Notification;
import lombok.Getter;
import me.gong.mcleaks.MCLeaksAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class RAC extends JavaPlugin {

    @Getter public RAC rac;
    @Getter public CheckManager checkManager;
    @Getter public PlayerManager playerManager;
    @Getter public PacketManager packetManager;
    @Getter public CommandManager commandManager;
    @Getter public PatchManager patchManager;
    @Getter public Notification notifications;
    @Getter public Config cfg;
    @Getter public Log log;
    @Getter public ProtocolManager protocolManager;
    @Getter public MCLeaksAPI mcleaksApi;

    @Override
    public void onEnable() {
        this.rac = this;
        this.log = new Log(this);
        this.cfg = new Config(this);
        this.protocolManager = ProtocolLibrary.getProtocolManager();

        cfg.createFiles();
        cfg.loadFiles();

        this.mcleaksApi = MCLeaksAPI.builder().threadCount(2).expireAfter(10, TimeUnit.MINUTES).build();

        this.checkManager = new CheckManager(this);
        this.playerManager = new PlayerManager(this);
        this.packetManager = new PacketManager(this);
        this.commandManager = new CommandManager(this);
        this.patchManager = new PatchManager(this);
        this.notifications = new Notification(this);

        loadListeners();
        loadCommands();
        loadTasks();
    }

    @Override
    public void onDisable() {
        mcleaksApi.shutdown();

        this.rac = null;
        this.log = null;
        this.cfg = null;
        this.protocolManager = null;
        this.mcleaksApi = null;
        this.checkManager = null;
        this.playerManager = null;
        this.packetManager = null;
        this.commandManager = null;
        this.patchManager = null;
        this.notifications = null;

        Bukkit.getScheduler().cancelAllTasks();
    }

    private void loadTasks() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new NotificationQueueTask(rac), 0L, cfg.notificationQueueInterval);
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new GeneralEventsListener(this), this);
    }

    private void loadCommands() {
        getCommand("rac").setExecutor(new RACCommandExecutor(this));
    }

}
