package io.github.apricotfarmer11.mods.tubion.core.tubnet;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.yggdrasil.YggdrasilGameProfileRepository;
import net.minecraft.client.MinecraftClient;

import javax.annotation.Nullable;

public class TubnetPlayer {
    public String username;
    public TubnetPlayerRank rank = TubnetPlayerRank.DEFAULT;
    public TubnetPlayer(String playerName, @Nullable TubnetPlayerRank rank) {
        username = playerName;
        if (rank != null) this.rank = rank;
    }
    enum TubnetPlayerRank {
        // TODO: Custom ranks
        DEFAULT,
        STAR,
        STAR_PLUS,
        MODERATOR,
        ADMIN,
    }
}
