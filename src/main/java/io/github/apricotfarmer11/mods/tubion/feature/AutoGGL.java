package io.github.apricotfarmer11.mods.tubion.feature;

import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.github.apricotfarmer11.mods.tubion.core.helper.PlayerHelper;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.GameMode;
import io.github.apricotfarmer11.mods.tubion.event.GameHudEvents;
import io.github.apricotfarmer11.mods.tubion.event.api.EventManager;
import io.github.apricotfarmer11.mods.tubion.event.api.EventTarget;
import io.github.apricotfarmer11.mods.tubion.event.api.events.Event;
import io.github.apricotfarmer11.mods.tubion.event.ui.ChatMessageEvent;
import io.github.apricotfarmer11.mods.tubion.event.ui.TitleModifyEvent;
import net.minecraft.util.ActionResult;

import java.util.Arrays;

public class AutoGGL {
    private static final GameMode[] COMPETITIVE_GAMES = {
            GameMode.BATTLE_ROYALE,
            GameMode.CRYSTAL_RUSH,
            GameMode.LIGHT_STRIKE,
    };

    public AutoGGL() {
        EventManager.register(this);
    }

    @EventTarget
    public void onChat(ChatMessageEvent ev) {
        String message = ev.getMessage().getString();
        if (Arrays.stream(COMPETITIVE_GAMES).anyMatch(a -> a == TubnetCore.getInstance().getGameMode()) && TubionMod.getConfig().autoGg && message.matches("(Game finished|Game over|Your team took)")) {
            PlayerHelper.sendChatMessage("gg");
        }
    }

    @EventTarget
    public void onTitleUpdate(TitleModifyEvent ev) {
        String title = ev.getMessage().getString();
        if (title.equals("go!") && Arrays.stream(COMPETITIVE_GAMES).anyMatch(a -> a == TubnetCore.getInstance().getGameMode()) && TubionMod.getConfig().autoGf) {
            PlayerHelper.sendChatMessage("glhf");
        }
    }
}