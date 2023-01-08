package io.github.apricotfarmer11.mods.tubion.feature;

import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.github.apricotfarmer11.mods.tubion.core.helper.PlayerHelper;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.GameMode;
import io.github.apricotfarmer11.mods.tubion.event.GameHudEvents;
import net.minecraft.client.MinecraftClient;

import java.util.Arrays;

public class AutoGGL {
    private static final GameMode[] COMPETITIVE_GAMES = {
            GameMode.BATTLE_ROYALE,
            GameMode.CRYSTAL_RUSH,
            GameMode.LIGHT_STRIKE,
    };
    public AutoGGL() {
        GameHudEvents.TITLE_SET.register((text) -> {
            if (TubionMod.getConfig().autoGf && Arrays.stream(COMPETITIVE_GAMES).anyMatch((a) -> a == TubnetCore.getInstance().getGameMode()) && text.getString().contains("go!")) {
                PlayerHelper.sendChatMessage("glhf");
            }
        });
    }
}