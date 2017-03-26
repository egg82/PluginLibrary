package ninja.egg82.plugin.commands;

import org.bukkit.command.CommandSender;

import ninja.egg82.patterns.Command;

public abstract class PluginCommand extends Command {
	//vars
	protected CommandSender sender = null;
	protected org.bukkit.command.Command command = null;
	protected String label = null;
	protected String[] args = null;
	
	//constructor
	public PluginCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		super();
		this.sender = sender;
		this.command = command;
		this.label = label;
		this.args = args;
	}
	
	//public
	
	//private
	
}