package com.rmjtromp.pixelstats.core.events;

import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;

import net.minecraft.world.World;

public final class WorldUnloadEvent extends Event {

	private static HandlerList HANDLER_LIST = new HandlerList();
	private World world;
	
	public WorldUnloadEvent(World world) {
		this.world = world;
	}
	
	public World getWorld() {
		return world;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
