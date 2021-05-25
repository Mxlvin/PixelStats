package com.rmjtromp.pixelstats.core.gui.components;

import static org.lwjgl.opengl.GL11.glColor3f;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;

import com.rmjtromp.pixelstats.core.utils.ChatColor;
import com.rmjtromp.pixelstats.core.utils.drawings.Colorable;
import com.rmjtromp.pixelstats.core.utils.drawings.Point;
import com.rmjtromp.pixelstats.core.utils.drawings.Size;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import scala.actors.threadpool.Arrays;

public class Text extends Component implements Colorable {
	
	private Color color = Color.WHITE;
	private String text = "",
			unformattedText = "";
	private IChatComponent component = new ChatComponentText("");
	private Size<Integer> size = new Size<>(0, 0);
	private boolean shadow = false,
			rainbow = false;
	private final FontRenderer fr;
	
	public Text(String text) {
		this(text, true);
	}
	
	public Text(String text, boolean shadow) {
		this(text, Color.WHITE, shadow);
	}
	
	public Text(IChatComponent component) {
		this(component, true);
	}
	
	public Text(String text, Color color) {
		this(text, color, true);
	}
	
	public Text(IChatComponent component, Color color) {
		this(component, color, true);
	}
	
	public Text(IChatComponent component, boolean shadow) {
		this(component, Color.WHITE, shadow);
	}
	
	public Text(String text, Color color, boolean shadow) {
		this(new ChatComponentText(text), color, shadow);
	}
	
	public Text(IChatComponent text, Color color, boolean shadow) {
		fr = Minecraft.getMinecraft().fontRendererObj;
		setText(text);
		setColor(color);
		setShadow(shadow);
	}

	@Override
	public void draw(@NotNull Point<Integer> point) {
		int color;
		String text = this.text;
		if(!rainbow) color = this.color.getRGB();
		else {
			float hue = (System.currentTimeMillis() % 4000) / 4000f;
			color = Color.HSBtoRGB(hue, 1, 1);
			text = unformattedText;
		}
		
		if(getShadow()) fr.drawStringWithShadow(text, point.getX(), point.getY(), color);
		else fr.drawString(text, point.getX(), point.getY(), color);
		glColor3f(1, 1, 1);
		
		if(ComponentUtils.pointIsBetweenTwoPoints(Screen.getMousePosition(), point, size)) {
			ComponentUtils.handleComponentHover(component, Screen.getMousePosition().getX(), Screen.getMousePosition().getY());
		}
	}
	
	public String getUnformattedText() {
		return unformattedText;
	}
	
	public String getText() {
		return text;
	}
	
	public Text setShadow(boolean shadow) {
		this.shadow = shadow;
		return this;
	}
	
	public boolean getShadow() {
		return shadow;
	}
	
	public IChatComponent getChatComponent() {
		return component;
	}
	
	public Text setText(String text) {
		this.text = text;
		this.unformattedText = ChatColor.stripcolor(text);
		this.component = new ChatComponentText(text);
		recalculateSize();
		return this;
	}
	
	public Text setText(IChatComponent component) {
		this.text = component.getFormattedText();
		this.unformattedText = ChatColor.stripcolor(text);
		this.component = component;
		recalculateSize();
		return this;
	}
	
	public Text setRainbow(boolean rainbow) {
		this.rainbow = rainbow;
		return this;
	}
	
	public boolean getRainbow() {
		return rainbow;
	}
	
	private void recalculateSize() {
		size = new Size<>(fr.getStringWidth(text), fr.FONT_HEIGHT);
	}

	@Override
	public Text setSize(@NotNull Size<Integer> size) {
		throw new IllegalStateException("Manually setting the size of Text components is not supported.");
	}

	@Override
	public Size<Integer> getSize() {
		return size;
	}

	@Override
	public Text setColor(@NotNull Color color) {
		this.color = color;
		return this;
	}

	@Override
	public Color getColor() {
		return color;
	}

}
