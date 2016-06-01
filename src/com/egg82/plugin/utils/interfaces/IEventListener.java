package com.egg82.plugin.utils.interfaces;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import com.egg82.plugin.commands.EventCommand;

public interface IEventListener extends Listener {
	void initialize();
	void destroy();
	void addEvent(Class<? extends Event> event, Class<? extends EventCommand> command);
	void removeEvent(Class<? extends Event> event);
	void clearEvents();
	boolean hasEvent(Class<? extends Event> event);
}