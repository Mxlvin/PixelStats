package com.rmjtromp.pixelstats.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class ChatColor {
	
	private ChatColor() {
		throw new IllegalStateException("Utility class");
	}

	public static final String BLACK = '\u00A7' + "0";
	public static final String DARK_BLUE = '\u00A7' + "1";
	public static final String DARK_GREEN = '\u00A7' + "2";
	public static final String DARK_AQUA = '\u00A7' + "3";
	public static final String DARK_RED = '\u00A7' + "4";
	public static final String DARK_PURPLE = '\u00A7' + "5";
	public static final String GOLD = '\u00A7' + "6";
	public static final String GRAY = '\u00A7' + "7";
	public static final String DARK_GRAY = '\u00A7' + "8";
	public static final String BLUE = '\u00A7' + "9";
	public static final String GREEN = '\u00A7' + "a";
	public static final String AQUA = '\u00A7' + "b";
	public static final String RED = '\u00A7' + "c";
	public static final String LIGHT_PURPLE = '\u00A7' + "d";
	public static final String YELLOW = '\u00A7' + "e";
	public static final String WHITE = '\u00A7' + "f";
	public static final String BOLD = '\u00A7' + "l";
	public static final String STRIKE_THROUGH = '\u00A7' + "m";
	public static final String UNDERLINE = '\u00A7' + "n";
	public static final String ITALIC = '\u00A7' + "o";
	public static final String RESET = '\u00A7' + "r";

	public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
	    char[] b = textToTranslate.toCharArray();
	    for (int i = 0; i < b.length - 1; i++) {
	        if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i+1]) > -1) {
	            b[i] = '\u00A7';
	            b[i+1] = Character.toLowerCase(b[i+1]);
	        }
	    }
	    return new String(b);
	}

	public static String stripcolor(String input) {
		return Pattern.compile("(?i)" + '\u00A7' + "[0-9A-FK-OR]").matcher(input).replaceAll("");
	}

	public static IChatComponent colorEncodeToComponent(String input) {
		return new ChatComponentText(translateAlternateColorCodes('&', "&f"+input));
	}
	
	public static List<String> colorEncode(Collection<String> collection) {
		List<String> r = new ArrayList<>();
		collection.forEach(c -> r.add(colorEncode(c)));
		return r;
	}
	
	public static String colorEncode(String input) {
		return translateAlternateColorCodes('&', input);
	}
	
    public static String getLastColors(String input) {
        int length = input.length();

        // Search backwards from the end as it is faster
        for (int index = length - 1; index > -1; index--) {
            char section = input.charAt(index);
            if (section == '\u00A7' && index < length - 1) {
                char c = input.charAt(index + 1);
                if((c+"").matches("^[0-9a-fr]$")) {
                	return "" + '\u00A7' + c;
                }
            }
        }
        return "";
    }
	
	public static String fromString(String string) {
		if(string.equalsIgnoreCase("BLACK")) return ChatColor.BLACK;
		else if(string.equalsIgnoreCase("DARK_BLUE")) return ChatColor.DARK_BLUE;
		else if(string.equalsIgnoreCase("DARK_GREEN")) return ChatColor.DARK_GREEN;
		else if(string.equalsIgnoreCase("DARK_AQUA")) return ChatColor.DARK_AQUA;
		else if(string.equalsIgnoreCase("DARK_RED")) return ChatColor.DARK_RED;
		else if(string.equalsIgnoreCase("DARK_PURPLE")) return ChatColor.DARK_PURPLE;
		else if(string.equalsIgnoreCase("GOLD")) return ChatColor.GOLD;
		else if(string.equalsIgnoreCase("GRAY")) return ChatColor.GRAY;
		else if(string.equalsIgnoreCase("DARK_GRAY")) return ChatColor.DARK_GRAY;
		else if(string.equalsIgnoreCase("BLUE")) return ChatColor.BLUE;
		else if(string.equalsIgnoreCase("GREEN")) return ChatColor.GREEN;
		else if(string.equalsIgnoreCase("AQUA")) return ChatColor.AQUA;
		else if(string.equalsIgnoreCase("RED")) return ChatColor.RED;
		else if(string.equalsIgnoreCase("LIGHT_PURPLE")) return ChatColor.LIGHT_PURPLE;
		else if(string.equalsIgnoreCase("YELLOW")) return ChatColor.YELLOW;
		else if(string.equalsIgnoreCase("WHITE")) return ChatColor.WHITE;
		return "";
	}

}
