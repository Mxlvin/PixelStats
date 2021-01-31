package com.rmjtromp.pixelstats.core.utils;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public final class ComponentUtils {

	private ComponentUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static IChatComponent fromString(String arg0) {
		return new ChatComponentText(ChatColor.translateAlternateColorCodes('&', arg0));
	}
	
	public static IChatComponent join(String arg0, IChatComponent...arg1) {
		return join(arg0 != null && !arg0.isEmpty() ? fromString(arg0) : null, arg1);
	}
	
	public static IChatComponent join(IChatComponent arg0, IChatComponent...arg1) {
		IChatComponent a = new ChatComponentText("");
		if(arg1.length > 0) {
			for(int i = 0; i < arg1.length; i++) {
				if(arg0 != null && i > 0) a.appendSibling(arg0.createCopy());
				a.appendSibling(arg1[i]);
			}
		}
		return a;
	}
	
	@Deprecated
	public static IChatComponent join(IChatComponent...arg0) {
		IChatComponent a = new ChatComponentText("");
		for(IChatComponent arg : arg0) a.appendSibling(arg);
		return a;
	}
	
	public static IChatComponent join(String arg0, String...arg1) {
		return fromString(String.join(arg0, arg1));
	}
	
}
