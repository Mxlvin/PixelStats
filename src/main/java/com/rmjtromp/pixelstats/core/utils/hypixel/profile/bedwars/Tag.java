package com.rmjtromp.pixelstats.core.utils.hypixel.profile.bedwars;

import com.rmjtromp.pixelstats.core.utils.ChatColor;

public enum Tag {

	SNIPER(ChatColor.RED + "S"),
	RISKY(ChatColor.RED + "R"),
	HACKER(ChatColor.RED + "H"),
	NICK(ChatColor.DARK_RED + "N"),
	PARTY(ChatColor.BLUE + "P");
	
	private final String a;
	private Tag(String b) {
		a = ChatColor.colorEncode(b);
	}
	
	@Override
	public String toString() {
		return a;
	}
	
}
