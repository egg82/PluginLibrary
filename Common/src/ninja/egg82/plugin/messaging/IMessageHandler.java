package ninja.egg82.plugin.messaging;

import java.io.Closeable;

import ninja.egg82.plugin.enums.MessageHandlerType;
import ninja.egg82.plugin.handlers.async.AsyncMessageHandler;

public interface IMessageHandler extends Closeable {
    // functions
    String getSenderId();

    void setSenderId(String senderId);

    void createChannel(String channelName);

    void destroyChannel(String channelName);

    void sendToId(String senderId, String channelName, byte[] data);

    void broadcastToProxies(String channelName, byte[] data);

    void broadcastToServers(String channelName, byte[] data);

    int addHandlersFromPackage(String packageName);

    int addHandlersFromPackage(String packageName, boolean recursive);

    boolean addHandler(Class<? extends AsyncMessageHandler> clazz);

    boolean removeHandler(Class<? extends AsyncMessageHandler> clazz);

    void clearHandlers();

    void clearChannels();

    MessageHandlerType getType();
}
