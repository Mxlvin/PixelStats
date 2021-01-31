package com.rmjtromp.pixelstats.core.events;

import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;

import net.minecraft.client.gui.GuiScreen;

public final class ScreenDrawingEvent extends Event {

	private static HandlerList HANDLER_LIST = new HandlerList();
	private GuiScreen screen;
	
	public ScreenDrawingEvent(GuiScreen screen) {
		this.screen = screen;
	}
	
	public GuiScreen getScreen() {
		return screen;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
