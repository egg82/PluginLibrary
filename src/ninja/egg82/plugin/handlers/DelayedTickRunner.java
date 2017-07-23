package ninja.egg82.plugin.handlers;

import java.util.function.Consumer;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.TickCommand;
import ninja.egg82.plugin.reflection.exceptionHandlers.IExceptionHandler;

class DelayedTickRunner implements Runnable {
	//vars
	private IExceptionHandler exceptionHandler = ServiceLocator.getService(IExceptionHandler.class);
	
	private TickCommand command = null;
	private Consumer<Object> removalCallback = null;
	
	//constructor
	public DelayedTickRunner(TickCommand command, Consumer<Object> removalCallback) {
		this.command = command;
		this.removalCallback = removalCallback;
	}
	
	//public
	public void run() {
		removalCallback.accept(command.getClass());
		
		try {
			command.start();
		} catch (Exception ex) {
			exceptionHandler.silentException(ex);
			throw ex;
		}
	}
	
	//private
	
}
