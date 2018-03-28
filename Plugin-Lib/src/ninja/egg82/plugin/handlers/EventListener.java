package ninja.egg82.plugin.handlers;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ninja.egg82.concurrent.DynamicConcurrentQueue;
import ninja.egg82.concurrent.IConcurrentQueue;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.BasePlugin;
import ninja.egg82.plugin.commands.events.EventCommand;
import ninja.egg82.plugin.commands.events.HighEventCommand;
import ninja.egg82.plugin.commands.events.HighestEventCommand;
import ninja.egg82.plugin.commands.events.IEventCommand;
import ninja.egg82.plugin.commands.events.LowEventCommand;
import ninja.egg82.plugin.commands.events.LowestEventCommand;
import ninja.egg82.plugin.commands.events.MonitorEventCommand;
import ninja.egg82.plugin.core.CoreEventHandler;
import ninja.egg82.utils.ReflectUtil;

public class EventListener implements Listener, Closeable {
	//vars
	private static final Logger logger = LoggerFactory.getLogger(EventListener.class);
	
	private CoreEventHandler highestEventHandler = new CoreEventHandler();
	private CoreEventHandler highEventHandler = new CoreEventHandler();
	private CoreEventHandler normalEventHandler = new CoreEventHandler();
	private CoreEventHandler lowEventHandler = new CoreEventHandler();
	private CoreEventHandler lowestEventHandler = new CoreEventHandler();
	private CoreEventHandler monitorEventHandler = new CoreEventHandler();
	
	private IConcurrentQueue<Event> lastEvents = new DynamicConcurrentQueue<Event>();
	
	private BasePlugin plugin = ServiceLocator.getService(BasePlugin.class);
	private PluginManager manager = Bukkit.getServer().getPluginManager();
	
	//constructor
	public EventListener() {
		List<Class<Event>> events = ReflectUtil.getClasses(Event.class, "org.bukkit.event", true, false, false);
		
		for (Class<Event> e : events) {
			manager.registerEvent(e, this, EventPriority.HIGHEST, highestEventExecutor, plugin, false);
			manager.registerEvent(e, this, EventPriority.HIGH, highEventExecutor, plugin, false);
			manager.registerEvent(e, this, EventPriority.NORMAL, normalEventExecutor, plugin, false);
			manager.registerEvent(e, this, EventPriority.LOW, lowEventExecutor, plugin, false);
			manager.registerEvent(e, this, EventPriority.LOWEST, lowestEventExecutor, plugin, false);
			manager.registerEvent(e, this, EventPriority.MONITOR, monitorEventExecutor, plugin, false);
		}
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				lastEvents.clear();
			}
		}, 1L, 1L);
	}
	
	//public
	public boolean addEventHandler(Class<? extends Event> event, Class<? extends IEventCommand<? extends Event>> clazz) {
		if (event == null) {
			throw new ArgumentNullException("event");
		}
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		if (ReflectUtil.doesExtend(EventCommand.class, clazz)) {
			return normalEventHandler.addEventHandler(event, clazz);
		} else if (ReflectUtil.doesExtend(MonitorEventCommand.class, clazz)) {
			return highestEventHandler.addEventHandler(event, clazz);
		} else if (ReflectUtil.doesExtend(HighestEventCommand.class, clazz)) {
			return highestEventHandler.addEventHandler(event, clazz);
		} else if (ReflectUtil.doesExtend(HighEventCommand.class, clazz)) {
			return highEventHandler.addEventHandler(event, clazz);
		} else if (ReflectUtil.doesExtend(LowestEventCommand.class, clazz)) {
			return lowestEventHandler.addEventHandler(event, clazz);
		} else if (ReflectUtil.doesExtend(LowEventCommand.class, clazz)) {
			return lowEventHandler.addEventHandler(event, clazz);
		}
		
		return false;
	}
	public boolean removeEventHandler(Class<? extends IEventCommand<? extends Event>> clazz) {
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		if (ReflectUtil.doesExtend(EventCommand.class, clazz)) {
			return normalEventHandler.removeEventHandler(clazz);
		} else if (ReflectUtil.doesExtend(MonitorEventCommand.class, clazz)) {
			return highestEventHandler.removeEventHandler(clazz);
		} else if (ReflectUtil.doesExtend(HighestEventCommand.class, clazz)) {
			return highestEventHandler.removeEventHandler(clazz);
		} else if (ReflectUtil.doesExtend(HighEventCommand.class, clazz)) {
			return highEventHandler.removeEventHandler(clazz);
		} else if (ReflectUtil.doesExtend(LowestEventCommand.class, clazz)) {
			return lowestEventHandler.removeEventHandler(clazz);
		} else if (ReflectUtil.doesExtend(LowEventCommand.class, clazz)) {
			return lowEventHandler.removeEventHandler(clazz);
		}
		
		return false;
	}
	public boolean removeEventHandler(Class<? extends Event> event, Class<? extends IEventCommand<? extends Event>> clazz) {
		if (event == null) {
			throw new ArgumentNullException("event");
		}
		if (clazz == null) {
			throw new ArgumentNullException("clazz");
		}
		
		if (ReflectUtil.doesExtend(EventCommand.class, clazz)) {
			return normalEventHandler.removeEventHandler(event, clazz);
		} else if (ReflectUtil.doesExtend(MonitorEventCommand.class, clazz)) {
			return highestEventHandler.removeEventHandler(event, clazz);
		} else if (ReflectUtil.doesExtend(HighestEventCommand.class, clazz)) {
			return highestEventHandler.removeEventHandler(event, clazz);
		} else if (ReflectUtil.doesExtend(HighEventCommand.class, clazz)) {
			return highEventHandler.removeEventHandler(event, clazz);
		} else if (ReflectUtil.doesExtend(LowestEventCommand.class, clazz)) {
			return lowestEventHandler.removeEventHandler(event, clazz);
		} else if (ReflectUtil.doesExtend(LowEventCommand.class, clazz)) {
			return lowEventHandler.removeEventHandler(event, clazz);
		}
		
		return false;
	}
	public boolean hasEventHandler(Class<? extends Event> event) {
		if (event == null) {
			return false;
		}
		
		if (normalEventHandler.hasEventHandler(event)) {
			return true;
		} else if (monitorEventHandler.hasEventHandler(event)) {
			return true;
		} else if (highestEventHandler.hasEventHandler(event)) {
			return true;
		} else if (highEventHandler.hasEventHandler(event)) {
			return true;
		} else if (lowestEventHandler.hasEventHandler(event)) {
			return true;
		} else if (lowEventHandler.hasEventHandler(event)) {
			return true;
		}
		
		return false;
	}
	public void clear() {
		highestEventHandler.clear();
		highEventHandler.clear();
		normalEventHandler.clear();
		lowEventHandler.clear();
		lowestEventHandler.clear();
		monitorEventHandler.clear();
	}
	public void close() {
		clear();
		
		List<Class<Event>> events = ReflectUtil.getClasses(Event.class, "org.bukkit.event", true, false, false);
		for (Class<Event> e : events) {
			unregisterEvent(e);
		}
	}
	
	public void registerEvent(Class<? extends Event> e) {
		manager.registerEvent(e, this, EventPriority.HIGHEST, highestEventExecutor, plugin, false);
		manager.registerEvent(e, this, EventPriority.HIGH, highEventExecutor, plugin, false);
		manager.registerEvent(e, this, EventPriority.NORMAL, normalEventExecutor, plugin, false);
		manager.registerEvent(e, this, EventPriority.LOW, lowEventExecutor, plugin, false);
		manager.registerEvent(e, this, EventPriority.LOWEST, lowestEventExecutor, plugin, false);
		manager.registerEvent(e, this, EventPriority.MONITOR, monitorEventExecutor, plugin, false);
	}
	public void unregisterEvent(Class<? extends Event> e) {
		Method m = ReflectUtil.getMethod("getHandlerList", e);
		
		if (m == null) {
			return;
		}
		
		HandlerList list = null;
		try {
			list = (HandlerList) m.invoke(null, new Object[0]);
		} catch (Exception ex) {
			logger.warn("Could not unregister event.", ex);
			return;
		}
		
		list.unregister(this);
	}
	
	public int addEventsFromPackage(String packageName) {
		return addEventsFromPackage(packageName, true);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int addEventsFromPackage(String packageName, boolean recursive) {
		if (packageName == null) {
			throw new ArgumentNullException("packageName");
		}
		
		int numEvents = 0;
		
		List<Class<? extends IEventCommand>> enums = ReflectUtil.getClassesParameterized(IEventCommand.class, packageName, recursive, false, false);
		for (Class<? extends IEventCommand> c : enums) {
			Class<? extends Event> eventType = null;
			try {
				eventType = (Class<? extends Event>) ((ParameterizedType) c.getGenericSuperclass()).getActualTypeArguments()[0];
			} catch (Exception ex) {
				continue;
			}
			
			if (addEventHandler(eventType, (Class<IEventCommand<? extends Event>>) c)) {
				numEvents++;
			}
		}
		
		return numEvents;
	}
	
	//private
	private EventExecutor highestEventExecutor = new EventExecutor() {
		public void execute(Listener listener, Event event) throws EventException {
			onAnyEvent(EventPriority.HIGHEST, event, event.getClass());
		}
	};
	private EventExecutor highEventExecutor = new EventExecutor() {
		public void execute(Listener listener, Event event) throws EventException {
			onAnyEvent(EventPriority.HIGH, event, event.getClass());
		}
	};
	private EventExecutor normalEventExecutor = new EventExecutor() {
		public void execute(Listener listener, Event event) throws EventException {
			onAnyEvent(EventPriority.NORMAL, event, event.getClass());
		}
	};
	private EventExecutor lowEventExecutor = new EventExecutor() {
		public void execute(Listener listener, Event event) throws EventException {
			onAnyEvent(EventPriority.LOW, event, event.getClass());
		}
	};
	private EventExecutor lowestEventExecutor = new EventExecutor() {
		public void execute(Listener listener, Event event) throws EventException {
			onAnyEvent(EventPriority.LOWEST, event, event.getClass());
		}
	};
	private EventExecutor monitorEventExecutor = new EventExecutor() {
		public void execute(Listener listener, Event event) throws EventException {
			onAnyEvent(EventPriority.MONITOR, event, event.getClass());
		}
	};
	
	private <T extends Event> void onAnyEvent(EventPriority priority, T event, Class<? extends Event> clazz) {
		if (isDuplicate(event)) {
			return;
		}
		
		if (priority == EventPriority.NORMAL) {
			normalEventHandler.onAnyEvent(event, clazz);
		} else if (priority == EventPriority.MONITOR) {
			monitorEventHandler.onAnyEvent(event, clazz);
		} else if (priority == EventPriority.HIGHEST) {
			highestEventHandler.onAnyEvent(event, clazz);
		} else if (priority == EventPriority.HIGH) {
			highEventHandler.onAnyEvent(event, clazz);
		} else if (priority == EventPriority.LOWEST) {
			lowestEventHandler.onAnyEvent(event, clazz);
		} else if (priority == EventPriority.LOW) {
			lowEventHandler.onAnyEvent(event, clazz);
		}
	}
	
	private boolean isDuplicate(Event event) {
		if (lastEvents.contains(event)) {
			return true;
		}
		lastEvents.add(event);
		return false;
	}
}
