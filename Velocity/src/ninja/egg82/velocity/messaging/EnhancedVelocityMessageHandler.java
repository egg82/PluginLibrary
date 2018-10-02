package ninja.egg82.velocity.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.ChannelRegistrar;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import ninja.egg82.analytics.exceptions.IExceptionHandler;
import ninja.egg82.concurrent.DynamicConcurrentDeque;
import ninja.egg82.concurrent.IConcurrentDeque;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.tuples.Unit;
import ninja.egg82.plugin.enums.MessageHandlerType;
import ninja.egg82.plugin.enums.SenderType;
import ninja.egg82.plugin.handlers.async.AsyncMessageHandler;
import ninja.egg82.plugin.messaging.IMessageHandler;
import ninja.egg82.plugin.utils.ChannelUtil;
import ninja.egg82.utils.CollectionUtil;
import ninja.egg82.utils.ReflectUtil;
import ninja.egg82.velocity.BasePlugin;
import ninja.egg82.velocity.core.VelocityMessageSender;
import ninja.egg82.velocity.enums.VelocityMessageHandlerType;

public class EnhancedVelocityMessageHandler implements IMessageHandler {
    // vars

    // Channel names
    private IConcurrentDeque<String> channelNames = new DynamicConcurrentDeque<String>();
    // Channel identifiers
    private ConcurrentMap<String, ChannelIdentifier> channelIdentifiers = new ConcurrentHashMap<String, ChannelIdentifier>();
    // Actual server senders
    private IConcurrentDeque<VelocityMessageSender> servers = new DynamicConcurrentDeque<VelocityMessageSender>();
    // Map for storing handlers to run when a message is received
    private ConcurrentHashMap<Class<? extends AsyncMessageHandler>, Unit<AsyncMessageHandler>> handlers = new ConcurrentHashMap<Class<? extends AsyncMessageHandler>, Unit<AsyncMessageHandler>>();

    // Thread pool for message sending thread. The thread count for actually sending
    // messages should never exceed 1 for data consistency
    private ScheduledExecutorService threadPool = null;

    // The plugin using this code
    private BasePlugin plugin = ServiceLocator.getService(BasePlugin.class);
    // The channel registrar/manager
    private ChannelRegistrar manager = null;
    // This server's sender ID, for replying directly to the server
    private volatile String senderId = null;
    // Name of the plugin for namespaced channels
    private String pluginName = null;

    // constructor
    public EnhancedVelocityMessageHandler(String pluginName, String senderId) {
        if (pluginName == null) {
            throw new IllegalArgumentException("pluginName cannot be null.");
        }
        if (senderId == null) {
            throw new IllegalArgumentException("senderId cannot be null.");
        }

        this.senderId = senderId;
        this.pluginName = pluginName.toLowerCase();

        manager = plugin.getProxy().getChannelRegistrar();

        threadPool = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(pluginName + "-EnhancedVelocity-%d").build());
        threadPool.scheduleWithFixedDelay(onBacklogThread, 150L, 150L, TimeUnit.MILLISECONDS);

        if (plugin != null) {
            // Add all servers this Velocity knows about
            for (RegisteredServer server : plugin.getProxy().getAllServers()) {
                for (VelocityMessageSender sender : servers) {
                    RegisteredServer s = sender.getServer();

                    if (server.getServerInfo().getAddress().getAddress().getHostAddress().equals(s.getServerInfo().getAddress().getAddress().getHostAddress())
                        && server.getServerInfo().getAddress().getPort() == s.getServerInfo().getAddress().getPort()) {
                        return;
                    }
                }

                servers.add(new VelocityMessageSender(server));
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

        ChannelIdentifier id = MinecraftChannelIdentifier.create(pluginName, channelName);

        try {
            // Register the channel
            manager.register(id);
        } catch (Exception ex) {
            IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
            if (handler != null) {
                handler.sendException(ex);
            }
            throw new RuntimeException("Cannot create channel.", ex);
        }

        channelIdentifiers.put(channelName, id);
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

        ChannelIdentifier id = channelIdentifiers.remove(channelName);

        try {
            // Unregister the channel
            manager.unregister(id);
        } catch (Exception ex) {
            IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
            if (handler != null) {
                handler.sendException(ex);
            }
            throw new RuntimeException("Cannot destroy channel.", ex);
        }
    }

    public void sendToId(String senderId, String channelName, byte[] data) {
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

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        try {
            out.writeByte(SenderType.PROXY.getType());
        } catch (Exception ex) {
            throw new RuntimeException("Could not write headers.");
        }
        if (!ChannelUtil.writeAll(out, senderId, senderId, data)) {
            throw new RuntimeException("Could not write headers.");
        }

        byte[] message = stream.toByteArray();

        for (VelocityMessageSender sender : servers) {
            sender.submit(channelIdentifiers.get(channelName), message);
        }

        // Submit a new send task
        threadPool.submit(onSendThread);
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

            c.setSender(senderId);
            c.setSenderType(SenderType.PROXY);
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

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        try {
            out.writeByte(SenderType.PROXY.getType());
        } catch (Exception ex) {
            throw new RuntimeException("Could not write headers.");
        }
        if (!ChannelUtil.writeAll(out, senderId, "bukkit", data)) {
            throw new RuntimeException("Could not write headers.");
        }

        byte[] message = stream.toByteArray();

        for (VelocityMessageSender sender : servers) {
            sender.submit(channelIdentifiers.get(channelName), message);
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
        for (VelocityMessageSender sender : servers) {
            sender.close();
        }
        // Clear the connections
        servers.clear();
    }

    public void onPluginMessage(PluginMessageEvent e) {
        // Message sender
        String sender = "";
        // The sender type. Bukkit or Bungee?
        SenderType senderType = SenderType.UNKNOWN;
        // The tag - or who this message is meant to be for
        String tag = "proxy";
        // And finally the data
        byte[] data = e.getData();

        // Make sure we can read everything
        if (e.getData().length >= 5) {
            // Read headers
            ByteArrayInputStream stream = new ByteArrayInputStream(e.getData());
            DataInputStream in = new DataInputStream(stream);

            try {
                senderType = SenderType.fromType(in.readByte());
                sender = in.readUTF();
                tag = in.readUTF();
                data = new byte[in.available()];
                in.read(data);
            } catch (Exception ex) {

            }
        }

        String channelName = parseChannelName(e.getIdentifier().getId());

        if (tag.equals("proxy") || tag.equals(senderId)) {
            // Message is directed at us
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

                c.setSender(sender);
                c.setSenderType(senderType);
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
        } else {
            // Message isn't directed at us. "Bounce" it and forward the message to all
            // Bukkit servers
            for (VelocityMessageSender s : servers) {
                s.submit(e.getIdentifier(), e.getData());
            }

            // Submit a new send task
            threadPool.submit(onSendThread);
        }
    }

    public MessageHandlerType getType() {
        return VelocityMessageHandlerType.ENHANCED_VELOCITY;
    }

    // private
    private Runnable onSendThread = new Runnable() {
        public void run() {
            // Iterate connections and flush their queues
            for (VelocityMessageSender s : servers) {
                s.sendAll();
            }
        }
    };
    private Runnable onBacklogThread = new Runnable() {
        public void run() {
            // Iterate connections and flush their queues
            for (VelocityMessageSender s : servers) {
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
