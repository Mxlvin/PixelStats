package com.rmjtromp.pixelstats.core.utils.drawings;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;

public interface Colorable {

	Colorable setColor(@NotNull Color color);
	default Colorable setColor(int rgb) {
		return setColor(new Color(rgb, true));
	}
	
	Color getColor();

}
