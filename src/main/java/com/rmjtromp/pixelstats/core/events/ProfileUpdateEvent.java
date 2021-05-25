package com.rmjtromp.pixelstats.core.events;

import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;
import com.rmjtromp.pixelstats.core.utils.hypixel.profile.HypixelProfile;

public final class ProfileUpdateEvent extends Event {

	private static HandlerList HANDLER_LIST = new HandlerList();

	private final HypixelProfile profile;
	
	public ProfileUpdateEvent(HypixelProfile profile) {
		this.profile = profile;
	}
	
	public HypixelProfile getProfile() {
		return profile;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
