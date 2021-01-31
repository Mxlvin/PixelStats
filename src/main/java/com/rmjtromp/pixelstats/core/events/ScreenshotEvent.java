package com.rmjtromp.pixelstats.core.events;

import java.io.File;

import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;

import net.minecraft.client.gui.ChatLine;

public final class ScreenshotEvent extends Event {

	private static HandlerList HANDLER_LIST = new HandlerList();
	private File file;
	private ChatLine line;
	
	public ScreenshotEvent(File file, ChatLine line) {
		this.file = file;
		this.line = line;
	}
	
	public File getFile() {
		return file;
	}
	
	public ChatLine getChatLine() {
		return line;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
