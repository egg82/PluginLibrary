package ninja.egg82.plugin.utils;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.potion.PotionEffectType;

import ninja.egg82.utils.ReflectUtil;

public final class PotionEffectTypeHelper {
	//vars
	private PotionEffectType[] effects = null;
	
	//constructor
	public PotionEffectTypeHelper() {
		Object[] enums = ReflectUtil.getStaticFields(PotionEffectType.class);
		effects = Arrays.copyOf(enums, enums.length, PotionEffectType[].class);
	}
	
	//public
	public PotionEffectType[] getAllEffectTypes() {
		return effects.clone();
	}
	
	public PotionEffectType[] filter(PotionEffectType[] list, String filter, boolean whitelist) {
		if (list == null) {
			throw new IllegalArgumentException("list cannot be null.");
		}
		if (filter == null) {
			throw new IllegalArgumentException("filter cannot be null.");
		}
		
		filter = filter.toLowerCase();
		
		ArrayList<PotionEffectType> filteredEffectTypes = new ArrayList<PotionEffectType>();
		
		for (PotionEffectType s : list) {
			String name = s.toString().toLowerCase();
			if (whitelist) {
				if (name.contains(filter)) {
					filteredEffectTypes.add(s);
				}
			} else {
				if (!name.contains(filter)) {
					filteredEffectTypes.add(s);
				}
			}
		}
		
		return filteredEffectTypes.toArray(new PotionEffectType[0]);
	}
	
	//private
	
}
