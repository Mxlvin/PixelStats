package com.rmjtromp.pixelstats.core.utils.hypixel;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Strings;
import com.rmjtromp.pixelstats.core.EventsManager;
import com.rmjtromp.pixelstats.core.Hypixel;
import com.rmjtromp.pixelstats.core.KeybindManager;
import com.rmjtromp.pixelstats.core.events.KeyPressEvent;
import com.rmjtromp.pixelstats.core.events.RenderTickEvent;
import com.rmjtromp.pixelstats.core.utils.ChatColor;
import com.rmjtromp.pixelstats.core.utils.ComponentUtils;
import com.rmjtromp.pixelstats.core.utils.events.EventHandler;
import com.rmjtromp.pixelstats.core.utils.events.Listener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.IChatComponent;

public class DebugUtil {

	private static DebugUtil util = null;
	private static List<String> debug = new ArrayList<>();
	private static final KeyBinding DEBUG_KEY = KeybindManager.registerKeybind("key.debug", Keyboard.KEY_F4, "key.categories.pixelstats");
	private final FontRenderer fr;
	
	public static void init() {
		if(util == null) new DebugUtil();
	}
	
	private DebugUtil() {
		util = this;
		fr = Minecraft.getMinecraft().fontRendererObj;
		
		EventsManager.registerEvents(new Listener() {
			boolean toggle = true;
			@EventHandler
			public void onKeyPress(KeyPressEvent e) {
				if(DEBUG_KEY.isPressed()) {
					toggle = !toggle;
					Minecraft.getMinecraft().thePlayer.addChatComponentMessage(ComponentUtils.fromString("&7Debug mode was " + (toggle ? "&aenabled" : "&cdisabled")+"&7."));
				}
			}
			
			@EventHandler
			public void onRenderTick(RenderTickEvent e) {
				if(toggle) {
					List<String> list = new ArrayList<>();
					Hypixel hypixel = Hypixel.getInstance();
					if(hypixel != null) {
						list.add(ChatColor.colorEncode("&7game type: &f"+(hypixel.getActiveGameType() != null ? hypixel.getActiveGameType().toString() : "&cnull")));
						list.add(ChatColor.colorEncode("&7game activity: &f"+(hypixel.getActivity() != null ? hypixel.getActivity() : "&cnull")));
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
