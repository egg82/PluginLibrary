package ninja.egg82.bungeecord.exceptions;

import net.md_5.bungee.api.CommandSender;
import ninja.egg82.bungeecord.commands.PluginCommand;

public class IncorrectCommandUsageException extends RuntimeException {
	//vars
	public static final IncorrectCommandUsageException EMPTY = new IncorrectCommandUsageException(null, null, null);
	private static final long serialVersionUID = 8079412755589117759L;
	
	private PluginCommand command = null;
	private CommandSender sender = null;
	private String[] args = null;
	
	//constructor
	public IncorrectCommandUsageException(CommandSender sender, PluginCommand command, String[] args) {
		super();
		
		this.command = command;
		this.sender = sender;
		this.args = args;
	}
	
	//public
	public PluginCommand getCommand() {
		return command;
	}
	public CommandSender getSender() {
		return sender;
	}
	public String[] getArgs() {
		return args;
	}
	
	//private
	
}
