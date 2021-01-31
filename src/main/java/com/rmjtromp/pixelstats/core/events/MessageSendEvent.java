package com.rmjtromp.pixelstats.core.events;

import com.rmjtromp.pixelstats.core.utils.events.Cancellable;
import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;

public class MessageSendEvent extends Event implements Cancellable {

	private static HandlerList HANDLER_LIST = new HandlerList();
	private boolean cancelled = false;
	private String message = null;
	
	public MessageSendEvent(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
