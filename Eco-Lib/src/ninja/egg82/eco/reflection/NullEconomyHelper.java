package ninja.egg82.eco.reflection;

import org.bukkit.OfflinePlayer;

public class NullEconomyHelper implements IEconomyHelper {
	//vars
	
	//constructor
	public NullEconomyHelper() {
		
	}
	
	//public
	public boolean deposit(OfflinePlayer player, double amount) {
		return false;
	}
	public boolean deposit(String bankName, double amount) {
		return false;
	}
	public boolean withdraw(OfflinePlayer player, double amount) {
		return false;
	}
	public boolean withdraw(String bankName, double amount) {
		return false;
	}
	public double getBalance(OfflinePlayer player) {
		return 0.0d;
	}
	public double getBalance(String bankName) {
		return 0.0d;
	}
	
	public boolean createBank(String name, OfflinePlayer owner) {
		return false;
	}
	public boolean deleteBank(String name) {
		return false;
	}
	public boolean hasBank(String name) {
		return false;
	}
	public boolean isBankOwner(String name, OfflinePlayer player) {
		return false;
	}
	
	public String format(double amount) {
		return null;
	}
	
	public boolean isValidLibrary() {
		return false;
	}
	
	//private
	
}
