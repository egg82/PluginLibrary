package ninja.egg82.bukkit.processors;

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

import ninja.egg82.bukkit.BasePlugin;
import ninja.egg82.bukkit.core.CoreEventHandler;
import ninja.egg82.concurrent.DynamicConcurrentSet;
import ninja.egg82.concurrent.IConcurrentSet;
import ninja.egg82.patterns.DoubleBuffer;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.tuples.pair.Pair;
import ninja.egg82.plugin.handlers.events.EventHandler;
import ninja.egg82.plugin.handlers.events.HighEventHandler;
import ninja.egg82.plugin.handlers.events.HighestEventHandler;
import ninja.egg82.plugin.handlers.events.IEventHandler;
import ninja.egg82.plugin.handlers.events.LowEventHandler;
import ninja.egg82.plugin.handlers.events.LowestEventHandler;
import ninja.egg82.plugin.handlers.events.MonitorEventHandler;
import ninja.egg82.utils.ReflectUtil;

public class EventProcessor implements Listener, Closeable {
    // vars
    private static final Logger logger = LoggerFactory.getLogger(EventProcessor.class);

    private CoreEventHandler highestEventHandler = new CoreEventHandler();
    private CoreEventHandler highEventHandler = new CoreEventHandler();
    private CoreEventHandler normalEventHandler = new CoreEventHandler();
    private CoreEventHandler lowEventHandler = new CoreEventHandler();
    private CoreEventHandler lowestEventHandler = new CoreEventHandler();
    private CoreEventHandler monitorEventHandler = new CoreEventHandler();

    private IConcurrentSet<String> events = new DynamicConcurrentSet<String>();
    private DoubleBuffer<Pair<Event, EventPriority>> lastEvents = new DoubleBuffer<Pair<Event, EventPriority>>();

    private BasePlugin plugin = ServiceLocator.getService(BasePlugin.class);
    private PluginManager manager = Bukkit.getServer().getPluginManager();

    // constructor
    @SuppressWarnings("deprecation")
    public EventProcessor() {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                lastEvents.swapBuffers();
                lastEvents.getBackBuffer().clear();
            }
        }, 20L, 20L);
    }

    // public
    public boolean addHandler(Class<? extends Event> event, Class<? extends IEventHandler<? extends Event>> clazz) {
        if (event == null) {
            throw new IllegalArgumentException("event cannot be null.");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null.");
        }

        registerEvent(event);

        if (ReflectUtil.doesExtend(EventHandler.class, clazz)) {
            return normalEventHandler.addEventHandler(event, clazz);
        } else if (ReflectUtil.doesExtend(MonitorEventHandler.class, clazz)) {
            return monitorEventHandler.addEventHandler(event, clazz);
        } else if (ReflectUtil.doesExtend(HighestEventHandler.class, clazz)) {
            return highestEventHandler.addEventHandler(event, clazz);
        } else if (ReflectUtil.doesExtend(HighEventHandler.class, clazz)) {
            return highEventHandler.addEventHandler(event, clazz);
        } else if (ReflectUtil.doesExtend(LowestEventHandler.class, clazz)) {
            return lowestEventHandler.addEventHandler(event, clazz);
        } else if (ReflectUtil.doesExtend(LowEventHandler.class, clazz)) {
            return lowEventHandler.addEventHandler(event, clazz);
        }

        return false;
    }

    public boolean removeHandler(Class<? extends IEventHandler<? extends Event>> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null.");
        }

        if (ReflectUtil.doesExtend(EventHandler.class, clazz)) {
            return normalEventHandler.removeEventHandler(clazz);
        } else if (ReflectUtil.doesExtend(MonitorEventHandler.class, clazz)) {
            return monitorEventHandler.removeEventHandler(clazz);
        } else if (ReflectUtil.doesExtend(HighestEventHandler.class, clazz)) {
            return highestEventHandler.removeEventHandler(clazz);
        } else if (ReflectUtil.doesExtend(HighEventHandler.class, clazz)) {
            return highEventHandler.removeEventHandler(clazz);
        } else if (ReflectUtil.doesExtend(LowestEventHandler.class, clazz)) {
            return lowestEventHandler.removeEventHandler(clazz);
        } else if (ReflectUtil.doesExtend(LowEventHandler.class, clazz)) {
            return lowEventHandler.removeEventHandler(clazz);
        }

        return false;
    }

    public boolean removeHandler(Class<? extends Event> event, Class<? extends IEventHandler<? extends Event>> clazz) {
        if (event == null) {
            throw new IllegalArgumentException("event cannot be null.");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null.");
        }

        if (ReflectUtil.doesExtend(EventHandler.class, clazz)) {
            return normalEventHandler.removeEventHandler(event, clazz);
        } else if (ReflectUtil.doesExtend(MonitorEventHandler.class, clazz)) {
            return monitorEventHandler.removeEventHandler(event, clazz);
        } else if (ReflectUtil.doesExtend(HighestEventHandler.class, clazz)) {
            return highestEventHandler.removeEventHandler(event, clazz);
        } else if (ReflectUtil.doesExtend(HighEventHandler.class, clazz)) {
            return highEventHandler.removeEventHandler(event, clazz);
        } else if (ReflectUtil.doesExtend(LowestEventHandler.class, clazz)) {
            return lowestEventHandler.removeEventHandler(event, clazz);
        } else if (ReflectUtil.doesExtend(LowEventHandler.class, clazz)) {
            return lowEventHandler.removeEventHandler(event, clazz);
        }

        return false;
    }

    public boolean hasHandler(Class<? extends Event> event) {
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
        if (!events.add(e.getName())) {
            return;
        }

        manager.registerEvent(e, this, EventPriority.HIGHEST, highestEventExecutor, plugin, false);
        manager.registerEvent(e, this, EventPriority.HIGH, highEventExecutor, plugin, false);
        manager.registerEvent(e, this, EventPriority.NORMAL, normalEventExecutor, plugin, false);
        manager.registerEvent(e, this, EventPriority.LOW, lowEventExecutor, plugin, false);
        manager.registerEvent(e, this, EventPriority.LOWEST, lowestEventExecutor, plugin, false);
        manager.registerEvent(e, this, EventPriority.MONITOR, monitorEventExecutor, plugin, false);
    }

    public void unregisterEvent(Class<? extends Event> e) {
        if (!events.remove(e.getName())) {
            return;
        }

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

    public int addHandlersFromPackage(String packageName) {
        return addHandlersFromPackage(packageName, true);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public int addHandlersFromPackage(String packageName, boolean recursive) {
        if (packageName == null) {
            throw new IllegalArgumentException("packageName cannot be null.");
        }

        int numEvents = 0;

        List<Class<? extends IEventHandler>> enums = ReflectUtil.getClassesParameterized(IEventHandler.class, packageName, recursive, false, false);
        for (Class<? extends IEventHandler> c : enums) {
            Class<? extends Event> eventType = null;
            try {
                eventType = (Class<? extends Event>) ((ParameterizedType) c.getGenericSuperclass()).getActualTypeArguments()[0];
            } catch (Exception ex) {
                continue;
            }

            if (addHandler(eventType, (Class<IEventHandler<? extends Event>>) c)) {
                numEvents++;
            }
        }

        return numEvents;
    }

    // private
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
        if (isDuplicate(event, priority)) {
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

    private boolean isDuplicate(Event event, EventPriority priority) {
        if (!lastEvents.getCurrentBuffer().add(new Pair<Event, EventPriority>(event, priority))) {
            return true;
        }
        return false;
    }
}
