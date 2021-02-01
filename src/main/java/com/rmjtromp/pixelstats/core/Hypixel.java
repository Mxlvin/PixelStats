package com.rmjtromp.pixelstats.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.rmjtromp.pixelstats.core.events.ClientTickEvent;
import com.rmjtromp.pixelstats.core.events.MessageReceiveEvent;
import com.rmjtromp.pixelstats.core.events.WorldLoadEvent;
import com.rmjtromp.pixelstats.core.games.AbstractGame;
import com.rmjtromp.pixelstats.core.games.bedwars.BedWars;
import com.rmjtromp.pixelstats.core.utils.HypixelProfile;
import com.rmjtromp.pixelstats.core.utils.ReflectionUtil;
import com.rmjtromp.pixelstats.core.utils.events.EventHandler;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;
import com.rmjtromp.pixelstats.core.utils.events.Listener;
import com.rmjtromp.pixelstats.core.utils.hypixel.KeyManager;

import net.hypixel.api.HypixelAPI;
import net.hypixel.api.util.GameType;
import net.minecraft.client.Minecraft;

public final class Hypixel {

	private static Hypixel hypixel = null;
	private static final JsonParser PARSER = new JsonParser();
	
	private GameActivity gameActivity = null;
	private HypixelAPI API = null;
	
	private GameType activeGameType = null;
	private List<AbstractGame> games = new ArrayList<>();
	
	private static Field enabledField;
	
	public enum GameActivity { LOBBY, IN_GAME, AFK }

	public static void initialize() {
		if (hypixel == null) new Hypixel();
		hypixel.gameActivity = null;
		hypixel.activeGameType = null;
		KeyManager.initialize(key -> hypixel.API = new HypixelAPI(key));
		
		EventsManager.registerEvents(hypixel.listener);
	}
	
	public static void uninitialize() {
		HypixelProfile.uninitialize();
		HandlerList.unregisterAll(hypixel.listener);
		KeyManager.uninitialize();
		
		// disable all active 'games'
		hypixel.uninitializeActiveGames();
	}
	
	static {
		try {
			enabledField = ReflectionUtil.findField(AbstractGame.class, "enabled");
		} catch(NoSuchFieldException e) {/* ignore */}
	}

	private Hypixel() {
		hypixel = this;
		
		try {
			games.add(new BedWars());
		} catch (NoSuchFieldException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public HypixelAPI getAPI() {
		return API;
	}
	
	public static Hypixel getInstance() {
		return hypixel;
	}
	
	private void uninitializeActiveGames() {
		hypixel.activeGameType = null;
		hypixel.gameActivity = null;
		if(hypixel.games.isEmpty()) return;
		hypixel.games.stream().filter(AbstractGame::isEnabled).forEach(game -> {
			game.uninitialize();
			try {
				enabledField.set(game, false);
			} catch (IllegalArgumentException | IllegalAccessException e) {/* ignore */}
		});
	}

	private void setGamemode(GameType type) {
		if (activeGameType != null && type != null && activeGameType.equals(type)) return;
		if(activeGameType != null) uninitializeActiveGames();
		activeGameType = type;
		if(type != null) {
			hypixel.games.stream().filter(game -> game.getType().equals(type)).forEach(game -> {
				game.initialize();
				try {
					enabledField.set(game, true);
				} catch (IllegalArgumentException | IllegalAccessException e) {/* ignore */}
			});
		}
	}
	
	public GameType getActiveGameType() {
		return activeGameType;
	}
	
	public GameActivity getActivity() {
		return gameActivity;
	}
	
	private Listener listener = new Listener() {

		/*
		 * When the player joins a new world, wait until the player and the world instance are both not null
		 * send a command "/locraw" to get the location of the player, assign the game-activity and game-type of the player
		 */
		@EventHandler
		public void onWorldLoad(WorldLoadEvent e) {
			EventsManager.registerEvents(new Listener() {
				int tick = 0;
				@EventHandler
				public void onTick(ClientTickEvent e) {
					if(Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null) {
						HandlerList.unregisterAll(this);
						
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
								if(e.getMessage().getUnformattedText().matches("^\\{.+?\\}$")) {
									try {
										JsonObject object = PARSER.parse(e.getMessage().getUnformattedText()).getAsJsonObject();
										if(object.has("server")) {
											String server = object.get("server").getAsString();
											if(server.equals("limbo")) gameActivity = GameActivity.AFK;
											else if(server.startsWith("mini") || server.startsWith("mega")) gameActivity = GameActivity.IN_GAME;
											else if(server.contains("lobby")) gameActivity = GameActivity.LOBBY;
											
											if(object.has("gametype")) {
												String gametype = object.get("gametype").getAsString();
												GameType nType = null;
												for(GameType type : GameType.values()) {
													if(gametype.equalsIgnoreCase(type.getDbName())) {
														nType = type;
														break;
													}
												}
												setGamemode(nType);
											}
										}
										e.setCancelled(true);
										HandlerList.unregisterAll(this);
									} catch(JsonSyntaxException e1) {
										e1.printStackTrace();
									}
									
								}
							}
						});
						Minecraft.getMinecraft().thePlayer.sendChatMessage("/locraw");
						
						return;
					}
					// unregister listener after 5 seconds of inactivity
					if(tick > 100) HandlerList.unregisterAll(this);
					tick++; 
				}
			});
		}

	};

}
