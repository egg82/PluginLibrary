package ninja.egg82.plugin.services;

import org.bukkit.ChatColor;

import ninja.egg82.patterns.Registry;

public class LanguageRegistry extends Registry {
	//vars
	
	//constructor
	public LanguageRegistry() {
		super();
		
		setRegister("no-permissions", String.class, ChatColor.RED + "You do not have permissions to run this command!");
		setRegister("incorrect-usage", String.class, ChatColor.RED + "Incorrect usage.");
		setRegister("player-not-found", String.class, ChatColor.RED + "Player not found.");
		setRegister("console-not-allowed", String.class, ChatColor.RED + "The console is not allowed to perform this action.");
		setRegister("player-not-allowed", String.class, ChatColor.RED + "Only the console is allowed to perform this action.");
	}
	
	//public
	
	//private
	
}
