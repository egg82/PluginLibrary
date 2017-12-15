package ninja.egg82.bungeecord.exceptions;

import net.md_5.bungee.api.CommandSender;

public class InvalidPermissionsException extends RuntimeException {
	//vars
	public static final InvalidPermissionsException EMPTY = new InvalidPermissionsException(null, null);
	private static final long serialVersionUID = -5264068655615577659L;
	
	private CommandSender sender = null;
	private String permissionsType = null;

	//constructor
	public InvalidPermissionsException(CommandSender sender, String permissionsType) {
		super();
		
		this.sender = sender;
		this.permissionsType = permissionsType;
	}
	
	//public
	public CommandSender getSender() {
		return sender;
	}
	public String getPermissionsType() {
		return permissionsType;
	}
	
	//private
	
}
