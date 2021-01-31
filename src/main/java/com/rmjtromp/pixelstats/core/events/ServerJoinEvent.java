package com.rmjtromp.pixelstats.core.events;

import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;

import net.minecraft.client.multiplayer.ServerData;

public class ServerJoinEvent extends Event {

	private static HandlerList HANDLER_LIST = new HandlerList();
	private ServerData data;
	private boolean isLocal = true;
	
	public ServerJoinEvent(ServerData data, boolean isLocal) {
		this.data = data;
		this.isLocal = isLocal;
	}
	
	public ServerData getServerData() {
		return data;
	}
	
	public boolean isLocal() {
		return isLocal;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
