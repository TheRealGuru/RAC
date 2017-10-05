package gg.revival.rac.modules;

import com.google.common.collect.*;
import gg.revival.rac.RAC;
import gg.revival.rac.modules.cont.*;
import gg.revival.rac.punishments.ActionType;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CheckManager {

    @Getter private RAC rac;
    @Getter public Set<Check> checks;

    // TODO: No Knockback, Ascension, Glide

    public CheckManager(RAC rac) {
        this.rac = rac;
        this.checks = Sets.newConcurrentHashSet();

        AutoclickerA autoclickerA = new AutoclickerA(rac, "Autoclicker [A]", Cheat.AUTOCLICKER, rac.getCfg().getAutoclickerActionType(), rac.getCfg().getAutoclickerNotifyVl(),
                rac.getCfg().getAutoclickerActionVl(), rac.getCfg().getAutoclickerExpireDelay(), rac.getCfg().isAutoclickerEnabled());
        Bukkit.getPluginManager().registerEvents(autoclickerA, rac);
        checks.add(autoclickerA);

        Phase phase = new Phase(rac, "Phase", Cheat.PHASE, rac.getCfg().getPhaseActionType(), rac.getCfg().getPhaseNotifyVl(), rac.getCfg().getPhaseActionVl(),
                rac.getCfg().getPhaseExpireDelay(), rac.getCfg().isPhaseEnabled());
        Bukkit.getPluginManager().registerEvents(phase, rac);
        checks.add(phase);

        VClip vclip = new VClip(rac, "V-Clip", Cheat.VCLIP, rac.getCfg().getVclipActionType(), rac.getCfg().getVclipNotifyVl(), rac.getCfg().getVclipActionVl(),
                rac.getCfg().getVclipExpireDelay(), rac.getCfg().isVclipEnabled());
        Bukkit.getPluginManager().registerEvents(vclip, rac);
        checks.add(vclip);

        Reach reach = new Reach(rac, "Reach", Cheat.REACH, ActionType.BAN, 2, 5, 10, true);
        Bukkit.getPluginManager().registerEvents(reach, rac);
        checks.add(reach);

        Flight flight = new Flight(rac, "Flight", Cheat.FLIGHT, ActionType.BAN, 2, 4, 30, true);
        Bukkit.getPluginManager().registerEvents(flight, rac);
        checks.add(flight);

        Jesus jesus = new Jesus(rac, "Jesus", Cheat.JESUS, ActionType.BAN, 2, 4, 10, true);
        Bukkit.getPluginManager().registerEvents(jesus, rac);
        checks.add(jesus);

        Step step = new Step(rac, "Step", Cheat.STEP, ActionType.KICK, 5, 15, 15, true);
        Bukkit.getPluginManager().registerEvents(step, rac);
        checks.add(step);

        BedLeave bedLeave = new BedLeave(rac, "BedLeave", Cheat.BEDLEAVE, ActionType.BAN, 2, 4, 30, true);
        Bukkit.getPluginManager().registerEvents(bedLeave, rac);
        checks.add(bedLeave);

        FastBlocks fastBlocks = new FastBlocks(rac, "FastBlocks", Cheat.FASTBLOCK, ActionType.BAN, 3, 6, 15, true);
        Bukkit.getPluginManager().registerEvents(fastBlocks, rac);
        checks.add(fastBlocks);

        FastBow fastBow = new FastBow(rac, "FastBow", Cheat.FASTBOW, ActionType.KICK, 3, 10, 10, true);
        Bukkit.getPluginManager().registerEvents(fastBow, rac);
        checks.add(fastBow);

        NoSwing noSwing = new NoSwing(rac, "NoSwing", Cheat.NOSWING, ActionType.BAN, 5, 10, 15, true);
        Bukkit.getPluginManager().registerEvents(noSwing, rac);
        checks.add(noSwing);

        Regen regen = new Regen(rac, "Regen", Cheat.REGEN, ActionType.BAN, 1, 2, 30, true);
        Bukkit.getPluginManager().registerEvents(regen, rac);
        checks.add(regen);

        SpeedA speedA = new SpeedA(rac, "Speed", Cheat.SPEED, ActionType.BAN, 2, 4, 10, true);
        Bukkit.getPluginManager().registerEvents(speedA, rac);
        checks.add(speedA);

        KillAuraA auraA = new KillAuraA(rac, "Aura [A]", Cheat.AURA, rac.getCfg().getAuraAActionType(), rac.getCfg().getAuraANotifyVl(), rac.getCfg().getAuraAActionVl(), rac.getCfg().getAuraAExpireDelay(), rac.getCfg().isAuraAEnabled());
        Bukkit.getPluginManager().registerEvents(auraA, rac);
        checks.add(auraA);

        KillAuraB auraB = new KillAuraB(rac, "Aura [B]", Cheat.AURA, ActionType.BAN, 2, 4, 10, true);
        Bukkit.getPluginManager().registerEvents(auraB, rac);
        checks.add(auraB);
    }

    /**
     * Returns a list containing a players violations for a specific cheat
     * @param cheat
     * @param uuid
     * @return
     */
    public ImmutableList<Violation> getViolationsByCheat(Cheat cheat, UUID uuid) {
        List<Violation> result = Lists.newArrayList();

        for(Check check : checks) {
            if(!check.getCheat().equals(cheat)) continue;
            if(!check.getViolations().containsKey(uuid) || check.getViolations().get(uuid).isEmpty()) continue;
            result.addAll(check.getViolations().get(uuid));
        }

        return ImmutableList.copyOf(result);
    }

    /**
     * Returns a map containing a players violations for each check
     * @param uuid
     * @return
     */
    public ImmutableMap<Check, List<Violation>> getViolations(UUID uuid) {
        Map<Check, List<Violation>> result = Maps.newHashMap();

        for(Check check : checks) {
            if(!check.getViolations().containsKey(uuid) || check.getViolations().get(uuid).isEmpty()) continue;
            result.put(check, check.getViolations().get(uuid));
        }

        return ImmutableMap.copyOf(result);
    }

}
