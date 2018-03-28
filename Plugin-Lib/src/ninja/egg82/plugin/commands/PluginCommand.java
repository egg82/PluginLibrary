package ninja.egg82.plugin.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import ninja.egg82.patterns.Command;

public abstract class PluginCommand extends Command {
	//vars
	protected CommandSender sender = null;
	protected org.bukkit.command.Command command = null;
	protected String commandName = null;
	protected String label = null;
	protected String[] args = null;
	
	//constructor
	public PluginCommand() {
		super();
	}
	
	//public
	public final CommandSender getSender() {
		return sender;
	}
	public final void setSender(CommandSender sender) {
		this.sender = sender;
	}
	
	public final org.bukkit.command.Command getCommand() {
		return command;
	}
	public final void setCommand(org.bukkit.command.Command command) {
		this.command = command;
	}
	
	public final String getCommandName() {
		return commandName;
	}
	public final void setCommandName(String commandName) {
		this.commandName = commandName;
	}
	
	public final String getLabel() {
		return label;
	}
	public final void setLabel(String label) {
		this.label = label;
	}
	
	public final String[] getArgs() {
		return args;
	}
	public final void setArgs(String[] args) {
		this.args = args;
	}
	
	public final void undo() {
		onUndo();
	}
	
	public List<String> tabComplete() {
		return null;
	}
	
	//private
	protected abstract void onUndo();
}