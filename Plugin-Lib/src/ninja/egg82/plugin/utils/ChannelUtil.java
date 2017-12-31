package ninja.egg82.plugin.utils;

import java.io.DataOutput;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.handlers.IMessageHandler;

public class ChannelUtil {
	//vars
	
	//constructor
	public ChannelUtil() {
		
	}
	
	//public
	public static void sendToServer(String serverId, String channelName, byte[] data) {
		if (serverId == null) {
			throw new ArgumentNullException("serverId");
		}
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (data == null) {
			return;
		}
		
		IMessageHandler handler = ServiceLocator.getService(IMessageHandler.class);
		handler.sendToServer(serverId, channelName, data);
	}
	public static void broadcastToBungee(String channelName, byte[] data) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (data == null) {
			return;
		}
		
		IMessageHandler handler = ServiceLocator.getService(IMessageHandler.class);
		handler.broadcastToBungee(channelName, data);
	}
	public static void broadcastToBukkit(String channelName, byte[] data) {
		if (channelName == null) {
			throw new ArgumentNullException("channelName");
		}
		if (data == null) {
			return;
		}
		
		IMessageHandler handler = ServiceLocator.getService(IMessageHandler.class);
		handler.broadcastToBukkit(channelName, data);
	}
	
	public static boolean writeAll(DataOutput out, Object... data) {
		for (int i = 0; i < data.length; i++) {
			Object obj = data[i];
			
			try {
				if (obj instanceof byte[]) {
					out.write((byte[]) obj);
				} else if (obj instanceof Boolean) {
					out.writeBoolean((boolean) obj);
				} else if (obj instanceof Byte) {
					out.writeByte((byte) obj);
				} else if (obj instanceof Short) {
					out.writeShort((short) obj);
				} else if (obj instanceof Character) {
					out.writeChar((char) obj);
				} else if (obj instanceof Integer) {
					out.writeInt((int) obj);
				} else if (obj instanceof Long) {
					out.writeLong((long) obj);
				} else if (obj instanceof Float) {
					out.writeFloat((float) obj);
				} else if (obj instanceof Double) {
					out.writeDouble((double) obj);
				} else if (obj instanceof String) {
					out.writeUTF((String) obj);
				} else {
					ServiceLocator.getService(IExceptionHandler.class).silentException(new Exception("Provided type of " + obj.getClass().getName() + " is not recognized."));
					return false;
				}
			} catch (Exception ex) {
				ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
				return false;
			}
		}
		
		return true;
	}
	
	//private
	
}
