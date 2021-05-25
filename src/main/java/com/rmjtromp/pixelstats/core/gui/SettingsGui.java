package com.rmjtromp.pixelstats.core.gui;

import java.io.IOException;

import com.rmjtromp.pixelstats.core.Hypixel;

import net.minecraft.client.gui.GuiScreen;

public class SettingsGui extends GuiScreen {

	private TextField keyField;
	private static final Hypixel hypixel = Hypixel.getInstance();
	
	@Override
	public void initGui() {
		super.initGui();
		
		keyField = new TextField(0, this.width / 2 - 100, 30, 200, 20);
		keyField.setText(hypixel.getKey() != null ? hypixel.getKey().toString() : "");
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		keyField.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		keyField.textboxKeyTyped(typedChar, keyCode);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		keyField.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
}
