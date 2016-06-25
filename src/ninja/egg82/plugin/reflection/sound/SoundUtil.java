package ninja.egg82.plugin.reflection.sound;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Sound;

import ninja.egg82.plugin.reflection.sound.interfaces.ISoundUtil;
import ninja.egg82.utils.Util;

public class SoundUtil implements ISoundUtil {
	//vars
	Sound[] sounds = null;
	
	//constructor
	public SoundUtil() {
		Object[] enums = Util.getStaticFields(Sound.class);
		sounds = Arrays.copyOf(enums, enums.length, Sound[].class);
	}
	
	//public
	public Sound[] getAllSounds() {
		return sounds.clone();
	}
	
	public Sound[] filter(Sound[] list, String filter, boolean whitelist) {
		ArrayList<Sound> filteredSounds = new ArrayList<Sound>();
		
		for (Sound s : list) {
			String name = s.toString().toLowerCase();
			if (whitelist) {
				if (name.contains(filter)) {
					filteredSounds.add(s);
				}
			} else {
				if (!name.contains(filter)) {
					filteredSounds.add(s);
				}
			}
		}
		
		return filteredSounds.toArray(new Sound[0]);
	}
	
	//private
	
}
