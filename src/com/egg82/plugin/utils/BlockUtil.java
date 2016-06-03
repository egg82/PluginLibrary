package com.egg82.plugin.utils;

import org.bukkit.Location;
import org.bukkit.Material;

public class BlockUtil {
	//vars
	
	//constructor
	public BlockUtil() {
		
	}
	
	//public
	public static Location getTopAirBlock(Location l) {
		l = l.clone();
		do {
			while (l.getBlock().getType() != Material.AIR) {
				l = l.add(0.0d, 1.0d, 0.0d);
			}
			while (l.add(0.0d, 1.0d, 0.0d).getBlock().getType() != Material.AIR) {
				
			}
			l.subtract(0.0d, 1.0d, 0.0d);
		} while (l.getBlock().getType() != Material.AIR || l.add(0.0d, 1.0d, 0.0d).getBlock().getType() != Material.AIR);
		l.subtract(0.0d, 1.0d, 0.0d);
		while (l.subtract(0.0d, 1.0d, 0.0d).getBlock().getType() == Material.AIR) {
			
		}
		l.add(0.0d, 1.0d, 0.0d);
		
		return l;
	}
	
	//private
	
}