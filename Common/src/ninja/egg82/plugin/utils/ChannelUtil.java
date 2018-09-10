package ninja.egg82.plugin.utils;

import java.io.DataOutput;

import ninja.egg82.analytics.exceptions.IExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.messaging.IMessageHandler;

public class ChannelUtil {
    // vars

    // constructor
    public ChannelUtil() {

    }

    // public
    @SuppressWarnings("resource")
    public static void sendToId(String senderId, String channelName, byte[] data) {
        if (senderId == null) {
            throw new IllegalArgumentException("senderId cannot be null.");
        }
        if (channelName == null) {
            throw new IllegalArgumentException("channelName cannot be null.");
        }
        if (data == null) {
            return;
        }

        IMessageHandler handler = ServiceLocator.getService(IMessageHandler.class);
        if (handler == null) {
            throw new IllegalArgumentException("handler cannot be null.");
        }

        handler.sendToId(senderId, channelName, data);
    }

    @SuppressWarnings("resource")
    public static void broadcastToProxies(String channelName, byte[] data) {
        if (channelName == null) {
            throw new IllegalArgumentException("channelName cannot be null.");
        }
        if (data == null) {
            return;
        }

        IMessageHandler handler = ServiceLocator.getService(IMessageHandler.class);
        if (handler == null) {
            throw new IllegalArgumentException("handler cannot be null.");
        }

        handler.broadcastToProxies(channelName, data);
    }

    @SuppressWarnings("resource")
    public static void broadcastToServers(String channelName, byte[] data) {
        if (channelName == null) {
            throw new IllegalArgumentException("channelName cannot be null.");
        }
        if (data == null) {
            return;
        }

        IMessageHandler handler = ServiceLocator.getService(IMessageHandler.class);
        if (handler == null) {
            throw new IllegalArgumentException("handler cannot be null.");
        }

        handler.broadcastToServers(channelName, data);
    }

    public static boolean writeAll(DataOutput out, Object... data) {
        for (int i = 0; i < data.length; i++) {
            Object obj = data[i];

            try {
                if (obj instanceof byte[]) {
                    out.write((byte[]) obj);
                } else if (obj instanceof Boolean) {
                    out.writeBoolean(((Boolean) obj).booleanValue());
                } else if (obj instanceof Byte) {
                    out.writeByte(((Byte) obj).byteValue());
                } else if (obj instanceof Short) {
                    out.writeShort(((Short) obj).shortValue());
                } else if (obj instanceof Character) {
                    out.writeChar(((Character) obj).charValue());
                } else if (obj instanceof Integer) {
                    out.writeInt(((Integer) obj).intValue());
                } else if (obj instanceof Long) {
                    out.writeLong(((Long) obj).longValue());
                } else if (obj instanceof Float) {
                    out.writeFloat(((Float) obj).floatValue());
                } else if (obj instanceof Double) {
                    out.writeDouble(((Double) obj).doubleValue());
                } else if (obj instanceof String) {
                    out.writeUTF((String) obj);
                } else {
                    IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
                    if (handler != null) {
                        handler.sendException(new RuntimeException("Provided type of " + obj.getClass().getName() + " is not recognized."));
                    }
                    return false;
                }
            } catch (Exception ex) {
                IExceptionHandler handler = ServiceLocator.getService(IExceptionHandler.class);
                if (handler != null) {
                    handler.sendException(ex);
                }
                return false;
            }
        }

        return true;
    }

    // private

}
