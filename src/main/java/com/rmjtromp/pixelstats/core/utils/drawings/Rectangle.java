package com.rmjtromp.pixelstats.core.utils.drawings;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;

public class Rectangle implements Drawable<Integer>, Sizeable<Integer>, Colorable {
	
	private Color color = Color.WHITE;
	private final Border<Integer> border = new Border<>(0, Color.LIGHT_GRAY);
	private Size<Integer> size = new Size<>(10, 10);
	// top-left, top-right, bottom-right, bottom-left
	private int[] radius = {0, 0, 0, 0};
	
	public Rectangle(@NotNull Color color) {
		setColor(color);
	}
	
	public Rectangle(@NotNull Size<Integer> size, @NotNull Color color) {
		setSize(size);
		setColor(color);
	}
	
	public Rectangle(@NotNull Size<Integer> size) {
		setSize(size);
	}
	
	public Color getColor() {
		return color;
	}
	
	public boolean hasBorder() {
		return border.getWidth() > 0 && border.getColor() != null;
	}
	
	public Border<Integer> getBorder() {
		return border;
	}

	@Override
	public Rectangle setSize(@NotNull Size<Integer> size) {
		if(size == null) throw new NullPointerException("Size can not be null");
		this.size = size;
		return this;
	}

	@Override
	public Rectangle setColor(@NotNull Color color) {
		if(color == null) throw new NullPointerException("Color can not be null");
		this.color = color;
		return this;
	}

	@Override
	public Size<Integer> getSize() {
		return size;
	}
	
	public Rectangle setRadius(int radius) {
		if(radius < 0) throw new IllegalArgumentException("Radius must be equal to or larger than zero");
		for(int i = 0; i < this.radius.length; i++) {
			this.radius[i] = radius;
		}
		return this;
	}
	
	public Rectangle setRadius(int topLeft, int topRight, int bottomRight, int bottomLeft) {
		int[] radius = {topLeft, topRight, bottomRight, bottomLeft};
		for(int i = 0; i < radius.length; i++) {
			if(radius[i] < 0) throw new IllegalArgumentException("Radius must be equal to or larger than zero");
		}
		this.radius = radius;
		return this;
	}
	
	public int[] getRadius() {
		int shortestLength = Math.min(getWidth()/2, getHeight()/2);
		return new int[]{
				Math.min(this.radius[0], shortestLength),
				Math.min(this.radius[1], shortestLength),
				Math.min(this.radius[2], shortestLength),
				Math.min(this.radius[3], shortestLength)
				};
	}
	
	public boolean isRounded() {
		for(int i = 0; i < radius.length; i++) {
			if(radius[i] > 0) return true;
		}
		return false;
	}

	@Override
	public void draw(@NotNull Point<Integer> point) {
		if(isRounded()) {
			GLDraw.drawRoundedRect(point, size, getRadius(), color);
			if(hasBorder()) {
				GLDraw.drawRect(point.add(getRadius()[0], -getBorder().getWidth()), new Size<>(size.getWidth() - getRadius()[0] - getRadius()[1], getBorder().getWidth()), getBorder().getColor());
				GLDraw.drawRect(point.add(size.getWidth(), getRadius()[1]), new Size<>(getBorder().getWidth(), size.getHeight() - getRadius()[1] - getRadius()[2]), getBorder().getColor());
				GLDraw.drawRect(point.add(-getBorder().getWidth(), getRadius()[0]), new Size<>(getBorder().getWidth(), size.getHeight() - getRadius()[0] - getRadius()[3]), getBorder().getColor());
				GLDraw.drawRect(point.add(getRadius()[3], size.getHeight()), new Size<>(size.getWidth() - getRadius()[3] - getRadius()[2], getBorder().getWidth()), getBorder().getColor());
				
				if(getRadius()[0] != 0) GLDraw.drawHollowArc(point.add(getRadius()[0], getRadius()[0]), getRadius()[0], getBorder().getWidth(), -180f, 90f, getBorder().getColor());
				else GLDraw.drawRect(point.add(-getBorder().getWidth(), -getBorder().getWidth()), new Size<>(getBorder().getWidth(), getBorder().getWidth()), getBorder().getColor());
				
				if(getRadius()[1] != 0) GLDraw.drawHollowArc(point.addX(size.getWidth()).add(-getRadius()[1], getRadius()[1]), getRadius()[1], getBorder().getWidth(), -90f, 90f, getBorder().getColor());
				else GLDraw.drawRect(point.add(size.getWidth(), -getBorder().getWidth()), new Size<>(getBorder().getWidth(), getBorder().getWidth()), getBorder().getColor());
				
				if(getRadius()[2] != 0) GLDraw.drawHollowArc(point.add(size.getWidth(), size.getHeight()).add(-getRadius()[2], -getRadius()[2]), getRadius()[2], getBorder().getWidth(), 0f, 90f, getBorder().getColor());
				else GLDraw.drawRect(point.add(size.getWidth(), size.getHeight()), new Size<>(getBorder().getWidth(), getBorder().getWidth()), getBorder().getColor());
				
				if(getRadius()[3] != 0) GLDraw.drawHollowArc(point.addY(size.getHeight()).add(getRadius()[3], -getRadius()[3]), getRadius()[3], getBorder().getWidth(), 90f, 90f, getBorder().getColor());
				else GLDraw.drawRect(point.add(-getBorder().getWidth(), size.getHeight()), new Size<>(getBorder().getWidth(), getBorder().getWidth()), getBorder().getColor());
			}
		} else {
			GLDraw.drawRect(point, size, color);
			if(hasBorder()) {
				Point<Integer> topLeft = new Point<>(point.getX() - getBorder().getWidth(), point.getY() - getBorder().getWidth());
				Point<Integer> topRight = new Point<>(point.getX() + size.getWidth() + getBorder().getWidth(), point.getY() - getBorder().getWidth());
				Point<Integer> bottomLeft = new Point<>(point.getX() - getBorder().getWidth(), point.getY() + size.getHeight() + getBorder().getWidth());
				Point<Integer> bottomRight = new Point<>(point.getX() + size.getWidth() + getBorder().getWidth(), point.getY() + size.getHeight() + getBorder().getWidth());
				
				// draw line top-left to top-right
				GLDraw.draw(topLeft, topRight.addY(getBorder().getWidth()), getBorder().getColor());
				GLDraw.draw(topLeft, bottomLeft.addX(getBorder().getWidth()), getBorder().getColor());
				GLDraw.draw(bottomLeft, bottomRight.addY(-getBorder().getWidth()), getBorder().getColor());
				GLDraw.draw(topRight, bottomRight.addX(-getBorder().getWidth()), getBorder().getColor());
			}
		}
	}

}
