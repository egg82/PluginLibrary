package ninja.egg82.plugin.commands;

import org.bukkit.command.CommandSender;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.command.Command;
import ninja.egg82.plugin.enums.CustomServiceType;
import ninja.egg82.plugin.utils.interfaces.IPermissionsManager;

public class PluginCommand extends Command {
	//vars
	protected CommandSender sender = null;
	protected org.bukkit.command.Command command = null;
	protected String label = null;
	protected String[] args = null;
	
	protected IPermissionsManager permissionsManager = (IPermissionsManager) ServiceLocator.getService(CustomServiceType.PERMISSIONS_MANAGER);
	
	//constructor
	public PluginCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		super(0);
		this.sender = sender;
		this.command = command;
		this.label = label;
		this.args = args;
	}
	
	//public
	
	//private
	
}