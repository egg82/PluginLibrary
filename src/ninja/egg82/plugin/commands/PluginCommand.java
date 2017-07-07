package ninja.egg82.plugin.commands;

import java.util.List;

import org.bukkit.command.Command;
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
	
	public List<String> tabComplete(CommandSender sender, Command command, String label, String[] args) {
		return null;
	}
	
	//private
	protected abstract void onUndo();
}