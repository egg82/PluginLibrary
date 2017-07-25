package ninja.egg82.plugin.reflection.exceptionHandlers.builders;

public class GameAnalyticsBuilder implements IBuilder {
	//vars
	private String gameKey = null;
	private String secretKey = null;
	
	//constructor
	public GameAnalyticsBuilder(String gameKey, String secretKey) {
		this.gameKey = gameKey;
		this.secretKey = secretKey;
	}
	
	//public
	public String[] getParams() {
		return new String[] {
			gameKey,
			secretKey
		};
	}
	
	//private
	
}
