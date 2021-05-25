package com.rmjtromp.pixelstats.core.utils.drawings;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.IChatComponent;

public class GLDraw extends GuiScreen {
	
	private static final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
	
	public static void drawRect(Point<Integer> point, Size<Integer> size, Color color) {
		Gui.drawRect(point.getX(), point.getY(), point.getX() + size.getWidth(), point.getY() + size.getHeight(), color.getRGB());
	}
	
	public static void draw(Point<Integer> pointOne, Point<Integer> pointTwo, Color color) {
		Gui.drawRect(pointOne.getX(), pointOne.getY(), pointTwo.getX(), pointTwo.getY(), color.getRGB());
	}
	
	public static void drawString(String string, Point<Integer> point, Color color, int fontSize) {
		GL11.glPushMatrix();
        GL11.glScalef(fontSize, fontSize, 100f);
        fr.drawString(string, point.getX() / fontSize , point.getY() / fontSize, color.getRGB());
        GL11.glPopMatrix();
	}
	
	public static void drawStringWithShadow(String string, Point<Integer> point, Color color, int fontSize) {
		GL11.glPushMatrix();
        GL11.glScalef(fontSize, fontSize, 100f);
        fr.drawStringWithShadow(string, point.getX() / fontSize , point.getY() / fontSize, color.getRGB());
        GL11.glPopMatrix();
	}
	
	public static void drawString(String string, Point<Integer> point, Color color) {
        fr.drawString(string, point.getX(), point.getY(), color.getRGB());
	}
	
	public static void drawStringWithShadow(String string, Point<Integer> point, Color color) {
        fr.drawStringWithShadow(string, point.getX(), point.getY(), color.getRGB());
	}
	
	public static void drawString(String string, Point<Integer> point) {
        fr.drawString(string, point.getX(), point.getY(), -1);
	}
	
	public static void drawStringWithShadow(String string, Point<Integer> point) {
        fr.drawStringWithShadow(string, point.getX(), point.getY(), -1);
	}
	
	public static void drawString(IChatComponent component, Point<Integer> point, Color color) {
        fr.drawString(component.getFormattedText(), point.getX(), point.getY(), color.getRGB());
	}
	
	public static void drawStringWithShadow(IChatComponent component, Point<Integer> point, Color color) {
        fr.drawStringWithShadow(component.getFormattedText(), point.getX(), point.getY(), color.getRGB());
	}
	
	public static void drawString(IChatComponent component, Point<Integer> point) {
        fr.drawString(component.getFormattedText(), point.getX(), point.getY(), -1);
	}
	
	public static void drawStringWithShadow(IChatComponent component, Point<Integer> point) {
        fr.drawStringWithShadow(component.getFormattedText(), point.getX(), point.getY(), -1);
	}
    
    public static void  drawRoundedRect(Point<Integer> point, Size<Integer> size, int radius, Color color) {
    	drawRect(point.getX(), point.getY() + radius, point.getX() + radius, point.getY() + size.getHeight() - radius, color.getRGB());
    	drawRect(point.getX() + radius, point.getY(), point.getX() + size.getWidth() - radius, point.getY() + size.getHeight(), color.getRGB());
    	drawRect(point.getX() + size.getWidth() - radius, point.getY() + radius, point.getX() + size.getWidth(), point.getY() + size.getHeight() - radius, color.getRGB());
        drawArc(point.add(radius, radius), radius, 0, 90, color);
        drawArc(point.add(size.getWidth() - radius, radius), radius, 270, 360, color);
        drawArc(point.add(size.getWidth() - radius, size.getHeight() - radius), radius, 180, 270, color);
        drawArc(point.add(radius, size.getHeight() - radius), radius, 90, 180, color);
    }
    
    public static void drawArc(Point<Integer> point, int radius, int startAngle, int endAngle, Color color) {
    	GL11.glPushMatrix();
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    GL11.glEnable(GL11.GL_LINE_SMOOTH);
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    GL11.glEnable(GL11.GL_CULL_FACE);
	    GL11.glDisable(GL11.GL_DEPTH_TEST);
	    GL11.glDisable(GL11.GL_LIGHTING);
	    
        GL11.glColor4f((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, (float) color.getAlpha() / 255);
        
        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

        worldRenderer.begin(6, DefaultVertexFormats.POSITION);
        worldRenderer.pos(point.getX(), point.getY(), 0).endVertex();

        for (int i = (int) (startAngle / 360.0 * 100); i <= (int) (endAngle / 360.0 * 100); i++) {
            double angle = (Math.PI * 2 * i / 100) + Math.toRadians(180);
            worldRenderer.pos(point.getX() + Math.sin(angle) * radius, point.getY() + Math.cos(angle) * radius, 0).endVertex();
        }
        
        Tessellator.getInstance().draw();

	    GL11.glEnable(GL11.GL_DEPTH_TEST);
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_LINE_SMOOTH);
	    GL11.glPopMatrix();
    }
    
    public static void drawCircle(Point<Integer> point, Size<Integer> size, Color color) {
    	drawArc(point.add(size.getWidth() / 2, size.getHeight() / 2), size.getWidth() / 2, 0, 360, color);
    }
    
    public static void drawHollowArc(Point<Integer> point, int radius, float width, float startDegree, float degree, Color color) {
	    GL11.glPushMatrix();
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    GL11.glEnable(GL11.GL_LINE_SMOOTH);
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    GL11.glEnable(GL11.GL_CULL_FACE);
	    GL11.glDisable(GL11.GL_DEPTH_TEST);
	    GL11.glDisable(GL11.GL_LIGHTING);
	    
    	GL11.glColor4f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, color.getAlpha()/255f);
    	for(float rad = radius + .25f; rad < radius + width - .25f; rad+=.25f) {
    		GL11.glBegin(GL11.GL_LINE_STRIP);
    		for (int i = (int) (startDegree / 360.0 * 300); i <= (int) ((startDegree+degree) / 360.0 * 300); i++) {
    			double angle = 2 * Math.PI * i / 300;
    			double x = point.getX() + Math.cos(angle) * rad;
    			double y = point.getY() + Math.sin(angle) * rad;
    			GL11.glVertex2d(x, y);
    		}
    		GL11.glEnd();
    	}

	    GL11.glEnable(GL11.GL_DEPTH_TEST);
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_LINE_SMOOTH);
	    GL11.glPopMatrix();
    }
    
    public static void drawHollowCircle(Point<Integer> point, int radius, int width, Color color) {
    	drawHollowArc(point, radius, width, 0f, 360f, color);
    }

	public static void drawRoundedRect(Point<Integer> point, Size<Integer> size, int[] radius, Color color) {
		if(radius.length != 4) throw new IllegalArgumentException("Radius array length must be 4");
        drawArc(point.add(radius[0], radius[0]), radius[0], 0, 90, color);
        drawArc(point.add(size.getWidth() - radius[1], radius[1]), radius[1], 270, 360, color);
        drawArc(point.add(size.getWidth() - radius[2], size.getHeight() - radius[2]), radius[2], 180, 270, color);
        drawArc(point.add(radius[3], size.getHeight() - radius[3]), radius[3], 90, 180, color);

        drawRect(point.addX(radius[0]), new Size<>(size.getWidth() - radius[0] - radius[1], radius[0]), color);
        drawRect(point.add(size.getWidth() - radius[1], radius[1]), new Size<>(radius[1], size.getHeight() - radius[1] - radius[2]), color);
        drawRect(point.add(radius[3], size.getHeight() - radius[2]), new Size<>(size.getWidth() - radius[2] - radius[3], radius[2]), color);
        drawRect(point.addY(radius[0]), new Size<>(radius[3], size.getHeight() - radius[0] - radius[3]), color);
        drawRect(point.add(radius[3], radius[0]), new Size<>(size.getWidth() - radius[3] - radius[1], size.getHeight() - radius[0] - radius[2]), color);
	}
	
}
