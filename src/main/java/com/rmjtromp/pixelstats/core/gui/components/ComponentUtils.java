package com.rmjtromp.pixelstats.core.gui.components;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.rmjtromp.pixelstats.core.utils.drawings.Point;
import com.rmjtromp.pixelstats.core.utils.drawings.Size;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityList;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class ComponentUtils {
	
	private static final Splitter NEWLINE_SPLITTER = Splitter.on('\n');

	public static boolean pointIsBetweenTwoPoints(Point<Integer> point, Point<Integer> firstPoint,
			Point<Integer> secondPoint) {
		return point.getX() >= firstPoint.getX() && point.getY() >= firstPoint.getY()
				&& point.getX() <= secondPoint.getX() && point.getY() <= secondPoint.getY();
	}

	public static boolean pointIsBetweenTwoPoints(Point<Integer> point, Point<Integer> firstPoint, Size<Integer> size) {
		return pointIsBetweenTwoPoints(point, firstPoint, firstPoint.add(size.getWidth(), size.getHeight()));
	}

	public static void handleComponentHover(IChatComponent component, int x, int y) {
		if (component != null && component.getChatStyle().getChatHoverEvent() != null) {
			HoverEvent hoverevent = component.getChatStyle().getChatHoverEvent();

			if (hoverevent.getAction() == HoverEvent.Action.SHOW_ITEM) {
				ItemStack itemstack = null;

				try {
					NBTBase nbtbase = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());

					if (nbtbase instanceof NBTTagCompound) {
						itemstack = ItemStack.loadItemStackFromNBT((NBTTagCompound) nbtbase);
					}
				} catch (NBTException var11) {
					;
				}

				if (itemstack != null) {
					renderToolTip(itemstack, x, y);
				} else {
					drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Item!", x, y);
				}
			} else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
				if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips) {
					try {
						NBTBase nbtbase1 = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());

						if (nbtbase1 instanceof NBTTagCompound) {
							List<String> list1 = Lists.<String>newArrayList();
							NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase1;
							list1.add(nbttagcompound.getString("name"));

							if (nbttagcompound.hasKey("type", 8)) {
								String s = nbttagcompound.getString("type");
								list1.add("Type: " + s + " (" + EntityList.getIDFromString(s) + ")");
							}

							list1.add(nbttagcompound.getString("id"));
							drawHoveringText(list1, x, y);
						} else {
							drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
						}
					} catch (NBTException var10) {
						drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
					}
				}
			} else if (hoverevent.getAction() == HoverEvent.Action.SHOW_TEXT) {
				drawHoveringText(NEWLINE_SPLITTER.splitToList(hoverevent.getValue().getFormattedText()), x, y);
			} else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ACHIEVEMENT) {
				StatBase statbase = StatList.getOneShotStat(hoverevent.getValue().getUnformattedText());

				if (statbase != null) {
					IChatComponent ichatcomponent = statbase.getStatName();
					IChatComponent ichatcomponent1 = new ChatComponentTranslation(
							"stats.tooltip.type." + (statbase.isAchievement() ? "achievement" : "statistic"),
							new Object[0]);
					ichatcomponent1.getChatStyle().setItalic(Boolean.valueOf(true));
					String s1 = statbase instanceof Achievement ? ((Achievement) statbase).getDescription() : null;
					List<String> list = Lists.newArrayList(
							new String[] { ichatcomponent.getFormattedText(), ichatcomponent1.getFormattedText() });

					if (s1 != null) {
						list.addAll(Minecraft.getMinecraft().fontRendererObj.listFormattedStringToWidth(s1, 150));
					}

					drawHoveringText(list, x, y);
				} else {
					drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid statistic/achievement!", x, y);
				}
			}

			GlStateManager.disableLighting();
		}
	}

	public static void drawHoveringText(List<String> textLines, int x, int y) {
		drawHoveringText(textLines, x, y, Minecraft.getMinecraft().fontRendererObj);
	}

	public static void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font) {
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(textLines, x, y, sr.getScaledWidth(),
				sr.getScaledHeight(), -1, font);
	}
	
    public static void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
        drawHoveringText(Arrays.<String>asList(new String[] {tabName}), mouseX, mouseY);
    }

	public static void renderToolTip(ItemStack stack, int x, int y) {
		List<String> list = stack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);

		for (int i = 0; i < list.size(); ++i) {
			if (i == 0) {
				list.set(i, stack.getRarity().rarityColor + (String) list.get(i));
			} else {
				list.set(i, EnumChatFormatting.GRAY + (String) list.get(i));
			}
		}

		FontRenderer font = stack.getItem().getFontRenderer(stack);
		drawHoveringText(list, x, y, (font == null ? Minecraft.getMinecraft().fontRendererObj : font));
	}

	public static void drawGradientRect(int zLevel, int left, int top, int right, int bottom, int startColor,
			int endColor) {
		float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
		float startRed = (float) (startColor >> 16 & 255) / 255.0F;
		float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
		float startBlue = (float) (startColor & 255) / 255.0F;
		float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
		float endRed = (float) (endColor >> 16 & 255) / 255.0F;
		float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
		float endBlue = (float) (endColor & 255) / 255.0F;

		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(7425);

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(right, top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
		worldrenderer.pos(left, top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
		worldrenderer.pos(left, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
		worldrenderer.pos(right, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
		tessellator.draw();

		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

}
