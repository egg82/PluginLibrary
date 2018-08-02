package ninja.egg82.bukkit.reflection.skull;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullHelper_1_13 implements ISkullHelper {
	//vars
	
	//constructor
	public SkullHelper_1_13() {
		
	}
	
	//public
	public ItemStack createSkull(UUID owner) {
		if (owner == null) {
			throw new IllegalArgumentException("owner cannot be null.");
		}
		return createSkull(Bukkit.getOfflinePlayer(owner));
	}
	@SuppressWarnings("deprecation")
	public ItemStack createSkull(String owner) {
		if (owner == null) {
			throw new IllegalArgumentException("owner cannot be null.");
		}
		return createSkull(Bukkit.getOfflinePlayer(owner));
	}
	public ItemStack createSkull(Player owner) {
		if (owner == null) {
			throw new IllegalArgumentException("owner cannot be null.");
		}
		return createSkull(Bukkit.getPlayer(owner.getUniqueId()));
	}
	public ItemStack createSkull(OfflinePlayer owner) {
		if (owner == null) {
			throw new IllegalArgumentException("owner cannot be null.");
		}
		
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        skull.setDurability((short) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		if (skullMeta == null) {
			skullMeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
		}
        skullMeta.setDisplayName(owner.getName());
        skullMeta.setOwningPlayer(owner);
        skull.setItemMeta(skullMeta);
        
        return skull;
	}
	
	public ItemStack createSkull(short data) {
		ItemStack skull = null;
		
		if (data == 0) {
			skull = new ItemStack(Material.SKELETON_SKULL, 1);
		} else if (data == 1) {
			skull = new ItemStack(Material.WITHER_SKELETON_SKULL, 1);
		} else if (data == 2) {
			skull = new ItemStack(Material.ZOMBIE_HEAD, 1);
		} else if (data == 3) {
			skull = new ItemStack(Material.PLAYER_HEAD, 1);
		} else if (data == 4) {
			skull = new ItemStack(Material.CREEPER_HEAD, 1);
		} else if (data == 5) {
			skull = new ItemStack(Material.DRAGON_HEAD, 1);
		} else {
			skull = new ItemStack(Material.SKELETON_SKULL, 1);
		}
		
        skull.setDurability(data);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		if (skullMeta == null) {
			skullMeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(skull.getType());
		}
        skull.setItemMeta(skullMeta);
        
        return skull;
	}
	
	public OfflinePlayer getOwner(ItemStack stack) {
		if (stack == null) {
			return null;
		}
		if (!stack.hasItemMeta() || !(stack.getItemMeta() instanceof SkullMeta)) {
			return null;
		}
		
		SkullMeta meta = (SkullMeta) stack.getItemMeta();
		if (!meta.hasOwner()) {
			return null;
		}
		return meta.getOwningPlayer();
	}
	
	//private
	
}
