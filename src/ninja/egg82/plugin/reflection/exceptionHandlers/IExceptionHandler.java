package ninja.egg82.plugin.reflection.exceptionHandlers;

public interface IExceptionHandler {
	//functions
	void connect(String accessToken, String environment);
	void addThread(Thread thread);
	void silentException(Exception ex);
	void throwException(RuntimeException ex);
}
