package ninja.egg82.plugin.commands;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ninja.egg82.events.patterns.command.CommandEvent;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.command.Command;
import ninja.egg82.plugin.enums.SpigotCommandErrorType;
import ninja.egg82.plugin.enums.SpigotMessageType;
import ninja.egg82.plugin.enums.SpigotServiceType;
import ninja.egg82.plugin.utils.interfaces.IPermissionsManager;

public class PluginCommand extends Command {
	//vars
	protected CommandSender sender = null;
	protected org.bukkit.command.Command command = null;
	protected String label = null;
	protected String[] args = null;
	
	protected IPermissionsManager permissionsManager = (IPermissionsManager) ServiceLocator.getService(SpigotServiceType.PERMISSIONS_MANAGER);
	
	//constructor
	public PluginCommand() {
		super();
	}
	
	//public
	public void onQuit(String name, Player player) {
		
	}
	public void onDeath(String name, Player player) {
		
	}
	public void onLogin(String name, Player player) {
		
	}
	
	public void setSender(CommandSender sender) {
		this.sender = sender;
	}
	public void setCommand(org.bukkit.command.Command command) {
		this.command = command;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public void setArgs(String[] args) {
		this.args = args;
	}
	
	//private
	protected boolean isValid(boolean needsPlayer, String permissions, int[] argsLengths, int[] playerArgs) {
		boolean isPlayer = sender instanceof Player;
		
		if (needsPlayer && !isPlayer) {
			sender.sendMessage(SpigotMessageType.CONSOLE_NOT_ALLOWED);
			dispatch(CommandEvent.ERROR, SpigotCommandErrorType.CONSOLE_NOT_ALLOWED);
			return false;
		}
		if (isPlayer && !permissionsManager.playerHasPermission((Player) sender, permissions)) {
			sender.sendMessage(SpigotMessageType.NO_PERMISSIONS);
			dispatch(CommandEvent.ERROR, SpigotCommandErrorType.NO_PERMISSIONS);
			return false;
		}
		
		if (!ArrayUtils.contains(argsLengths, args.length)) {
			sender.sendMessage(SpigotMessageType.INCORRECT_USAGE);
			sender.getServer().dispatchCommand(sender, "help " + command.getName());
			dispatch(CommandEvent.ERROR, SpigotCommandErrorType.INCORRECT_USAGE);
			return false;
		}
		
		if (playerArgs != null) {
			for (int i = 0; i < playerArgs.length; i++) {
				if (i < args.length) {
					if (!tryPlayer(Bukkit.getPlayer(args[i]))) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	private boolean tryPlayer(Player player) {
		if (player == null) {
			sender.sendMessage(SpigotMessageType.PLAYER_NOT_FOUND);
			dispatch(CommandEvent.ERROR, SpigotCommandErrorType.PLAYER_NOT_FOUND);
			return false;
		}
		
		return true;
	}
}