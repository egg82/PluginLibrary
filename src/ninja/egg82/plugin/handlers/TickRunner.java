package ninja.egg82.plugin.handlers;

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
		command.start();
	}
	
	//private
	
}
