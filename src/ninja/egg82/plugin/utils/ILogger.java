package ninja.egg82.plugin.utils;

import java.util.logging.Level;

public interface ILogger {
	//functions
	void initialize(java.util.logging.Logger logger);
	void log(String message);
	void log(String message, Level level);
}