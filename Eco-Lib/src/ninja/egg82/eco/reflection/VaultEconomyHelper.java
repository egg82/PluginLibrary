package ninja.egg82.eco.reflection;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;

public class VaultEconomyHelper implements IEconomyHelper {
	//vars
	private Economy economy = null;
	
	//constructor
	public VaultEconomyHelper() {
		RegisteredServiceProvider<Economy> service = ServiceLocator.getService(JavaPlugin.class).getServer().getServicesManager().getRegistration(Economy.class);
		if (service == null || service.getProvider() == null) {
			throw new RuntimeException("Vault economy service was not found!");
		}
		economy = service.getProvider();
	}
	
	//public
	public boolean deposit(OfflinePlayer player, double amount) {
		if (player == null) {
			throw new ArgumentNullException("player");
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
			throw new ArgumentNullException("bankName");
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
			throw new ArgumentNullException("player");
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
			throw new ArgumentNullException("bankName");
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
			throw new ArgumentNullException("player");
		}
		
		if (!economy.hasAccount(player)) {
			economy.createPlayerAccount(player);
		}
		
		return economy.getBalance(player);
	}
	public double getBalance(String bankName) {
		if (bankName == null) {
			throw new ArgumentNullException("bankName");
		}
		
		if (!economy.getBanks().contains(bankName)) {
			return 0.0d;
		}
		
		return economy.bankBalance(bankName).balance;
	}
	
	public boolean createBank(String name, OfflinePlayer owner) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		if (owner == null) {
			throw new ArgumentNullException("owner");
		}
		
		return economy.createBank(name, owner).transactionSuccess();
	}
	public boolean deleteBank(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return economy.deleteBank(name).transactionSuccess();
	}
	public boolean hasBank(String name) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		
		return economy.getBanks().contains(name);
	}
	public boolean isBankOwner(String name, OfflinePlayer player) {
		if (name == null) {
			throw new ArgumentNullException("name");
		}
		if (player == null) {
			throw new ArgumentNullException("player");
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
