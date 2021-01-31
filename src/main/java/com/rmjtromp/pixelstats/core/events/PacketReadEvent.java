package com.rmjtromp.pixelstats.core.events;

import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;

import net.minecraft.network.Packet;

public final class PacketReadEvent extends Event {

	private static HandlerList HANDLER_LIST = new HandlerList();
	private Packet<?> packet;
	
	public PacketReadEvent(Packet<?> packet) {
		this.packet = packet;
	}
	
	public Packet<?> getPacket() {
		return packet;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
