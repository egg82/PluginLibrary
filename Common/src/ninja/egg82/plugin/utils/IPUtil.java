package ninja.egg82.plugin.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;

public class IPUtil {
	//vars
	private static String[] sites = new String[] {
		"http://checkip.amazonaws.com",
		"https://icanhazip.com/",
		"http://www.trackip.net/ip",
		"http://myexternalip.com/raw",
		"http://ipecho.net/plain",
		"https://bot.whatismyipaddress.com/"
	};
	
	//constructor
	public IPUtil() {
		
	}
	
	//public
	public static String getExternalIp() {
		URL url = null;
		BufferedReader in = null;
		
		for (String addr : sites) {
			try {
				url = new URL(addr);
				in = new BufferedReader(new InputStreamReader(url.openStream()));
				String ip = in.readLine();
				InetAddress.getByName(ip);
				return ip;
			} catch (Exception ex) {
				continue;
			} finally {
				if (in != null) {
					try {
						in.close();
						in = null;
					} catch (Exception ex) {
						
					}
				}
			}
		}
		
		return null;
	}
	
	//private
	
}
