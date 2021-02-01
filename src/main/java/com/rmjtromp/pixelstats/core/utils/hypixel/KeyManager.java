package com.rmjtromp.pixelstats.core.utils.hypixel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rmjtromp.pixelstats.PixelStats;
import com.rmjtromp.pixelstats.core.EventsManager;
import com.rmjtromp.pixelstats.core.events.ClientTickEvent;
import com.rmjtromp.pixelstats.core.events.MessageReceiveEvent;
import com.rmjtromp.pixelstats.core.utils.ChatColor;
import com.rmjtromp.pixelstats.core.utils.events.EventHandler;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;
import com.rmjtromp.pixelstats.core.utils.events.Listener;

import net.minecraft.client.Minecraft;

public final class KeyManager {

	private static final Pattern KEY_PATTERN = Pattern.compile("^Your new API key is ([a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8})$");
	private static KeyManager manager = null;
	
	private Properties properties = new Properties();
	private Consumer<UUID> callback = null;
	private UUID key = null;
	private Timer timer = null;
	
	public static KeyManager initialize(Consumer<UUID> callback) {
		if(manager == null) new KeyManager();
		manager.callback = callback;
		EventsManager.registerEvents(manager.listener);
		manager.timer = new Timer();
		manager.timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				manager.saveProperties();
			}
			
		}, 300000, 300000);
		
		return manager;
	}
	
	public static void uninitialize() {
		if(manager.timer != null) manager.timer.cancel();
		manager.timer = null;
		HandlerList.unregisterAll(manager.listener);
		manager.saveProperties();
	}
	
	private KeyManager() {
		manager = this;
		try {
			File propFile = new File(PixelStats.getModDir() + File.separator + "config.properties");
			if(!propFile.exists()) propFile.createNewFile();
			InputStream stream = new FileInputStream(propFile);
			properties.load(stream);
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String apiKey = properties.containsKey("api-key") ? properties.getProperty("api-key") : "";
		if(apiKey.matches("^([a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8})$")) setKey(UUID.fromString(apiKey), false);
	}
	
	public UUID getKey() {
		return key;
	}
	
	private synchronized void setKey(UUID key, boolean update) {
		if(key != null) {
			if(this.key != null && this.key.equals(key)) return;
			this.key = key;
			if(update) properties.setProperty("api-key", this.key.toString());
			try {
				if(callback != null) callback.accept(this.key);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void saveProperties() {
		try {
			File propFile = new File(PixelStats.getModDir() + File.separator + "config.properties");
			if(!propFile.exists()) propFile.createNewFile();
			FileWriter writer = new FileWriter(propFile);
			properties.store(writer, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean awaitingNewKey = false;
	public static synchronized void requestNewKey(Runnable callback) {
		if(!awaitingNewKey) {
			awaitingNewKey = true;
			EventsManager.registerEvents(new Listener() {
				int tick = 0;
				@EventHandler
				public void onTick(ClientTickEvent e) {
					// unregister listener after 2 seconds of inactivity
					if(tick > 40) {
						HandlerList.unregisterAll(this);
						awaitingNewKey = false;
					}
					tick++; 
				}

				@EventHandler
				public void onMessageReceive(MessageReceiveEvent e) {
					Matcher matcher = KEY_PATTERN.matcher(ChatColor.stripcolor(e.getMessage().getUnformattedText()));
					if (matcher.matches()) {
						manager.setKey(UUID.fromString(matcher.group(1)), true);
						e.setCancelled(true);
						HandlerList.unregisterAll(this);
						awaitingNewKey = false;
						if(callback != null) callback.run();
					}
				}
			});
			Minecraft.getMinecraft().thePlayer.sendChatMessage("/api new");
		}
	}
	
	private Listener listener = new Listener() {
		
		@EventHandler
		public void onMessageReceive(MessageReceiveEvent e) {
			Matcher matcher = KEY_PATTERN.matcher(ChatColor.stripcolor(e.getMessage().getUnformattedText()));
			if (matcher.matches()) manager.setKey(UUID.fromString(matcher.group(1)), true);
		}
		
	};
	
}
