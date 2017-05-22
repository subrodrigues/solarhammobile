package tp.solardospresuntos.android.utils;

import java.io.File;

/**
 * Created by filiperodrigues on 22/05/17.
 */

public class FileUtils {
    private static final String[] imageFileOkExtensions =  new String[] {"jpg", "png","jpeg"};

    public static boolean validateImageFileExtension(File file) {
        for (String extension : imageFileOkExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
