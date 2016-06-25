package ninja.egg82.plugin.reflection.sound.interfaces;

import org.bukkit.Sound;

public interface ISoundUtil {
	Sound[] getAllSounds();
	Sound[] filter(Sound[] list, String filter, boolean whitelist);
}
