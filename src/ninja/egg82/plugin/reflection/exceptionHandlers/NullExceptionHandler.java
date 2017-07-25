package ninja.egg82.plugin.reflection.exceptionHandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.apache.commons.lang3.NotImplementedException;

import ninja.egg82.plugin.reflection.exceptionHandlers.builders.IBuilder;

public class NullExceptionHandler extends Handler implements IExceptionHandler {
	//vars
	private ArrayList<LogRecord> logs = new ArrayList<LogRecord>();
	private ArrayList<Exception> exceptions = new ArrayList<Exception>();
	
	//constructor
	public NullExceptionHandler() {
		
	}
	
	//public
	public void connect(IBuilder builder) {
		throw new NotImplementedException("This API does not support exceptions.");
	}
	
	public void addThread(Thread thread) {
		
	}
	public void silentException(Exception ex) {
		exceptions.add(ex);
	}
	public void throwException(RuntimeException ex) {
		exceptions.add(ex);
	}
	
	public void publish(LogRecord record) {
		logs.add(record);
	}
	public void flush() {
		
	}
	public void close() throws SecurityException {
		
	}
	
	public List<Exception> getUnsentExceptions() {
		return exceptions;
	}
	public void setUnsentExceptions(List<Exception> list) {
		exceptions.clear();
		exceptions.addAll(list);
	}
	public List<LogRecord> getUnsentLogs() {
		return logs;
	}
	public void setUnsentLogs(List<LogRecord> list) {
		logs.clear();
		logs.addAll(list);
	}
	
	public boolean isLimitReached() {
		return false;
	}
	
	//private
	
}
