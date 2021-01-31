package com.rmjtromp.pixelstats.core.events;

import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;

import net.minecraft.client.gui.ChatLine;

public class PostMessageReceiveEvent extends Event {

	private static HandlerList HANDLER_LIST = new HandlerList();
	private final ChatLine line;
	
	public PostMessageReceiveEvent(ChatLine line) {
		this.line = line;
	}

	public ChatLine getLine() {
		return this.line;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
