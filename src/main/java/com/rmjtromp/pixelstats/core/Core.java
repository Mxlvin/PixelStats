package com.rmjtromp.pixelstats.core;

import java.util.UUID;

import org.lwjgl.input.Keyboard;

import com.rmjtromp.pixelstats.PixelStats;
import com.rmjtromp.pixelstats.core.events.KeyPressEvent;
import com.rmjtromp.pixelstats.core.events.ScreenDrawingEvent;
import com.rmjtromp.pixelstats.core.events.ServerJoinEvent;
import com.rmjtromp.pixelstats.core.events.ServerQuitEvent;
import com.rmjtromp.pixelstats.core.gui.SettingsGui;
import com.rmjtromp.pixelstats.core.gui.Test;
import com.rmjtromp.pixelstats.core.utils.Console;
import com.rmjtromp.pixelstats.core.utils.events.EventHandler;
import com.rmjtromp.pixelstats.core.utils.events.Listener;
import com.rmjtromp.pixelstats.core.utils.hypixel.DebugUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.settings.KeyBinding;

public final class Core implements Listener {
	
	private static Core core = null;
	public static boolean asd = false;
	private static final KeyBinding Settings = KeybindManager.registerKeybind("Settings", Keyboard.KEY_ESCAPE, "PixelStats");
	private final SettingsGui settingsGui = new SettingsGui();
	
	private Core() {
		Console.log("Initializing Core");
		EventsManager.init();
		KeybindManager.init();
		DebugUtil.init();
//		Test.init();

		EventsManager.registerEvents(new Listener() {
			
			private boolean isOnHypixel = false;
	    	
	        @EventHandler
	        public void onServerJoin(ServerJoinEvent e) {
	        	if(!e.isLocal() && e.getServerData().serverIP.toLowerCase().contains("hypixel.net")) {
	        		Hypixel.initialize();
	        		isOnHypixel = true;
	        	}
	        }
	        
	        @EventHandler
	        public void onServerQuit(ServerQuitEvent e) {
	        	if(isOnHypixel) {
	        		Hypixel.uninitialize();
	        		isOnHypixel = false;
	        	}
	        }
	        
	        @EventHandler
	        public void onKeyPress(KeyPressEvent e) {
	        	if(Settings.isPressed()) {
//	        		Minecraft.getMinecraft().displayGuiScreen(settingsGui);
	        	}
	        }
	        
	        @EventHandler
	        public void drawScreen(ScreenDrawingEvent e) {
	        	if(e.getScreen() instanceof GuiMainMenu) {
	        		Minecraft.getMinecraft().displayGuiScreen(new Test());
	        	}
	        }
	        
	    });
	}

	public static Core init(PixelStats mod) {
		EventsManager.registerEvents(mod);
		if(core == null) core = new Core();
		return core;
	}
    
	public static UUID stringToUUID(String string) {
		if(string.matches("^([a-f0-9]{8}(?:-?[a-f0-9]{4}){4}[a-f0-9]{8})$")) {
			String stringUUID;
			if(string.matches("^([a-f0-9]{8}(?:[a-f0-9]{4}){4}[a-f0-9]{8})$")) {
				string = string.substring(0, 8) + "-" + string.substring(8);
				string = string.substring(0, 13) + "-" + string.substring(13);
				string = string.substring(0, 18) + "-" + string.substring(18);
				string = string.substring(0, 23) + "-" + string.substring(23);
			}
			stringUUID = string;

			if(stringUUID.length() == 36) {
				try {
					return UUID.fromString(stringUUID);
				} catch(IllegalArgumentException e) {/* ignore */}
			}
		}
		return null;
	}
	
}
