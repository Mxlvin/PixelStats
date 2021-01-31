package com.rmjtromp.pixelstats.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Strings;
import com.rmjtromp.pixelstats.PixelStats;
import com.rmjtromp.pixelstats.core.events.ClientTickEvent;
import com.rmjtromp.pixelstats.core.events.KeyPressEvent;
import com.rmjtromp.pixelstats.core.events.MessageReceiveEvent;
import com.rmjtromp.pixelstats.core.events.RenderTickEvent;
import com.rmjtromp.pixelstats.core.events.ScoreboardUpdateEvent;
import com.rmjtromp.pixelstats.core.events.WorldLoadEvent;
import com.rmjtromp.pixelstats.core.games.IGame;
import com.rmjtromp.pixelstats.core.games.bedwars.BedWars;
import com.rmjtromp.pixelstats.core.guis.SettingsGUI;
import com.rmjtromp.pixelstats.core.utils.ChatColor;
import com.rmjtromp.pixelstats.core.utils.HypixelProfile;
import com.rmjtromp.pixelstats.core.utils.events.EventHandler;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;
import com.rmjtromp.pixelstats.core.utils.events.Listener;

import net.hypixel.api.HypixelAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.IChatComponent;

public final class Hypixel {

	private static Hypixel hypixel = null;
	private static boolean isOnHypixel = false;
	private static GAMEMODE gamemode = null;
	private static UUID key = null;
	private static Properties properties = new Properties();
	private static GAMESTATUS status = null;
	private static HypixelAPI API = null;
	
	@SuppressWarnings("unused")
	private SettingsGUI settingsGUI = new SettingsGUI();
	
	private static Timer timer = new Timer();
	private static final Pattern KEY_PATTERN = Pattern.compile("^Your new API key is ([a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8})$");
	private static final KeyBinding DEBUG_KEY = KeybindManager.registerKeybind("key.debug", Keyboard.KEY_P, "key.categories.pixelstats");
	
	public enum GAMESTATUS { LOBBY, IN_GAME, AFK }
	public enum GAMEMODE {
		MAIN_LOBBY("hypixel"),
		BED_WARS,
		SKYWARS,
		MURDER_MYSTERY,
		ARCADE_GAMES,
		UHC_CHAMPIONS,
		ARENA_BRAWL,
		BUILD_BATTLE,
		COPS_AND_CRIMS,
		DUELS,
		MEGA_WALLS,
		PAINTBALL_WARFARE,
		QUAKECRAFT,
		BLITZ_SURVIVAL_GAMES,
		SMASH_HEROES,
		SPEED_UHC,
		THE_TNT_GAMES,
		TURBO_KART_RACERS,
		VAMPIREZ,
		THE_WALLS,
		WARLORDS;
		
		private IGame game = null;
		private String regex;
		private GAMEMODE(String regex) {
			this.regex = regex;
		}
		
		private GAMEMODE() {}
		
		private void setGame(IGame game) {
			this.game = game;
		}
		
		private String getRegex() {
			return regex != null ? regex : toString().toLowerCase().replace("_", " ");
		}
		
		public void initialize() {
			if(game != null) game.initialize();
		}
		
		public void uninitialize() {
			if(game != null) game.uninitialize();
		}
	}

	public static void initialize() {
		if (hypixel == null) new Hypixel();
		isOnHypixel = true;
		gamemode = null;
		status = null;
		
		EventsManager.registerEvents(hypixel.listener);
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				saveProperties();
			}
			
		}, 300000, 300000);
	}
	
	public static void uninitialize() {
		isOnHypixel = false;
		timer.cancel();
		saveProperties();
		HypixelProfile.uninitialize();
		HandlerList.unregisterAll(hypixel.listener);
		if(hypixel.getGameMode() != null) hypixel.getGameMode().uninitialize();
	}

	private Hypixel() {
		hypixel = this;
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
		
		try {
			GAMEMODE.BED_WARS.setGame(new BedWars());
		} catch (NoSuchFieldException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public HypixelAPI getAPI() {
		return API;
	}

	private void setKey(UUID key, boolean update) {
		if(key != null) {
			Hypixel.key = key;
			Hypixel.API = new HypixelAPI(Hypixel.key);
			if(update) properties.setProperty("api-key", Hypixel.key.toString());
		}
	}

	public static boolean isOnHypixel() {
		return isOnHypixel;
	}

	public static Properties getProperties() {
		return properties;
	}
	
	public static Hypixel getInstance() {
		return hypixel;
	}

	public static void saveProperties() {
		try {
			File propFile = new File(PixelStats.getModDir() + File.separator + "config.properties");
			if(!propFile.exists()) propFile.createNewFile();
			FileWriter writer = new FileWriter(propFile);
			properties.store(writer, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setGamemode(GAMEMODE game) {
		if (gamemode == game) return;
		if(gamemode != null) gamemode.uninitialize();
		gamemode = game;
		if(game != null) game.initialize();
	}
	
	public GAMEMODE getGameMode() {
		return gamemode;
	}
	
	public GAMESTATUS getStatus() {
		return status;
	}
	
	private static List<String> debug = new ArrayList<>();
	
	public static void clearDebug() {
		debug.clear();
	}
	
	public static void debug(IChatComponent component) {
		debug(component.getFormattedText());
	}
	
	public static void debug(String string) {
		if(debug.size() > 10) debug = debug.subList(debug.size() - 10, debug.size());
		debug.add(ChatColor.colorEncode(string));
	}
	
	private boolean awaitingNewKey = false;
	public void requestNewKey(Runnable callback) {
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
					if (matcher.find()) {
						Hypixel.getInstance().setKey(UUID.fromString(matcher.group(1)), true);
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
		
		boolean toggle = false;
		@EventHandler
		public void onKeyPress(KeyPressEvent e) {
			if(DEBUG_KEY.isPressed()) {
				toggle = !toggle;
			}
		}

		@EventHandler
		public void onRenderTick(RenderTickEvent e) {
			if(!toggle) {
				FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
				List<String> list = new ArrayList<>();
				list.add(ChatColor.colorEncode("&7GameMode: &f"+(gamemode == null ? "&cnull" : gamemode.toString())));
				list.add(ChatColor.colorEncode("&7Status: &f"+(status == null ? "&cnull" : status.toString())));
				
				if(!debug.isEmpty()) {
					list.add("");
					
			        for (int i = 0; i < list.size(); ++i) {
			            String s = list.get(i);

			            if (!Strings.isNullOrEmpty(s)) {
			                int fontHeight = fr.FONT_HEIGHT;
			                int stringWidth = fr.getStringWidth(s);
			                int y = 2 + fontHeight * i;
			                Gui.drawRect(1, y - 1, 2 + stringWidth + 1, y + fontHeight - 1, -1873784752);
			                fr.drawString(s, 2, y, 14737632);
			            }
			        }
				}

			}
		}
		
		private Pattern pattern = Pattern.compile("^You are currently (?:connected to server|in) (.*?)$");
		@EventHandler
		public void onMessageReceiveEvent(final MessageReceiveEvent e) {
			final String strippedMessage = ChatColor.stripcolor(e.getMessage().getUnformattedText());
			Matcher matcher = pattern.matcher(strippedMessage);
			if(matcher.matches()) {
				if(matcher.group(1).toLowerCase().contains("lobby") || matcher.group(1).toLowerCase().startsWith("min") || matcher.group(1).toLowerCase().startsWith("mega")) {
					status = matcher.group(1).toLowerCase().contains("lobby") ? GAMESTATUS.LOBBY : GAMESTATUS.IN_GAME;
				} else if(matcher.group(1).equalsIgnoreCase("limbo")) {
					status = GAMESTATUS.AFK;
				}
			} else if(strippedMessage.matches("^You have \\d+ unclaimed (?:leveling|achievement) rewards?!$")) status = GAMESTATUS.LOBBY;
			else if(strippedMessage.matches("^\\w{1,16} has (?:joined \\(\\d+\\/\\d+\\)|quit)!$")) status = GAMESTATUS.IN_GAME;
			else if(strippedMessage.matches("^Your new API key is ([a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8})$")) {
				Matcher m1 = KEY_PATTERN.matcher(strippedMessage);
				if (m1.find()) setKey(UUID.fromString(m1.group(1)), true);
			}
		}

		@EventHandler
		public void onWorldLoad(WorldLoadEvent e) {
			if(Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null) {
				EventsManager.registerEvents(new Listener() {
					int tick = 0;
					@EventHandler
					public void onTick(ClientTickEvent e) {
						// unregister listener after 2 seconds of inactivity
						if(tick > 40) HandlerList.unregisterAll(this);
						tick++; 
					}
					
					@EventHandler
					public void onMessageReceiveEvent(final MessageReceiveEvent e) {
						if(e.getMessage().getUnformattedText().matches("^You are currently (?:connected to server|in) (.*?)$")) {
							e.setCancelled(true);
							HandlerList.unregisterAll(this);
						}
					}
				});
				Minecraft.getMinecraft().thePlayer.sendChatMessage("/whereami");
			}
		}
		
		private long lastCheck = 0L;
		@EventHandler
		public void onScoreboardUpdate(ScoreboardUpdateEvent e) {
			Scoreboard sb = e.getScoreboard();
			if (sb != null && sb.getObjectiveInDisplaySlot(1) != null) {
				
				if(status == null && System.currentTimeMillis() - lastCheck > 30000 && Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null) {
					EventsManager.registerEvents(new Listener() {
						int tick = 0;
						@EventHandler
						public void onTick(ClientTickEvent e) {
							// unregister listener after 2 seconds of inactivity
							if(tick > 40) HandlerList.unregisterAll(this);
							tick++; 
						}
						
						@EventHandler
						public void onMessageReceiveEvent(final MessageReceiveEvent e) {
							if(e.getMessage().getUnformattedText().matches("^You are currently (?:connected to server|in) (.*?)$")) {
								e.setCancelled(true);
								HandlerList.unregisterAll(this);
							}
						}
					});
					lastCheck = System.currentTimeMillis();
					Minecraft.getMinecraft().thePlayer.sendChatMessage("/whereami");
				}
				
				String displayName = ChatColor.stripcolor(sb.getObjectiveInDisplaySlot(1).getDisplayName());
				for(GAMEMODE mode : GAMEMODE.values()) {
					if(displayName.toLowerCase().matches(mode.getRegex())) {
						setGamemode(mode);
						return;
					}
					
				}
				setGamemode(null);
			}
		}

	};

}
