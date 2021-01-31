package com.rmjtromp.pixelstats.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AntiSpam {
	
	private static final Pattern REPEATED_CHARACTERS = Pattern.compile("([\\w])\\1{3,}", Pattern.CASE_INSENSITIVE);
	private static final Pattern REPEATED_SYMBOLS = Pattern.compile("([^\\w])\\1{3,}");
	private static final Pattern REPEATED_WORDS = Pattern.compile("(\\b[^\\s]+\\b)(?:(?:\\s+)?\\1(?:\\s+)?)+");
	
	public static String replaceRepeatingCharacters(String string) {
		Matcher m1 = REPEATED_CHARACTERS.matcher(string);
		while(m1.find()) string = m1.replaceAll("$1");
		
		Matcher m2 = REPEATED_SYMBOLS.matcher(string);
		while(m2.find()) string = m2.replaceAll("$1$1");
		
		Matcher m3 = REPEATED_WORDS.matcher(string);
		while(m3.find()) string = m3.replaceAll("$1");
		return string;
	}

}
