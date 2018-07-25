package ninja.egg82.bukkit.reflection.material;

import org.bukkit.Material;

public class MaterialHelper_1_13 implements IMaterialHelper {
	//vars
	
	//constructor
	public MaterialHelper_1_13() {
		
	}
	
	//public
	public Material getByName(String name) {
		Material retVal = Material.matchMaterial(name);
		if (retVal == null) {
			retVal = Material.matchMaterial(name, true);
		}
		return retVal;
	}
	@SuppressWarnings("deprecation")
	public Material getById(int id) {
		for (Material m : Material.values()) {
			if (m.getId() == id) {
				return m;
			}
		}
		return null;
	}
	
	//private
	
}
