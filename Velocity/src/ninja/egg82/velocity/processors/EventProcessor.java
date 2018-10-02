package ninja.egg82.velocity.processors;

import java.io.Closeable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Lists;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.PostOrder;

import ninja.egg82.concurrent.DynamicConcurrentSet;
import ninja.egg82.concurrent.IConcurrentSet;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.handlers.events.async.AsyncEventHandler;
import ninja.egg82.plugin.handlers.events.async.HighAsyncEventHandler;
import ninja.egg82.plugin.handlers.events.async.HighestAsyncEventHandler;
import ninja.egg82.plugin.handlers.events.async.IAsyncEventHandler;
import ninja.egg82.plugin.handlers.events.async.LowAsyncEventHandler;
import ninja.egg82.plugin.handlers.events.async.LowestAsyncEventHandler;
import ninja.egg82.utils.ReflectUtil;
import ninja.egg82.velocity.BasePlugin;
import ninja.egg82.velocity.core.CoreEventHandler;

public class EventProcessor implements Closeable {
    // vars
    private CoreEventHandler firstEventHandler = new CoreEventHandler();
    private CoreEventHandler earlyEventHandler = new CoreEventHandler();
    private CoreEventHandler normalEventHandler = new CoreEventHandler();
    private CoreEventHandler lateEventHandler = new CoreEventHandler();
    private CoreEventHandler lastEventHandler = new CoreEventHandler();

    private IConcurrentSet<String> events = new DynamicConcurrentSet<String>();
    private ConcurrentMap<String, List<com.velocitypowered.api.event.EventHandler<?>>> handlers = new ConcurrentHashMap<String, List<com.velocitypowered.api.event.EventHandler<?>>>();

    private BasePlugin plugin = ServiceLocator.getService(BasePlugin.class);
    private EventManager manager = null;

    // constructor
    public EventProcessor() {
        manager = plugin.getProxy().getEventManager();
    }

    // public
    public boolean addHandler(Class<?> event, Class<? extends IAsyncEventHandler<?>> clazz) {
        if (event == null) {
            throw new IllegalArgumentException("event cannot be null.");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null.");
        }

        registerEvent(event);

        if (ReflectUtil.doesExtend(AsyncEventHandler.class, clazz)) {
            return normalEventHandler.addEventHandler(event, clazz);
        } else if (ReflectUtil.doesExtend(HighestAsyncEventHandler.class, clazz)) {
            return lastEventHandler.addEventHandler(event, clazz);
        } else if (ReflectUtil.doesExtend(HighAsyncEventHandler.class, clazz)) {
            return lateEventHandler.addEventHandler(event, clazz);
        } else if (ReflectUtil.doesExtend(LowestAsyncEventHandler.class, clazz)) {
            return firstEventHandler.addEventHandler(event, clazz);
        } else if (ReflectUtil.doesExtend(LowAsyncEventHandler.class, clazz)) {
            return earlyEventHandler.addEventHandler(event, clazz);
        }

        return false;
    }
    public boolean removeHandler(Class<? extends IAsyncEventHandler<?>> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null.");
        }

        if (ReflectUtil.doesExtend(AsyncEventHandler.class, clazz)) {
            return normalEventHandler.removeEventHandler(clazz);
        } else if (ReflectUtil.doesExtend(HighestAsyncEventHandler.class, clazz)) {
            return lastEventHandler.removeEventHandler(clazz);
        } else if (ReflectUtil.doesExtend(HighAsyncEventHandler.class, clazz)) {
            return lateEventHandler.removeEventHandler(clazz);
        } else if (ReflectUtil.doesExtend(LowestAsyncEventHandler.class, clazz)) {
            return firstEventHandler.removeEventHandler(clazz);
        } else if (ReflectUtil.doesExtend(LowAsyncEventHandler.class, clazz)) {
            return earlyEventHandler.removeEventHandler(clazz);
        }

        return false;
    }
    public boolean removeHandler(Class<?> event, Class<? extends IAsyncEventHandler<?>> clazz) {
        if (event == null) {
            throw new IllegalArgumentException("event cannot be null.");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null.");
        }

        if (ReflectUtil.doesExtend(AsyncEventHandler.class, clazz)) {
            return normalEventHandler.removeEventHandler(event, clazz);
        } else if (ReflectUtil.doesExtend(HighestAsyncEventHandler.class, clazz)) {
            return lastEventHandler.removeEventHandler(event, clazz);
        } else if (ReflectUtil.doesExtend(HighAsyncEventHandler.class, clazz)) {
            return lateEventHandler.removeEventHandler(event, clazz);
        } else if (ReflectUtil.doesExtend(LowestAsyncEventHandler.class, clazz)) {
            return firstEventHandler.removeEventHandler(event, clazz);
        } else if (ReflectUtil.doesExtend(LowAsyncEventHandler.class, clazz)) {
            return earlyEventHandler.removeEventHandler(event, clazz);
        }

        return false;
    }
    public boolean hasHandler(Class<?> event) {
        if (event == null) {
            return false;
        }

        if (normalEventHandler.hasEventHandler(event)) {
            return true;
        } else if (lastEventHandler.hasEventHandler(event)) {
            return true;
        } else if (lateEventHandler.hasEventHandler(event)) {
            return true;
        } else if (firstEventHandler.hasEventHandler(event)) {
            return true;
        } else if (earlyEventHandler.hasEventHandler(event)) {
            return true;
        }

        return false;
    }

    public void clear() {
        lastEventHandler.clear();
        lateEventHandler.clear();
        normalEventHandler.clear();
        earlyEventHandler.clear();
        firstEventHandler.clear();
    }
    public void close() {
        clear();

        List<Class<Object>> events = ReflectUtil.getClasses(Object.class, "com.velocitypowered.api.event", true, false, false);
        for (Class<Object> e : events) {
            unregisterEvent(e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void registerEvent(Class<?> e) {
        if (!events.add(e.getName())) {
            return;
        }

        com.velocitypowered.api.event.EventHandler last = new com.velocitypowered.api.event.EventHandler() {
            public void execute(Object event) {
                onAnyEvent(PostOrder.LAST, event, event.getClass());
            }
        };
        com.velocitypowered.api.event.EventHandler late = new com.velocitypowered.api.event.EventHandler() {
            public void execute(Object event) {
                onAnyEvent(PostOrder.LATE, event, event.getClass());
            }
        };
        com.velocitypowered.api.event.EventHandler normal = new com.velocitypowered.api.event.EventHandler() {
            public void execute(Object event) {
                onAnyEvent(PostOrder.NORMAL, event, event.getClass());
            }
        };
        com.velocitypowered.api.event.EventHandler early = new com.velocitypowered.api.event.EventHandler() {
            public void execute(Object event) {
                onAnyEvent(PostOrder.EARLY, event, event.getClass());
            }
        };
        com.velocitypowered.api.event.EventHandler first = new com.velocitypowered.api.event.EventHandler() {
            public void execute(Object event) {
                onAnyEvent(PostOrder.FIRST, event, event.getClass());
            }
        };

        manager.register(plugin, e, PostOrder.LAST, last);
        manager.register(plugin, e, PostOrder.LATE, late);
        manager.register(plugin, e, PostOrder.NORMAL, normal);
        manager.register(plugin, e, PostOrder.EARLY, early);
        manager.register(plugin, e, PostOrder.FIRST, first);

        handlers.put(e.getName(), Lists.newArrayList(last, late, normal, early, first));
    }
    public void unregisterEvent(Class<?> e) {
        if (!events.remove(e.getName())) {
            return;
        }

        List<com.velocitypowered.api.event.EventHandler<?>> oldHandlers = handlers.remove(e.getName());
        for (int i = 0; i < oldHandlers.size(); i++) {
            manager.unregister(plugin, oldHandlers.get(i));
        }
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

        List<Class<? extends IAsyncEventHandler>> enums = ReflectUtil.getClassesParameterized(IAsyncEventHandler.class, packageName, recursive, false, false);
        for (Class<? extends IAsyncEventHandler> c : enums) {
            Class<?> eventType = null;
            try {
                eventType = (Class<?>) ((ParameterizedType) c.getGenericSuperclass()).getActualTypeArguments()[0];
            } catch (Exception ex) {
                continue;
            }

            if (addHandler(eventType, (Class<IAsyncEventHandler<?>>) c)) {
                numEvents++;
            }
        }

        return numEvents;
    }

    // private
    private <T> void onAnyEvent(PostOrder priority, T event, Class<?> clazz) {
        if (priority == PostOrder.NORMAL) {
            normalEventHandler.onAnyEvent(event, clazz);
        } else if (priority == PostOrder.LAST) {
            lastEventHandler.onAnyEvent(event, clazz);
        } else if (priority == PostOrder.LATE) {
            lateEventHandler.onAnyEvent(event, clazz);
        } else if (priority == PostOrder.FIRST) {
            firstEventHandler.onAnyEvent(event, clazz);
        } else if (priority == PostOrder.EARLY) {
            earlyEventHandler.onAnyEvent(event, clazz);
        }
    }
}
