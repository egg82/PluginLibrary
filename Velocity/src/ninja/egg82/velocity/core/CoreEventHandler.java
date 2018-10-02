package ninja.egg82.velocity.core;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import ninja.egg82.analytics.exceptions.IExceptionHandler;
import ninja.egg82.concurrent.DynamicConcurrentDeque;
import ninja.egg82.concurrent.IConcurrentDeque;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.handlers.events.async.IAsyncEventHandler;
import ninja.egg82.utils.CollectionUtil;

public class CoreEventHandler {
    // vars
    private ConcurrentHashMap<String, IConcurrentDeque<Class<? extends IAsyncEventHandler<?>>>> events = new ConcurrentHashMap<String, IConcurrentDeque<Class<? extends IAsyncEventHandler<?>>>>();
    private ConcurrentHashMap<String, IConcurrentDeque<IAsyncEventHandler<?>>> initializedEvents = new ConcurrentHashMap<String, IConcurrentDeque<IAsyncEventHandler<?>>>();

    // constructor
    public CoreEventHandler() {

    }

    // public
    public boolean addEventHandler(Class<?> event, Class<? extends IAsyncEventHandler<?>> clazz) {
        String key = event.getName();

        IConcurrentDeque<Class<? extends IAsyncEventHandler<?>>> pool = events.get(key);
        if (pool == null) {
            pool = new DynamicConcurrentDeque<Class<? extends IAsyncEventHandler<?>>>();
        }
        pool = CollectionUtil.putIfAbsent(events, key, pool);
        if (pool.contains(clazz)) {
            return false;
        }

        pool.add(clazz);
        initializedEvents.remove(key);
        return true;
    }

    public boolean removeEventHandler(Class<? extends IAsyncEventHandler<?>> clazz) {
        boolean modified = false;
        for (Entry<String, IConcurrentDeque<Class<? extends IAsyncEventHandler<?>>>> kvp : events.entrySet()) {
            if (kvp.getValue().remove(clazz)) {
                initializedEvents.remove(kvp.getKey());
                modified = true;
            }
        }
        return modified;
    }
    public boolean removeEventHandler(Class<?> event, Class<? extends IAsyncEventHandler<?>> clazz) {
        String key = event.getName();

        IConcurrentDeque<Class<? extends IAsyncEventHandler<?>>> pool = events.get(key);
        if (pool == null) {
            return false;
        }

        if (!pool.remove(clazz)) {
            return false;
        }

        initializedEvents.remove(key);
        return true;
    }
    public boolean hasEventHandler(Class<?> event) {
        return (event != null && events.containsKey(event.getName())) ? true : false;
    }

    public void clear() {
        initializedEvents.clear();
        events.clear();
    }

    @SuppressWarnings("unchecked")
    public <T> void onAnyEvent(T event, Class<?> clazz) {
        String key = clazz.getName();

        IConcurrentDeque<IAsyncEventHandler<?>> run = initializedEvents.get(key);
        IConcurrentDeque<Class<? extends IAsyncEventHandler<?>>> c = events.get(key);

        // run might be null, but c will never be as long as the event actually exists
        if (c == null) {
            return;
        }

        Exception lastEx = null;

        // Lazy initialize. No need to create an event that's never been used
        if (run == null) {
            // Create a new event and store it
            run = new DynamicConcurrentDeque<IAsyncEventHandler<?>>();

            for (Class<? extends IAsyncEventHandler<?>> e : c) {
                try {
                    // We would prefer the command to fail here, hence the cast
                    run.add(e.newInstance());
                } catch (Exception ex) {
                    IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
                    if (handler != null) {
                        handler.sendException(ex);
                    }
                    lastEx = ex;
                }
            }

            run = CollectionUtil.putIfAbsent(initializedEvents, key, run);
        }

        for (IAsyncEventHandler<?> e : run) {
            ((IAsyncEventHandler<T>) e).setEvent(event);
            try {
                e.start();
            } catch (Exception ex) {
                IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
                if (handler != null) {
                    handler.sendException(ex);
                }
                lastEx = ex;
            }
        }

        if (lastEx != null) {
            throw new RuntimeException(lastEx);
        }
    }

    // private

}
