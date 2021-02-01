package com.rmjtromp.pixelstats.core.games;

import net.hypixel.api.util.GameType;

public abstract class AbstractGame {

	private final GameType type;
	private boolean enabled = false;
	
	public AbstractGame(GameType type) {
		this.type = type;
	}
	
	public GameType getType() {
		return type;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public abstract void initialize();
	public abstract void uninitialize();

}
