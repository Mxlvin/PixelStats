package com.rmjtromp.pixelstats.core.utils.hypixel.profile.bedwars;

import com.rmjtromp.pixelstats.core.utils.ChatColor;
import com.rmjtromp.pixelstats.core.utils.Valuable;

public class WLR implements Valuable<Double> {
	
	private double value;
	
	public WLR(double arg) {
		this.value = arg;
	}

	@Override
	public Double getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		String _wlr = Double.toString(value);
		if(value < 1) return ChatColor.GRAY + _wlr;
		else if(value < 2) return ChatColor.WHITE + _wlr;
		else if(value < 4) return ChatColor.YELLOW + _wlr;
		else if(value < 8) return ChatColor.GOLD + _wlr;
		else if(value < 16) return ChatColor.RED + _wlr;
		else if(value < 32) return ChatColor.DARK_PURPLE + _wlr;
		else if(value < 64) return ChatColor.BLUE + _wlr;
		else if(value < 128) return ChatColor.AQUA + _wlr;
		else if(value >= 128) return ChatColor.GREEN + _wlr;
		else return ChatColor.GRAY + _wlr;
	}

}
