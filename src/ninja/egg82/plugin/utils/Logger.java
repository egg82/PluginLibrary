package ninja.egg82.plugin.utils;

import ninja.egg82.plugin.utils.interfaces.ILogger;

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
	public void destroy() {
		
	}
	
	public java.util.logging.Logger getLogger() {
		return logger;
	}
	
	//private
	
}