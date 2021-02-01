package com.rmjtromp.pixelstats.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rmjtromp.pixelstats.core.utils.events.Event;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;
import com.rmjtromp.pixelstats.core.utils.events.Listener;
import com.rmjtromp.pixelstats.core.utils.events.RegisteredListener;

import net.minecraftforge.common.MinecraftForge;

public final class EventsManager {

	private static EventsManager manager = null;
	private static final List<Listener> queue = new ArrayList<>();

	private EventsManager() {}
	
	public static void init() {
		if(manager == null) manager = new EventsManager();
		for(Listener listener : queue) registerEvents(listener);
		if(!queue.isEmpty()) queue.clear();
		
		MinecraftForge.EVENT_BUS.register(new ForgeEventListener());
	}
	
	public static void registerEvents(Listener listener) {
		if(manager != null) {
	    	for (Map.Entry<Class<? extends Event>, Set<RegisteredListener>> entry : RegisteredListener.createRegisteredListeners(listener).entrySet()) {
	    		Class<? extends Event> clazz = entry.getKey();
	    		Set<RegisteredListener> value = entry.getValue();
	    		Class<? extends Event> registrationClass = getRegistrationClass(clazz);
	    		HandlerList list = getEventListeners(registrationClass);
	            if(list != null) list.registerAll(value);
	        }
		} else queue.add(listener);
	}
	
	public static void callEvent(Event event) {
        HandlerList handlers = event.getHandlers();
        RegisteredListener[] listeners = handlers.getRegisteredListeners();

        for (RegisteredListener registration : listeners) {
            try {
                registration.callEvent(event);
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }
	}
    
    private static HandlerList getEventListeners(Class<? extends Event> type) {
        try {
        	Class<?> clazz = getRegistrationClass(type);
        	if(clazz != null) {
                Method method = clazz.getDeclaredMethod("getHandlerList");
                method.setAccessible(true);
                return (HandlerList) method.invoke(null);
        	}
        } catch (Exception e) {/* ignore */}
        return null;
    }
    
    private static Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null
                    && !clazz.getSuperclass().equals(Event.class)
                    && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
            	try {
            		throw new Exception("Unable to find handler list for event " + clazz.getName());
            	} catch(Exception e1) {/* ignore */}
            }
        }
        return null;
    }
	
}
