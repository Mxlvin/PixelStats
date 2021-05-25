package com.rmjtromp.pixelstats.core.events;

import java.util.Timer;
import java.util.TimerTask;

import com.rmjtromp.pixelstats.core.EventsManager;
import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;

public final class TickEvent {
	
	private TickEvent() {}
	
	public static final class GifTick extends Event {
		
		static {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					EventsManager.callEvent(new GifTick());
				}
			}, 0, 10);
		}

		private static HandlerList HANDLER_LIST = new HandlerList();

		@Override
		public HandlerList getHandlers() {
			return HANDLER_LIST;
		}
		
		public static HandlerList getHandlerList() {
			return HANDLER_LIST;
		}
		
	}
	
	public static final class RenderTick extends Event {

		private static HandlerList HANDLER_LIST = new HandlerList();

		@Override
		public HandlerList getHandlers() {
			return HANDLER_LIST;
		}
		
		public static HandlerList getHandlerList() {
			return HANDLER_LIST;
		}
		
	}
	
	public static final class ClientTick extends Event {

		private static HandlerList HANDLER_LIST = new HandlerList();

		@Override
		public HandlerList getHandlers() {
			return HANDLER_LIST;
		}
		
		public static HandlerList getHandlerList() {
			return HANDLER_LIST;
		}
		
	}

}
