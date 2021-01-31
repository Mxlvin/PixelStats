package com.rmjtromp.pixelstats.core;

import java.sql.Connection;
import java.util.UUID;

import com.rmjtromp.pixelstats.PixelStats;
import com.rmjtromp.pixelstats.core.events.ServerJoinEvent;
import com.rmjtromp.pixelstats.core.events.ServerQuitEvent;
import com.rmjtromp.pixelstats.core.utils.events.EventHandler;
import com.rmjtromp.pixelstats.core.utils.events.Listener;

public final class Core implements Listener {
	
	private static Core core = null;
	private static Connection conn = null;
	
	private Core() {
		EventsManager.init();
		KeybindManager.init();

		EventsManager.registerEvents(new Listener() {
	    	
	        @EventHandler
	        public void onServerJoin(ServerJoinEvent e) {
	        	if(!e.isLocal() && e.getServerData().serverIP.toLowerCase().contains("hypixel.net")) Hypixel.initialize();
	        }
	        
	        @EventHandler
	        public void onServerQuit(ServerQuitEvent e) {
	        	if(Hypixel.isOnHypixel()) Hypixel.uninitialize();
	        }
	        
	    });
	}

	public static Core init(PixelStats mod) {
		EventsManager.registerEvents(mod);
		if(core == null) core = new Core();
		return core;
	}
	
    public static Connection getConnection() {
    	return conn;
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
    
//    private void initializeConnection() {
//    	try {
//    		Class.forName("org.sqlite.JDBC");
//    		File databaseFile = new File(BWStats.getModDir() + File.separator + "bwstats.db");
//    		if(!databaseFile.exists()) databaseFile.createNewFile();
//	        conn = DriverManager.getConnection("jdbc:sqlite:"+databaseFile.getAbsolutePath());
//    	} catch(Exception e) {
//    		e.printStackTrace();
//    	}
//    }
	
}
