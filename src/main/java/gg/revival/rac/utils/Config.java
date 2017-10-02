package gg.revival.rac.utils;

import gg.revival.rac.RAC;
import gg.revival.rac.punishments.ActionType;
import lombok.Getter;

public class Config {

    @Getter private RAC rac;

    @Getter public boolean pingLeaksApi;
    @Getter public int notificationQueueInterval;

    @Getter public boolean autoclickerEnabled;
    @Getter public ActionType autoclickerActionType;
    @Getter public int autoclickerNotifyVl;
    @Getter public int autoclickerActionVl;
    @Getter public int autoclickerExpireDelay;
    @Getter public int autoclickerCpsThreshold;

    @Getter public boolean phaseEnabled;
    @Getter public ActionType phaseActionType;
    @Getter public int phaseNotifyVl;
    @Getter public int phaseActionVl;
    @Getter public int phaseExpireDelay;
    @Getter public double skipPhaseThreshold;
    @Getter public double blockPhaseDistance;

    @Getter public boolean vclipEnabled;
    @Getter public ActionType vclipActionType;
    @Getter public int vclipNotifyVl;
    @Getter public int vclipActionVl;
    @Getter public int vclipExpireDelay;

    @Getter public boolean auraAEnabled;
    @Getter public ActionType auraAActionType;
    @Getter public int auraANotifyVl;
    @Getter public int auraAActionVl;
    @Getter public int auraAExpireDelay;
    @Getter public int auraARequiredFlags;

    @Getter public boolean auraBEnabled;
    @Getter public ActionType auraBActionType;
    @Getter public int auraBNotifyVl;
    @Getter public int auraBActionVl;
    @Getter public int auraBExpireDelay;
    @Getter public int auraBInterval;

    public Config(RAC rac) {
        this.rac = rac;
    }

    public void createFiles() {
        rac.getConfig().options().copyDefaults(true);
        rac.saveConfig();

        rac.getLog().log("Created files");
    }

    public void loadFiles() {
        notificationQueueInterval = rac.getConfig().getInt("core-settings.notification-queue-interval");

        pingLeaksApi = rac.getConfig().getBoolean("mcleaks-api.ping-api");

        autoclickerEnabled = rac.getConfig().getBoolean("autoclicker.enabled");
        autoclickerActionType = ActionType.valueOf(rac.getConfig().getString("autoclicker.action"));
        autoclickerNotifyVl = rac.getConfig().getInt("autoclicker.notify-vl");
        autoclickerActionVl = rac.getConfig().getInt("autoclicker.action-vl");
        autoclickerExpireDelay = rac.getConfig().getInt("autoclicker.expire-delay");
        autoclickerCpsThreshold = rac.getConfig().getInt("autoclicker.cps-threshold");

        phaseEnabled = rac.getConfig().getBoolean("phase.enabled");
        phaseActionType = ActionType.valueOf(rac.getConfig().getString("phase.action"));
        phaseNotifyVl = rac.getConfig().getInt("phase.notify-vl");
        phaseActionVl = rac.getConfig().getInt("phase.action-vl");
        phaseExpireDelay = rac.getConfig().getInt("phase.expire-delay");
        skipPhaseThreshold = rac.getConfig().getDouble("phase.skip-phase-threshold");
        blockPhaseDistance = rac.getConfig().getDouble("phase.block-phase-distance");

        vclipEnabled = rac.getConfig().getBoolean("vclip.enabled");
        vclipActionType = ActionType.valueOf(rac.getConfig().getString("vclip.action"));
        vclipNotifyVl = rac.getConfig().getInt("vclip.notify-vl");
        vclipActionVl = rac.getConfig().getInt("vclip.action-vl");
        vclipExpireDelay = rac.getConfig().getInt("vclip.expire-delay");

        auraAEnabled = rac.getConfig().getBoolean("killaura.a.enabled");
        auraAActionType = ActionType.valueOf(rac.getConfig().getString("killaura.a.action"));
        auraANotifyVl = rac.getConfig().getInt("killaura.a.notify-vl");
        auraAActionVl = rac.getConfig().getInt("killaura.a.action-vl");
        auraAExpireDelay = rac.getConfig().getInt("killaura.a.expire-delay");
        auraARequiredFlags = rac.getConfig().getInt("killaura.a.required-flags");

        rac.getLog().log("Loaded files");
    }

    public void reloadFiles() {
        rac.reloadConfig();

        rac.getLog().log("Reloaded files");
    }

}
