package ninja.egg82.plugin.utils;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;

import ninja.egg82.utils.ReflectUtil;

public final class MaterialHelper {
	//vars
	private Material[] materials = null;
	
	//constructor
	public MaterialHelper() {
		Object[] enums = ReflectUtil.getStaticFields(Material.class);
		materials = Arrays.copyOf(enums, enums.length, Material[].class);
	}
	
	//public
	public Material[] getAllMaterials() {
		return materials.clone();
	}
	
	public Material[] filter(Material[] list, String filter, boolean whitelist) {
		if (list == null) {
			throw new IllegalArgumentException("list cannot be null.");
		}
		if (filter == null) {
			throw new IllegalArgumentException("filter cannot be null.");
		}
		
		filter = filter.toLowerCase();
		
		ArrayList<Material> filteredMaterials = new ArrayList<Material>();
		
		for (Material s : list) {
			String name = s.toString().toLowerCase();
			if (whitelist) {
				if (name.contains(filter)) {
					filteredMaterials.add(s);
				}
			} else {
				if (!name.contains(filter)) {
					filteredMaterials.add(s);
				}
			}
		}
		
		return filteredMaterials.toArray(new Material[0]);
	}
	
	//private
	
}
