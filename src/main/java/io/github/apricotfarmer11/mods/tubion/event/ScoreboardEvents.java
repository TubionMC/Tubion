package io.github.apricotfarmer11.mods.tubion.event;

import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class ScoreboardEvents {
    /**
     * An event that fires when an update to the scoreboard has been detected via packets.
     */
    public static Event<ScoreboardUpdateCallback> SCOREBOARD_UPDATE = EventFactory.createArrayBacked(ScoreboardUpdateCallback.class, (listeners) -> () -> {
        TubnetCore.getInstance().onScoreboardUpdate();
        for (ScoreboardUpdateCallback listener : listeners) {
            listener.onScoreboardUpdate();
        }
    });
    public interface ScoreboardUpdateCallback {
        void onScoreboardUpdate();
    }
}
