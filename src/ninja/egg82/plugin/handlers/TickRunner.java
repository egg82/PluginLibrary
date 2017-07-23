package ninja.egg82.plugin.handlers;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.TickCommand;
import ninja.egg82.plugin.reflection.exceptionHandlers.IExceptionHandler;

class TickRunner implements Runnable {
	//vars
	private IExceptionHandler exceptionHandler = ServiceLocator.getService(IExceptionHandler.class);
	
	private TickCommand command = null;
	
	//constructor
	public TickRunner(TickCommand command) {
		this.command = command;
	}
	
	//public
	public void run() {
		try {
			command.start();
		} catch (Exception ex) {
			exceptionHandler.silentException(ex);
			throw ex;
		}
	}
	
	//private
	
}
