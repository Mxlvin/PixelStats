package com.rmjtromp.pixelstats.core.utils.hypixel.profile.bedwars;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.rmjtromp.pixelstats.core.utils.ChatColor;

import scala.actors.threadpool.Arrays;

public final class Tags {
	
	private final List<Tag> tags = new ArrayList<>();
	private final String _tags;
	
	private Tags() {
		_tags = String.join(ChatColor.WHITE+"+", tags.stream().map(Object::toString).collect(Collectors.toList()));
	}
	
	public Tags(Tag...tags) {
		this();
		for(Tag tag : tags) this.tags.add(tag);
	}
	
	public Tags(Collection<Tag> tags) {
		this();
		for(Tag tag : tags) this.tags.add(tag);
	}
	
	public List<Tag> getTags() {
		return new ArrayList<>(tags);
	}
	
	@SuppressWarnings("unchecked")
	public boolean contains(Tag...tags) {
		return contains(Arrays.asList(tags));
	}
	
	public boolean contains(Collection<Tag> tags) {
		return this.tags.containsAll(tags);
	}
	
	public void forEach(Consumer<Tag> action) {
		tags.forEach(action);
	}
	
	@Override
	public String toString() {
		return _tags;
	}

}
