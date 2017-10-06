package ninja.egg82.plugin.handlers;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.TickCommand;

class TickRunner implements Runnable {
	//vars
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
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			throw ex;
		}
	}
	
	//private
	
}
