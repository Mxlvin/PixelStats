package com.rmjtromp.pixelstats.core.utils;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

public abstract class AbstractPlayer {
	
	protected GameProfile profile;
	
	protected AbstractPlayer(GameProfile profile) {
		this.profile = profile;
	}
	
	public String getName() {
		return profile.getName();
	}
	
	public UUID getUniqueId() {
		return profile.getId();
	}
	
	public GameProfile getGameProfile() {
		return profile;
	}
	
	@Override
	public String toString() {
		return profile.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AbstractPlayer) return ((AbstractPlayer) obj).getUniqueId().equals(this.getUniqueId());
		return false;
	}

}
