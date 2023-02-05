package io.github.apricotfarmer11.mods.tubion.event.ui;

import io.github.apricotfarmer11.mods.tubion.event.api.events.callables.EventTyped;
import io.github.apricotfarmer11.mods.tubion.event.api.types.EventType;

public class ScoreboardUpdateEvent extends EventTyped {
    public ScoreboardUpdateEvent() {
        super(EventType.POST);
    }
}
