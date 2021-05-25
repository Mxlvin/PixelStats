package com.rmjtromp.pixelstats.core.utils.hypixel.profile.bedwars;

import com.rmjtromp.pixelstats.core.utils.ChatColor;
import com.rmjtromp.pixelstats.core.utils.Valuable;

public class WinStreak implements Valuable<Integer> {
	
	private int value;
	
	public WinStreak(int arg) {
		this.value = arg;
	}

	@Override
	public Integer getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		String _ws = Integer.toString(value);
		if(value < 2) return ChatColor.GRAY + _ws;
		else if(value < 5) return ChatColor.WHITE + _ws;
		else if(value < 15) return ChatColor.YELLOW + _ws;
		else if(value < 30) return ChatColor.GOLD + _ws;
		else if(value < 75) return ChatColor.RED + _ws;
		else if(value < 100) return ChatColor.DARK_PURPLE + _ws;
		else if(value < 200) return ChatColor.BLUE + _ws;
		else if(value < 300) return ChatColor.AQUA + _ws;
		else if(value >= 300) return ChatColor.GREEN + _ws;
		else return ChatColor.GRAY + _ws;
	}

}
