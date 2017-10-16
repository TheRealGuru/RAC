package gg.revival.rac.learning;

import com.google.common.collect.Maps;
import gg.revival.rac.RAC;
import gg.revival.rac.learning.cont.ReachLearning;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class LearningManager {

    @Getter private RAC rac;

    // Contains every player that is currently being learned a set containing their DataSetTypes being learned
    @Getter public Map<UUID, Set<DataSetType>> learningPlayers;

    @Getter public ReachLearning reachLearning;
    @Getter @Setter public double learnedSprintingReach, learnedWalkingReach;

    public LearningManager(RAC rac) {
        this.rac = rac;
        this.reachLearning = new ReachLearning(rac, DataSetType.REACH);
        this.learningPlayers = Maps.newConcurrentMap();

        this.learnedSprintingReach = 0.0D; this.learnedWalkingReach = 0.0D;

        rac.getLog().log("Ready to learn!");
    }

    /**
     * Averages new reach values with existing and updates
     * @param walking
     * @param sprinting
     */
    public void applyAveragesToReach(double walking, double sprinting) {
        if(learnedWalkingReach == 0.0)
            learnedWalkingReach = walking;
        else
            learnedWalkingReach = (learnedWalkingReach + walking) / 2;

        if(learnedSprintingReach == 0.0)
            learnedSprintingReach = sprinting;
        else
            learnedSprintingReach = (learnedSprintingReach + sprinting) / 2;

        rac.getConfig().set("learning.reach.walk", learnedWalkingReach);
        rac.getConfig().set("learning.reach.sprint", learnedSprintingReach);

        rac.saveConfig();

        rac.getLog().log(Level.INFO + "Applied new values to Reach learning | New walk: " + learnedWalkingReach + ", New sprint: " + learnedSprintingReach);
    }

}
