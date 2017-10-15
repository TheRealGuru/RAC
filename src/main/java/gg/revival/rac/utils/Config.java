package gg.revival.rac.utils;

import gg.revival.rac.RAC;
import gg.revival.rac.punishments.ActionType;
import lombok.Getter;

public class Config {

    @Getter private RAC rac;

    @Getter public boolean pingLeaksApi;
    @Getter public int notificationQueueInterval;
    @Getter public boolean patchBoats;

    @Getter public boolean autoclickerEnabled;
    @Getter public ActionType autoclickerActionType;
    @Getter public int autoclickerNotifyVl;
    @Getter public int autoclickerActionVl;
    @Getter public int autoclickerExpireDelay;
    @Getter public int autoclickerCpsThreshold;

    @Getter public boolean flightEnabled;
    @Getter public ActionType flightActionType;
    @Getter public int flightNotifyVl;
    @Getter public int flightActionVl;
    @Getter public int flightExpireDelay;
    @Getter public long flightMaxMS;

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

    @Getter public boolean stepEnabled;
    @Getter public ActionType stepActionType;
    @Getter public int stepNotifyVl;
    @Getter public int stepActionVl;
    @Getter public int stepExpireDelay;
    @Getter public double stepMaxBlocks;

    @Getter public boolean auraAEnabled;
    @Getter public ActionType auraAActionType;
    @Getter public int auraANotifyVl;
    @Getter public int auraAActionVl;
    @Getter public int auraAExpireDelay;
    @Getter public int auraARequiredFlags;

    @Getter public boolean speedAEnabled;
    @Getter public ActionType speedAActionType;
    @Getter public int speedANotifyVl;
    @Getter public int speedAActionVl;
    @Getter public int speedAExpireDelay;

    @Getter public boolean auraBEnabled;
    @Getter public ActionType auraBActionType;
    @Getter public int auraBNotifyVl;
    @Getter public int auraBActionVl;
    @Getter public int auraBExpireDelay;
    @Getter public int auraBInterval;

    @Getter public boolean spiderEnabled;
    @Getter public ActionType spiderActionType;
    @Getter public int spiderNotifyVl;
    @Getter public int spiderActionVl;
    @Getter public int spiderExpireDelay;
    @Getter public double spiderMaxBlocks;

    @Getter public boolean blockGlitchEnabled;
    @Getter public ActionType blockGlitchActionType;
    @Getter public int blockGlitchNotifyVl;
    @Getter public int blockGlitchActionVl;
    @Getter public int blockGlitchExpireDelay;

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

        patchBoats = rac.getConfig().getBoolean("patches.boats");

        pingLeaksApi = rac.getConfig().getBoolean("mcleaks-api.ping-api");

        autoclickerEnabled = rac.getConfig().getBoolean("autoclicker.enabled");
        autoclickerActionType = ActionType.valueOf(rac.getConfig().getString("autoclicker.action"));
        autoclickerNotifyVl = rac.getConfig().getInt("autoclicker.notify-vl");
        autoclickerActionVl = rac.getConfig().getInt("autoclicker.action-vl");
        autoclickerExpireDelay = rac.getConfig().getInt("autoclicker.expire-delay");
        autoclickerCpsThreshold = rac.getConfig().getInt("autoclicker.cps-threshold");

        flightEnabled = rac.getConfig().getBoolean("flight.enabled");
        flightActionType = ActionType.valueOf(rac.getConfig().getString("flight.action"));
        flightNotifyVl = rac.getConfig().getInt("flight.notify-vl");
        flightActionVl = rac.getConfig().getInt("flight.action-vl");
        flightExpireDelay = rac.getConfig().getInt("flight.expire-delay");
        flightMaxMS = (long)rac.getConfig().getInt("flight.max-ms");

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

        stepEnabled = rac.getConfig().getBoolean("step.enabled");
        stepActionType = ActionType.valueOf(rac.getConfig().getString("step.action"));
        stepNotifyVl = rac.getConfig().getInt("step.notify-vl");
        stepActionVl = rac.getConfig().getInt("step.action-vl");
        stepExpireDelay = rac.getConfig().getInt("step.expire-delay");
        stepMaxBlocks = rac.getConfig().getDouble("step.max-blocks");

        speedAEnabled = rac.getConfig().getBoolean("speed.a.enabled");
        speedAActionType = ActionType.valueOf(rac.getConfig().getString("speed.a.action"));
        speedANotifyVl = rac.getConfig().getInt("speed.a.notify-vl");
        speedAActionVl = rac.getConfig().getInt("speed.a.action-vl");
        speedAExpireDelay = rac.getConfig().getInt("speed.a.expire-delay");

        auraAEnabled = rac.getConfig().getBoolean("killaura.a.enabled");
        auraAActionType = ActionType.valueOf(rac.getConfig().getString("killaura.a.action"));
        auraANotifyVl = rac.getConfig().getInt("killaura.a.notify-vl");
        auraAActionVl = rac.getConfig().getInt("killaura.a.action-vl");
        auraAExpireDelay = rac.getConfig().getInt("killaura.a.expire-delay");
        auraARequiredFlags = rac.getConfig().getInt("killaura.a.required-flags");

        spiderEnabled = rac.getConfig().getBoolean("spider.enabled");
        spiderActionType = ActionType.valueOf(rac.getConfig().getString("spider.action"));
        spiderNotifyVl = rac.getConfig().getInt("spider.notify-vl");
        spiderActionVl = rac.getConfig().getInt("spider.action-vl");
        spiderExpireDelay = rac.getConfig().getInt("spider.expire-delay");
        spiderMaxBlocks = rac.getConfig().getDouble("spider.max-blocks");

        blockGlitchEnabled = rac.getConfig().getBoolean("blockglitch.enabled");
        blockGlitchActionType = ActionType.valueOf(rac.getConfig().getString("blockglitch.action"));
        blockGlitchNotifyVl = rac.getConfig().getInt("blockglitch.notify-vl");
        blockGlitchActionVl = rac.getConfig().getInt("blockglitch.action-vl");
        blockGlitchExpireDelay = rac.getConfig().getInt("blockglitch.expire-delay");

        rac.getLog().log("Loaded files");
    }

    public void reloadFiles() {
        rac.reloadConfig();

        rac.getLog().log("Reloaded files");
    }

}
