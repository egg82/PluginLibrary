package ninja.egg82.plugin.services;

import org.bukkit.ChatColor;

import ninja.egg82.patterns.Registry;
import ninja.egg82.plugin.enums.SpigotLanguageType;

public class LanguageRegistry extends Registry<String> {
	//vars
	
	//constructor
	public LanguageRegistry() {
		super(String.class);
		
		setRegister(SpigotLanguageType.INCORRECT_COMMAND_USAGE, ChatColor.RED + "Incorrect usage.");
		setRegister(SpigotLanguageType.INVALID_PERMISSIONS, ChatColor.RED + "You do not have permissions to run this command!");
		setRegister(SpigotLanguageType.PLAYER_NOT_FOUND, ChatColor.RED + "Player not found.");
		setRegister(SpigotLanguageType.SENDER_NOT_ALLOWED, ChatColor.RED + "You are not allowed to perform this action. This does not neccesarily mean you do not have the correct permissions to.");
	}
	
	//public
	
	//private
	
}
