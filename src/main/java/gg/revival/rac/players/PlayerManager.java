package gg.revival.rac.players;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import gg.revival.rac.RAC;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

public class PlayerManager {

    @Getter private RAC rac;
    @Getter public Set<ACPlayer> players;

    public PlayerManager(RAC rac) {
        this.rac = rac;
        this.players = Sets.newConcurrentHashSet();
    }

    /**
     * Returns an ACPlayer object based on given UUID
     * @param uuid
     * @return
     */
    public ACPlayer getPlayerByUUID(UUID uuid) {
        ImmutableSet<ACPlayer> cache = ImmutableSet.copyOf(players);

        for(ACPlayer player : cache) {
            if(player.getUuid().equals(uuid))
                return player;
        }

        return null;
    }

    /**
     * Returns an ACPlayer object based on given username
     * @param username
     * @return
     */
    public ACPlayer getPlayerByUsername(String username) {
        ImmutableSet<ACPlayer> cache = ImmutableSet.copyOf(players);

        for(ACPlayer player : cache) {
            if(player.getUsername().equals(username))
                return player;
        }

        return null;
    }

}
