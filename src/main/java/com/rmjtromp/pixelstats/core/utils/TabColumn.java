package com.rmjtromp.pixelstats.core.utils;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class TabColumn {
	
	private static FontRenderer fr;
	private String label = "";
	private boolean displayLabel = true;
	private boolean hideIfEmpty = true;
	private int widest = 0;
	private int labelWidth = 0;
	private boolean center = true;
	private HashMap<UUID, String> values = new HashMap<>();
	
	public TabColumn() {
		if(fr == null) fr = Minecraft.getMinecraft().fontRendererObj;
	}
	
	public TabColumn(String label) {
		this();
		setLabel(label);
	}
	
	public TabColumn(String label, boolean displayLabel) {
		this();
		setLabel(label);
		setDisplayLabel(displayLabel);
	}
	
	public TabColumn(String label, boolean displayLabel, boolean hideIfEmpty) {
		this();
		setLabel(label);
		setDisplayLabel(displayLabel);
		setHideIfEmpty(hideIfEmpty);
	}
	
	public TabColumn(String label, boolean displayLabel, boolean hideIfEmpty, boolean center) {
		this();
		setLabel(label);
		setDisplayLabel(displayLabel);
		setHideIfEmpty(hideIfEmpty);
		setCenter(center);
	}
	
	public void setLabel(String label) {
		if(label == null) this.label = ChatColor.colorEncode("&cnull");
		else this.label = ChatColor.colorEncode(label);
		labelWidth = fr.getStringWidth(this.label);
	}
	
	public void setCenter(boolean center) {
		this.center = center;
	}
	
	public boolean shouldCenter() {
		return center;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setDisplayLabel(boolean toggle) {
		displayLabel = toggle;
	}
	
	public int getLabelWidth() {
		return labelWidth;
	}
	
	public boolean getDisplayLabel() {
		return displayLabel;
	}

	public void setHideIfEmpty(boolean toggle) {
		hideIfEmpty = toggle;
	}

	public boolean shouldHideIfEmpty() {
		return hideIfEmpty;
	}
	
	public boolean shouldShowColumn() {
		return values.size() > 0 || !hideIfEmpty;
	}
	
	public int getWidestValueWidth() {
		if(shouldShowColumn()) return displayLabel ? Math.max(labelWidth, widest) : widest;
		return 0;
	}
	
	public void add(final UUID identifier, final Object obj) {
		if(identifier == null) throw new NullPointerException("Value identifier can not be null");
		String value = obj.toString();
		if(ChatColor.stripcolor(value).isEmpty()) return;
		widest = Math.max(widest, fr.getStringWidth(value));
		
		if(has(identifier)) values.replace(identifier, value);
		else values.put(identifier, value);
	}
	
	public void remove(final UUID identifier) {
		if(has(identifier)) values.remove(identifier);
	}
	
	public boolean has(final UUID identifier) {
		return values.containsKey(identifier);
	}
	
	public String get(final UUID identifier) {
		return has(identifier) ? values.get(identifier) : null;
	}

}
