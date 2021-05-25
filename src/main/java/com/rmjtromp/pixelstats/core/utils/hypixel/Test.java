package com.rmjtromp.pixelstats.core.utils.hypixel;

import org.lwjgl.input.Mouse;

import com.rmjtromp.pixelstats.core.EventsManager;
import com.rmjtromp.pixelstats.core.events.GameInputEvent;
import com.rmjtromp.pixelstats.core.utils.events.EventHandler;
import com.rmjtromp.pixelstats.core.utils.events.Listener;
import com.rmjtromp.pixelstats.core.utils.hypixel.profile.HypixelProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

public class Test {
	
	private static Test test = null;
	
	public static void init() {
		if(test == null) new Test();
	}
	
	private Test() {
		test = this;
		EventsManager.registerEvents(listener);
	}
	
	private Listener listener = new Listener() {
		
		@EventHandler
		public void onGameInput(GameInputEvent e) {
			if(Mouse.getEventButtonState() && Mouse.getEventButton() == 2 && GuiScreen.isShiftKeyDown()) {
				MovingObjectPosition objectMouseOver = Minecraft.getMinecraft().objectMouseOver;
				if(objectMouseOver != null && objectMouseOver.typeOfHit.equals(MovingObjectPosition.MovingObjectType.ENTITY) && objectMouseOver.entityHit instanceof EntityPlayer) {
		            EntityPlayer player = (EntityPlayer) objectMouseOver.entityHit;
		            HypixelProfile.get(player.getGameProfile()).openProfileGUI();
				}
			}
		}
		
	};

}
