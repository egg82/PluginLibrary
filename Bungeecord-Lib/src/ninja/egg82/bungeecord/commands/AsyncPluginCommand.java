package ninja.egg82.bungeecord.commands;

import java.util.List;

import net.md_5.bungee.api.CommandSender;
import ninja.egg82.patterns.Command;

public abstract class AsyncPluginCommand extends Command {
	//vars
	protected CommandSender sender = null;
	protected net.md_5.bungee.api.plugin.Command command = null;
	protected String[] args = null;
	
	//constructor
	public AsyncPluginCommand() {
		super();
	}
	
	//public
	public final CommandSender getSender() {
		return sender;
	}
	public final void setSender(CommandSender sender) {
		this.sender = sender;
	}
	
	public final net.md_5.bungee.api.plugin.Command getCommand() {
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
	
	public List<String> tabComplete() {
		return null;
	}
	
	//private
	protected abstract void onUndo();
}