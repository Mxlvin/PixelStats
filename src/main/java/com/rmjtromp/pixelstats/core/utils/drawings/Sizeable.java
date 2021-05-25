package com.rmjtromp.pixelstats.core.utils.drawings;

import org.jetbrains.annotations.NotNull;

public interface Sizeable<T extends Number> {
	
	Sizeable<T> setSize(@NotNull Size<T> size);
	
	Size<T> getSize();

	default Sizeable<T> setSize(@NotNull T width, @NotNull T height) {
		return setSize(new Size<>(width, height));
	}

	default Sizeable<T> setWidth(@NotNull T width) {
		return setSize(new Size<>(width, getHeight()));
	}

	default Sizeable<T> setHeight(@NotNull T height) {
		return setSize(new Size<>(getWidth(), height));
	}
	
	default T getWidth() {
		return getSize().getWidth();
	}
	
	default T getHeight() {
		return getSize().getHeight();
	}

}
