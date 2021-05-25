package com.rmjtromp.pixelstats.core.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Strings;
import com.rmjtromp.pixelstats.core.games.bedwars.gui.HypixelProfileGUI;
import com.rmjtromp.pixelstats.core.utils.drawings.GLDraw;
import com.rmjtromp.pixelstats.core.utils.drawings.Point;
import com.rmjtromp.pixelstats.core.utils.drawings.Rectangle;
import com.rmjtromp.pixelstats.core.utils.drawings.Size;
import com.rmjtromp.pixelstats.core.utils.hypixel.profile.HypixelProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class SearchOverlay extends GuiScreen {
	
	private static SearchOverlay overlay = null;
	
	public static SearchOverlay getOverlay() {
		if(overlay == null) overlay = new SearchOverlay();
		return overlay;
	}
	
	private SearchOverlay() {
//		rect.setRadius(100, 0, 5, 20);
//		rect.getBorder().setColor(Color.BLUE);
//		rect.getBorder().setWidth(1);
	}
	
	private TextField searchField;
//	private Rectangle rect = new Rectangle(new Size<>(200, 100), new Color(Integer.MIN_VALUE, true));
//	private Point<Integer> center;
	
	@Override
	public void initGui() {
		super.initGui();
		
//		center = new Point<>(width/2, height/2);
		suggestions.clear();
		searchField = new TextField(0, this.width / 2 - 100, 30, 200, 20);
		searchField.setCanLoseFocus(false);
		searchField.setFocused(true);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		searchField.textboxKeyTyped(typedChar, keyCode);
		
		if(keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
			if(searchField.getText().matches("^\\w{1,16}$")) {
				Minecraft.getMinecraft().displayGuiScreen(new HypixelProfileGUI(HypixelProfile.get(searchField.getText())));
			}
		} else {
			renderSuggestions();
			if(keyCode == Keyboard.KEY_TAB) {
				if(!Strings.isNullOrEmpty(searchField.getPlaceholder())) {
					searchField.setText(searchField.getPlaceholder());
					searchField.setPlaceholder(null);
				}
			}
		}
	}
	
	private List<HypixelProfile> suggestions = new ArrayList<>();
	private void renderSuggestions() {
		suggestions.clear();
		if(!searchField.getText().isEmpty()) {
			suggestions = HypixelProfile.getCachedProfiles().stream().filter(profile -> {
				if(profile.getExpiration() > System.currentTimeMillis()) {
					return profile.getName().toLowerCase().startsWith(searchField.getText().toLowerCase()) || profile.getName().toLowerCase().contains(searchField.getText().toLowerCase());
				}
				return false;
			}).collect(Collectors.toList());
			
			final String input = searchField.getText().toLowerCase();
			suggestions.sort((p1, p2) -> {
				final String p1n = p1.getName().toLowerCase();
				final String p2n = p2.getName().toLowerCase();
				
				if(p1n.startsWith(input) || p2n.startsWith(input)) {
					if(p1n.startsWith(input) && p2n.startsWith(input)) return String.CASE_INSENSITIVE_ORDER.compare(p1n, p2n);
					else if(p1n.startsWith(input)) return -1;
					else return 1;
				} else if(p1n.contains(input) || p2n.contains(input)) {
					if(p1n.contains(input) && p2n.contains(input)) return String.CASE_INSENSITIVE_ORDER.compare(p1n, p2n);
					else if(p1n.contains(input)) return -1;
					else return 1;
				} else return String.CASE_INSENSITIVE_ORDER.compare(p1n, p2n);
			});
		}
		
		if(!suggestions.isEmpty()) {
			String firstSuggestion = suggestions.stream().map(profile -> profile.getName().toLowerCase()).filter(name -> name.startsWith(searchField.getText().toLowerCase())).findFirst().orElse(null);
			if(firstSuggestion != null) searchField.setPlaceholder(searchField.getText() + firstSuggestion.substring(searchField.getText().length()));
			searchField.setPlaceholder(null);
		} else searchField.setPlaceholder(null);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		searchField.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawGradientRect(0, 0, width, height/2, Integer.MIN_VALUE, 0x00000000);
		this.fontRendererObj.drawString("Search", this.width / 2 - 100 + 4, 30 - this.fontRendererObj.FONT_HEIGHT - 2, -1);
		searchField.drawTextBox();
//		try {
//			rect.draw(center.add(-rect.getWidth()/2, -rect.getHeight()/2));
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//		int y = 65;
//		final int x = width / 2 - rect.getSize().getWidth() / 2;
//		for(HypixelProfile profile : suggestions) {
//			rect.draw(new Point<>(x, y));
//			fontRendererObj.drawStringWithShadow(profile.getDisplayName(), x + 10, y + 10 - fontRendererObj.FONT_HEIGHT / 2, -1);
//			y += 25;
//		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

}
