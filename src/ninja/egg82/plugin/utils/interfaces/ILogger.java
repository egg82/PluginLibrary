package ninja.egg82.plugin.utils.interfaces;

public interface ILogger {
	void initialize(java.util.logging.Logger logger);
	void destroy();
	java.util.logging.Logger getLogger();
}