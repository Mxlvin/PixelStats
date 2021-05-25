package com.rmjtromp.pixelstats.core.utils;

import java.util.Random;

public class NumberUtils {
	
	private static final Random random = new Random();
	
	private NumberUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Returns value constrained by minimum and maximum value.<br>
	 * If value is less than minimum value, the minimum value will be returned<br>
	 * and vice-versa, otherwise the input value will be returned instead.
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	public static int constraintToRange(int value, int min, int max) {
		if(value < min) return min;
		else if(value > max) return max;
		return value;
	}
	
	/**
	 * Returns the difference between two numbers
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public static int getDistance(int arg0, int arg1) {
		int distance = arg0 - arg1;
		return distance >= 0 ? distance : distance * -1;
	}
	
	/**
	 * Returns the highest number
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public static int getHighestValue(int arg0, int arg1) {
		return Math.max(arg0, arg1);
	}
	
	/**
	 * Returns the lowest number
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public static int getLowestValue(int arg0, int arg1) {
		return Math.min(arg0, arg1);
	}

	/**
	 * Returns the difference between two numbers
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public static double getDistance(double arg0, double arg1) {
		double distance = arg0 - arg1;
		return distance >= 0 ? distance : distance * -1;
	}

	/**
	 * Returns the highest number
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public static double getHighestValue(double arg0, double arg1) {
		return Math.max(arg0, arg1);
	}

	/**
	 * Returns the lowest number
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public static double getLowestValue(double arg0, double arg1) {
		return Math.min(arg0, arg1);
	}

	/**
	 * Rounds to nearest half
	 * @param d
	 * @return
	 */
	static double roundToHalf(double d) {
	    return Math.round(d * 2.0D) / 2.0D;
	}

	/**
	 * Rounds to nearest 45
	 * @param f
	 * @return
	 */
	static float roundToNearest45Deg(float f) {
		return Math.round(f * 45.0F) / 45.0F;
	}

	/**
	 * Generated a random number
	 * @param min
	 * @param max
	 * @return
	 */
	public static int generateRandomNumber(int min, int max) {
		return random.nextInt(max-min) + min;
	}

	public static Number addNumbers(Number a, Number b) {
	    if(a instanceof Double || b instanceof Double) {
	        return a.doubleValue() + b.doubleValue();
	    } else if(a instanceof Float || b instanceof Float) {
	        return a.floatValue() + b.floatValue();
	    } else if(a instanceof Long || b instanceof Long) {
	        return a.longValue() + b.longValue();
	    } else {
	        return a.intValue() + b.intValue();
	    }
	}
	
}
