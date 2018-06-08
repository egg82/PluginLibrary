package ninja.egg82.bungeecord.services;

import java.io.StringReader;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import ninja.egg82.patterns.registries.VariableRegistry;

public class ConfigRegistry extends VariableRegistry<String> {
	//vars
	
	//constructor
	public ConfigRegistry() {
		super(String.class);
	}
	
	//public
	public void load(Configuration config) {
		deepSet("", config);
	}
	public Configuration save() {
		ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
		Configuration retVal = provider.load(new StringReader(""));
		for (String key : getKeys()) {
			retVal.set(key, getRegister(key));
		}
		return retVal;
	}
	
	//private
	private void deepSet(String currentPath, Configuration config) {
		for (String key : config.getKeys()) {
			if (config.get(key) != null) {
				if (config.get(key) instanceof Configuration) {
					deepSet(currentPath + key + ".", config.getSection(key));
				} else {
					setRegister(currentPath + key, config.get(key));
				}
			}
		}
	}
}
