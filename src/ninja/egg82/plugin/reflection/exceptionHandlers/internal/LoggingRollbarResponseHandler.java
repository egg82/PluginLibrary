package ninja.egg82.plugin.reflection.exceptionHandlers.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import com.rollbar.sender.RollbarResponse;
import com.rollbar.sender.RollbarResponseCode;
import com.rollbar.sender.RollbarResponseHandler;

public class LoggingRollbarResponseHandler implements RollbarResponseHandler {
	//vars
	private ArrayList<LogRecord> logs = new ArrayList<LogRecord>();
	private ArrayList<Exception> exceptions = new ArrayList<Exception>();
	
	private Throwable lastException = null;
	private LogRecord lastLog = null;
	
	public boolean limitReached = false;
	
	//constructor
	public LoggingRollbarResponseHandler() {
		
	}
	
	//public
	public void handleResponse(RollbarResponse response) {
		if (!response.isSuccessful()) {
			if (response.statusCode() == RollbarResponseCode.TooManyRequests) {
				limitReached = true;
			}
			
			if (lastException != null) {
				exceptions.add(new Exception(lastException));
				lastException = null;
			} else if (lastLog != null) {
				logs.add(lastLog);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Exception> getUnsentExceptions() {
		return (List<Exception>) exceptions.clone();
	}
	public void setUnsentExceptions(List<Exception> list) {
		exceptions.clear();
		exceptions.addAll(list);
	}
	@SuppressWarnings("unchecked")
	public List<LogRecord> getUnsentLogs() {
		return (List<LogRecord>) logs.clone();
	}
	public void setUnsentLogs(List<LogRecord> list) {
		logs.clear();
		logs.addAll(list);
	}
	
	public void addLog(LogRecord log) {
		logs.add(log);
	}
	public void addException(Exception ex) {
		exceptions.add(ex);
	}
	
	public void setLastException(Throwable ex) {
		lastException = ex;
		lastLog = null;
	}
	public void setLastLog(LogRecord record) {
		lastException = null;
		lastLog = record;
	}
	
	public void clearLogs() {
		logs.clear();
	}
	public void clearExceptions() {
		exceptions.clear();
	}
	
	//private
	
}
