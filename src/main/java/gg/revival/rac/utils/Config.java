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
    @Getter public double auraABaseOffsetLimit;

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

    @Getter public boolean badPacketsEnabled;
    @Getter public ActionType badPacketsActionType;
    @Getter public int badPacketsNotifyVl;
    @Getter public int badPacketsActionVl;
    @Getter public int badPacketsExpireDelay;
    @Getter public int badPacketsFlags;

    @Getter public boolean jesusEnabled;
    @Getter public ActionType jesusActionType;
    @Getter public int jesusNotifyVl;
    @Getter public int jesusActionVl;
    @Getter public int jesusExpireDelay;

    @Getter public boolean velocityEnabled;
    @Getter public ActionType velocityActionType;
    @Getter public int velocityNotifyVl;
    @Getter public int velocityActionVl;
    @Getter public int velocityExpireDelay;
    @Getter public int velocityFlags;

    public Config(RAC rac) {
        this.rac = rac;
    }

    public void createFiles() {
        rac.getConfig().options().copyDefaults(true);
        rac.getConfig().options().header(
                "------------------------------\n"
                        + "Revival Anticheat Configuration\n"
                        + "------------------------------\n");

        rac.saveConfig();

        rac.getLog().log("Created files");
    }

    public void loadFiles() {
        rac.getLearningManager().setLearnedSprintingReach(rac.getConfig().getDouble("learning.reach.sprint"));
        rac.getLearningManager().setLearnedWalkingReach(rac.getConfig().getDouble("learning.reach.walk"));

        notificationQueueInterval = rac.getConfig().getInt("core-settings.notification-queue-interval");

        patchBoats = rac.getConfig().getBoolean("patches.boats");

        pingLeaksApi = rac.getConfig().getBoolean("mcleaks-api.ping-api");

        autoclickerEnabled = rac.getConfig().getBoolean("checks.autoclicker.enabled");
        autoclickerActionType = ActionType.valueOf(rac.getConfig().getString("checks.autoclicker.action"));
        autoclickerNotifyVl = rac.getConfig().getInt("checks.autoclicker.notify-vl");
        autoclickerActionVl = rac.getConfig().getInt("checks.autoclicker.action-vl");
        autoclickerExpireDelay = rac.getConfig().getInt("checks.autoclicker.expire-delay");
        autoclickerCpsThreshold = rac.getConfig().getInt("checks.autoclicker.cps-threshold");

        flightEnabled = rac.getConfig().getBoolean("checks.flight.enabled");
        flightActionType = ActionType.valueOf(rac.getConfig().getString("checks.flight.action"));
        flightNotifyVl = rac.getConfig().getInt("checks.flight.notify-vl");
        flightActionVl = rac.getConfig().getInt("checks.flight.action-vl");
        flightExpireDelay = rac.getConfig().getInt("checks.flight.expire-delay");
        flightMaxMS = (long)rac.getConfig().getInt("checks.flight.max-ms");

        phaseEnabled = rac.getConfig().getBoolean("checks.phase.enabled");
        phaseActionType = ActionType.valueOf(rac.getConfig().getString("checks.phase.action"));
        phaseNotifyVl = rac.getConfig().getInt("checks.phase.notify-vl");
        phaseActionVl = rac.getConfig().getInt("checks.phase.action-vl");
        phaseExpireDelay = rac.getConfig().getInt("checks.phase.expire-delay");
        skipPhaseThreshold = rac.getConfig().getDouble("checks.phase.skip-phase-threshold");
        blockPhaseDistance = rac.getConfig().getDouble("checks.phase.block-phase-distance");

        vclipEnabled = rac.getConfig().getBoolean("checks.vclip.enabled");
        vclipActionType = ActionType.valueOf(rac.getConfig().getString("checks.vclip.action"));
        vclipNotifyVl = rac.getConfig().getInt("checks.vclip.notify-vl");
        vclipActionVl = rac.getConfig().getInt("checks.vclip.action-vl");
        vclipExpireDelay = rac.getConfig().getInt("checks.vclip.expire-delay");

        stepEnabled = rac.getConfig().getBoolean("checks.step.enabled");
        stepActionType = ActionType.valueOf(rac.getConfig().getString("checks.step.action"));
        stepNotifyVl = rac.getConfig().getInt("checks.step.notify-vl");
        stepActionVl = rac.getConfig().getInt("checks.step.action-vl");
        stepExpireDelay = rac.getConfig().getInt("checks.step.expire-delay");
        stepMaxBlocks = rac.getConfig().getDouble("checks.step.max-blocks");

        speedAEnabled = rac.getConfig().getBoolean("checks.speed.a.enabled");
        speedAActionType = ActionType.valueOf(rac.getConfig().getString("checks.speed.a.action"));
        speedANotifyVl = rac.getConfig().getInt("checks.speed.a.notify-vl");
        speedAActionVl = rac.getConfig().getInt("checks.speed.a.action-vl");
        speedAExpireDelay = rac.getConfig().getInt("checks.speed.a.expire-delay");

        auraAEnabled = rac.getConfig().getBoolean("checks.killaura.a.enabled");
        auraAActionType = ActionType.valueOf(rac.getConfig().getString("checks.killaura.a.action"));
        auraANotifyVl = rac.getConfig().getInt("checks.killaura.a.notify-vl");
        auraAActionVl = rac.getConfig().getInt("checks.killaura.a.action-vl");
        auraAExpireDelay = rac.getConfig().getInt("checks.killaura.a.expire-delay");
        auraARequiredFlags = rac.getConfig().getInt("checks.killaura.a.required-flags");
        auraABaseOffsetLimit = rac.getConfig().getDouble("checks.killaura.a.base-offset-limit");

        spiderEnabled = rac.getConfig().getBoolean("checks.spider.enabled");
        spiderActionType = ActionType.valueOf(rac.getConfig().getString("checks.spider.action"));
        spiderNotifyVl = rac.getConfig().getInt("checks.spider.notify-vl");
        spiderActionVl = rac.getConfig().getInt("checks.spider.action-vl");
        spiderExpireDelay = rac.getConfig().getInt("checks.spider.expire-delay");
        spiderMaxBlocks = rac.getConfig().getDouble("checks.spider.max-blocks");

        blockGlitchEnabled = rac.getConfig().getBoolean("checks.blockglitch.enabled");
        blockGlitchActionType = ActionType.valueOf(rac.getConfig().getString("checks.blockglitch.action"));
        blockGlitchNotifyVl = rac.getConfig().getInt("checks.blockglitch.notify-vl");
        blockGlitchActionVl = rac.getConfig().getInt("checks.blockglitch.action-vl");
        blockGlitchExpireDelay = rac.getConfig().getInt("checks.blockglitch.expire-delay");

        badPacketsEnabled = rac.getConfig().getBoolean("checks.badpackets.enabled");
        badPacketsActionType = ActionType.valueOf(rac.getConfig().getString("checks.badpackets.action"));
        badPacketsNotifyVl = rac.getConfig().getInt("checks.badpackets.notify-vl");
        badPacketsActionVl = rac.getConfig().getInt("checks.badpackets.action-vl");
        badPacketsExpireDelay = rac.getConfig().getInt("checks.badpackets.expire-delay");
        badPacketsFlags = rac.getConfig().getInt("checks.badpackets.flags");

        jesusEnabled = rac.getConfig().getBoolean("checks.jesus.enabled");
        jesusActionType = ActionType.valueOf(rac.getConfig().getString("checks.jesus.action"));
        jesusNotifyVl = rac.getConfig().getInt("checks.jesus.notify-vl");
        jesusActionVl = rac.getConfig().getInt("checks.jesus.action-vl");
        jesusExpireDelay = rac.getConfig().getInt("checks.jesus.expire-delay");

        velocityEnabled = rac.getConfig().getBoolean("checks.velocity.enabled");
        velocityActionType = ActionType.valueOf(rac.getConfig().getString("checks.velocity.action"));
        velocityNotifyVl = rac.getConfig().getInt("checks.velocity.notify-vl");
        velocityActionVl = rac.getConfig().getInt("checks.velocity.action-vl");
        velocityExpireDelay = rac.getConfig().getInt("checks.velocity.expire-delay");
        velocityFlags = rac.getConfig().getInt("checks.velocity.flags");

        rac.getLog().log("Loaded files");
    }

    public void reloadFiles() {
        rac.reloadConfig();

        rac.getLog().log("Reloaded files");
    }

}
