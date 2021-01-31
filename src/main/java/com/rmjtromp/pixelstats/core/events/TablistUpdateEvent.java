package com.rmjtromp.pixelstats.core.events;

import java.util.ArrayList;
import java.util.List;

import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;

import net.minecraft.network.play.server.S38PacketPlayerListItem.Action;
import net.minecraft.network.play.server.S38PacketPlayerListItem.AddPlayerData;;

public final class TablistUpdateEvent extends Event {

	private static HandlerList HANDLER_LIST = new HandlerList();
	
	private final Action a;
	private List<AddPlayerData> d;
	private boolean hasChanges = false;
	
	public TablistUpdateEvent(Action a, List<AddPlayerData> data) {
		this.a = a; this.d = data;
	}
	
	public Action getAction() {
		return a;
	}
	
	public List<AddPlayerData> getData() {
		return d != null ? d : new ArrayList<>();
	}
	
	public void setData(List<AddPlayerData> data) {
		hasChanges = true;
		d = data;
	}
	
	public boolean wasManipulated() {
		return hasChanges;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
