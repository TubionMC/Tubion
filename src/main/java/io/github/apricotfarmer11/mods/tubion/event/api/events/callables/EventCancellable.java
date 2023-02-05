package io.github.apricotfarmer11.mods.tubion.event.api.events.callables;

import io.github.apricotfarmer11.mods.tubion.event.api.events.Cancellable;
import io.github.apricotfarmer11.mods.tubion.event.api.events.Event;

/**
 * Abstract example implementation of the Cancellable interface.
 *
 * @author DarkMagician6
 * @since August 27, 2013
 */
public abstract class EventCancellable implements Event, Cancellable {

    private boolean cancelled;

    protected EventCancellable() {
    }

    /**
     * @see io.github.apricotfarmer11.mods.tubion.event.api.events.Cancellable.isCancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * @see io.github.apricotfarmer11.mods.tubion.event.api.events.Cancellable.setCancelled
     */
    @Override
    public void setCancelled(boolean state) {
        cancelled = state;
    }

}
