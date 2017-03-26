package ninja.egg82.plugin.handlers;

import java.util.function.Function;

import ninja.egg82.plugin.commands.TickCommand;

class DelayedTickRunner implements Runnable {
	//vars
	private TickCommand command = null;
	private Function<Object, Integer> removalCallback = null;
	
	//constructor
	public DelayedTickRunner(TickCommand command, Function<Object, Integer> removalCallback) {
		this.command = command;
		this.removalCallback = removalCallback;
	}
	
	//public
	public void run() {
		removalCallback.apply(command.getClass());
		command.start();
	}
	
	//private
	
}
