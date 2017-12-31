package ninja.egg82.plugin.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ninja.egg82.exceptions.ArgumentNullException;

public class TimeUtil {
	//vars
	private static DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
	private static Pattern timePattern = Pattern.compile("^([0-9]+)\\s?(seconds?|secs?|s|minutes?|mins?|m|hours?|hrs?|h|days?|dys?|d|weeks?|wks?|months?|mos?|years?|yrs?)", Pattern.CASE_INSENSITIVE);
	
	//constructor
	public TimeUtil() {
		
	}
	
	//public
	public static long getTime(String from) {
		if (from == null) {
			throw new ArgumentNullException("from");
		}
		
		Matcher m = timePattern.matcher(from);
		if (!m.matches()) {
			throw new RuntimeException("\"" + from + "\" does not match expected time pattern.");
		} else {
			long time = 1000L;
			
			if (m.group(2).equalsIgnoreCase("s") || m.group(2).equalsIgnoreCase("sec") || m.group(2).equalsIgnoreCase("secs") || m.group(2).equalsIgnoreCase("second") || m.group(2).equalsIgnoreCase("seconds")) {
				// Do nothing, time is already in seconds
			} else if (m.group(2).equalsIgnoreCase("m") || m.group(2).equalsIgnoreCase("min") || m.group(2).equalsIgnoreCase("mins") || m.group(2).equalsIgnoreCase("minute") || m.group(2).equalsIgnoreCase("minutess")) {
				time *= 60L;
			} else if (m.group(2).equalsIgnoreCase("h") || m.group(2).equalsIgnoreCase("hr") || m.group(2).equalsIgnoreCase("hrs") || m.group(2).equalsIgnoreCase("hour") || m.group(2).equalsIgnoreCase("hours")) {
				time *= 60L * 60L;
			} else if (m.group(2).equalsIgnoreCase("d") || m.group(2).equalsIgnoreCase("dy") || m.group(2).equalsIgnoreCase("dys") || m.group(2).equalsIgnoreCase("day") || m.group(2).equalsIgnoreCase("days")) {
				time *= 60L * 60L * 24L;
			} else if (m.group(2).equalsIgnoreCase("wk") || m.group(2).equalsIgnoreCase("wks") || m.group(2).equalsIgnoreCase("week") || m.group(2).equalsIgnoreCase("weeks")) {
				time *= 60L * 60L * 24L * 7L;
			} else if (m.group(2).equalsIgnoreCase("mo") || m.group(2).equalsIgnoreCase("mos") || m.group(2).equalsIgnoreCase("month") || m.group(2).equalsIgnoreCase("months")) {
				time *= 60L * 60L * 24L * 30L;
			} else if (m.group(2).equalsIgnoreCase("yr") || m.group(2).equalsIgnoreCase("yrs") || m.group(2).equalsIgnoreCase("year") || m.group(2).equalsIgnoreCase("years")) {
				time *= 60L * 60L * 24L * 365L;
			}
			
			time *= Integer.parseUnsignedInt(m.group(1));
			
			return time;
		}
	}
	
	public static String timeToHoursMinsSecs(long time) {
		short hours = 0;
		short minutes = 0;
		short seconds = 0;
		
		while (time >= 3600000) {
			hours++;
			time -= 3600000;
		}
		while (time >= 60000) {
			minutes++;
			time -= 60000;
		}
		while (time >= 1000) {
			seconds++;
			time -= 1000;
		}
		if (time >= 700) {
			seconds++;
		}
		
		return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
	}
	public static String timeToDateString(long time) {
		return formatter.format(new Date(time));
	}
	
	//private
	
}
