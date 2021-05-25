package com.rmjtromp.pixelstats.core.gui;

import java.awt.Color;

import com.google.common.base.Strings;
import com.rmjtromp.pixelstats.core.utils.drawings.Point;
import com.rmjtromp.pixelstats.core.utils.drawings.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class TextField extends GuiTextField {

	private final Rectangle searchBox;
	private final FontRenderer fr;
	private final Point<Integer> point;
	
	public TextField(int componentId, int x, int y, int width, int height) {
		super(componentId, Minecraft.getMinecraft().fontRendererObj, x + 4, y + (height - 8) / 2, width, height);
		point = new Point<>(x, y);
		fr = Minecraft.getMinecraft().fontRendererObj;
		searchBox = new Rectangle(new Color(Integer.MIN_VALUE, true));
		searchBox.setSize(width, height);
		searchBox.getBorder().setWidth(1);
		searchBox.getBorder().setColor(new Color(-6250336));
		
		super.setEnableBackgroundDrawing(false);
	}
	
	@Override
	public void setEnableBackgroundDrawing(boolean p_146185_1_) {}
	
	private String placeholder = null;
	
	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}
	
	public String getPlaceholder() {
		return placeholder;
	}
	
	private boolean enabled = true;
	@Override
	public void setEnabled(boolean p_146184_1_) {
		enabled = p_146184_1_;
		super.setEnabled(p_146184_1_);
	}
	
	@Override
	public void drawTextBox() {
        if(this.getVisible()) {
        	searchBox.draw(point);
        	if(!Strings.isNullOrEmpty(placeholder)) {
        		String s = fr.trimStringToWidth(placeholder, this.getWidth());
        		fr.drawStringWithShadow(s, xPosition, yPosition, enabled ? 0x20AAAAAA : 7368816);
        	}
        }
        super.drawTextBox();
	}

}
