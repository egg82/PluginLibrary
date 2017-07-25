package ninja.egg82.plugin.reflection.exceptionHandlers;

import java.util.List;
import java.util.logging.LogRecord;

import ninja.egg82.plugin.reflection.exceptionHandlers.builders.IBuilder;

public interface IExceptionHandler {
	//functions
	void connect(IBuilder builder);
	
	void addThread(Thread thread);
	void silentException(Exception ex);
	void throwException(RuntimeException ex);
	
	List<Exception> getUnsentExceptions();
	void setUnsentExceptions(List<Exception> list);
	List<LogRecord> getUnsentLogs();
	void setUnsentLogs(List<LogRecord> list);
	
	boolean isLimitReached();
}
