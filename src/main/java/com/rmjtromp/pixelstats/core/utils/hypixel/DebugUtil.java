package com.rmjtromp.pixelstats.core.utils.hypixel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Strings;
import com.rmjtromp.pixelstats.core.EventsManager;
import com.rmjtromp.pixelstats.core.Hypixel;
import com.rmjtromp.pixelstats.core.KeybindManager;
import com.rmjtromp.pixelstats.core.events.KeyPressEvent;
import com.rmjtromp.pixelstats.core.events.TickEvent;
import com.rmjtromp.pixelstats.core.games.bedwars.BedWars;
import com.rmjtromp.pixelstats.core.utils.ChatColor;
import com.rmjtromp.pixelstats.core.utils.Console;
import com.rmjtromp.pixelstats.core.utils.events.EventHandler;
import com.rmjtromp.pixelstats.core.utils.events.Listener;
import com.rmjtromp.pixelstats.core.utils.hypixel.profile.HypixelProfile;
import com.rmjtromp.pixelstats.core.utils.hypixel.profile.bedwars.BedwarsStats;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.IChatComponent;

public class DebugUtil {

	private static DebugUtil util = null;
	private static List<String> debug = new ArrayList<>();
	private final FontRenderer fr;
	private static final KeyBinding debugKey = KeybindManager.registerKeybind("Toggle Debug", Keyboard.KEY_F4, "PixelStats");
	
	public static void init() {
		if(util == null) new DebugUtil();
	}
	
	private DebugUtil() {
		Console.log("Initializing DebugUtil");
		util = this;
		fr = Minecraft.getMinecraft().fontRendererObj;
		
		EventsManager.registerEvents(new Listener() {
			boolean toggle = false;
			
			@EventHandler
			public void onKeyPress(KeyPressEvent e) {
				if(debugKey.isPressed()) toggle = !toggle;
			}
			
			@EventHandler
			public void onRenderTick(TickEvent.RenderTick e) {
				if(toggle) {
					List<String> list = new ArrayList<>();
					Hypixel hypixel = Hypixel.getInstance();
					if(hypixel != null) {
						list.add("game:");
						list.add(String.format("  type: &f%s", hypixel.getActiveGameType() != null ? hypixel.getActiveGameType().toString() : "&cnull"));
						list.add(String.format("  activity: &f%s", hypixel.getActivity() != null ? hypixel.getActivity().toString() : "&cnull"));
						list.add("");
						
						list.add("pixelstats:");
						list.add(String.format("  cache-size: &f%s", HypixelProfile.getCacheSize()));
						list.add(String.format("  avg-response-time: &f%sms", BedWars.getAverageResponseTime()));
						list.add("");

						String color = ChatColor.WHITE;
						int rpm = HypixelProfile.getRequestsManager().getTotalRPM();
						if(rpm < 60) color = ChatColor.WHITE;
						else if(rpm < 100) color = ChatColor.YELLOW;
						else if(rpm < 110) color = ChatColor.GOLD;
						else if(rpm >= 110) color = ChatColor.RED;
						
						list.add("requests-per-minute:");
						list.add("  hypixel-api:");
						list.add(String.format("    total: %s%s", color, rpm));
						list.add(String.format("    successful: &f%s", HypixelProfile.getRequestsManager().getSuccessfulRPM()));
						list.add(String.format("    failed: &f%s", HypixelProfile.getRequestsManager().getFailedRPM()));
						list.add("  bw-stats:");
						list.add(String.format("    total: &f%s", BedwarsStats.getRequestsManager().getTotalRPM()));
						list.add(String.format("    successful: &f%s", BedwarsStats.getRequestsManager().getSuccessfulRPM()));
						list.add(String.format("    failed: &f%s", BedwarsStats.getRequestsManager().getFailedRPM()));
						list = list.stream().map(line -> ChatColor.colorEncode("&7"+line)).collect(Collectors.toList());
					}
					
					if(!debug.isEmpty()) {
						if(hypixel != null) list.add("");
						list.addAll(debug);
					}

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
		});
	}
	
	
	public static void clearDebug() {
		debug.clear();
	}
	
	public static void debug(IChatComponent component) {
		debug(component.getFormattedText());
	}
	
	public static void debug(String string) {
		debug.add(ChatColor.colorEncode(string));
		if(debug.size() > 10) debug = debug.subList(debug.size() - 10, debug.size());
	}
	
}
