package ninja.egg82.plugin.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import com.google.common.io.Files;

public class UpdateUtil {
    // vars

    // constructor
    public UpdateUtil() {

    }

    // public
    public static void downloadLatest(File file, String url, String userAgent) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null.");
        }
        if (url == null) {
            throw new IllegalArgumentException("url cannot be null.");
        }
        if (userAgent == null) {
            throw new IllegalArgumentException("userAgent cannot be null.");
        }

        if (file.exists()) {
            throw new IllegalArgumentException("file already exists!");
        }

        URL site = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) site.openConnection();
        conn.setRequestProperty("Connection", "close");
        conn.setRequestProperty("User-Agent", userAgent);

        int status = conn.getResponseCode();
        if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER || status == 307) {
            downloadLatest(file, conn.getHeaderField("Location"), userAgent);
            return;
        }

        try (InputStream stream = conn.getInputStream(); ReadableByteChannel channel = Channels.newChannel(stream); FileOutputStream output = new FileOutputStream(file);) {
            output.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
        }
    }
    public static void replace(File oldFile, File newFile, boolean onExit) throws IOException, SecurityException {
        if (oldFile == null) {
            throw new IllegalArgumentException("oldFile cannot be null.");
        }
        if (newFile == null) {
            throw new IllegalArgumentException("newFile cannot be null.");
        }
        
        if (!newFile.exists()) {
            throw new IllegalArgumentException("newFile does not exist!");
        }

        if (oldFile.exists() && !oldFile.isFile()) {
            throw new IllegalArgumentException("oldFile is a directory!");
        }
        if (!newFile.isFile()) {
            throw new IllegalArgumentException("newFile is a directory!");
        }

        if (onExit) {
            oldFile.deleteOnExit();
        } else {
            oldFile.delete();
        }
        Files.move(newFile, new File(oldFile.getParentFile(), newFile.getName()));
    }

    public static String getSpigotDownloadLink(int resourceId) {
        return "https://api.spiget.org/v2/resources/" + resourceId + "/versions/latest/download";
    }
    public static String getSpigotDownloadLink(String resourceId) {
        return "https://api.spiget.org/v2/resources/" + resourceId + "/versions/latest/download";
    }

    public static boolean isUpdateAvailable(String currentVersion, String latestVersion) {
        if (latestVersion == null) {
            return false;
        }
        if (currentVersion == null) {
            return true;
        }

        int[] latest = VersionUtil.parseVersion(latestVersion, '.');
        int[] current = VersionUtil.parseVersion(currentVersion, '.');

        boolean equal = true;
        for (int i = 0; i < Math.min(latest.length, current.length); i++) {
            if (latest[i] < current[i]) {
                return false;
            } else if (latest[i] > current[i]) {
                equal = false;
                break;
            }
        }

        return (!equal) ? true : false;
    }
    public static String getVersion(String url) throws IOException {
        if (url == null) {
            throw new IllegalArgumentException("url cannot be null.");
        }

        URL site = new URL(url);
        URLConnection conn = site.openConnection();
        try (InputStream stream = conn.getInputStream(); InputStreamReader reader = new InputStreamReader(stream); BufferedReader in = new BufferedReader(reader);) {
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        }
    }

    // private

}
