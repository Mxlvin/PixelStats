package com.rmjtromp.pixelstats.core.gui.components;

import org.jetbrains.annotations.NotNull;

public class Padding<T extends Number> {
	
	private T top, right, bottom, left;
	
	public Padding(@NotNull T padding) {
		this(padding, padding);
	}
	
	public Padding(@NotNull T topAndBottom, @NotNull T leftAndRight) {
		this(topAndBottom, leftAndRight, topAndBottom, leftAndRight);
	}
	
	public Padding(@NotNull T top, @NotNull T right, @NotNull T bottom, @NotNull T left) {
		setPadding(top, right, bottom, left);
	}
	
	public T getTop() {
		return top;
	}
	
	public T getRight() {
		return right;
	}
	
	public T getBottom() {
		return bottom;
	}
	
	public T getLeft() {
		return left;
	}
	
	public Padding<T> setPadding(@NotNull T top, @NotNull T right, @NotNull T bottom, @NotNull T left) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
		return this;
	}
	
	public Padding<T> setPadding(@NotNull T x, @NotNull T y) {
		return setPadding(x, y, x, y);
	}
	
	public Padding<T> setPadding(@NotNull T padding) {
		return setPadding(padding, padding);
	}
	
	public Padding<T> setTop(@NotNull T padding) {
		this.top = padding;
		return this;
	}
	
	public Padding<T> setRight(@NotNull T padding) {
		this.right = padding;
		return this;
	}
	
	public Padding<T> setBottom(@NotNull T padding) {
		this.bottom = padding;
		return this;
	}
	
	public Padding<T> setLeft(@NotNull T padding) {
		this.left = padding;
		return this;
	}

}
