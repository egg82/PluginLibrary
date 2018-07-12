package ninja.egg82.bukkit.reflection.skull;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import ninja.egg82.bukkit.reflection.material.IMaterialHelper;
import ninja.egg82.bukkit.utils.CommandUtil;
import ninja.egg82.patterns.ServiceLocator;

public class SkullHelper_1_8 implements ISkullHelper {
	//vars
	private Material skull = getSkull();
	
	//constructor
	public SkullHelper_1_8() {
		
	}
	
	//public
	public ItemStack createSkull(UUID owner) {
		if (owner == null) {
			throw new IllegalArgumentException("owner cannot be null.");
		}
		return createSkull(CommandUtil.getOfflinePlayerByUuid(owner));
	}
	public ItemStack createSkull(String owner) {
		if (owner == null) {
			throw new IllegalArgumentException("owner cannot be null.");
		}
		return createSkull(CommandUtil.getOfflinePlayerByName(owner));
	}
	public ItemStack createSkull(Player owner) {
		if (owner == null) {
			throw new IllegalArgumentException("owner cannot be null.");
		}
		return createSkull(CommandUtil.getOfflinePlayerByUuid(owner.getUniqueId()));
	}
	@SuppressWarnings("deprecation")
	public ItemStack createSkull(OfflinePlayer owner) {
		if (owner == null) {
			throw new IllegalArgumentException("owner cannot be null.");
		}
		
		ItemStack skull = new ItemStack(this.skull, 1);
        skull.setDurability((short) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setDisplayName(owner.getName());
        skullMeta.setOwner(owner.getName());
        skull.setItemMeta(skullMeta);
        
        return skull;
	}
	
	@SuppressWarnings("deprecation")
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
		return CommandUtil.getOfflinePlayerByName(meta.getOwner());
	}
	
	//private
	private Material getSkull() {
		IMaterialHelper materialHelper = ServiceLocator.getService(IMaterialHelper.class);
		Material retVal = materialHelper.getByName("PLAYER_HEAD");
		return (retVal != null) ? retVal : materialHelper.getByName("SKULL_ITEM");
	}
}
