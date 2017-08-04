package ninja.egg82.plugin.reflection.exceptionHandlers.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.json.JSONArray;
import org.json.JSONObject;

import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.utils.FileUtil;
import ninja.egg82.utils.ICryptoUtil;

public class GameAnalyticsAPI {
	//vars
	private String apiUrl = "https://api.gameanalytics.com/v2";
	
	private String gameKey = null;
	private String secretKey = null;
	private String version = null;
	
	private ICryptoUtil cryptoUtil = ServiceLocator.getService(ICryptoUtil.class);
	
	private int tsOffset = 0;
	private boolean doSend = false;
	private String userId = Bukkit.getServerId().trim();
	private String sessionId = UUID.randomUUID().toString();
	
	//constructor
	public GameAnalyticsAPI(String gameKey, String secretKey, String version) {
		if (gameKey == null) {
			throw new ArgumentNullException("gameKey");
		}
		if (secretKey == null) {
			throw new ArgumentNullException("secretKey");
		}
		if (version == null) {
			throw new ArgumentNullException("version");
		}
		
		if (userId.isEmpty() || userId.equalsIgnoreCase("unnamed") || userId.equalsIgnoreCase("unknown") || userId.equalsIgnoreCase("default")) {
			userId = UUID.randomUUID().toString();
			writeProperties();
		}
		
		this.gameKey = gameKey;
		this.secretKey = secretKey;
		this.version = version;
		
		sendInit();
	}
	
	//public
	public void handleUncaughtErrors() {
		handleUncaughtErrors(Thread.currentThread());
	}
	public void handleUncaughtErrors(Thread thread) {
		if (thread == null) {
			throw new ArgumentNullException("thread");
		}
		thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable ex) {
				log(ex);
			}
		});
	}
	public void unhandleUncaughtErrors(Thread thread) {
		if (thread == null) {
			throw new ArgumentNullException("thread");
		}
		thread.setUncaughtExceptionHandler(null);
	}
	
	public void log(Throwable ex) {
		sendError(getTraceString(ex), "error", 0);
	}
	public void log(Throwable ex, Level logLevel) {
		if (ex == null) {
			throw new ArgumentNullException("ex");
		}
		if (logLevel == null) {
			throw new ArgumentNullException("logLevel");
		}
		
		sendError(getTraceString(ex), getLevelString(logLevel), 0);
	}
	public void log(String message) {
		if (message == null) {
			throw new ArgumentNullException("message");
		}
		
		sendError(message, parseLevelString(message), 0);
	}
	public void log(String message, Level logLevel) {
		if (message == null) {
			throw new ArgumentNullException("message");
		}
		if (logLevel == null) {
			throw new ArgumentNullException("logLevel");
		}
		
		sendError(message, getLevelString(logLevel), 0);
	}
	
	//private
	private void sendError(String message, String level, int tries) {
		if (tries > 100) {
			return;
		}
		
		new Thread(new Runnable() {
			public void run() {
				if (!doSend) {
					try {
						Thread.sleep(10000L);
					} catch (Exception ex) {
						
					}
					sendError(message, level, tries + 1);
				}
				
				HttpURLConnection conn = null;
				try {
					conn = (HttpURLConnection) new URL(apiUrl + "/" + gameKey + "/events").openConnection();
				} catch (Exception ex) {
					try {
						Thread.sleep(10000L);
					} catch (Exception ex2) {
						
					}
					sendError(message, level, tries + 1);
				}
				
				byte[] postData = cryptoUtil.toBytes(getJsonString(message, level));
				byte[] hmac = cryptoUtil.hmac256(postData, cryptoUtil.toBytes(secretKey));
				
				conn.setDoOutput(true);
				//conn.setDoInput(true);
				conn.setDoInput(false);
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("Connection", "close");
				//conn.setRequestProperty("Content-Encoding", "gzip");
				conn.setRequestProperty("Authorization", cryptoUtil.toString(cryptoUtil.base64Encode(hmac)));
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
				conn.setRequestProperty("User-Agent", "egg82/PluginLibrary/GameAnalyticsAPI");
				
				try {
					conn.setRequestMethod("POST");
					OutputStream out = conn.getOutputStream();
					out.write(postData);
					out.close();
				} catch (Exception ex) {
					try {
						Thread.sleep(10000L);
					} catch (Exception ex2) {
						
					}
					sendError(message, level, tries + 1);
				}
				
				/*try {
					int code = conn.getResponseCode();
					
					InputStream in = (code == 200) ? conn.getInputStream() : conn.getErrorStream();
					InputStreamReader reader = new InputStreamReader(in);
					BufferedReader buffer = new BufferedReader(reader);
					StringBuilder builder = new StringBuilder();
					String line = null;
					while ((line = buffer.readLine()) != null) {
						builder.append(line);
					}
					buffer.close();
					reader.close();
					in.close();
					
					if (code == 200) {
						JSONObject json = new JSONObject(builder.toString());
					}
				} catch (Exception ex) {
					try {
						Thread.sleep(10000L);
					} catch (Exception ex2) {
						
					}
					
					sendError(message, level, tries + 1);
					return;
				}*/
			}
		}).start();
	}
	private void sendInit() {
		new Thread(new Runnable() {
			public void run() {
				HttpURLConnection conn = null;
				try {
					conn = (HttpURLConnection) new URL(apiUrl + "/" + gameKey + "/init").openConnection();
				} catch (Exception ex) {
					try {
						Thread.sleep(10000L);
					} catch (Exception ex2) {
						
					}
					
					sendInit();
					return;
				}
				
				byte[] postData = cryptoUtil.toBytes(getJsonInitString());
				byte[] hmac = cryptoUtil.hmac256(postData, cryptoUtil.toBytes(secretKey));
				
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("Connection", "close");
				//conn.setRequestProperty("Content-Encoding", "gzip");
				conn.setRequestProperty("Authorization", cryptoUtil.toString(cryptoUtil.base64Encode(hmac)));
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
				conn.setRequestProperty("User-Agent", "egg82/PluginLibrary/GameAnalyticsAPI");
				
				try {
					conn.setRequestMethod("POST");
					OutputStream out = conn.getOutputStream();
					out.write(postData);
					out.flush();
					out.close();
				} catch (Exception ex) {
					try {
						Thread.sleep(10000L);
					} catch (Exception ex2) {
						
					}
					
					sendInit();
					return;
				}
				
				try {
					int code = conn.getResponseCode();
					
					InputStream in = (code == 200) ? conn.getInputStream() : conn.getErrorStream();
					InputStreamReader reader = new InputStreamReader(in);
					BufferedReader buffer = new BufferedReader(reader);
					StringBuilder builder = new StringBuilder();
					String line = null;
					while ((line = buffer.readLine()) != null) {
						builder.append(line);
					}
					buffer.close();
					reader.close();
					in.close();
					
					if (code == 200) {
						JSONObject json = new JSONObject(builder.toString());
						doSend = json.getBoolean("enabled");
						tsOffset = (int) ((System.currentTimeMillis() / 1000) - json.getInt("server_ts"));
						if (!doSend) {
							sendInit();
							return;
						}
					}
				} catch (Exception ex) {
					try {
						Thread.sleep(10000L);
					} catch (Exception ex2) {
						
					}
					
					sendInit();
					return;
				}
			}
		}).start();
	}
	
	private String getLevelString(Level level) {
		if (level == Level.SEVERE) {
			return "critical";
		} else if (level == Level.WARNING) {
			return "warning";
		} else if (level == Level.INFO) {
			return "info";
		} else if (level == Level.CONFIG || level == Level.FINE || level == Level.FINER || level == Level.FINEST) {
			return "debug";
		}
		
		return "error";
	}
	
	private String getTraceString(Throwable ex) {
		StringWriter writer = new StringWriter();
		PrintWriter print = new PrintWriter(writer);
		ex.printStackTrace(print);
		String str = writer.toString();
		print.close();
		try {
			writer.close();
		} catch (Exception ex2) {
			
		}
		return str;
	}
	
	private String parseLevelString(String message) {
		String temp = message.toLowerCase();
		
		int critical = lowestIndexOf(temp, "critical", "crit", "severe");
		int error = lowestIndexOf(temp, "error", "err");
		int warning = lowestIndexOf(temp, "warning", "warn");
		int info = lowestIndexOf(temp, "information", "info");
		int debug = temp.indexOf("debug");
		
		int lowest = min(critical, error, warning, info, debug);
		if (lowest == -1) {
			return "error";
		} else {
			if (lowest == critical) {
				return "critical";
			} else if (lowest == error) {
				return "error";
			} else if (lowest == warning) {
				return "warning";
			} else if (lowest == info) {
				return "info";
			} else {
				return "debug";
			}
		}
	}
	private int lowestIndexOf(String haystack, String... needles) {
		int retVal = Integer.MAX_VALUE;
		
		for (int i = 0; i < needles.length; i++) {
			int temp = haystack.indexOf(needles[i]);
			if (temp != -1 && temp < retVal) {
				retVal = temp;
			}
		}
		
		return (retVal != Integer.MAX_VALUE) ? retVal : -1;
	}
	private int min(int... vals) {
		int retVal = Integer.MAX_VALUE;
		
		for (int i = 0; i < vals.length; i++) {
			if (vals[i] != -1 && vals[i] < retVal) {
				retVal = vals[i];
			}
		}
		
		return (retVal != Integer.MAX_VALUE) ? retVal : -1;
	}
	
	private String getJsonString(String message, String level) {
		JSONArray retVal = new JSONArray();
		JSONObject error = new JSONObject();
		
		// Required
		error.put("device", System.getProperty("os.name").replaceAll("\\s", ""));
		error.put("v", 2);
		error.put("user_id", userId);
		error.put("client_ts", (System.currentTimeMillis() / 1000) - tsOffset);
		error.put("sdk_version", "rest api v2");
		error.put("os_version", System.getProperty("os.name").toLowerCase());
		error.put("manufacturer", parseSystemManufacturer(System.getProperty("os.name")));
		error.put("platform", parseSystemName(System.getProperty("os.name")));
		error.put("session_id", sessionId);
		error.put("session_num", 1);
		
		// Optional
		error.put("build", version);
		
		// Error
		error.put("category", "error");
		error.put("severity", level);
		error.put("message", message);
		
		retVal.put(error);
		
		return retVal.toString();
	}
	private String getJsonInitString() {
		JSONObject retVal = new JSONObject();
		
		retVal.put("platform", parseSystemName(System.getProperty("os.name")));
		retVal.put("os_version", System.getProperty("os.name").toLowerCase());
		retVal.put("sdk_version", "rest api v2");
		
		return retVal.toString();
	}
	
	private String parseSystemName(String field) {
		String lowerField = field.toLowerCase();
		
		if (lowerField.contains("win")) {
			return "windows";
		} else if (lowerField.contains("mac")) {
			return "macintosh";
		} else if (lowerField.contains("nix") || lowerField.contains("nux") || lowerField.contains("aix")) {
			return "unix";
		} else if (lowerField.contains("sunos")) {
			return "solaris";
		} else {
			return "unknown: '" + field + "'";
		}
	}
	private String parseSystemManufacturer(String field) {
		String lowerField = field.toLowerCase();
		
		if (lowerField.contains("win")) {
			return "microsoft";
		} else if (lowerField.contains("mac")) {
			return "apple";
		} else if (lowerField.contains("nix") || lowerField.contains("nux") || lowerField.contains("aix")) {
			return "bell";
		} else if (lowerField.contains("sunos")) {
			return "sun";
		} else {
			return "unknown";
		}
	}
	
	private void writeProperties() {
		File propertiesFile = new File(Bukkit.getWorldContainer(), "server.properties");
		String path = propertiesFile.getAbsolutePath();
		
		if (!FileUtil.pathExists(path) || !FileUtil.pathIsFile(path)) {
			return;
		}
		
		try {
			FileUtil.open(path);
			
			String[] lines = cryptoUtil.toString(FileUtil.read(path, 0L)).replaceAll("\r", "").split("\n");
			boolean found = false;
			for (int i = 0; i < lines.length; i++) {
				if (lines[i].trim().startsWith("server-id=")) {
					found = true;
					lines[i] = "server-id=" + userId;
				}
			}
			if (!found) {
				ArrayList<String> temp = new ArrayList<String>(Arrays.asList(lines));
				temp.add("server-id=" + userId);
				lines = temp.toArray(new String[0]);
			}
			
			FileUtil.erase(path);
			FileUtil.write(path, cryptoUtil.toBytes(String.join(FileUtil.LINE_SEPARATOR, lines)), 0L);
			FileUtil.close(path);
		} catch (Exception ex) {
			
		}
	}
	
	/*private byte[] compress(String input) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(outputStream);
		gzip.write(cryptoUtil.toBytes("UTF-8"));
		gzip.close();
		return outputStream.toByteArray();
	}*/
}
