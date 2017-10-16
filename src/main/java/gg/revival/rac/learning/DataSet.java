package gg.revival.rac.learning;

import com.google.common.collect.Maps;
import gg.revival.rac.RAC;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

public class DataSet {

    @Getter RAC rac;
    @Getter DataSetType type;
    @Getter Map<UUID, Integer> trackedPlayers;

    public DataSet(RAC rac, DataSetType type) {
        this.rac = rac;
        this.type = type;
        this.trackedPlayers = Maps.newConcurrentMap();
    }

}
