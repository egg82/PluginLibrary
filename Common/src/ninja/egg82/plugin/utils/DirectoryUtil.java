package ninja.egg82.plugin.utils;

import java.io.File;

public class DirectoryUtil {
    // vars

    // constructor
    public DirectoryUtil() {

    }

    // public
    public static void delete(File directory) {
        if (!directory.exists()) {
            return;
        }
        if (!directory.isDirectory()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            directory.delete();
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                delete(file);
            } else {
                file.delete();
            }
        }

        directory.delete();
    }

    // private

}
