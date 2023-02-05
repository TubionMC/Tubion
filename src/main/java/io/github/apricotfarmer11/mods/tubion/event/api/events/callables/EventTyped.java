package io.github.apricotfarmer11.mods.tubion.event.api.events.callables;

import io.github.apricotfarmer11.mods.tubion.event.api.events.Event;
import io.github.apricotfarmer11.mods.tubion.event.api.events.Typed;

/**
 * Abstract example implementation of the Typed interface.
 *
 * @author DarkMagician6
 * @since August 27, 2013
 */
public abstract class EventTyped implements Event, Typed {

    private final byte type;

    /**
     * Sets the type of the event when it's called.
     *
     */
    protected EventTyped(byte eventType) {
        type = eventType;
    }

    @Override
    public byte getType() {
        return type;
    }

}
