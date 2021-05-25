package com.rmjtromp.pixelstats.core.gui.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.rmjtromp.pixelstats.core.utils.drawings.Point;

import net.minecraft.client.gui.GuiScreen;
import scala.actors.threadpool.Arrays;

public class Screen extends GuiScreen {

	private List<Component> components = new ArrayList<>();
	
	public void addComponent(Component component) {
		components.add(component);
	}
	
	@SuppressWarnings("unchecked")
	public void addComponents(Component...components) {
		this.components.addAll(Arrays.asList(components));
	}
	
	private static Point<Integer> mousePosition = new Point<>(0, 0);
	public static Point<Integer> getMousePosition() {
		return mousePosition;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		mousePosition = new Point<>(0, 0);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		mousePosition = new Point<>(mouseX, mouseY);
		components.forEach(component -> component.draw(100, 100));
	}
	
	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		mousePosition = new Point<>(0, 0);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		Point<Integer> point = new Point<>(mouseX, mouseY);
		components.forEach(component -> component.handleMouse(point, Action.MouseAction.CLICK, mouseButton));
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		Point<Integer> point = new Point<>(mouseX, mouseY);
		components.forEach(component -> component.handleMouse(point, Action.MouseAction.DRAG, clickedMouseButton));
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		Point<Integer> point = new Point<>(mouseX, mouseY);
		components.forEach(component -> component.handleMouse(point, Action.MouseAction.RELEASE, state));
	}
	
}
