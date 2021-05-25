package com.rmjtromp.pixelstats.core.utils.hypixel.profile.bedwars;

import com.rmjtromp.pixelstats.core.utils.ChatColor;
import com.rmjtromp.pixelstats.core.utils.Valuable;

public class FKDR implements Valuable<Double> {
	
	private double value;
	
	public FKDR(double arg) {
		this.value = arg;
	}

	@Override
	public Double getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		String _FKDR = Double.toString(value);
		if(value < 1) return ChatColor.GRAY + _FKDR;
		else if(value < 2) return ChatColor.WHITE + _FKDR;
		else if(value < 4) return ChatColor.YELLOW + _FKDR;
		else if(value < 7) return ChatColor.GOLD + _FKDR;
		else if(value < 10) return ChatColor.RED + _FKDR;
		else if(value < 15) return ChatColor.DARK_PURPLE + _FKDR;
		else if(value < 25) return ChatColor.BLUE + _FKDR;
		else if(value < 50) return ChatColor.AQUA + _FKDR;
		else if(value >= 50) return ChatColor.DARK_GREEN + _FKDR;
		else return ChatColor.GRAY + _FKDR;
	}

}
