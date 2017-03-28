package ninja.egg82.plugin.reflection.sound;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Sound;

import ninja.egg82.utils.ReflectUtil;

public final class SoundUtil {
	//vars
	Sound[] sounds = null;
	
	//constructor
	public SoundUtil() {
		Object[] enums = ReflectUtil.getStaticFields(Sound.class);
		sounds = Arrays.copyOf(enums, enums.length, Sound[].class);
	}
	
	//public
	public Sound[] getAllSounds() {
		return sounds.clone();
	}
	
	public Sound[] filter(Sound[] list, String filter, boolean whitelist) {
		if (list == null) {
			throw new IllegalArgumentException("list cannot be null.");
		}
		if (filter == null) {
			throw new IllegalArgumentException("filter cannot be null.");
		}
		
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
