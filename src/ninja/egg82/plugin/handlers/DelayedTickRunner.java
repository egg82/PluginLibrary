package ninja.egg82.plugin.handlers;

import java.util.function.Consumer;

import ninja.egg82.plugin.commands.TickCommand;

class DelayedTickRunner implements Runnable {
	//vars
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
		command.start();
	}
	
	//private
	
}
