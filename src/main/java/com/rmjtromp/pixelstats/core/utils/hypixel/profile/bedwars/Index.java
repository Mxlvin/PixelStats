package com.rmjtromp.pixelstats.core.utils.hypixel.profile.bedwars;

import com.rmjtromp.pixelstats.core.utils.ChatColor;
import com.rmjtromp.pixelstats.core.utils.Valuable;

public class Index implements Valuable<Long> {
	
	private long value;
	
	public Index(long l) {
		this.value = l;
	}

	@Override
	public Long getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		if(value < 500) return ChatColor.GRAY + '\u2589';
		else if(value < 1000) return ChatColor.WHITE + '\u2589';
		else if(value < 3000) return ChatColor.YELLOW + '\u2589';
		else if(value < 7500) return ChatColor.GOLD + '\u2589';
		else if(value < 15000) return ChatColor.RED + '\u2589';
		else if(value < 30000) return ChatColor.DARK_PURPLE + '\u2589';
		else if(value < 99999) return ChatColor.BLUE + '\u2589';
		else if(value < 500000) return ChatColor.AQUA + '\u2589';
		else if(value >= 500000) return ChatColor.DARK_GREEN + '\u2589';
		else return ChatColor.GRAY + '\u2589';
	}

}
