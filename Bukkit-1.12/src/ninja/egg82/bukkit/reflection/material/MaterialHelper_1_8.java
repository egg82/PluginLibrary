package ninja.egg82.bukkit.reflection.material;

import org.bukkit.Material;

public class MaterialHelper_1_8 implements IMaterialHelper {
    // vars

    // constructor
    public MaterialHelper_1_8() {

    }

    // public
    public Material getByName(String name) {
        return Material.matchMaterial(name);
    }

    @SuppressWarnings("deprecation")
    public Material getById(int id) {
        return Material.getMaterial(id);
    }

    // private

}
