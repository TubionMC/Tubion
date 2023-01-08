package io.github.apricotfarmer11.mods.tubion.event;

import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public interface ChatMessageEvent {
    Event<ChatMessageEvent> EVENT = EventFactory.createArrayBacked(ChatMessageEvent.class, (listeners) -> (Text message) -> {
        TubnetCore.getInstance().onScoreboardUpdate();
        for (ChatMessageEvent listener : listeners) {
            ActionResult result = listener.onChat(message);

            if (result != ActionResult.PASS) return result;
        }
        return ActionResult.PASS;
    });
    ActionResult onChat(Text message);
}
