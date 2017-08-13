package ninja.egg82.plugin.exceptions;

import org.bukkit.command.CommandSender;

import ninja.egg82.plugin.commands.PluginCommand;

public class SenderNotAllowedException extends RuntimeException {
	//vars
	public static final SenderNotAllowedException EMPTY = new SenderNotAllowedException(null, null);
	private static final long serialVersionUID = 3971269475287419720L;
	
	private CommandSender sender = null;
	private PluginCommand command = null;

	//constructor
	public SenderNotAllowedException(CommandSender sender, PluginCommand command) {
		super();
		
		this.sender = sender;
		this.command = command;
	}
	
	//public
	public CommandSender getsender() {
		return sender;
	}
	public PluginCommand getCommand() {
		return command;
	}
	
	//private
	
}
