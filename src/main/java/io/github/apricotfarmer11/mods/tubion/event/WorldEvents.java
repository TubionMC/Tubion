package io.github.apricotfarmer11.mods.tubion.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class WorldEvents {
    public static Event<WorldInitCallback> INIT = EventFactory.createArrayBacked(WorldInitCallback.class, (listeners) -> () -> {
        for (WorldInitCallback listener : listeners) {
            listener.onWorldInit();
        }
    });
    public interface WorldInitCallback {
        void onWorldInit();
    }
}
