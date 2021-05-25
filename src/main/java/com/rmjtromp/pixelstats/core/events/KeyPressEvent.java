package com.rmjtromp.pixelstats.core.events;

import org.lwjgl.input.Keyboard;

import com.rmjtromp.pixelstats.core.utils.events.Cancellable;
import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;

public class KeyPressEvent extends Event implements Cancellable {

	private static HandlerList HANDLER_LIST = new HandlerList();
	private boolean cancelled = false;

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
	
	public boolean isCtrlDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
	}
	
	public boolean isShiftDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
