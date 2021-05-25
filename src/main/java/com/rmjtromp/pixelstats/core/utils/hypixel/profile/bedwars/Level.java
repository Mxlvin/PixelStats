package com.rmjtromp.pixelstats.core.utils.hypixel.profile.bedwars;

import com.rmjtromp.pixelstats.core.utils.ChatColor;
import com.rmjtromp.pixelstats.core.utils.Valuable;

public class Level implements Valuable<Integer> {
	
	private int value;
	
	public Level(int arg) {
		this.value = arg;
	}

	@Override
	public Integer getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		String[] l = Long.toString(value).split("");
		if(value < 100) return ChatColor.colorEncode(String.format("&7[%s%s]", value, '\u272B'));
		else if(value < 200) return ChatColor.colorEncode(String.format("&f[%s%s]", value, '\u272B'));
		else if(value < 300) return ChatColor.colorEncode(String.format("&6[%s%s]", value, '\u272B'));
		else if(value < 400) return ChatColor.colorEncode(String.format("&b[%s%s]", value, '\u272B'));
		else if(value < 500) return ChatColor.colorEncode(String.format("&2[%s%s]", value, '\u272B'));
		else if(value < 600) return ChatColor.colorEncode(String.format("&3[%s%s]", value, '\u272B'));
		else if(value < 700) return ChatColor.colorEncode(String.format("&4[%s%s]", value, '\u272B'));
		else if(value < 800) return ChatColor.colorEncode(String.format("&d[%s%s]", value, '\u272B'));
		else if(value < 900) return ChatColor.colorEncode(String.format("&9[%s%s]", value, '\u272B'));
		else if(value < 1000) return ChatColor.colorEncode(String.format("&5[%s%s]", value, '\u272B'));
		else if(value < 1100) return ChatColor.colorEncode(String.format("&c[&6%s&e%s&a%s&b%s&d%s&5]", l[0], l[1], l[2], l[3], '\u272A'));
		else if(value < 1200) return ChatColor.colorEncode(String.format("&7[&f%s&7%s]", value, '\u272A'));
		else if(value < 1300) return ChatColor.colorEncode(String.format("&7[&e%s&6%s&7]", value, '\u272A'));
		else if(value < 1400) return ChatColor.colorEncode(String.format("&7[&b%s&3%s&7]", value, '\u272A'));
		else if(value < 1500) return ChatColor.colorEncode(String.format("&7[&a%s&2%s&7]", value, '\u272A'));
		else if(value < 1600) return ChatColor.colorEncode(String.format("&7[&3%s&9%s&7]", value, '\u272A'));
		else if(value < 1700) return ChatColor.colorEncode(String.format("&7[&c%s&4%s&7]", value, '\u272A'));
		else if(value < 1800) return ChatColor.colorEncode(String.format("&7[&d%s&5%s&7]", value, '\u272A'));
		else if(value < 1900) return ChatColor.colorEncode(String.format("&7[&9%s&1%s&7]", value, '\u272A'));
		else if(value < 2000) return ChatColor.colorEncode(String.format("&7[&5%s&8%s&7]", value, '\u272A'));
		else if(value < 2100) return ChatColor.colorEncode(String.format("&8[&7%s&f%s&f%s&7%s&7%s&8]", l[0], l[1], l[2], l[3], '\u272A'));
		else if(value < 2200) return ChatColor.colorEncode(String.format("&f[%s&e%s%s&6%s%s]", l[0], l[1], l[2], l[3], '\u2740'));
		else if(value < 2300) return ChatColor.colorEncode(String.format("&6[%s&f%s%s&b%s&3%s]", l[0], l[1], l[2], l[3], '\u2740'));
		else if(value < 2400) return ChatColor.colorEncode(String.format("&5[%s&d%s%s&6%s&e%s]", l[0], l[1], l[2], l[3], '\u2740'));
		else if(value < 2500) return ChatColor.colorEncode(String.format("&b[%s&f%s%s&7%s%s&8]", l[0], l[1], l[2], l[3], '\u2740'));
		else if(value < 2600) return ChatColor.colorEncode(String.format("&f[%s&a%s%s&2%s%s]", l[0], l[1], l[2], l[3], '\u2740'));
		else if(value < 2700) return ChatColor.colorEncode(String.format("&4[%s&c%s%s&d%s%s&5]", l[0], l[1], l[2], l[3], '\u2740'));
		else if(value < 2800) return ChatColor.colorEncode(String.format("&e[%s&f%s%s&8%s%s]", l[0], l[1], l[2], l[3], '\u2740'));
		else if(value < 2900) return ChatColor.colorEncode(String.format("&a[%s&2%s%s&6%s%s&e]", l[0], l[1], l[2], l[3], '\u2740'));
		else if(value < 3000) return ChatColor.colorEncode(String.format("&b[%s&3%s%s&9%s%s&1]", l[0], l[1], l[2], l[3], '\u2740'));
		else if(value >= 3000 && value < 10000) return ChatColor.colorEncode(String.format("&e[%s&6%s%s&c%s%s&4]", l[0], l[1], l[2], l[3], '\u2740'));
		else return ChatColor.colorEncode(String.format("&7[%s%s]", value, '\u272B'));
	}

}
