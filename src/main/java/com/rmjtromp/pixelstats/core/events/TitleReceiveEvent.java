package com.rmjtromp.pixelstats.core.events;

import com.rmjtromp.pixelstats.core.utils.events.Cancellable;
import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;

import net.minecraft.util.IChatComponent;

public class TitleReceiveEvent extends Event implements Cancellable {
	
	public static enum Type { TITLE, SUBTITLE, TIMES, CLEAR, RESET; }

	private static HandlerList HANDLER_LIST = new HandlerList();
	private boolean cancelled = false;
	
	private IChatComponent component;
	private int fadeIn, duration, fadeOut;
	private Type type;
	
	public TitleReceiveEvent(IChatComponent component, int fadeIn, int duration, int fadeOut, Type type) {
		if(type == null) throw new NullPointerException();
		this.component = component; this.fadeIn = fadeIn; this.duration = duration; this.fadeOut = fadeOut; this.type = type;
	}
	
	public TitleReceiveEvent(IChatComponent component, int fadeIn, int duration, int fadeOut, net.minecraft.network.play.server.S45PacketTitle.Type type) {
		if(type == null) throw new NullPointerException();
		this.component = component; this.fadeIn = fadeIn; this.duration = duration; this.fadeOut = fadeOut;
		if(type.equals(net.minecraft.network.play.server.S45PacketTitle.Type.CLEAR)) this.type = Type.CLEAR;
		else if(type.equals(net.minecraft.network.play.server.S45PacketTitle.Type.TITLE)) this.type = Type.TITLE;
		else if(type.equals(net.minecraft.network.play.server.S45PacketTitle.Type.SUBTITLE)) this.type = Type.SUBTITLE;
		else if(type.equals(net.minecraft.network.play.server.S45PacketTitle.Type.TIMES)) this.type = Type.TIMES;
		else if(type.equals(net.minecraft.network.play.server.S45PacketTitle.Type.RESET)) this.type = Type.RESET;
	}
	
	public IChatComponent getMessage() {
		return component;
	}
	
	public int getFadeInTime() {
		return fadeIn;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public int getFadeOutTime() {
		return fadeOut;
	}
	
	public Type getType() {
		return type;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}
