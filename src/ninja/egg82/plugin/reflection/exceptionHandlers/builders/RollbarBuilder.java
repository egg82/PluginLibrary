package ninja.egg82.plugin.reflection.exceptionHandlers.builders;

public class RollbarBuilder implements IBuilder {
	//vars
	private String accessToken = null;
	private String environment = null;
	
	//constructor
	public RollbarBuilder(String accessToken, String environment) {
		this.accessToken = accessToken;
		this.environment = environment;
	}
	
	//public
	public String[] getParams() {
		return new String[] {
			accessToken,
			environment
		};
	}
	
	//private
	
}
