package ninja.egg82.bungeecord.services;

import net.md_5.bungee.api.ChatColor;
import ninja.egg82.bungeecord.enums.BungeeLanguageType;
import ninja.egg82.patterns.Registry;

public class LanguageRegistry extends Registry<String> {
	//vars
	
	//constructor
	public LanguageRegistry() {
		super(String.class);
		
		setRegister(BungeeLanguageType.INCORRECT_COMMAND_USAGE, ChatColor.RED + "Incorrect usage.");
		setRegister(BungeeLanguageType.INVALID_PERMISSIONS, ChatColor.RED + "You do not have permissions to run this command!");
		setRegister(BungeeLanguageType.PLAYER_NOT_FOUND, ChatColor.RED + "Player not found.");
		setRegister(BungeeLanguageType.SENDER_NOT_ALLOWED, ChatColor.RED + "You are not allowed to perform this action. This does not neccesarily mean you do not have the correct permissions to.");
	}
	
	//public
	
	//private
	
}
