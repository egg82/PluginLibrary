package ninja.egg82.bungeecord.core;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.patterns.DynamicObjectPool;
import ninja.egg82.patterns.IObjectPool;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.tuples.Pair;
import ninja.egg82.utils.ThreadUtil;

public class BungeeMessageSender {
	//vars
	private ServerInfo info = null;
	
	private IObjectPool<Pair<String, byte[]>> backlog = new DynamicObjectPool<Pair<String, byte[]>>();
	private volatile boolean busy = false;
	
	private ScheduledExecutorService threadPool = ThreadUtil.createSingleScheduledPool(new ThreadFactoryBuilder().setNameFormat(ServiceLocator.getService(Plugin.class).getDescription().getName() + "-bungee-%d").build());
	
	//constructor
	public BungeeMessageSender(ServerInfo info) {
		this.info = info;
		
		threadPool.scheduleWithFixedDelay(onBacklogThread, 150L, 150L, TimeUnit.MILLISECONDS);
	}
	public void finalize() {
		destroy();
	}
	
	//public
	public ServerInfo getInfo() {
		return info;
	}
	
	public void send(String channelName, byte[] message) {
		if (busy || backlog.size() > 0 || info.getPlayers().isEmpty()) {
			backlog.add(new Pair<String, byte[]>(channelName, message));
		} else {
			busy = true;
			sendInternal(channelName, message);
		}
	}
	
	public void destroy() {
		backlog.clear();
	}
	
	//private
	private void sendInternal(String channelName, byte[] message) {
		Exception lastEx = null;
		try {
			info.sendData(channelName, message);
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			lastEx = ex;
		}
		sendNextInternal();
		
		if (lastEx != null) {
			throw new RuntimeException("Could not send message.", lastEx);
		}
	}
	
	private void sendNext() {
		if (backlog.size() == 0) {
			busy = false;
			return;
		}
		
		Pair<String, byte[]> first = backlog.popFirst();
		sendInternal(first.getLeft(), first.getRight());
	}
	private void sendNextInternal() {
		threadPool.submit(new Runnable() {
			public void run() {
				sendNext();
			}
		});
	}
	
	private Runnable onBacklogThread = new Runnable() {
		public void run() {
			if (!busy && backlog.size() > 0 && !info.getPlayers().isEmpty()) {
				busy = true;
				sendNext();
			}
		}
	};
}
