package com.rmjtromp.pixelstats.core.events;

import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;

import net.minecraft.client.gui.GuiScreen;

public final class MouseInputEvent extends Event {

	private static HandlerList HANDLER_LIST = new HandlerList();
	
	private final GuiScreen gui;
	private final int x, y, button;
	
	public MouseInputEvent(GuiScreen gui, int x, int y, int button) {
		this.gui = gui;
		this.x = x;
		this.y = y;
		this.button = button;
	}
	
	public GuiScreen getGuiScreen() {
		return gui;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getButton() {
		return button;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
