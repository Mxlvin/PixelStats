package com.rmjtromp.pixelstats.core.events;

import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;

import net.minecraft.world.chunk.Chunk;

public final class ChunkLoadEvent extends Event {

	private static HandlerList HANDLER_LIST = new HandlerList();
	private Chunk chunk;
	
	public ChunkLoadEvent(Chunk chunk) {
		this.chunk = chunk;
	}
	
	public Chunk getChunk() {
		return chunk;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
