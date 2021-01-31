package com.rmjtromp.pixelstats.core.events;

import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;

import net.minecraft.client.multiplayer.ServerData;

public class ServerQuitEvent extends Event {

	private static HandlerList HANDLER_LIST = new HandlerList();
	private ServerData data;
	
	public ServerQuitEvent(ServerData data) {
		this.data = data;
	}
	
	public ServerData getServerData() {
		return data;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
