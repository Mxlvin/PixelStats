package com.rmjtromp.pixelstats.core.utils.hypixel.profile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.common.base.Strings;
import com.rmjtromp.pixelstats.core.utils.ChatColor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

public class HypixelProfileGUI extends GuiScreen {

	private final HypixelProfile profile;
	private final List<String> generalProfileInfo = new ArrayList<>();
    private final int fontHeight;
	private ResourceLocation resourceLocation = null;
	private int aX = 0;
	private int aY = 100;
	
	public HypixelProfileGUI(HypixelProfile profile) {
		this.profile = profile;
		fontHeight = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
	}
	
	@Override
	public void initGui() {
		if(resourceLocation == null) {
			try {
				URL url = new URL("https://visage.surgeplay.com/bust/128/"+profile.getUniqueId().toString());
				BufferedImage image = ImageIO.read(url.openStream());
				DynamicTexture texture = new DynamicTexture(image);
				resourceLocation = Minecraft.getMinecraft().renderEngine.getDynamicTextureLocation(profile.getName()+".png", texture);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		generalProfileInfo.clear();
		generalProfileInfo.add(profile.getDisplayName());

		int widest = 0;
		for(String pi : generalProfileInfo) widest = Math.max(widest, fontRendererObj.getStringWidth(pi));
		aX = width / 2 - (64 + 4 + widest) / 2;
		
		super.initGui();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();

		// section a
		for (int i = 0; i < generalProfileInfo.size(); ++i) {
            if(!Strings.isNullOrEmpty(generalProfileInfo.get(i))) fontRendererObj.drawString(generalProfileInfo.get(i)+ChatColor.RESET, aX + 64 + 4, aY + 5 + (2 + fontHeight * i), 14737632);
        }
		if(resourceLocation != null) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
	        drawModalRectWithCustomSizedTexture(aX, aY, 0, 0, 64, 64, 64, 64);
		}
		
		// section b
		
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

}
