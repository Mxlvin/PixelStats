package com.rmjtromp.pixelstats.core.utils.hypixel.profile.bedwars;

import com.rmjtromp.pixelstats.core.utils.ChatColor;
import com.rmjtromp.pixelstats.core.utils.Valuable;

public class BBLR implements Valuable<Double> {
	
	private double value;
	
	public BBLR(double arg) {
		this.value = arg;
	}

	@Override
	public Double getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		String _BBLR = Double.toString(value);
		if(value < 2) return ChatColor.GRAY + _BBLR;
		else if(value < 4) return ChatColor.WHITE + _BBLR;
		else if(value < 6) return ChatColor.YELLOW + _BBLR;
		else if(value < 8) return ChatColor.GOLD + _BBLR;
		else if(value < 12) return ChatColor.RED + _BBLR;
		else if(value < 15) return ChatColor.DARK_PURPLE + _BBLR;
		else if(value < 20) return ChatColor.BLUE + _BBLR;
		else if(value < 25) return ChatColor.AQUA + _BBLR;
		else if(value >= 25) return ChatColor.GREEN + _BBLR;
		else return ChatColor.GRAY + _BBLR;
	}

}
