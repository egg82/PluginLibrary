package ninja.egg82.plugin.utils;

import java.util.logging.Level;

public class Logger implements ILogger {
	//vars
	private java.util.logging.Logger logger = null;
	private boolean initialized = false;
	
	//constructor
	public Logger() {
		
	}
	
	//public
	public void initialize(java.util.logging.Logger logger) {
		if (logger == null || initialized) {
			return;
		}
		
		this.logger = logger;
		initialized = true;
	}
	
	public void log(String message) {
		log(message, Level.INFO);
	}
	public void log(String message, Level level) {
		logger.log(level, message);
	}
	
	//private
	
}