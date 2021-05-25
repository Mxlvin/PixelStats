package com.rmjtromp.pixelstats.core.utils.drawings;

import com.rmjtromp.pixelstats.core.utils.NumberUtils;

public final class Point<T extends Number> {

	private final T x;
	private final T y;
	
	public Point(T x, T y) {
		this.x = x;
		this.y = y;
	}
	
	public T getX() {
		return x;
	}
	
	public T getY() {
		return y;
	}
	
	@SuppressWarnings("unchecked")
	public Point<T> addX(T x) {
		return new Point<>((T) NumberUtils.addNumbers(this.x, x), y);
	}
	
	@SuppressWarnings("unchecked")
	public Point<T> addY(T y) {
		return new Point<>(x, (T) NumberUtils.addNumbers(this.y, y));
	}

	@SuppressWarnings("unchecked")
	public Point<T> add(T x, T y) {
		return new Point<>((T) NumberUtils.addNumbers(this.x, x), (T) NumberUtils.addNumbers(this.y, y));
	}
	
	public Point<T> setX(T x) {
		return new Point<>(x, y);
	}
	
	public Point<T> setY(T y) {
		return new Point<>(x, y);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Point) {
			return x.equals(((Point<?>)obj).x) && y.equals(((Point<?>)obj).y);
		}
		return false;
	}
	
}
