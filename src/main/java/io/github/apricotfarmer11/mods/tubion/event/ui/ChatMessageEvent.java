package io.github.apricotfarmer11.mods.tubion.event.ui;

import io.github.apricotfarmer11.mods.tubion.event.api.events.callables.EventCancellable;
import net.minecraft.text.Text;

/**
 * Event
 */
public class ChatMessageEvent extends EventCancellable {
    private Text message;
    public ChatMessageEvent(Text message) {
        this.message = message;
    }

    public Text getMessage() {
        return message;
    }
    public void setMessage(Text msg) {
        this.message = msg;
    }
}
