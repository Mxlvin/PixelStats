package com.rmjtromp.pixelstats.core.utils.hypixel.profile.bedwars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rmjtromp.pixelstats.core.games.bedwars.BedWars;
import com.rmjtromp.pixelstats.core.utils.Console;
import com.rmjtromp.pixelstats.core.utils.HTTPRequest;
import com.rmjtromp.pixelstats.core.utils.Multithreading;
import com.rmjtromp.pixelstats.core.utils.RequestsManager;
import com.rmjtromp.pixelstats.core.utils.RequestsManager.Request;
import com.rmjtromp.pixelstats.core.utils.RequestsManager.RequestResponse;
import com.rmjtromp.pixelstats.core.utils.RequestsManager.RequestStage;
import com.rmjtromp.pixelstats.core.utils.hypixel.profile.HypixelProfile;
import com.rmjtromp.pixelstats.core.utils.hypixel.profile.HypixelProfile.Channel;

public final class BedwarsStats {

	private static final JsonParser parser = new JsonParser();
	
	private final HypixelProfile profile;
	private int winstreak = 0, level = 1;
	private long finalKills = 0, finalDeaths = 0, wins = 0, losses = 0, gamesPlayed = 0, bedsBroken = 0, bedsLost = 0, kills = 0, deaths = 0, coins = 0;
	private List<Tag> tags = new ArrayList<>();
	
	public BedwarsStats(HypixelProfile parent, JsonObject playerObject) {
		profile = parent;
		if(playerObject != null) {
			JsonObject bedwarsObject = playerObject.get("stats").getAsJsonObject().get("Bedwars").getAsJsonObject();
			
			this.winstreak = bedwarsObject.has("winstreak") ? bedwarsObject.get("winstreak").getAsInt() : 0;
			this.finalKills = bedwarsObject.has("final_kills_bedwars") ? bedwarsObject.get("final_kills_bedwars").getAsLong() : 0;
			this.finalDeaths = bedwarsObject.has("final_deaths_bedwars") ? bedwarsObject.get("final_deaths_bedwars").getAsLong() : 0;
			this.kills = bedwarsObject.has("kills_bedwars") ? bedwarsObject.get("kills_bedwars").getAsLong() : 0;
			this.deaths = bedwarsObject.has("deaths_bedwars") ? bedwarsObject.get("deaths_bedwars").getAsLong() : 0;
			this.wins = bedwarsObject.has("wins_bedwars") ? bedwarsObject.get("wins_bedwars").getAsLong() : 0;
			this.losses = bedwarsObject.has("losses_bedwars") ? bedwarsObject.get("losses_bedwars").getAsLong() : 0;
			this.gamesPlayed = bedwarsObject.has("games_played_bedwars") ? bedwarsObject.get("games_played_bedwars").getAsLong() : 0;
			this.bedsBroken = bedwarsObject.has("beds_broken_bedwars") ? bedwarsObject.get("beds_broken_bedwars").getAsLong() : 0;
			this.bedsLost = bedwarsObject.has("beds_lost_bedwars") ? bedwarsObject.get("beds_lost_bedwars").getAsLong() : 0;
			this.coins = bedwarsObject.has("coins") ? bedwarsObject.get("coins").getAsLong() : 0;
			this.level = playerObject.has("achievements") && playerObject.get("achievements").getAsJsonObject().has("bedwars_level") ? playerObject.get("achievements").getAsJsonObject().get("bedwars_level").getAsInt() : 1;
		}
	}
	
	private WinStreak _ws = null;
	public WinStreak getWinstreak() {
		if(_ws == null) _ws = new WinStreak(winstreak);
		return _ws;
	}
	
	private FKDR _fkdr = null;
	public FKDR getFKDR() {
		if(_fkdr == null) _fkdr = new FKDR(finalDeaths != 0 ? Math.round((double) finalKills / (double) finalDeaths * 100.0D) / 100.0D: finalKills);
		return _fkdr;
	}
	
	private WLR _wlr = null;
	public WLR getWLR() {
		if(_wlr == null) _wlr = new WLR(losses != 0 ? Math.round((double) wins / (double) losses * 100.0D) / 100.0D: wins);
		return _wlr;
	}
	
	public long getCoins() {
		return coins;
	}
	
	public long getFinalKills() {
		return finalKills;
	}
	
	public long getGamesPlayed() {
		return gamesPlayed;
	}
	
	public long getFinalDeaths() {
		return finalDeaths;
	}
	
	public long getKills() {
		return kills;
	}
	
	public long getDeaths() {
		return deaths;
	}
	
	private double kdr = -1;
	public double getKDR() {
		if(kdr == -1) kdr = deaths != 0 ? Math.round((double) kills / (double) deaths * 100.0D) / 100.0D: kills;
		return kdr;
	}
	
	private Level _level = null;
	public Level getLevel() {
		if(_level == null) _level = new Level(level);
		return _level;
	}
	
	public long getWins() {
		return wins;
	}
	
	public long getLosses() {
		return losses;
	}
	
	private BBLR _bblr = null;
	public BBLR getBBLR() {
		if(_bblr == null) _bblr = new BBLR(bedsLost != 0 ? Math.round((double) bedsBroken / (double) bedsLost * 100.0D) / 100.0D : bedsBroken);
		return _bblr;
	}
	
	public long getBedsBroken() {
		return bedsBroken;
	}
	
	public long getBedsLost() {
		return bedsLost;
	}
	
	private Index _index = null;
	public Index getIndex() {
		if(_index == null) _index = new Index((long)(getLevel().getValue() * Math.pow(getFKDR().getValue(), 2)));
		return _index;
	}

	private static final RequestsManager requestsManager = new RequestsManager();
	
	public static RequestsManager getRequestsManager() {
		return requestsManager;
	}
	
	private boolean tagsLoaded = false;
	private synchronized void loadTags() {
		if(!tagsLoaded) {
			tagsLoaded = true;
			Request request = requestsManager.createRequest();
			if(profile.getChatChannel().equals(Channel.PARTY)) tags.add(Tag.PARTY);
			Multithreading.runAsync(() -> {
				try {
					request.setStage(RequestStage.REQUESTED);
					HttpURLConnection connection = HTTPRequest.get(BedWars.getEndpointUrl() + ":8080/?playerv5=" +profile.getName());
					connection.setRequestProperty("Host", "db.dfg87dcbvse44.xyz");
					connection.setRequestProperty("User-Agent", "python-requests/2.24.0");
					connection.setDoInput(true);
					if(connection != null && connection.getResponseCode() == 200) {
						request.setResponse(RequestResponse.SUCCESSFUL);
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
						
						String r = response.toString();
						Console.debug(r);
						JsonObject res = parser.parse(r).getAsJsonObject();
						boolean sniper = res.has("sniper") &&  res.get("sniper").getAsBoolean();
						if(sniper) tags.add(Tag.SNIPER);
						
						int reports = res.has("report") ? res.get("report").getAsInt() : 0;
						if(reports > 0) {
							if(reports > 3) tags.add(Tag.HACKER);
							else tags.add(Tag.RISKY);
						}
					} else request.setResponse(RequestResponse.FAILED);
				} catch(IOException e) {
					request.setResponse(RequestResponse.FAILED);
					e.printStackTrace();
				}
			});
		}
	}
	
	public List<Tag> getTags() {
		if(!tagsLoaded) loadTags();
		return tags;
	}
	
}
