package com.rmjtromp.pixelstats.core;

import java.util.List;

import com.rmjtromp.pixelstats.core.utils.Console;

import java.util.ArrayList;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public final class KeybindManager {
	
	private static KeybindManager manager = null;
	private static final List<KeyBinding> queue = new ArrayList<>();

	private KeybindManager() {
		Console.log("Initializing KeybindManager");
	}
	
	public static void init() {
		if(manager == null) manager = new KeybindManager();
		for(KeyBinding keybind : queue) ClientRegistry.registerKeyBinding(keybind);
		if(!queue.isEmpty()) queue.clear();
	}
	
	public static KeyBinding registerKeybind(String description, int keyCode, String category) {
		KeyBinding keybind = new KeyBinding(description, keyCode, category);
		if(manager != null) ClientRegistry.registerKeyBinding(keybind);
		else queue.add(keybind);
		return keybind;
	}
	
}
