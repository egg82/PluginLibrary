package ninja.egg82.plugin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class WebUtil {
    // vars

    // constructor
    public WebUtil() {

    }

    // public
    public static JSONArray getJsonArray(String url, String userAgent) throws IOException, ProtocolException, ParseException {
        return getJsonArray(url, userAgent, "GET", null);
    }
    public static JSONArray getJsonArray(String url, String userAgent, String method) throws IOException, ProtocolException, ParseException {
        return getJsonArray(url, userAgent, method, null);
    }
    public static JSONArray getJsonArray(String url, String userAgent, Map<String, String> headers) throws IOException, ProtocolException, ParseException {
        return getJsonArray(url, userAgent, "GET", headers);
    }
    public static JSONArray getJsonArray(String url, String userAgent, String method, Map<String, String> headers) throws IOException, ProtocolException, ParseException {
        String retVal = getString(url, userAgent, method, headers);
        if (retVal == null) {
            return null;
        }
        return JSONUtil.parseArray(retVal);
    }

    public static JSONObject getJsonObject(String url, String userAgent) throws IOException, ProtocolException, ParseException {
        return getJsonObject(url, userAgent, "GET", null);
    }
    public static JSONObject getJsonObject(String url, String userAgent, String method) throws IOException, ProtocolException, ParseException {
        return getJsonObject(url, userAgent, method, null);
    }
    public static JSONObject getJsonObject(String url, String userAgent, Map<String, String> headers) throws IOException, ProtocolException, ParseException {
        return getJsonObject(url, userAgent, "GET", headers);
    }
    public static JSONObject getJsonObject(String url, String userAgent, String method, Map<String, String> headers) throws IOException, ProtocolException, ParseException {
        String retVal = getString(url, userAgent, method, headers);
        if (retVal == null) {
            return null;
        }
        return JSONUtil.parseObject(retVal);
    }
    
    public static String getString(String url, String userAgent, String method, Map<String, String> headers) throws IOException, ProtocolException {
        if (url == null) {
            throw new IllegalArgumentException("url cannot be null.");
        }
        if (userAgent == null) {
            throw new IllegalArgumentException("userAgent cannot be null.");
        }
        if (method == null) {
            throw new IllegalArgumentException("method cannot be null.");
        }

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

        conn.setDoOutput(false);
        conn.setDoInput(true);
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Connection", "close");
        conn.setRequestProperty("User-Agent", userAgent);
        if (headers != null) {
            for (Entry<String, String> kvp : headers.entrySet()) {
                conn.setRequestProperty(kvp.getKey(), kvp.getValue());
            }
        }

        conn.setRequestMethod(method);

        int code = conn.getResponseCode();

        try (InputStream in = (code == 200) ? conn.getInputStream() : conn.getErrorStream();
            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader buffer = new BufferedReader(reader);) {
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = buffer.readLine()) != null) {
                builder.append(line);
            }

            if (code == 200) {
                return builder.toString();
            }
        }

        return null;
    }

    // private

}
