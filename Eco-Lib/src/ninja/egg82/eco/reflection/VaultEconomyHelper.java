package ninja.egg82.eco.reflection;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class VaultEconomyHelper implements IEconomyHelper {
	//vars
	private Economy economy = null;
	
	//constructor
	public VaultEconomyHelper() {
		RegisteredServiceProvider<Economy> service = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if (service == null || service.getProvider() == null) {
			throw new RuntimeException("Vault economy service was not found!");
		}
		economy = service.getProvider();
	}
	
	//public
	public boolean deposit(OfflinePlayer player, double amount) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		if (amount < 0) {
			throw new RuntimeException("amount cannot be < 0.");
		}
		
		if (!economy.hasAccount(player)) {
			economy.createPlayerAccount(player);
		}
		
		return economy.depositPlayer(player, amount).transactionSuccess();
	}
	public boolean deposit(String bankName, double amount) {
		if (bankName == null) {
			throw new IllegalArgumentException("bankName cannot be null.");
		}
		if (amount < 0) {
			throw new RuntimeException("amount cannot be < 0.");
		}
		
		if (!economy.getBanks().contains(bankName)) {
			return false;
		}
		
		return economy.bankDeposit(bankName, amount).transactionSuccess();
	}
	public boolean withdraw(OfflinePlayer player, double amount) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		if (amount < 0) {
			throw new RuntimeException("amount cannot be < 0.");
		}
		
		if (!economy.hasAccount(player)) {
			economy.createPlayerAccount(player);
		}
		
		return economy.withdrawPlayer(player, amount).transactionSuccess();
	}
	public boolean withdraw(String bankName, double amount) {
		if (bankName == null) {
			throw new IllegalArgumentException("bankName cannot be null.");
		}
		if (amount < 0) {
			throw new RuntimeException("amount cannot be < 0.");
		}
		
		if (!economy.getBanks().contains(bankName)) {
			return false;
		}
		
		return economy.bankWithdraw(bankName, amount).transactionSuccess();
	}
	public double getBalance(OfflinePlayer player) {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		
		if (!economy.hasAccount(player)) {
			economy.createPlayerAccount(player);
		}
		
		return economy.getBalance(player);
	}
	public double getBalance(String bankName) {
		if (bankName == null) {
			throw new IllegalArgumentException("bankName cannot be null.");
		}
		
		if (!economy.getBanks().contains(bankName)) {
			return 0.0d;
		}
		
		return economy.bankBalance(bankName).balance;
	}
	
	public boolean createBank(String name, OfflinePlayer owner) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		if (owner == null) {
			throw new IllegalArgumentException("owner cannot be null.");
		}
		
		return economy.createBank(name, owner).transactionSuccess();
	}
	public boolean deleteBank(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return economy.deleteBank(name).transactionSuccess();
	}
	public boolean hasBank(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		
		return economy.getBanks().contains(name);
	}
	public boolean isBankOwner(String name, OfflinePlayer player) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null.");
		}
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		
		return economy.isBankOwner(name, player).transactionSuccess();
	}
	
	public String format(double amount) {
		return economy.format(amount);
	}
	
	public boolean isValidLibrary() {
		return true;
	}
	
	//private
	
}
