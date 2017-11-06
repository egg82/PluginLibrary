package ninja.egg82.eco.reflection;

import org.bukkit.OfflinePlayer;

public interface IEconomyHelper {
	//functions
	boolean deposit(OfflinePlayer player, double amount);
	boolean deposit(String bankName, double amount);
	boolean withdraw(OfflinePlayer player, double amount);
	boolean withdraw(String bankName, double amount);
	double getBalance(OfflinePlayer player);
	double getBalance(String bankName);
	
	boolean createBank(String name, OfflinePlayer owner);
	boolean deleteBank(String name);
	boolean hasBank(String name);
	boolean isBankOwner(String name, OfflinePlayer player);
	
	String format(double amount);
	
	boolean isValidLibrary();
}
