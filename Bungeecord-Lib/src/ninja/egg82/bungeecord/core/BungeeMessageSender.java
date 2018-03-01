package ninja.egg82.bungeecord.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.swing.Timer;

import net.md_5.bungee.api.config.ServerInfo;
import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.patterns.DynamicObjectPool;
import ninja.egg82.patterns.IObjectPool;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.tuples.Pair;

public class BungeeMessageSender {
	//vars
	private ServerInfo info = null;
	
	private IObjectPool<Pair<String, byte[]>> backlog = new DynamicObjectPool<Pair<String, byte[]>>();
	private volatile boolean busy = false;
	private Timer backlogTimer = null;
	
	private ExecutorService threadPool = Executors.newFixedThreadPool(20, ServiceLocator.getService(ThreadFactory.class));
	
	//constructor
	public BungeeMessageSender(ServerInfo info) {
		this.info = info;
		
		backlogTimer = new Timer(100, onBacklogTimer);
		backlogTimer.setRepeats(true);
		backlogTimer.start();
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
		threadPool.shutdownNow();
		
		backlogTimer.stop();
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
		threadPool.execute(new Runnable() {
			public void run() {
				sendNext();
			}
		});
	}
	
	private ActionListener onBacklogTimer = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (!busy && backlog.size() > 0 && !info.getPlayers().isEmpty()) {
				busy = true;
				sendNext();
			}
		}
	};
}
