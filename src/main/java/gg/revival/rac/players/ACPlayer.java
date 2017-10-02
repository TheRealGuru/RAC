package gg.revival.rac.players;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class ACPlayer {

    @Getter UUID uuid;
    @Getter String username;
    @Getter @Setter long recentVelocity, recentAttack;

    public ACPlayer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.recentVelocity = System.currentTimeMillis();
        this.recentAttack = System.currentTimeMillis();
    }

}
