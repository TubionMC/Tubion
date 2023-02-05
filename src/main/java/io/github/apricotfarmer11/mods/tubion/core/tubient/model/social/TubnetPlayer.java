package io.github.apricotfarmer11.mods.tubion.core.tubient.model.social;

import io.github.apricotfarmer11.mods.tubion.core.tubient.model.types.PlayerEdition;
import io.github.apricotfarmer11.mods.tubion.core.tubient.model.types.TubnetPlayerRank;

import java.util.UUID;

/**
 * A TubNet User
 */
public class TubnetPlayer {
    // User-specifics, not related to TN
    private String username;
    private UUID uuid;

    // TubNet Specifics
    private PlayerEdition edition = PlayerEdition.JAVA;
    private TubnetPlayerRank rank = TubnetPlayerRank.PLAYER;

    // /player/info/:(:user|:uuid)
    private boolean tubionUser = false;

    public String getUsername() {
        return username;
    }
    public UUID getUuid() {
        return uuid;
    }

    public PlayerEdition getEdition() {
        return edition;
    }
    public TubnetPlayerRank getRank() {
        return rank;
    }

    public boolean isTubionUser() {
        return tubionUser;
    }


    public void setUsername(String username) {
        this.username = username;
    }
    public void setUuid(UUID uid) {
        this.uuid = uid;
    }

    public void setEdition(PlayerEdition ed) {
        this.edition = ed;
    }
    public void setRank(TubnetPlayerRank rank) {
        this.rank = rank;
    }

    public void setUsingTubion(boolean usingTI) {
        this.tubionUser = usingTI;
    }
}
