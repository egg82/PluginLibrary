package ninja.egg82.bukkit.reflection.player;

import org.bukkit.entity.Player;

public interface IPlayerHelper {
	//functions
	void hidePlayer(Player player, Player playerToHide);
	void showPlayer(Player player, Player playerToShow);
	
	int getPing(Player player);
	
	boolean supportsOffhand();
}
