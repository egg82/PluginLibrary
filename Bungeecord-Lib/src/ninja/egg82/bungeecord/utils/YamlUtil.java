package ninja.egg82.bungeecord.utils;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.utils.FileUtil;

public class YamlUtil {
	//vars
	private static Plugin plugin = null;
	
	//constructor
	public YamlUtil() {
		
	}
	
	//public
	public static Configuration getOrLoadDefaults(File yamlFile, String resourceName) {
		return getOrLoadDefaults(yamlFile.getAbsolutePath(), resourceName);
	}
	public static Configuration getOrLoadDefaults(String yamlFile, String resourceName) {
		return getOrLoadDefaults(yamlFile, resourceName, false);
	}
	public static Configuration getOrLoadDefaults(File yamlFile, String resourceName, boolean saveFile) {
		return getOrLoadDefaults(yamlFile.getAbsolutePath(), resourceName, saveFile);
	}
	public static Configuration getOrLoadDefaults(String yamlFile, String resourceName, boolean saveFile) {
		ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
		
		if (yamlFile == null || resourceName == null) {
			return provider.load(new StringReader(""));
		}
		
		if (plugin == null) {
			plugin = ServiceLocator.getService(Plugin.class);
		}
		
		Configuration defaultConfig = null;
		try {
			defaultConfig = provider.load(new InputStreamReader(plugin.getResourceAsStream(resourceName)));
		} catch (Exception ex) {
			defaultConfig = provider.load(new StringReader(""));
		}
		if (defaultConfig == null) {
			defaultConfig = provider.load(new StringReader(""));
		}
		
		if (FileUtil.pathExists(yamlFile) && !FileUtil.pathIsFile(yamlFile)) {
			return defaultConfig;
		}
		
		File file = new File(yamlFile);
		Configuration config = null;
		
		try {
			config = provider.load(file);
		} catch (Exception ex) {
			config = provider.load(new StringReader(""));
		}
		
		boolean changed = deepCopy(defaultConfig, config);
		
		if (saveFile) {
			if (!FileUtil.pathExists(yamlFile)) {
				try {
					FileUtil.createDirectory(new File(yamlFile).getParent());
					InputStream stream = plugin.getResourceAsStream(resourceName);
					Files.copy(stream, new File(yamlFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception ex) {
					
				}
			} else {
				if (changed) {
					try {
						provider.save(config, file);
					} catch (Exception ex) {
						
					}
				}
			}
		}
		
		return config;
	}
	
	//private
	private static boolean deepCopy(Configuration from, Configuration to) {
		boolean changed = false;
		for (String key : from.getKeys()) {
			if (from.get(key) != null) {
				if (from.get(key) instanceof Configuration) {
					if (!to.contains(key)) {
						changed = true;
						to.set(key, new Configuration());
					}
					if (deepCopy(from.getSection(key), to.getSection(key))) {
						changed = true;
					}
				} else {
					if (!to.contains(key)) {
						changed = true;
						to.set(key, from.get(key));
					}
				}
			}
		}
		return changed;
	}
}
