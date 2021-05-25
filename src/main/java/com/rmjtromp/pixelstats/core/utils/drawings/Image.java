package com.rmjtromp.pixelstats.core.utils.drawings;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class Image implements Drawable<Integer>, Sizeable<Integer> {
	
	protected final Minecraft mc = Minecraft.getMinecraft();
	protected final TextureManager tm = mc.getTextureManager();
	private final ResourceLocation resource;
	private final Size<Integer> imageSize;
	
	// the size at which the image will be rendered
	private Size<Integer> size;
	
	// the position of the crop
	private Point<Float> cropPosition = new Point<>(0f, 0f);
	// the size of the crop
	private Size<Integer> cropSize;
	
	// image offset
	private Point<Integer> offset = new Point<>(0, 0);
	
	public Image(@NotNull InputStream input) throws IOException {
		this(ImageIO.read(input));
	}
	
	public Image(@NotNull BufferedImage image) {
		imageSize = new Size<>(image.getWidth(), image.getHeight());
		size = imageSize.clone();
		cropSize = imageSize.clone();
		resource = tm.getDynamicTextureLocation(UUID.randomUUID().toString().substring(10), new DynamicTexture(image));
	}
	
	public Size<Integer> getImageSize() {
		return imageSize;
	}

	@Override
	public Image setSize(@NotNull Size<Integer> size) {
		this.size = size;
		return this;
	}
	
	public Image crop(@NotNull Point<Float> position, @NotNull Size<Integer> size) {
		setCropPosition(position);
		setCropSize(size);
		return this;
	}
	
	public Image setCropPosition(@NotNull Point<Float> position) {
		this.cropPosition = position;
		return this;
	}
	
	public Point<Float> getCropPosition() {
		return cropPosition;
	}
	
	public Image setCropSize(@NotNull Size<Integer> size) {
		this.cropSize = size;
		return this;
	}
	
	public Size<Integer> getCropSize() {
		return cropSize;
	}

	@Override
	public Size<Integer> getSize() {
		return size;
	}
	
	public Image setOffset(Point<Integer> offset) {
		this.offset = offset;
		return this;
	}
	
	public Point<Integer> getOffset() {
		return offset;
	}
	
	public ResourceLocation getResourceLocation() {
		return resource;
	}

	@Override
	public void draw(@NotNull Point<Integer> point) {
		tm.bindTexture(getResourceLocation());
		ScaledResolution sr = new ScaledResolution(mc);
		int factor = sr.getScaleFactor();
		GlStateManager.color(1F, 1F, 1F, 1F);
		Gui.drawModalRectWithCustomSizedTexture(
				point.getX()/factor + getOffset().getX()/factor, // image x render position
				point.getY()/factor + getOffset().getY()/factor, // image y render position
				getCropPosition().getX()/factor, // image x crop position
				getCropPosition().getY()/factor, // image y crop position
				getCropSize().getWidth()/factor, // width of image crop
				getCropSize().getHeight()/factor, // height of image crop
				(float)getWidth()/factor, // width at which it will be rendered at
				(float)getHeight()/factor // height at which it will be rendered at
				);
	}

}
