package ninja.egg82.plugin.handlers;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.SynchronousCommand;
import ninja.egg82.plugin.commands.AsyncTickCommand;
import ninja.egg82.plugin.commands.TickCommand;

class TickRunner implements Runnable {
	//vars
	private SynchronousCommand command = null;
	
	//constructor
	public TickRunner(TickCommand command) {
		this.command = command;
	}
	public TickRunner(AsyncTickCommand command) {
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
