package io.github.apricotfarmer11.mods.tubion.event;

import com.ibm.icu.text.CaseMap;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class GameHudEvents {
    /**
     * Fired when the title has been changed.
     */
    public static Event<TitleSetCallback> TITLE_SET = EventFactory.createArrayBacked(TitleSetCallback.class, (listeners) -> (text) -> {
        for (TitleSetCallback listener : listeners) {
            listener.onTitleSet(text);
        }
    });

    public interface TitleSetCallback {
        void onTitleSet(Text title);
    }
}
