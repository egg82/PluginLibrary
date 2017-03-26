package ninja.egg82.plugin.reflection.sound;

import org.bukkit.Sound;

public interface ISoundUtil {
	//functions
	Sound[] getAllSounds();
	Sound[] filter(Sound[] list, String filter, boolean whitelist);
}
