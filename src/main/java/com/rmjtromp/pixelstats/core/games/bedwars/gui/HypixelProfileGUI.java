package com.rmjtromp.pixelstats.core.games.bedwars.gui;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.rmjtromp.pixelstats.core.EventsManager;
import com.rmjtromp.pixelstats.core.events.ProfileUpdateEvent;
import com.rmjtromp.pixelstats.core.utils.ChatColor;
import com.rmjtromp.pixelstats.core.utils.events.EventHandler;
import com.rmjtromp.pixelstats.core.utils.events.HandlerList;
import com.rmjtromp.pixelstats.core.utils.events.Listener;
import com.rmjtromp.pixelstats.core.utils.hypixel.profile.HypixelProfile;
import com.rmjtromp.pixelstats.core.utils.hypixel.profile.bedwars.BedwarsStats;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class HypixelProfileGUI extends GuiScreen {

	private final HypixelProfile profile;
    private final int fontHeight;
    private List<Column> columns = new ArrayList<>();
    private GuiButton closeButton;
    private Listener listener = new Listener() {
		
		@EventHandler
		public void onProfileUpdate(ProfileUpdateEvent e) {
			if(e.getProfile().equals(profile)) initColumns();
		}
		
	};
    private static FontRenderer fr = null;
    private static final NumberFormat numberFormat;
    
    static {
    	numberFormat = NumberFormat.getInstance();
    	numberFormat.setGroupingUsed(true);
    }
	
	public HypixelProfileGUI(HypixelProfile profile) {
		if(fr == null) fr = Minecraft.getMinecraft().fontRendererObj;
		this.profile = profile;
		fontHeight = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
	}
	
	private void initColumns() {
		columns.clear();
		
		if(!profile.isNick()) {
			BedwarsStats bw = profile.getBedwars();
			
			columns.add(
				new Column()
					.addEntry("&bFinal Kills", String.format("&f%s &7[%s Final Deaths]", formatNumber(bw.getFinalKills()), formatNumber(bw.getFinalDeaths())))
					.addEntry("&bWins", String.format("&f%s &7[%s Losses]", formatNumber(bw.getWins()), formatNumber(bw.getLosses())))
					.addEntry("&bBeds Broken", String.format("&f%s &7[%s Beds Lost]", formatNumber(bw.getBedsBroken()), formatNumber(bw.getBedsLost())))
					.addEntry("&bKills", String.format("&f%s &7[%s Deaths]", formatNumber(bw.getKills()), formatNumber(bw.getDeaths())))
			);
			
			columns.add(
				new Column()
					.addEntry("&bFKDR", bw.getFKDR().getValue())
					.addEntry("&bWLR", bw.getWLR().getValue())
					.addEntry("&bBBLR", bw.getBBLR().getValue())
					.addEntry("&bKDR", bw.getKDR())
			);
			
			columns.add(
				new Column()
					.addEntry("&bBedwars Star", bw.getLevel().getValue())
					.addEntry("&bWinstreak", bw.getWinstreak().getValue())
					.addEntry("&bGames Played", bw.getGamesPlayed())
					.addEntry("&bCoins", bw.getCoins())
			);
		}
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		this.buttonList.add(closeButton = new GuiButton(0, this.width / 2 - 100, this.height / 2 + 75, "Close"));
		
		EventsManager.registerEvents(listener);
		initColumns();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();

		int centerX = this.width / 2;
		int centerY = this.height / 2;
		int y = centerY - 75;
		try {
			int x = centerX;
			int width = 0;
			for(Column c : columns) width += c.getWidth();
			width += (columns.size() - 1) * Column.spacer;
			x -= width / 2;

			if(!profile.isNick()) {
				int nextFkdr = (int) Math.floor(profile.getBedwars().getFKDR().getValue()) + 1;
				
				long fkneeded = ((long) Math.ceil(profile.getBedwars().getFinalKills() / profile.getBedwars().getFKDR().getValue() * nextFkdr));
				long diff = fkneeded - profile.getBedwars().getFinalKills();
				
				fr.drawStringWithShadow(ChatColor.colorEncode(String.format("&7You need &b%s &7final kills to get to &b%s &7fkdr.", diff, nextFkdr)), x, y + 100 + fr.FONT_HEIGHT, -1);
				fr.drawStringWithShadow(ChatColor.colorEncode(String.format("%s %s&r", profile.getBedwars().getLevel(), profile.getDisplayName())), x, y - (fontHeight * 2), -1);
			}
			for(Column c : columns) {
				int _y = y;
				for(Entry<String, String> entry : c.entries) {
					fr.drawStringWithShadow(entry.getKey(), x, _y, -1);
					fr.drawStringWithShadow(entry.getValue(), x, _y + fontHeight, -1);
					_y += fontHeight * 3;
				}
				x += c.getWidth() + Column.spacer;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		
		if(button.equals(closeButton)) mc.thePlayer.closeScreen();
	}
	
	@Override
	public void onGuiClosed() {
		HandlerList.unregisterAll(listener);
		super.onGuiClosed();
	}
	
	private static String formatNumber(Number num) {
		try {
			return numberFormat.format(num);
		} catch(Exception e) {
			return num.toString();
		}
	}
	
	private static class Column {
		
		private List<Entry<String, String>> entries = new ArrayList<>();
		private int widestEntry = 0;
		private static int spacer = 10;
		
		private Column addEntry(String key, String value) {
			key = ChatColor.colorEncode(key) + ChatColor.RESET;
			value = ChatColor.colorEncode(value) + ChatColor.RESET;
			widestEntry = Math.max(widestEntry, Math.max(fr.getStringWidth(key), fr.getStringWidth(value)));
			entries.add(MyEntry.createEntry(key, value));
			return this;
		}
		
		private Column addEntry(String key, Number value) {
			return addEntry(key, formatNumber(value));
		}
		
		public int getWidth() {
			return widestEntry;
		}
		
	}
	
	private static class MyEntry<K, V> implements Entry<K, V> {
		
		private final K key;
		private V value;
		
		private MyEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}
		
		private static <K, V> Entry<K, V> createEntry(K key, V value) {
			return new MyEntry<K, V>(key, value);
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			this.value = value;
			return value;
		}
		
	}

}
