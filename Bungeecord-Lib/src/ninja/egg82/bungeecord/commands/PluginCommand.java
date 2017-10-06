package ninja.egg82.bungeecord.commands;

import java.util.List;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import ninja.egg82.patterns.SynchronousCommand;

public abstract class PluginCommand extends SynchronousCommand {
	//vars
	protected CommandSender sender = null;
	protected Command command = null;
	protected String[] args = null;
	
	//constructor
	public PluginCommand(CommandSender sender, Command command, String[] args) {
		super();
		this.sender = sender;
		this.command = command;
		this.args = args;
	}
	
	//public
	public final CommandSender getSender() {
		return sender;
	}
	public final void setSender(CommandSender sender) {
		this.sender = sender;
	}
	
	public final Command getCommand() {
		return command;
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
	
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return null;
	}
	
	//private
	protected abstract void onUndo();
}