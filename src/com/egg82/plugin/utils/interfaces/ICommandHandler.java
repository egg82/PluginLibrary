package com.egg82.plugin.utils.interfaces;

import org.bukkit.command.CommandSender;

import com.egg82.plugin.commands.PluginCommand;

public interface ICommandHandler {
	void initialize();
	void destroy();
	void addCommand(String command, Class<? extends PluginCommand> commandToRun);
	void removeCommand(String command);
	void clearCommands();
	void runCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args);
	boolean hasCommand(String command);
}