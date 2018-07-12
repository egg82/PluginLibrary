package ninja.egg82.bukkit.reflection.material;

import java.lang.reflect.Method;

import org.bukkit.Material;

public class MaterialHelper_1_13 implements IMaterialHelper {
	//vars
	private Method matchMaterial = null;
	
	//constructor
	public MaterialHelper_1_13() {
		try {
			matchMaterial = Material.class.getMethod("matchMaterial", String.class, Boolean.class);
		} catch (Exception ex) {
			throw new RuntimeException("Could not hook 1.13 matchMaterial via getMethod.");
		}
	}
	
	//public
	public Material getByName(String name) {
		Material retVal = Material.matchMaterial(name);
		if (retVal == null) {
			try {
				retVal = (Material) matchMaterial.invoke(null, name, Boolean.TRUE);
			} catch (Exception ex) {
				
			}
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
