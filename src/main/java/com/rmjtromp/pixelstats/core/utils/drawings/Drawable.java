package com.rmjtromp.pixelstats.core.utils.drawings;

import org.jetbrains.annotations.NotNull;

public interface Drawable<T extends Number> {

	void draw(@NotNull Point<T> point);
	
	default void draw(T x, T y) {
		draw(new Point<>(x, y));
	}

}
