package com.rmjtromp.pixelstats.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.rmjtromp.pixelstats.core.Hypixel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.server.MinecraftServer;

public final class HypixelProfile extends AbstractPlayer implements Comparable<HypixelProfile> {

	private static final List<HypixelProfile> profiles = new ArrayList<>();
	private static final JsonParser parser = new JsonParser();

	private enum State { UNLOADED, LOADING, LOADED }
	public enum Rank { ADMIN, MODERATOR, HELPER, YOUTUBER, SUPERSTAR, MVP_PLUS, MVP, VIP_PLUS, VIP, NON }
	private static final List<Rank> RANKS = Arrays.asList(Rank.values());
	
	private BedwarsStats bedwars;
	private EntityOtherPlayerMP entity;
	private GUI profileGUI;
	private long expiration;
	private boolean nick = false;
	private State state = State.UNLOADED;
	private Rank rank = Rank.NON;
	
	private String prefix = ChatColor.GRAY;
	
	public static HypixelProfile get(GameProfile gameProfile) {
		return get(gameProfile, null);
	}
	
	public static HypixelProfile get(GameProfile gameProfile, Consumer<HypixelProfile> update) {
		if(gameProfile == null) return null;
		List<HypixelProfile> res = profiles.stream().filter(p -> p != null && p.getGameProfile().equals(gameProfile)).collect(Collectors.toList());
		if(!res.isEmpty()) {
			HypixelProfile profile = res.get(0);
			if(System.currentTimeMillis() > profile.expiration) profile.updatePlayerAsync(update);
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
			if(System.currentTimeMillis() > profile.expiration) profile.updatePlayerAsync(update);
			return profile;
		} else return get(MinecraftServer.getServer().getPlayerProfileCache().getGameProfileForUsername(username), update);
	}
	
	public static void uninitialize() {
		BedwarsStats.QUEUE.clearQueue();
	}
	
	private HypixelProfile(GameProfile profile) {
		super(profile);
		profiles.add(this);
		expiration = System.currentTimeMillis() + 30000; // 30 seconds
		
		// initialize defaults
		this.entity = new EntityOtherPlayerMP(Minecraft.getMinecraft().theWorld, getGameProfile());
		this.bedwars = new BedwarsStats(this, null);
		this.profileGUI = new GUI(this);
	}
	
	public boolean isNick() {
		return nick;
	}
	
	private void updatePlayerAsync(Consumer<HypixelProfile> action) {
		if(!state.equals(State.LOADING) && !isNick()) {
			if(Hypixel.getInstance().getAPI() != null) {
				state = State.LOADING;
				Hypixel.debug("&eRequesting "+getName()+"'s profile.");
				Hypixel.getInstance().getAPI().getPlayerByUuid(getUniqueId()).whenCompleteAsync((player, e) -> {
					state = State.LOADED;
					if(e != null) e.printStackTrace();
					else {
						expiration = System.currentTimeMillis() + 300000; // 5 minutes
						JsonObject playerObject = player.getPlayer();
						if(playerObject != null) {
							
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
						} else {
							this.nick = true;
							this.bedwars = null;
						}
						action.accept(this);
					}
				});
			} else Hypixel.getInstance().requestNewKey(() -> updatePlayerAsync(action));
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
		return prefix != null && !prefix.isEmpty() ? prefix + " " + getName() : ChatColor.GRAY+getName();
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
	
	public static final class BedwarsStats {
		
		private final HypixelProfile profile;
		private long winstreak = 0, finalKills = 0, finalDeaths = 0, level = 1, wins = 0, losses = 0, gamesPlayed = 0, bedsBroken = 0, bedsLost = 0;
		private boolean sniper = false;
		private int reports = 0;
		
		private BedwarsStats(HypixelProfile parent, JsonObject playerObject) {
			profile = parent;
			if(playerObject != null) {
				JsonObject bedwarsObject = playerObject.get("stats").getAsJsonObject().get("Bedwars").getAsJsonObject();
				
				this.winstreak = bedwarsObject.has("winstreak") ? bedwarsObject.get("winstreak").getAsLong() : 0;
				this.finalKills = bedwarsObject.has("final_kills_bedwars") ? bedwarsObject.get("final_kills_bedwars").getAsLong() : 0;
				this.finalDeaths = bedwarsObject.has("final_deaths_bedwars") ? bedwarsObject.get("final_deaths_bedwars").getAsLong() : 0;
				this.wins = bedwarsObject.has("wins_bedwars") ? bedwarsObject.get("wins_bedwars").getAsLong() : 0;
				this.losses = bedwarsObject.has("losses_bedwars") ? bedwarsObject.get("losses_bedwars").getAsLong() : 0;
				this.gamesPlayed = bedwarsObject.has("games_played_bedwars") ? bedwarsObject.get("games_played_bedwars").getAsLong() : 0;
				this.bedsBroken = bedwarsObject.has("beds_broken_bedwars") ? bedwarsObject.get("beds_broken_bedwars").getAsLong() : 0;
				this.bedsLost = bedwarsObject.has("beds_lost_bedwars") ? bedwarsObject.get("beds_lost_bedwars").getAsLong() : 0;
				this.level = playerObject.has("achievements") && playerObject.get("achievements").getAsJsonObject().has("bedwars_level") ? playerObject.get("achievements").getAsJsonObject().get("bedwars_level").getAsLong() : 1;
			}
		}
		
		public long getWinstreak() {
			return winstreak;
		}
		
		public double getFKDR() {
			return finalDeaths != 0 ? Math.round((double) finalKills / (double) finalDeaths * 100.0D) / 100.0D: finalKills;
		}
		
		public long getFinalKills() {
			return finalKills;
		}
		
		public long getFinalDeaths() {
			return finalDeaths;
		}
		
		public long getLevel() {
			return level;
		}
		
		public String getLevelString() {
			String[] l = Long.toString(level).split("");
			long level = getLevel();
			if(level < 100) return ChatColor.colorEncode(String.format("&7[%s%s]", level, '\u272B'));
			else if(level < 200) return ChatColor.colorEncode(String.format("&f[%s%s]", level, '\u272B'));
			else if(level < 300) return ChatColor.colorEncode(String.format("&6[%s%s]", level, '\u272B'));
			else if(level < 400) return ChatColor.colorEncode(String.format("&b[%s%s]", level, '\u272B'));
			else if(level < 500) return ChatColor.colorEncode(String.format("&2[%s%s]", level, '\u272B'));
			else if(level < 600) return ChatColor.colorEncode(String.format("&3[%s%s]", level, '\u272B'));
			else if(level < 700) return ChatColor.colorEncode(String.format("&4[%s%s]", level, '\u272B'));
			else if(level < 800) return ChatColor.colorEncode(String.format("&d[%s%s]", level, '\u272B'));
			else if(level < 900) return ChatColor.colorEncode(String.format("&9[%s%s]", level, '\u272B'));
			else if(level < 1000) return ChatColor.colorEncode(String.format("&5[%s%s]", level, '\u272B'));
			else if(level < 1100) return ChatColor.colorEncode(String.format("&c[&6%s&e%s&a%s&b%s&d%s&5]", l[0], l[1], l[2], l[3], '\u272A'));
			else if(level < 1200) return ChatColor.colorEncode(String.format("&7[&f%s&7%s]", level, '\u272A'));
			else if(level < 1300) return ChatColor.colorEncode(String.format("&7[&e%s&6%s&7]", level, '\u272A'));
			else if(level < 1400) return ChatColor.colorEncode(String.format("&7[&b%s&3%s&7]", level, '\u272A'));
			else if(level < 1500) return ChatColor.colorEncode(String.format("&7[&a%s&2%s&7]", level, '\u272A'));
			else if(level < 1600) return ChatColor.colorEncode(String.format("&7[&3%s&9%s&7]", level, '\u272A'));
			else if(level < 1700) return ChatColor.colorEncode(String.format("&7[&c%s&4%s&7]", level, '\u272A'));
			else if(level < 1800) return ChatColor.colorEncode(String.format("&7[&d%s&5%s&7]", level, '\u272A'));
			else if(level < 1900) return ChatColor.colorEncode(String.format("&7[&9%s&1%s&7]", level, '\u272A'));
			else if(level < 2000) return ChatColor.colorEncode(String.format("&7[&5%s&8%s&7]", level, '\u272A'));
			else if(level < 2100) return ChatColor.colorEncode(String.format("&8[&7%s&f%s&f%s&7%s&7%s&8]", l[0], l[1], l[2], l[3], '\u272A'));
			else if(level < 2200) return ChatColor.colorEncode(String.format("&f[%s&e%s%s&6%s%s]", l[0], l[1], l[2], l[3], '\u2740'));
			else if(level < 2300) return ChatColor.colorEncode(String.format("&6[%s&f%s%s&b%s&3%s]", l[0], l[1], l[2], l[3], '\u2740'));
			else if(level < 2400) return ChatColor.colorEncode(String.format("&5[%s&d%s%s&6%s&e%s]", l[0], l[1], l[2], l[3], '\u2740'));
			else if(level < 2500) return ChatColor.colorEncode(String.format("&b[%s&f%s%s&7%s%s&8]", l[0], l[1], l[2], l[3], '\u2740'));
			else if(level < 2600) return ChatColor.colorEncode(String.format("&f[%s&a%s%s&2%s%s]", l[0], l[1], l[2], l[3], '\u2740'));
			else if(level < 2700) return ChatColor.colorEncode(String.format("&4[%s&c%s%s&d%s%s&5]", l[0], l[1], l[2], l[3], '\u2740'));
			else if(level < 2800) return ChatColor.colorEncode(String.format("&e[%s&f%s%s&8%s%s]", l[0], l[1], l[2], l[3], '\u2740'));
			else if(level < 2900) return ChatColor.colorEncode(String.format("&a[%s&2%s%s&6%s%s&e]", l[0], l[1], l[2], l[3], '\u2740'));
			else if(level < 3000) return ChatColor.colorEncode(String.format("&b[%s&3%s%s&9%s%s&1]", l[0], l[1], l[2], l[3], '\u2740'));
			else if(level >= 3000 && level < 10000) return ChatColor.colorEncode(String.format("&e[%s&6%s%s&c%s%s&4]", l[0], l[1], l[2], l[3], '\u2740'));
			else return ChatColor.colorEncode(String.format("&7[%s%s]", level, '\u272B'));
		}
		
		public long getWins() {
			return wins;
		}
		
		public long getLosses() {
			return losses;
		}
		
		public int getWinRate() {
			return (int) Math.round((double) wins / (double) gamesPlayed * 100.0D);
		}
		
		public double getBBLR() {
			return bedsLost != 0 ? Math.round((double) bedsBroken / (double) bedsLost * 100.0D) / 100.0D : bedsBroken;
		}
		
		public long getIndex() {
			return (long)(getLevel() * getFKDR() * getFKDR());
		}

		private static final AsyncRunnable QUEUE = new AsyncRunnable("TagLoader", 3);
		private State tagState = State.UNLOADED;
		private void loadTags() {
			tagState = State.LOADING;
			QUEUE.addToQueue(() -> {
				try {
					HttpURLConnection connection = HTTPRequest.get("http://161.35.53.44:8080/?playerv5="+profile.getName());
					if(connection != null && connection.getResponseCode() == 200) {
						// Get Response
						InputStream is = connection.getInputStream();
						BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
						StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
						String line;
						while ((line = rd.readLine()) != null) {
							response.append(line);
							response.append('\r');
						}
						rd.close();
						
						tagState = State.LOADED;
						JsonObject res = parser.parse(response.toString()).getAsJsonObject();
						sniper = res.has("sniper") && res.get("sniper").getAsBoolean();
						reports = res.has("report") ? res.get("report").getAsInt() : 0;
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			});
		}
		
		public List<String> getTags() {
			if(tagState.equals(State.UNLOADED)) loadTags();
			
			List<String> tags = new ArrayList<>();
			if(sniper) tags.add(ChatColor.RED + "S");
			if(reports > 0) {
				if(reports > 2) tags.add(ChatColor.RED + "H");
				else tags.add(ChatColor.RED + "R");
			}
			return tags;
		}
		
		public String getIndexColor() {
			double index = getIndex();
			if(index < 500) return ChatColor.GRAY + '\u2589';
			else if(index < 1000) return ChatColor.WHITE + '\u2589';
			else if(index < 3000) return ChatColor.YELLOW + '\u2589';
			else if(index < 7500) return ChatColor.GOLD + '\u2589';
			else if(index < 15000) return ChatColor.RED + '\u2589';
			else if(index < 30000) return ChatColor.DARK_PURPLE + '\u2589';
			else if(index < 99999) return ChatColor.BLUE + '\u2589';
			else if(index < 500000) return ChatColor.AQUA + '\u2589';
			else if(index >= 500000) return ChatColor.DARK_GREEN + '\u2589';
			else return ChatColor.GRAY + '\u2589';
		}
		
	}
	
	public static final class GUI extends GuiScreen {
		
		private final HypixelProfile profile;
		
		public GUI(HypixelProfile profile) {
			this.profile = profile;
		}
		
		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			drawDefaultBackground();
			int x = this.width/2;
			int y = this.height/2;
			drawEntityOnScreen(x, y, 30, -45, 5, profile.getEntityPlayer());
			super.drawScreen(mouseX, mouseY, partialTicks);
		}
		
		private static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
	        GlStateManager.enableColorMaterial();
	        GlStateManager.pushMatrix();
	        GlStateManager.translate((float)posX, (float)posY, 50.0F);
	        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
	        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
	        float f = ent.renderYawOffset;
	        float f1 = ent.rotationYaw;
	        float f2 = ent.rotationPitch;
	        float f3 = ent.prevRotationYawHead;
	        float f4 = ent.rotationYawHead;
	        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
	        RenderHelper.enableStandardItemLighting();
	        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
	        GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
	        ent.renderYawOffset = (float)Math.atan((double)(mouseX / 40.0F)) * 20.0F;
	        ent.rotationYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
	        ent.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
	        ent.rotationYawHead = ent.rotationYaw;
	        ent.prevRotationYawHead = ent.rotationYaw;
	        GlStateManager.translate(0.0F, 0.0F, 0.0F);
	        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
	        rendermanager.setPlayerViewY(180.0F);
	        rendermanager.setRenderShadow(false);
	        rendermanager.renderEntityWithPosYaw(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
	        rendermanager.setRenderShadow(true);
	        ent.renderYawOffset = f;
	        ent.rotationYaw = f1;
	        ent.rotationPitch = f2;
	        ent.prevRotationYawHead = f3;
	        ent.rotationYawHead = f4;
	        GlStateManager.popMatrix();
	        RenderHelper.disableStandardItemLighting();
	        GlStateManager.disableRescaleNormal();
	        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
	        GlStateManager.disableTexture2D();
	        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	    }
		
	}
	
}
