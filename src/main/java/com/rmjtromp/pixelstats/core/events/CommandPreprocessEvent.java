package com.rmjtromp.pixelstats.core.events;

import com.rmjtromp.pixelstats.core.utils.events.Cancellable;
import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;

import net.minecraft.command.ICommand;

public class CommandPreprocessEvent extends Event implements Cancellable {

	private static HandlerList HANDLER_LIST = new HandlerList();
	private boolean cancelled = false;
	private ICommand command;
	private String[] params;

	public CommandPreprocessEvent(ICommand command, String[] params) {
		this.command = command; this.params = params;
	}
	
	public ICommand getCommand() {
		return command;
	}
	
	public String[] getParameters() {
		return params;
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
