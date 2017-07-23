package ninja.egg82.plugin.exceptions;

public class PlayerNotFoundException extends RuntimeException {
	//vars
	public static final PlayerNotFoundException EMPTY = new PlayerNotFoundException(null);
	private static final long serialVersionUID = 3670759368857607534L;
	
	private String name = null;
	
	//constructor
	public PlayerNotFoundException(String name) {
		super();
		
		this.name = name;
	}
	
	//public
	public String getName() {
		return name;
	}
	
	//private
	
}
