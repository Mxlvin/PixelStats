package com.rmjtromp.pixelstats.core.gui;

import com.rmjtromp.pixelstats.core.gui.components.Screen;
import com.rmjtromp.pixelstats.core.gui.components.Text;
import com.rmjtromp.pixelstats.core.utils.ChatColor;

import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

public class Test extends Screen {

	public Test() {
		IChatComponent component = ChatColor.colorEncodeToComponent("&eHello World, &fthis is my test&c\n&aasdLol");
		ChatStyle style = new ChatStyle();
		style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatColor.colorEncodeToComponent("&9You're hovering over me ;)")));
		component.setChatStyle(style);
		Text text = new Text(component);
		text.setRainbow(true);
		addComponent(text);
	}
	
}
