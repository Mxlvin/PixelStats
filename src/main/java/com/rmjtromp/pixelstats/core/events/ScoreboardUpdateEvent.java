package com.rmjtromp.pixelstats.core.events;

import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Scoreboard;

public class ScoreboardUpdateEvent extends Event {

	private static HandlerList HANDLER_LIST = new HandlerList();
	
	public Scoreboard getScoreboard() {
		return Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null ? Minecraft.getMinecraft().thePlayer.getWorldScoreboard() : null;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
