package com.rmjtromp.pixelstats.core.gui.components;

import org.jetbrains.annotations.NotNull;

import com.rmjtromp.pixelstats.core.utils.drawings.Drawable;
import com.rmjtromp.pixelstats.core.utils.drawings.Point;
import com.rmjtromp.pixelstats.core.utils.drawings.Sizeable;

public abstract class Component implements Drawable<Integer>, Sizeable<Integer> {
	
	private Position position = Position.RELATIVE;
	private Display display = Display.BLOCK;
	
	public boolean handleMouse(Point<Integer> point, Action.MouseAction action, int mouseButton) {
		return false;
	}
	
	public void setPosition(@NotNull Position position) {
		this.position = position;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public void setDisplay(@NotNull Display display) {
		this.display = display;
	}
	
	public Display getDisplay() {
		return display;
	}
	
//	default boolean

}
