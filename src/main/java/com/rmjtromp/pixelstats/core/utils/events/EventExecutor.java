package com.rmjtromp.pixelstats.core.utils.events;

/**
 * Interface which defines the class for event call backs to plugins
 */
public interface EventExecutor {
    public void execute(Listener listener, Event event) throws EventException;
}
