package ninja.egg82.plugin.reflection.exceptionHandlers;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.rollbar.Rollbar;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.enums.SpigotInitType;
import ninja.egg82.startup.InitRegistry;

public class RollbarExceptionHandler extends Handler implements IExceptionHandler {
	//vars
	private Rollbar rollbar = null;
	
	private ArrayList<LogRecord> logs = new ArrayList<LogRecord>();
	private ArrayList<Exception> exceptions = new ArrayList<Exception>();
	
	//constructor
	public RollbarExceptionHandler() {
		Logger.getLogger("ninja.egg82.core.PasswordHasher").addHandler(this);
		Logger.getLogger("ninja.egg82.patterns.events.EventHandler").addHandler(this);
	}
	
	public void connect(String accessToken, String environment) {
		rollbar = new Rollbar(accessToken, environment).codeVersion(ServiceLocator.getService(InitRegistry.class).getRegister(SpigotInitType.PLUGIN_VERSION, String.class));
		rollbar.handleUncaughtErrors();
		
		for (LogRecord record : logs) {
			if (record.getThrown() != null) {
				rollbar.log(record.getThrown(), getLevel(record.getLevel()));
			} else if (record.getMessage() != null) {
				rollbar.log(record.getMessage(), getLevel(record.getLevel()));
			}
		}
		logs.clear();
		for (Exception ex : exceptions) {
			rollbar.log(ex);
		}
		exceptions.clear();
	}
	public void addThread(Thread thread) {
		if (rollbar != null) {
			rollbar.handleUncaughtErrors(thread);
		}
	}
	public void silentException(Exception ex) {
		if (rollbar != null) {
			rollbar.log(ex);
		} else {
			exceptions.add(ex);
		}
	}
	public void throwException(RuntimeException ex) {
		if (rollbar != null) {
			rollbar.log(ex);
		} else {
			exceptions.add(ex);
		}
		throw ex;
	}
	
	public void publish(LogRecord record) {
		if (rollbar != null) {
			if (record.getThrown() != null) {
				rollbar.log(record.getThrown(), getLevel(record.getLevel()));
			} else if (record.getMessage() != null) {
				rollbar.log(record.getMessage(), getLevel(record.getLevel()));
			}
		} else {
			logs.add(record);
		}
	}
	public void flush() {
		
	}
	public void close() throws SecurityException {
		
	}
	
	//public
	
	//private
	private com.rollbar.payload.data.Level getLevel(Level level) {
		if (level == Level.SEVERE) {
			return com.rollbar.payload.data.Level.CRITICAL;
		}  else if (level == Level.WARNING) {
			return com.rollbar.payload.data.Level.WARNING;
		} else if (level == Level.INFO) {
			return com.rollbar.payload.data.Level.INFO;
		}
		
		return com.rollbar.payload.data.Level.DEBUG;
	}
}
