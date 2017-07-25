package ninja.egg82.plugin.reflection.exceptionHandlers.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.UUID;

import com.rollbar.payload.Payload;
import com.rollbar.sender.ConnectionFailedException;
import com.rollbar.sender.PayloadSender;
import com.rollbar.sender.RollbarResponse;
import com.rollbar.sender.RollbarResponseHandler;
import com.rollbar.sender.Sender;

public class AsyncPayloadSender implements Sender {
	//vars
	private URL url = null;
	
	//constructor
	public AsyncPayloadSender() {
		try {
			url = new URL(PayloadSender.DEFAULT_API_ENDPOINT);
		} catch (Exception ex) {
			
		}
	}
	
	//public
	public RollbarResponse send(Payload payload) {
		final byte[] bytes;
		try {
			bytes = payload.toJson().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Rollbar Requires UTF8 Encoding and your JVM does not support UTF8, please update your JVM");
		}
		
		new Thread(new Runnable() {
			public void run() {
				HttpURLConnection connection = null;
				try {
					connection = getConnection();
					sendJson(connection, bytes);
				} catch (ConnectionFailedException e) {
					
				}
			}
		}).start();
		
		return RollbarResponse.success(UUID.randomUUID().toString());
	}
	public void send(Payload payload, RollbarResponseHandler handler) {
		RollbarResponse response = send(payload);
		if (handler != null) {
			handler.handleResponse(response);
		}
	}
	
	//private
	private HttpURLConnection getConnection() throws ConnectionFailedException {
		HttpURLConnection connection = getHttpURLConnection();
		setMethodToPOST(connection);
		setJsonSendAndReceive(connection);
		return connection;
	}

	private void sendJson(HttpURLConnection connection, byte[] bytes) throws ConnectionFailedException {
		OutputStream out;
		try {
			out = connection.getOutputStream();
		} catch (IOException e) {
			throw new ConnectionFailedException(url, "OpeningBodyWriter", e);
		}
		try {
			out.write(bytes, 0, bytes.length);
		} catch (IOException e) {
			throw new ConnectionFailedException(url, "WritingToBody", e);
		}
		try {
			out.close();
		} catch (IOException e) {
			throw new ConnectionFailedException(url, "Closing Body Writer", e);
		}
	}
    
	private void setJsonSendAndReceive(HttpURLConnection connection) {
		connection.setRequestProperty("Accept-Charset", "utf-8");
		connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		connection.setRequestProperty("Accept", "application/json");
	}
    
	private void setMethodToPOST(HttpURLConnection connection) throws ConnectionFailedException {
		try {
			connection.setRequestMethod("POST");
		} catch (ProtocolException e) {
			throw new ConnectionFailedException(url, "Setting method to POST Failed", e);
		}
		connection.setDoOutput(true);
	}

	private HttpURLConnection getHttpURLConnection() throws ConnectionFailedException {
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			throw new ConnectionFailedException(url, "Initializing URL Connection", e);
		}
		return connection;
	}
}
