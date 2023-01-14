package io.github.apricotfarmer11.mods.tubion.feature;

import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.github.apricotfarmer11.mods.tubion.core.helper.PlayerHelper;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.GameMode;
import io.github.apricotfarmer11.mods.tubion.event.ChatMessageEvent;
import io.github.apricotfarmer11.mods.tubion.event.GameHudEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

import java.util.Arrays;

public class AutoGGL {
    private static final GameMode[] COMPETITIVE_GAMES = {
            GameMode.BATTLE_ROYALE,
            GameMode.CRYSTAL_RUSH,
            GameMode.LIGHT_STRIKE,
    };

    public AutoGGL() {
        GameHudEvents.TITLE_SET.register((text) -> {
            if (Arrays.stream(COMPETITIVE_GAMES).anyMatch((a) -> a == TubnetCore.getInstance().getGameMode())) {
                // GLHF
                if (TubionMod.getConfig().autoGf && text.getString().contains("go!")) {
                    PlayerHelper.sendChatMessage("glhf");
                } else if (TubionMod.getConfig().autoGg && (text.getString().contains("game over") || text.getString().contains("game won"))) {
                    PlayerHelper.sendChatMessage("gg");
                }
            }
        });
        ChatMessageEvent.EVENT.register(message -> {
            if (TubionMod.getConfig().autoGg && message.getString().contains("Your team took")) {
                PlayerHelper.sendChatMessage("gg");
            }
            return ActionResult.PASS;
        });
    }
}