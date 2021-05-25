package com.rmjtromp.pixelstats.core.utils.hypixel.profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.rmjtromp.pixelstats.PixelStats;
import com.rmjtromp.pixelstats.core.EventsManager;
import com.rmjtromp.pixelstats.core.Hypixel;
import com.rmjtromp.pixelstats.core.events.KeyPressEvent;
import com.rmjtromp.pixelstats.core.events.ProfileUpdateEvent;
import com.rmjtromp.pixelstats.core.utils.AbstractPlayer;
import com.rmjtromp.pixelstats.core.utils.ChatColor;
import com.rmjtromp.pixelstats.core.utils.Console;
import com.rmjtromp.pixelstats.core.utils.RequestsManager;
import com.rmjtromp.pixelstats.core.utils.RequestsManager.Request;
import com.rmjtromp.pixelstats.core.utils.RequestsManager.RequestResponse;
import com.rmjtromp.pixelstats.core.utils.RequestsManager.RequestStage;
import com.rmjtromp.pixelstats.core.utils.events.EventHandler;
import com.rmjtromp.pixelstats.core.utils.events.Listener;
import com.rmjtromp.pixelstats.core.utils.hypixel.profile.bedwars.BedwarsStats;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.server.MinecraftServer;

public final class HypixelProfile extends AbstractPlayer implements Comparable<HypixelProfile> {

	private static final PixelStats pixelstats = PixelStats.getInstance();
	private static final Hypixel hypixel = Hypixel.getInstance();
	private static final List<HypixelProfile> profiles = new ArrayList<>();

	private enum State { UNLOADED, LOADING, LOADED }
	public enum Rank { ADMIN, MODERATOR, HELPER, YOUTUBER, SUPERSTAR, MVP_PLUS, MVP, VIP_PLUS, VIP, NON }
	public enum Channel { ALL, GUILD, PARTY, OFFICER, SKYBLOCK_COOP };
	private static final List<Rank> RANKS = Arrays.asList(Rank.values());
	
	private BedwarsStats bedwars;
	private EntityOtherPlayerMP entity;
	private HypixelProfileGUI profileGUI;
	private long expiration;
	private boolean nick = false;
	private State state = State.UNLOADED;
	private Rank rank = Rank.NON;
	private Channel chatChannel = Channel.ALL;
	
	
	private String prefix = ChatColor.GRAY;
	
	public static HypixelProfile get(GameProfile gameProfile) {
		return get(gameProfile, null);
	}
	
	public static int getCacheSize() {
		return profiles.size();
	}
	
	public static List<HypixelProfile> getCachedProfiles() {
		return new ArrayList<>(profiles);
	}
	
	static {
		EventsManager.registerEvents(new Listener() {
			
			@EventHandler
			public void onKeyPress(KeyPressEvent e) {
				if(Keyboard.isKeyDown(Keyboard.KEY_DELETE) && e.isCtrlDown()) {
					profiles.removeAll(profiles.stream().filter(profile -> System.currentTimeMillis() > profile.getExpiration()).collect(Collectors.toList()));
				}
			}
			
		});
	}
	
	public static HypixelProfile get(GameProfile gameProfile, Consumer<HypixelProfile> update) {
		if(gameProfile == null) return null;
		List<HypixelProfile> res = profiles.stream().filter(p -> p != null && p.getGameProfile().equals(gameProfile)).collect(Collectors.toList());
		if(!res.isEmpty()) {
			HypixelProfile profile = res.get(0);
			if(System.currentTimeMillis() > profile.getExpiration()) profile.updatePlayerAsync(update);
			return profile;
		} else {
			HypixelProfile profile = new HypixelProfile(gameProfile);
			profile.updatePlayerAsync(update);
			return profile;
		}
	}
	
	public static HypixelProfile get(String username) {
		return get(username, null);
	}

	public static HypixelProfile get(String username, Consumer<HypixelProfile> update) {
		List<HypixelProfile> res = profiles.stream().filter(profile -> profile.getName().equalsIgnoreCase(username)).collect(Collectors.toList());
		if(!res.isEmpty()) {
			HypixelProfile profile = res.get(0);
			if(System.currentTimeMillis() > profile.getExpiration()) profile.updatePlayerAsync(update);
			return profile;
		} else return get(MinecraftServer.getServer().getPlayerProfileCache().getGameProfileForUsername(username), update);
	}
	
	public static void uninitialize() {
//		BedwarsStats.QUEUE.clearQueue();
	}
	
	private HypixelProfile(GameProfile profile) {
		super(profile);
		profiles.add(this);
		expiration = System.currentTimeMillis() + 30000; // 30 seconds
		
		// initialize defaults
		this.bedwars = new BedwarsStats(this, null);
		this.profileGUI = new HypixelProfileGUI(this);
	}
	
	public boolean isNick() {
		return nick;
	}
	
	private void load(JsonObject playerObject) {
		if(playerObject != null) {
			if(playerObject.has("channel")) {
				Channel channel = Channel.valueOf(playerObject.get("channel").getAsString());
				if(channel != null) this.chatChannel = channel;
			}
			
			String packageRank = playerObject.has("packageRank") ? playerObject.get("packageRank").getAsString() : null;
			String newPackageRank = playerObject.has("newPackageRank") ? playerObject.get("newPackageRank").getAsString() : null;
			String monthlyPackageRank = playerObject.has("monthlyPackageRank") ? playerObject.get("monthlyPackageRank").getAsString() : null;
			String rankPlusColor = playerObject.has("rankPlusColor") ? playerObject.get("rankPlusColor").getAsString() : null;
			String monthlyRankColor = playerObject.has("monthlyRankColor") ? playerObject.get("monthlyRankColor").getAsString() : null;
			String rank = playerObject.has("rank") ? playerObject.get("rank").getAsString() : null;
			String prefix = playerObject.has("prefix") ? playerObject.get("prefix").getAsString() : null;
			
			String finalPrefix = ChatColor.GRAY;
			
			if((packageRank != null && !packageRank.isEmpty()) || (newPackageRank != null && !newPackageRank.isEmpty())) {
				Rank r1 = packageRank != null && !packageRank.isEmpty() && Rank.valueOf(packageRank) != null ? Rank.valueOf(packageRank) : Rank.NON;
				Rank r2 = newPackageRank != null && !newPackageRank.isEmpty() && Rank.valueOf(newPackageRank) != null ? Rank.valueOf(newPackageRank) : Rank.NON;
				
				// set rank to the highest one
				this.rank = RANKS.indexOf(r1) <= RANKS.indexOf(r2) ? r1 : r2;
			}
			if(monthlyPackageRank != null && monthlyPackageRank.equalsIgnoreCase("SUPERSTAR")) this.rank = Rank.SUPERSTAR;
			if(rank != null && !rank.isEmpty()) this.rank = Rank.valueOf(rank) != null ? Rank.valueOf(rank) : this.rank;
			
			if(this.rank.equals(Rank.ADMIN)) finalPrefix = ChatColor.RED+"[ADMIN]";
			else if(this.rank.equals(Rank.MODERATOR)) finalPrefix = ChatColor.DARK_GREEN+"[MOD]";
			else if(this.rank.equals(Rank.HELPER)) finalPrefix = ChatColor.BLUE+"[HELPER]";
			else if(this.rank.equals(Rank.YOUTUBER)) finalPrefix = ChatColor.RED+"["+ChatColor.WHITE+"YOUTUBE"+ChatColor.RED+"]";
			else if(this.rank.equals(Rank.SUPERSTAR) || this.rank.equals(Rank.MVP_PLUS)) {
				String plusColor = rankPlusColor != null && !rankPlusColor.isEmpty() && ChatColor.fromString(rankPlusColor) != null ? ChatColor.fromString(rankPlusColor) : ChatColor.RED;
				if(this.rank.equals(Rank.SUPERSTAR)) {
					String color = monthlyRankColor != null && !monthlyRankColor.isEmpty() && ChatColor.fromString(monthlyRankColor) != null ? ChatColor.fromString(monthlyRankColor) : ChatColor.GOLD;
					finalPrefix = color+"[MVP"+plusColor+"++"+color+"]";
				} else finalPrefix = ChatColor.AQUA+"[MVP"+plusColor+"+"+ChatColor.AQUA+"]";
			}
			else if(this.rank.equals(Rank.MVP)) finalPrefix = ChatColor.AQUA+"[MVP]";
			else if(this.rank.equals(Rank.VIP_PLUS)) finalPrefix = ChatColor.GREEN+"[VIP"+ChatColor.GOLD+"+"+ChatColor.GREEN+"]";
			else if(this.rank.equals(Rank.VIP)) finalPrefix = ChatColor.GREEN+"[VIP]";
			else finalPrefix = ChatColor.GRAY;
			
			if(prefix != null && !prefix.isEmpty()) finalPrefix = prefix;
			
			this.prefix = finalPrefix;
			
			this.bedwars = new BedwarsStats(this, playerObject);
			
			EventsManager.callEvent(new ProfileUpdateEvent(this));
		} else {
			this.nick = true;
			this.bedwars = null;
		}
	}
	private static final RequestsManager requestsManager = new RequestsManager();
	
	public static RequestsManager getRequestsManager() {
		return requestsManager;
	}
	
	public Channel getChatChannel() {
		return chatChannel;
	}
	
	public boolean isInParty() {
		return Hypixel.getInstance().getPartyMembers().contains(this);
	}
	
	private void updatePlayerAsync(Consumer<HypixelProfile> action) {
		if(!state.equals(State.LOADING) && !isNick()) {
			if(Hypixel.getInstance().getAPI() != null) {
				Request request = requestsManager.createRequest();
				state = State.LOADING;
				Console.info("Requesting "+getName()+"'s profile.");
				request.setStage(RequestStage.REQUESTED);
				Hypixel.getInstance().getAPI().getPlayerByUuid(getUniqueId()).whenCompleteAsync((player, e) -> {
					state = State.LOADED;
					if(e != null) {
						request.setResponse(RequestResponse.FAILED);
						e.printStackTrace();
					} else {
						request.setResponse(RequestResponse.SUCCESSFUL);
						expiration = System.currentTimeMillis() + 300000; // 5 minutes
						load(player.getPlayer());
						action.accept(this);
					}
				});
			} else hypixel.requestNewKey(() -> updatePlayerAsync(action));
		}
	}
	
	public EntityOtherPlayerMP getEntityPlayer() {
		return entity;
	}
	
	public void openProfileGUI() {
		Minecraft.getMinecraft().displayGuiScreen(profileGUI);
	}
	
	public Rank getRank() {
		return rank;
	}
	
	public String getPrefix() {
		return prefix != null && !prefix.isEmpty() ? prefix : ChatColor.GRAY;
	}
	
	public boolean hasDisplayName() {
		return prefix != null && !prefix.isEmpty();
	}
	
	public String getDisplayName() {
		return ChatColor.stripcolor(getPrefix()).isEmpty() ? getPrefix()+getName() : getPrefix() + " " + getName();
	}
	
	public BedwarsStats getBedwars() {
		return bedwars;
	}
	
	@Override
	public String toString() {
		return "HypixelProfile{"+getName()+"}";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof HypixelProfile) {
			return getGameProfile().equals(((HypixelProfile) obj).getGameProfile());
		}
		return false;
	}

	@Override
	public int compareTo(HypixelProfile o) {
		return RANKS.indexOf(getRank()) - RANKS.indexOf(o.getRank());
		// TODO compare network level if ranks are equal
	}

	public long getExpiration() {
		return expiration;
	}
	
}
