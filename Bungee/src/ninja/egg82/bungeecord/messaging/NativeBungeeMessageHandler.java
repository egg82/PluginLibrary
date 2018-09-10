package ninja.egg82.bungeecord.messaging;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.NotImplementedException;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Plugin;
import ninja.egg82.analytics.exceptions.IExceptionHandler;
import ninja.egg82.bungeecord.core.BungeeMessageSender;
import ninja.egg82.bungeecord.enums.BungeeMessageHandlerType;
import ninja.egg82.concurrent.DynamicConcurrentDeque;
import ninja.egg82.concurrent.IConcurrentDeque;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.tuples.Unit;
import ninja.egg82.plugin.enums.MessageHandlerType;
import ninja.egg82.plugin.enums.SenderType;
import ninja.egg82.plugin.handlers.async.AsyncMessageHandler;
import ninja.egg82.plugin.messaging.IMessageHandler;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;

public class NativeBungeeMessageHandler implements IMessageHandler {
    // vars

    // Channel names
    private IConcurrentDeque<String> channelNames = new DynamicConcurrentDeque<String>();
    // Actual server senders
    private IConcurrentDeque<BungeeMessageSender> servers = new DynamicConcurrentDeque<BungeeMessageSender>();
    // Map for storing handlers to run when a message is received
    private ConcurrentHashMap<Class<? extends AsyncMessageHandler>, Unit<AsyncMessageHandler>> handlers = new ConcurrentHashMap<Class<? extends AsyncMessageHandler>, Unit<AsyncMessageHandler>>();

    // Thread pool for message sending thread. The thread count for actually sending
    // messages should never exceed 1 for data consistency
    private ScheduledExecutorService threadPool = null;

    // The plugin using this code
    private Plugin plugin = ServiceLocator.getService(Plugin.class);
    // This server's sender ID, for replying directly to the server
    private volatile String senderId = null;
    // Name of the plugin for namespaced channels
    private String pluginName = null;

    // constructor
    public NativeBungeeMessageHandler(String pluginName, String senderId) {
        if (pluginName == null) {
            throw new IllegalArgumentException("pluginName cannot be null.");
        }
        if (senderId == null) {
            throw new IllegalArgumentException("senderId cannot be null.");
        }

        this.senderId = senderId;
        this.pluginName = pluginName.toLowerCase();

        threadPool = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(pluginName + "-NativeBungee-%d").build());
        threadPool.scheduleWithFixedDelay(onBacklogThread, 150L, 150L, TimeUnit.MILLISECONDS);

        if (plugin != null) {
            // Add all servers this Bungee knows about
            for (Entry<String, ServerInfo> kvp : plugin.getProxy().getServers().entrySet()) {
                for (BungeeMessageSender sender : servers) {
                    ServerInfo i = sender.getInfo();

                    if (kvp.getValue().getAddress().getAddress().getHostAddress().equals(i.getAddress().getAddress().getHostAddress())
                        && kvp.getValue().getAddress().getPort() == i.getAddress().getPort()) {
                        return;
                    }
                }

                servers.add(new BungeeMessageSender(kvp.getValue()));
            }
        }
    }

    // public
    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        if (senderId == null) {
            throw new IllegalArgumentException("senderId cannot be null.");
        }

        this.senderId = senderId;
    }

    public void createChannel(String channelName) {
        if (channelName == null) {
            throw new IllegalArgumentException("channelName cannot be null.");
        }

        if (channelNames.contains(channelName)) {
            // Channel already exists. No need to re-create it
            return;
        }

        try {
            // Register the channel
            plugin.getProxy().registerChannel(pluginName + ":" + channelName);
        } catch (Exception ex) {
            IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
            if (handler != null) {
                handler.sendException(ex);
            }
            throw new RuntimeException("Cannot create channel.", ex);
        }

        channelNames.add(channelName);
    }

    public void destroyChannel(String channelName) {
        if (channelName == null) {
            throw new IllegalArgumentException("channelName cannot be null.");
        }

        if (!channelNames.remove(channelName)) {
            // Channel doesn't exist. No need to delete what isn't there
            return;
        }

        try {
            // Unregister the channel
            plugin.getProxy().unregisterChannel(pluginName + ":" + channelName);
        } catch (Exception ex) {
            IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
            if (handler != null) {
                handler.sendException(ex);
            }
            throw new RuntimeException("Cannot destroy channel.", ex);
        }
    }

    public void sendToId(String senderId, String channelName, byte[] data) {
        throw new NotImplementedException("Native messaging cannot send to specific servers.");
    }

    public void broadcastToProxies(String channelName, byte[] data) {
        if (channelName == null) {
            throw new IllegalArgumentException("channelName cannot be null.");
        }
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null.");
        }

        if (!channelNames.contains(channelName)) {
            // Channel doesn't exist, throw an exception
            throw new RuntimeException("Channel \"" + channelName + "\" does not exist.");
        }

        Exception lastEx = null;
        // Iterate handlers and fire them
        for (Entry<Class<? extends AsyncMessageHandler>, Unit<AsyncMessageHandler>> kvp : handlers.entrySet()) {
            AsyncMessageHandler c = null;

            // Lazy initialize
            if (kvp.getValue().getType() == null) {
                c = createHandler(kvp.getKey());
                kvp.getValue().setType(c);
            } else {
                c = kvp.getValue().getType();
            }

            c.setSender("");
            c.setSenderType(SenderType.UNKNOWN);
            c.setChannelName(channelName);
            c.setData(data);

            try {
                c.start();
            } catch (Exception ex) {
                IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
                if (handler != null) {
                    handler.sendException(ex);
                }
                lastEx = ex;
            }
        }
        if (lastEx != null) {
            throw new RuntimeException("Cannot run message handler.", lastEx);
        }
    }

    public void broadcastToServers(String channelName, byte[] data) {
        if (channelName == null) {
            throw new IllegalArgumentException("channelName cannot be null.");
        }
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null.");
        }

        if (!channelNames.contains(channelName)) {
            // Channel doesn't exist, throw an exception
            throw new RuntimeException("Channel \"" + channelName + "\" does not exist.");
        }

        for (BungeeMessageSender sender : servers) {
            sender.submit(pluginName + ":" + channelName, data);
        }

        // Submit a new send task
        threadPool.submit(onSendThread);
    }

    public int addHandlersFromPackage(String packageName) {
        return addHandlersFromPackage(packageName, true);
    }

    public int addHandlersFromPackage(String packageName, boolean recursive) {
        if (packageName == null) {
            throw new IllegalArgumentException("packageName cannot be null.");
        }

        // Number of commands successfully added
        int numMessages = 0;

        // Reflect package (recursively, if specified) and get everything that extends
        // AsyncMessageHandler
        List<Class<AsyncMessageHandler>> enums = ReflectUtil.getClasses(AsyncMessageHandler.class, packageName, recursive, false, false);
        // Iterate collection and add the handlers
        for (Class<AsyncMessageHandler> c : enums) {
            if (addHandler(c)) {
                // Increment success counter
                numMessages++;
            }
        }

        return numMessages;
    }

    public boolean addHandler(Class<? extends AsyncMessageHandler> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null.");
        }

        // Create a new unit. This allows us to lazy-initialize handlers and only use
        // memory we need
        Unit<AsyncMessageHandler> unit = new Unit<AsyncMessageHandler>(null);
        // Add the new handler. If it already existed in the collection then we return
        // false; otherwise we return true
        return (CollectionUtil.putIfAbsent(handlers, clazz, unit).hashCode() == unit.hashCode()) ? true : false;
    }

    public boolean removeHandler(Class<? extends AsyncMessageHandler> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null.");
        }

        // Remove the old handler. If the collection was modified as a result of this
        // operation we return true; else false
        return (handlers.remove(clazz) != null) ? true : false;
    }

    public void clearHandlers() {
        // Pretty easy removal. No need for special rules or logic
        handlers.clear();
    }

    public void clearChannels() {
        // Iterate channel names and destroy each channel
        // Because of the nature of ObjectPool no ConcurrentModificationException will
        // be thrown and all channels at time of iteration will be removed
        for (String c : channelNames) {
            try {
                destroyChannel(c);
            } catch (Exception ex) {

            }
        }
    }

    public void close() {
        try {
            // Gracefully shut the thread pool down, waiting for 5 seconds if needed
            threadPool.shutdown();
            if (!threadPool.awaitTermination(5000L, TimeUnit.MILLISECONDS)) {
                // Less-than-gracefully kill the thread pool
                threadPool.shutdownNow();
            }
        } catch (Exception ex) {

        }

        // Clear the channels and handlers
        clearChannels();
        clearHandlers();

        // Close all the connections
        for (BungeeMessageSender sender : servers) {
            sender.close();
        }
        // Clear the connections
        servers.clear();
    }

    public void onPluginMessage(PluginMessageEvent e) {
        Exception lastEx = null;

        String channelName = parseChannelName(e.getTag());

        // Iterate handlers and fire them
        for (Entry<Class<? extends AsyncMessageHandler>, Unit<AsyncMessageHandler>> kvp : handlers.entrySet()) {
            AsyncMessageHandler c = null;

            // Lazy initialize
            if (kvp.getValue().getType() == null) {
                c = createHandler(kvp.getKey());
                kvp.getValue().setType(c);
            } else {
                c = kvp.getValue().getType();
            }

            c.setSender("");
            c.setSenderType(SenderType.UNKNOWN);
            c.setChannelName(channelName);
            c.setData(e.getData());

            try {
                c.start();
            } catch (Exception ex) {
                IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
                if (handler != null) {
                    handler.sendException(ex);
                }
                lastEx = ex;
            }
        }
        if (lastEx != null) {
            throw new RuntimeException("Cannot run message handler.", lastEx);
        }
    }

    public MessageHandlerType getType() {
        return BungeeMessageHandlerType.NATIVE_BUNGEE;
    }

    // private
    private Runnable onSendThread = new Runnable() {
        public void run() {
            // Iterate connections and flush their queues
            for (BungeeMessageSender s : servers) {
                s.sendAll();
            }
        }
    };
    private Runnable onBacklogThread = new Runnable() {
        public void run() {
            // Iterate connections and flush their queues
            for (BungeeMessageSender s : servers) {
                s.sendAll();
            }
        }
    };

    private AsyncMessageHandler createHandler(Class<? extends AsyncMessageHandler> c) {
        AsyncMessageHandler run = null;

        try {
            run = c.newInstance();
        } catch (Exception ex) {
            IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
            if (handler != null) {
                handler.sendException(ex);
            }
            throw new RuntimeException("Cannot initialize message handler.", ex);
        }

        return run;
    }

    private String parseChannelName(String channelName) {
        int index = channelName.indexOf(':');
        if (index > -1) {
            channelName = channelName.substring(index + 1);
        }
        return channelName;
    }
}
