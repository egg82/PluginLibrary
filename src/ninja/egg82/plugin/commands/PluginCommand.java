package ninja.egg82.plugin.commands;

import org.bukkit.command.CommandSender;

import ninja.egg82.patterns.SynchronousCommand;

public abstract class PluginCommand extends SynchronousCommand {
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
	public CommandSender getSender() {
		return sender;
	}
	public void setSender(CommandSender sender) {
		this.sender = sender;
	}
	
	public org.bukkit.command.Command getCommand() {
		return command;
	}
	public void setCommand(org.bukkit.command.Command command) {
		this.command = command;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String[] getArgs() {
		return args;
	}
	public void setArgs(String[] args) {
		this.args = args;
	}
	
	//private
	
}