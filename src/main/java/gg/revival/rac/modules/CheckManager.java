package gg.revival.rac.modules;

import com.google.common.collect.*;
import gg.revival.rac.RAC;
import gg.revival.rac.modules.cont.*;
import gg.revival.rac.punishments.ActionType;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CheckManager {

    @Getter private RAC rac;
    @Getter public Set<Check> checks;

    // TODO: No Knockback

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

        Flight flight = new Flight(rac, "Flight", Cheat.FLIGHT, rac.getCfg().getFlightActionType(), rac.getCfg().getFlightNotifyVl(),
                rac.getCfg().getFlightActionVl(), rac.getCfg().getFlightExpireDelay(), rac.getCfg().isFlightEnabled());
        Bukkit.getPluginManager().registerEvents(flight, rac);
        checks.add(flight);

        Ascension ascension = new Ascension(rac, "Ascension", Cheat.ASCENSION, ActionType.KICK, 2, 4, 30, true);
        Bukkit.getPluginManager().registerEvents(ascension, rac);
        checks.add(ascension);

        Glide glide = new Glide(rac, "Glide", Cheat.GLIDE, ActionType.KICK, 2, 4, 30, true);
        Bukkit.getPluginManager().registerEvents(glide, rac);
        checks.add(glide);

        Jesus jesus = new Jesus(rac, "Jesus", Cheat.JESUS, rac.getCfg().getJesusActionType(), rac.getCfg().getJesusNotifyVl(), rac.getCfg().getJesusActionVl(),
                rac.getCfg().getJesusExpireDelay(), rac.getCfg().isJesusEnabled());
        Bukkit.getPluginManager().registerEvents(jesus, rac);
        checks.add(jesus);

        Step step = new Step(rac, "Step", Cheat.STEP, rac.getCfg().getStepActionType(), rac.getCfg().getStepNotifyVl(),
                rac.getCfg().getStepActionVl(), rac.getCfg().getStepExpireDelay(), rac.getCfg().isStepEnabled());
        Bukkit.getPluginManager().registerEvents(step, rac);
        checks.add(step);

        Spider spider = new Spider(rac, "Spider", Cheat.SPIDER, rac.getCfg().getSpiderActionType(), rac.getCfg().getSpiderNotifyVl(),
                rac.getCfg().getSpiderActionVl(), rac.getCfg().getSpiderExpireDelay(), rac.getCfg().isSpiderEnabled());
        Bukkit.getPluginManager().registerEvents(spider, rac);
        checks.add(spider);

        NoFall noFall = new NoFall(rac, "NoFall", Cheat.NOFALL, ActionType.BAN, 2, 4, 30, true);
        Bukkit.getPluginManager().registerEvents(noFall, rac);
        checks.add(spider);

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

        SpeedA speedA = new SpeedA(rac, "Speed [A]", Cheat.SPEED_A, ActionType.BAN, rac.getCfg().getSpeedANotifyVl(), rac.getCfg().getSpeedAActionVl(),
                rac.getCfg().getSpeedAExpireDelay(), rac.getCfg().isSpeedAEnabled());
        Bukkit.getPluginManager().registerEvents(speedA, rac);
        checks.add(speedA);

        KillAuraA auraA = new KillAuraA(rac, "Aura [A]", Cheat.AURA_A, rac.getCfg().getAuraAActionType(), rac.getCfg().getAuraANotifyVl(), rac.getCfg().getAuraAActionVl(),
                rac.getCfg().getAuraAExpireDelay(), rac.getCfg().isAuraAEnabled());
        Bukkit.getPluginManager().registerEvents(auraA, rac);
        checks.add(auraA);

        KillAuraB auraB = new KillAuraB(rac, "Aura [B]", Cheat.AURA_B, rac.getCfg().getAuraBActionType(), rac.getCfg().getAuraBNotifyVl(), rac.getCfg().getAuraBActionVl(),
                rac.getCfg().getAuraBExpireDelay(), rac.getCfg().isAuraBEnabled());
        Bukkit.getPluginManager().registerEvents(auraB, rac);
        checks.add(auraB);

        BadPackets badPackets = new BadPackets(rac, "BadPackets", Cheat.BADPACKETS, rac.getCfg().getBadPacketsActionType(), rac.getCfg().getBadPacketsNotifyVl(), rac.getCfg().getBadPacketsActionVl(),
                rac.getCfg().getBadPacketsExpireDelay(), rac.getCfg().isBadPacketsEnabled());
        Bukkit.getPluginManager().registerEvents(badPackets, rac);
        checks.add(badPackets);

        BlockGlitch blockGlitch = new BlockGlitch(rac, "BlockGlitch", Cheat.BLOCKGLITCH, rac.getCfg().getBlockGlitchActionType(), rac.getCfg().getBlockGlitchNotifyVl(),
                rac.getCfg().getBlockGlitchActionVl(), rac.getCfg().getBlockGlitchExpireDelay(), rac.getCfg().isBlockGlitchEnabled());
        Bukkit.getPluginManager().registerEvents(blockGlitch, rac);
        checks.add(blockGlitch);
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

    public Check getCheckByCheat(Cheat cheat) {
        for(Check check : checks)
            if(check.getCheat().equals(cheat)) return check;

        return null;
    }

    /**
     * Opens a GUI to the given player showing all violations currently active for the supplied "lookup" player
     * @param displayTo
     * @param lookup
     */
    public void showPlayerViolations(Player displayTo, Player lookup) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.BOLD + "Player: " + ChatColor.DARK_GREEN + lookup.getName());
        ImmutableMap<Check, List<Violation>> violations = getViolations(lookup.getUniqueId());

        for(Check check : violations.keySet()) {
            ItemStack icon = new ItemStack(Material.BARRIER);
            ItemMeta meta = icon.getItemMeta();

            icon.setAmount(violations.get(check).size());

            meta.setDisplayName(ChatColor.GREEN + check.getName());

            List<String> lore = Lists.newArrayList();

            for(Violation violation : violations.get(check))
                lore.add(ChatColor.RED + violation.getInformation());

            meta.setLore(lore);
            icon.setItemMeta(meta);

            gui.addItem(icon);
        }

        displayTo.openInventory(gui);
    }

    /**
     * Opens up a GUI to the given player showing all violations currently active for the provided cheat type
     * @param displayTo
     * @param check
     */
    public void showCheckViolations(Player displayTo, Check check) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.BOLD + "Check: " + ChatColor.RED + check.getName());

        int cursor = 0;

        for(UUID uuid : check.getViolations().keySet()) {
            if(cursor >= 54) continue;
            if(Bukkit.getPlayer(uuid) == null || !Bukkit.getPlayer(uuid).isOnline()) continue;

            Player player = Bukkit.getPlayer(uuid);

            ItemStack icon = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
            SkullMeta meta = (SkullMeta)icon.getItemMeta();

            icon.setAmount(check.getViolations().get(uuid).size());

            meta.setDisplayName(ChatColor.GREEN + player.getName());
            meta.setOwner(player.getName());

            List<String> lore = Lists.newArrayList();

            for(Violation vl : check.getViolations().get(uuid))
                lore.add(ChatColor.RED + vl.getInformation());

            meta.setLore(lore);
            icon.setItemMeta(meta);

            gui.addItem(icon);

            cursor++;
        }

        displayTo.openInventory(gui);
    }

}
