package ninja.egg82.plugin.reflection.exceptionHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.Timer;

import com.rollbar.Rollbar;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.enums.SpigotInitType;
import ninja.egg82.plugin.reflection.exceptionHandlers.builders.IBuilder;
import ninja.egg82.plugin.reflection.exceptionHandlers.internal.LoggingRollbarResponseHandler;
import ninja.egg82.startup.InitRegistry;

public class RollbarExceptionHandler extends Handler implements IExceptionHandler {
	//vars
	private Rollbar rollbar = null;
	private LoggingRollbarResponseHandler responseHandler = new LoggingRollbarResponseHandler();
	
	private Timer resendTimer = null;
	
	//constructor
	public RollbarExceptionHandler() {
		Logger.getLogger("ninja.egg82.core.PasswordHasher").addHandler(this);
		Logger.getLogger("ninja.egg82.patterns.events.EventHandler").addHandler(this);
	}
	
	//public
	public void connect(IBuilder builder) {
		String[] params = builder.getParams();
		if (params == null || params.length != 2) {
			throw new IllegalArgumentException("params must have a length of 2. Use ninja.egg82.plugin.reflection.exceptionHandlers.builders.RollbarBuilder");
		}
		rollbar = new Rollbar(params[0], params[1]).codeVersion(ServiceLocator.getService(InitRegistry.class).getRegister(SpigotInitType.PLUGIN_VERSION, String.class)).responseHandler(responseHandler);
		//rollbar = new Rollbar(accessToken, environment, new AsyncPayloadSender()).codeVersion(ServiceLocator.getService(InitRegistry.class).getRegister(SpigotInitType.PLUGIN_VERSION, String.class));
		handleUncaughtErrors(Thread.currentThread());
		
		List<LogRecord> records = responseHandler.getUnsentLogs();
		responseHandler.clearLogs();
		for (LogRecord record : records) {
			responseHandler.setLastLog(record);
			if (record.getThrown() != null) {
				rollbar.log(record.getThrown(), getLevel(record.getLevel()));
			} else if (record.getMessage() != null) {
				rollbar.log(record.getMessage(), getLevel(record.getLevel()));
			}
		}
		List<Exception> exceptions = responseHandler.getUnsentExceptions();
		responseHandler.clearExceptions();
		for (Exception ex : exceptions) {
			responseHandler.setLastException(ex);
			rollbar.log(ex);
		}
		
		resendTimer = new Timer(60 * 60 * 1000, onResendTimer);
		resendTimer.setRepeats(true);
		resendTimer.start();
	}
	
	public void addThread(Thread thread) {
		if (rollbar != null) {
			handleUncaughtErrors(thread);
		}
	}
	public void silentException(Exception ex) {
		if (rollbar != null) {
			responseHandler.setLastException(ex);
			rollbar.log(ex);
		} else {
			responseHandler.addException(ex);
		}
	}
	public void throwException(RuntimeException ex) {
		if (rollbar != null) {
			responseHandler.setLastException(ex);
			rollbar.log(ex);
		} else {
			responseHandler.addException(ex);
		}
		throw ex;
	}
	
	public void publish(LogRecord record) {
		if (rollbar != null) {
			responseHandler.setLastLog(record);
			if (record.getThrown() != null) {
				rollbar.log(record.getThrown(), getLevel(record.getLevel()));
			} else if (record.getMessage() != null) {
				rollbar.log(record.getMessage(), getLevel(record.getLevel()));
			}
		} else {
			responseHandler.addLog(record);
		}
	}
	public void flush() {
		
	}
	public void close() throws SecurityException {
		
	}
	
	public List<Exception> getUnsentExceptions() {
		return responseHandler.getUnsentExceptions();
	}
	public void setUnsentExceptions(List<Exception> list) {
		responseHandler.setUnsentExceptions(list);
		
		if (rollbar != null) {
			List<Exception> exceptions = responseHandler.getUnsentExceptions();
			responseHandler.clearExceptions();
			for (Exception ex : exceptions) {
				responseHandler.setLastException(ex);
				rollbar.log(ex);
			}
		}
	}
	public List<LogRecord> getUnsentLogs() {
		return responseHandler.getUnsentLogs();
	}
	public void setUnsentLogs(List<LogRecord> list) {
		responseHandler.setUnsentLogs(list);
		
		if (rollbar != null) {
			List<LogRecord> records = responseHandler.getUnsentLogs();
			responseHandler.clearLogs();
			for (LogRecord record : records) {
				responseHandler.setLastLog(record);
				if (record.getThrown() != null) {
					rollbar.log(record.getThrown(), getLevel(record.getLevel()));
				} else if (record.getMessage() != null) {
					rollbar.log(record.getMessage(), getLevel(record.getLevel()));
				}
			}
		}
	}
	
	public boolean isLimitReached() {
		return responseHandler.limitReached;
	}
	
	//private
	private com.rollbar.payload.data.Level getLevel(Level level) {
		if (level == Level.SEVERE) {
			return com.rollbar.payload.data.Level.CRITICAL;
		}  else if (level == Level.WARNING) {
			return com.rollbar.payload.data.Level.WARNING;
		} else if (level == Level.INFO) {
			return com.rollbar.payload.data.Level.INFO;
		} else if (level == Level.CONFIG || level == Level.FINE || level == Level.FINER || level == Level.FINEST) {
			return com.rollbar.payload.data.Level.DEBUG;
		}
		
		return com.rollbar.payload.data.Level.ERROR;
	}
	
	private void handleUncaughtErrors(Thread thread) {
		thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable ex) {
				responseHandler.setLastException(ex);
				rollbar.log(ex);
			}
		});
	}
	
	private ActionListener onResendTimer = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			List<LogRecord> records = responseHandler.getUnsentLogs();
			responseHandler.clearLogs();
			for (LogRecord record : records) {
				responseHandler.setLastLog(record);
				if (record.getThrown() != null) {
					rollbar.log(record.getThrown(), getLevel(record.getLevel()));
				} else if (record.getMessage() != null) {
					rollbar.log(record.getMessage(), getLevel(record.getLevel()));
				}
			}
			List<Exception> exceptions = responseHandler.getUnsentExceptions();
			responseHandler.clearExceptions();
			for (Exception ex : exceptions) {
				responseHandler.setLastException(ex);
				rollbar.log(ex);
			}
		}
	};
}
