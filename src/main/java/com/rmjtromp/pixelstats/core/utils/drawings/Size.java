package com.rmjtromp.pixelstats.core.utils.drawings;

import org.jetbrains.annotations.NotNull;

public final class Size<T extends Number> {

	private final T width;
	private final T height;
	
	public Size(@NotNull T width, @NotNull T height) {
		this.width = width;
		this.height = height;
	}
	
	public T getWidth() {
		return width;
	}
	
	public T getHeight() {
		return height;
	}
	
	public Size<T> clone() {
		return new Size<>(width, height);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Size) {
			return width.equals(((Size<?>)obj).width) && height.equals(((Size<?>)obj).height);
		}
		return false;
	}
	
}
