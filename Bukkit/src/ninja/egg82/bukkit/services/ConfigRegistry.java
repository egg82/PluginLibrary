package ninja.egg82.bukkit.services;

import org.bukkit.configuration.file.YamlConfiguration;

import ninja.egg82.patterns.registries.VariableRegistry;

public class ConfigRegistry extends VariableRegistry<String> {
	//vars
	
	//constructor
	public ConfigRegistry() {
		super(String.class);
	}
	
	//public
	public void load(YamlConfiguration config) {
		for (String key : config.getKeys(true)) {
			if (!config.isConfigurationSection(key)) {
				setRegister(key, config.get(key));
			}
		}
	}
	public YamlConfiguration save() {
		YamlConfiguration retVal = new YamlConfiguration();
		for (String key : getKeys()) {
			retVal.set(key, getRegister(key));
		}
		return retVal;
	}
	
	//private
	
}
