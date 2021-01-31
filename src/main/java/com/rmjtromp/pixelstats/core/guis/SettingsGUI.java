package com.rmjtromp.pixelstats.core.guis;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Strings;
import com.rmjtromp.pixelstats.core.EventsManager;
import com.rmjtromp.pixelstats.core.KeybindManager;
import com.rmjtromp.pixelstats.core.events.KeyPressEvent;
import com.rmjtromp.pixelstats.core.events.RenderTickEvent;
import com.rmjtromp.pixelstats.core.utils.ChatColor;
import com.rmjtromp.pixelstats.core.utils.events.EventHandler;
import com.rmjtromp.pixelstats.core.utils.events.Listener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.IChatComponent;

public final class SettingsGUI implements Listener {
	
	private List<IChatComponent> test = new ArrayList<>();
	private KeyBinding settings = KeybindManager.registerKeybind("key.settings", Keyboard.KEY_F4, "key.categories.pixelstats");
	private boolean enabled = false;
	
	public SettingsGUI() {
		EventsManager.registerEvents(this);
		test.add(ChatColor.colorEncodeToComponent("&f&lPixelStats &7- Bedwars 4v4v4v4"));
		test.add(ChatColor.colorEncodeToComponent(""));
		test.add(ChatColor.colorEncodeToComponent("&6[238*] &b[MVP&e+&b] iStudLion &7- &f2.3&7FKDR &f1.3&7BBLR &f1.0&7WR"));
		test.add(ChatColor.colorEncodeToComponent("&f[162*] &b[MVP&e+&b] iStudLion &7- &f2.3&7FKDR &f1.3&7BBLR &f1.0&7WR"));
		test.add(ChatColor.colorEncodeToComponent("&7[89*] &b[MVP&e+&b] iStudLion &7- &f2.3&7FKDR &f1.3&7BBLR &f1.0&7WR"));
		test.add(ChatColor.colorEncodeToComponent("&7[33*] &b[MVP&e+&b] iStudLion &7- &f2.3&7FKDR &f1.3&7BBLR &f1.0&7WR"));
	}
	
	@EventHandler
	public void onKeyPress(KeyPressEvent e) {
		if(settings.isPressed()) enabled = !enabled;
	}
	
	@EventHandler
	public void onRender(RenderTickEvent e) {
		if(enabled) renderDebugInfoLeft();
	}
	
    protected void renderDebugInfoLeft() {
        List<IChatComponent> list = test;
        float transparency = Minecraft.getMinecraft().gameSettings.chatOpacity * 0.9F + 0.1F;
        int opacity = (int)((float)255 * transparency);

        for (int i = 0; i < list.size(); ++i) {
            String s = list.get(i).getFormattedText();

            if (!Strings.isNullOrEmpty(s)) {
            	FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
                int j = fontRenderer.FONT_HEIGHT;
                int k = fontRenderer.getStringWidth(s);
                int i1 = 2 + j * i;
                Gui.drawRect(1, i1 - 1, 2 + k + 1, i1 + j - 1, -1873784752 + (opacity << 24));
                GlStateManager.enableBlend();
                fontRenderer.drawStringWithShadow(s, 2, i1, 16777215 + (opacity << 24));
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
            }
        }
    }
	
}
