package gg.revival.rac.patches;

import gg.revival.rac.RAC;
import gg.revival.rac.patches.landboat.BoatPatchListener;
import gg.revival.rac.patches.landboat.BoatPatchTask;
import lombok.Getter;
import org.bukkit.Bukkit;

public class PatchManager {

    @Getter private RAC rac;

    public PatchManager(RAC rac) {
        this.rac = rac;

        loadPatches();
    }

    @SuppressWarnings("deprecation")
    private void loadPatches() {
        Bukkit.getScheduler().runTaskTimer(rac, new BoatPatchTask(rac), 0L, 100L);
        Bukkit.getPluginManager().registerEvents(new BoatPatchListener(rac), rac);
    }

}
