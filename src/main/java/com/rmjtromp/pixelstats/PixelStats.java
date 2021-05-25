package com.rmjtromp.pixelstats;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;

import com.rmjtromp.pixelstats.core.Core;
import com.rmjtromp.pixelstats.core.Settings;
import com.rmjtromp.pixelstats.core.utils.Console;
import com.rmjtromp.pixelstats.core.utils.events.Listener;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "pixelstats", version = PixelStats.version, clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public final class PixelStats implements Listener {

	protected static final String version = "2.19.1";
	private static PixelStats mod = null;
	private static File modDir = null;
	private final Settings settings;
	
	public PixelStats() throws IOException, JSONException {
		Console.log(String.format("Initializing PixelStats (v%s)", version));
		mod = this;
		modDir = new File(Minecraft.getMinecraft().mcDataDir + File.separator + ".rmjtromp" + File.separator + "pixelstats");
		if(!modDir.exists() || !modDir.isDirectory()) modDir.mkdir();
		settings = Settings.init();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		Console.debug("FMLInitializationEvent triggered");
		Core.init(this);
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	public static PixelStats getInstance() {
		return mod;
	}
	
	public static File getModDir() {
		return modDir;
	}

}
