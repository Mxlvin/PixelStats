package com.rmjtromp.pixelstats.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.rmjtromp.pixelstats.PixelStats;
import com.rmjtromp.pixelstats.core.events.KeyPressEvent;
import com.rmjtromp.pixelstats.core.events.MessageReceiveEvent;
import com.rmjtromp.pixelstats.core.events.TickEvent;
import com.rmjtromp.pixelstats.core.events.WorldLoadEvent;
import com.rmjtromp.pixelstats.core.games.AbstractGame;
import com.rmjtromp.pixelstats.core.games.bedwars.BedWars;
import com.rmjtromp.pixelstats.core.gui.SearchOverlay;
import com.rmjtromp.pixelstats.core.utils.ChatColor;
import com.rmjtromp.pixelstats.core.utils.Console;
import com.rmjtromp.pixelstats.core.utils.Multithreading;
import com.rmjtromp.pixelstats.core.utils.ReflectionUtil;
import com.rmjtromp.pixelstats.core.utils.events.EventHandler;
import com.rmjtromp.pixelstats.core.utils.events.EventPriority;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;
import com.rmjtromp.pixelstats.core.utils.events.Listener;
import com.rmjtromp.pixelstats.core.utils.hypixel.DebugUtil;
import com.rmjtromp.pixelstats.core.utils.hypixel.profile.HypixelProfile;

import net.hypixel.api.HypixelAPI;
import net.hypixel.api.util.GameType;
import net.minecraft.client.Minecraft;

public final class Hypixel {

	private static final PixelStats pixelstats = PixelStats.getInstance();
	private static final JsonParser PARSER = new JsonParser();
	private static Hypixel hypixel = null;
	
	private GameActivity gameActivity = null;
	private HypixelAPI API = null;
	private UUID apiKey = null;
	
	private GameType activeGameType = null;
	private List<AbstractGame> games = new ArrayList<>();
	private List<HypixelProfile> partyMembers = new ArrayList<>();
	
	private static Field enabledField;
	
	public enum GameActivity { LOBBY, IN_GAME, AFK }

	public static void initialize() {
		Console.info("Initializing Hypixel");
		if (hypixel == null) hypixel = new Hypixel();
		hypixel.gameActivity = null;
		hypixel.activeGameType = null;
		
		EventsManager.registerEvents(hypixel.listener);
	}
	
	public static void uninitialize() {
		Console.info("Uninitializing Hypixel");
		HypixelProfile.uninitialize();
		HandlerList.unregisterAll(hypixel.listener);
		
		// disable all active 'games'
		hypixel.uninitializeActiveGames();
	}
	
	static {
		try {
			enabledField = ReflectionUtil.findField(AbstractGame.class, "enabled");
		} catch(NoSuchFieldException ignore) {/* cant do anything about it */}
	}

	private Hypixel() {
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
		Console.debug("Uninitializing active game");
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
		Console.debug("Changing gamemode to: ", type);
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
//		return GameActivity.IN_GAME;
		return gameActivity;
	}
	
	public static HypixelProfile getPlayer() {
		return HypixelProfile.get(Minecraft.getMinecraft().thePlayer.getGameProfile());
	}
	
	public List<HypixelProfile> getPartyMembers() {
		return partyMembers;
	}
	
	public UUID getKey() {
		return apiKey;
	}
	
	private void setKey(@NotNull UUID key, boolean updateFile) {
		if(this.apiKey != null && this.apiKey.equals(key)) return;
		this.apiKey = key;
		API = new HypixelAPI(key);
		
		if(updateFile) {
			pixelstats.getSettings().put("api-key", key.toString());
			pixelstats.getSettings().save();
			Console.debug("A new API key was set.");
		}
	}
	
	private boolean awaitingNewKey = false;
	public synchronized void requestNewKey(Runnable callback) {
		if(!awaitingNewKey) {
			awaitingNewKey = true;
			Console.debug("Requesting a new API key...");
			EventsManager.registerEvents(new Listener() {
				int tick = 0;
				@EventHandler
				public void onTick(TickEvent.ClientTick e) {
					// unregister listener after 5 seconds of inactivity
					if(tick > 100) {
						HandlerList.unregisterAll(this);
						awaitingNewKey = false;
					}
					tick++; 
				}

				@EventHandler
				public void onMessageReceive(MessageReceiveEvent e) {
					Matcher matcher = KEY_PATTERN.matcher(ChatColor.stripcolor(e.getMessage().getUnformattedText()));
					if (matcher.matches()) {
						Console.debug("New key captured. Replacing current key.");
						setKey(UUID.fromString(matcher.group(1)), true);
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
	
	private static final Pattern KEY_PATTERN = Pattern.compile("^Your new API key is ([a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8})$");
	
	private static final Pattern PARTY_MEMBERS = Pattern.compile("(?<=: | . )(?:\\[\\w+\\+*\\] )?(\\w{1,16})(?= .(?: |$))", Pattern.CASE_INSENSITIVE);
	private static final Pattern YOULL_BE_PARTYING_WITH = Pattern.compile("(?<=: |, |, and )(?:\\[\\w+\\+*\\] )?(\\w{1,16})(?=[,\\.!]|$| and)", Pattern.CASE_INSENSITIVE);
	private static final Pattern PARTY_JOIN_LEADER = Pattern.compile("^You have joined (?:\\[\\w+\\+*\\] )?(\\w{1,16})'s party!$", Pattern.CASE_INSENSITIVE);
	private static final Pattern PARTY_DISBAND = Pattern.compile("^(?:\\[\\w+\\+*\\] )?(\\w{1,16}) has disbanded the party!$", Pattern.CASE_INSENSITIVE);
	private static final Pattern PARTY_JOIN_PATTERN = Pattern.compile("^(?:\\[\\w+\\+*\\] )?(\\w{1,16}) joined the party\\.$", Pattern.CASE_INSENSITIVE);
	private static final Pattern PARTY_LEAVE_PATTERN = Pattern.compile("^(?:\\[\\w+\\+*\\] )?(\\w{1,16}) (?:has left the party|was removed from the party because they disconnected|has been removed from the party)\\.?$", Pattern.CASE_INSENSITIVE);
	
	private Listener listener = new Listener() {

		/*
		 * When the player joins a new world, wait until the player and the world instance are both not null
		 * send a command "/locraw" to get the location of the player, assign the game-activity and game-type of the player
		 */
		@EventHandler
		public void onWorldLoad(WorldLoadEvent e) {
			Console.warning("WorldLoadEvent triggered");
			EventsManager.registerEvents(new Listener() {
				int tick = 0;
				boolean executedCommand = false;
				@EventHandler
				public void onTick(TickEvent.ClientTick e) {
					if(!executedCommand && Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null) {
						executedCommand = true;
						HandlerList.unregisterAll(this);
						
						EventsManager.registerEvents(new Listener() {
							int tick = 0;
							@EventHandler
							public void onTick(TickEvent.ClientTick e) {
								// unregister listener after 5 seconds of inactivity
								if(tick >= 100) {
									HandlerList.unregisterAll(this);
									Console.debug("Unregistering TickEvent/MessageReceiveEvent; 5 second timeout. Lobby message was not found.");
								}
								tick++; 
							}
							
							@EventHandler
							public void onMessageReceiveEvent(final MessageReceiveEvent e) {
								if(e.getMessage().getUnformattedText().matches("^\\{.+?\\}$")) {
									try {
										JsonObject object = PARSER.parse(e.getMessage().getUnformattedText()).getAsJsonObject();
										if(object.has("server")) {
											Console.debug("Player location found");
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
						Console.debug("Trying to locate player...");
						return;
					}
					// unregister listener after 5 seconds of inactivity
					if(tick > 100) {
						HandlerList.unregisterAll(this);
						Console.debug("Unregistering TickEvent; 5 second timeout. Player & World is null.");
					}
					tick++; 
				}
			});
		}
		
		@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
		public void onMessageReceive(MessageReceiveEvent e) {
			// message is not supposed to be edited here, so run it asynchronously so that rest of code can run smoothly
			Multithreading.runAsync(() -> {
				String strippedMessage = ChatColor.stripcolor(e.getMessage().getUnformattedText());
				
				Matcher matcher = KEY_PATTERN.matcher(ChatColor.stripcolor(e.getMessage().getUnformattedText()));
				if (matcher.matches()) {
					Console.debug("New key captured. Replacing current key.");
					setKey(UUID.fromString(matcher.group(1)), true);
				}
				
				if(strippedMessage.equalsIgnoreCase("You left the party.")) {
					DebugUtil.debug("you left the party");
					partyMembers.clear();
					return;
				}
				
				if(strippedMessage.equalsIgnoreCase("The party was disbanded because all invites expired and the party was empty")) {
					DebugUtil.debug("&cparty was disbanded");
					partyMembers.clear();
					return;
				}
				
				Matcher m1 = PARTY_LEAVE_PATTERN.matcher(strippedMessage);
				if(m1.matches()) {
					DebugUtil.debug(m1.group(1)+" left the party");
					partyMembers.stream().filter(member -> member.getName().equalsIgnoreCase(m1.group(1))).forEach(partyMembers::remove);
					return;
				}
				
				Matcher m2 = PARTY_JOIN_PATTERN.matcher(strippedMessage);
				if(m2.matches()) {
					DebugUtil.debug(m2.group(1)+" joined the party");
					partyMembers.add(HypixelProfile.get(m2.group(1)));
					return;
				}
				
				Matcher m3 = PARTY_DISBAND.matcher(strippedMessage);
				if(m3.matches()) {
					DebugUtil.debug("party was disbanded");
					partyMembers.clear();
					return;
				}
				
				Matcher m4 = PARTY_JOIN_LEADER.matcher(strippedMessage);
				if(m4.matches()) {
					partyMembers.clear();
					partyMembers.add(HypixelProfile.get(Minecraft.getMinecraft().thePlayer.getName()));
					DebugUtil.debug("you joined the party lead by "+m4.group(1));
					partyMembers.add(HypixelProfile.get(m4.group(1)));
					return;
				}
				
				if(strippedMessage.toLowerCase().startsWith("you'll be partying with: ")) {
					Matcher m5 = YOULL_BE_PARTYING_WITH.matcher(strippedMessage);
					while (m5.find()) 
						DebugUtil.debug("you joined the party with "+m5.group(1));
						partyMembers.add(HypixelProfile.get(m5.group(1)));
					return;
				}
				
				if(strippedMessage.toLowerCase().startsWith("party leader:") || strippedMessage.toLowerCase().startsWith("party moderators:") || strippedMessage.toLowerCase().startsWith("party members:")) {
					// if the leader is listed, means that the party list is being shown, therefore party members should be reset
					if(strippedMessage.toLowerCase().startsWith("party leader:")) partyMembers.clear();
					
					Matcher m6 = PARTY_MEMBERS.matcher(strippedMessage);
					while (m6.find()) 
						DebugUtil.debug("you joined the party with "+m6.group(1));
						partyMembers.add(HypixelProfile.get(m6.group(1)));
					return;
				}
				
			});
		}
        
        @EventHandler
        public void onKeyPress(KeyPressEvent e) {
        	if(Keyboard.isKeyDown(Keyboard.KEY_F) && e.isCtrlDown()) {
        		Minecraft.getMinecraft().displayGuiScreen(SearchOverlay.getOverlay());
    		}
        }

	};

}
