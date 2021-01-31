package com.rmjtromp.pixelstats;

import java.io.File;

import com.rmjtromp.pixelstats.core.Core;
import com.rmjtromp.pixelstats.core.utils.events.Listener;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "pixelstats", version = "2.18.2d", clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public final class PixelStats implements Listener {

	private static PixelStats mod = null;
	private static File modDir = null;
	
	public PixelStats() {
		mod = this;
		modDir = new File(Minecraft.getMinecraft().mcDataDir + File.separator + ".rmjtromp" + File.separator + "pixelstats");
		if(!modDir.exists() || !modDir.isDirectory()) modDir.mkdir();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		Core.init(this);
	}
	
	public static PixelStats getInstance() {
		return mod;
	}
	
	public static File getModDir() {
		return modDir;
	}

}
