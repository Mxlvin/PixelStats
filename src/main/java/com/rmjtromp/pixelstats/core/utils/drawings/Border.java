package com.rmjtromp.pixelstats.core.utils.drawings;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;

public class Border<T extends Number> {
	
	private T width;
	private Color color;
	
	public Border(@NotNull T width, @NotNull Color color) {
		setWidth(width);
		setColor(color);
	}
	
	public Border<T> setWidth(@NotNull T width) {
		this.width = width;
		return this;
	}
	
	public T getWidth() {
		return width;
	}
	
	public Border<T> setColor(@NotNull Color color) {
		this.color = color;
		return this;
	}
	
	public Color getColor() {
		return color;
	}

}
