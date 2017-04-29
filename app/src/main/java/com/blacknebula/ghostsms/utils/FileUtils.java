package com.blacknebula.ghostsms.utils;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author hazem
 */

public class FileUtils {

    public static void writeToFile(String filePath, byte[] bytes, Context context) {
        try {
            final FileOutputStream fileOutputStream = context.openFileOutput(filePath, Context.MODE_PRIVATE);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (IOException e) {
            Logger.error(Logger.Type.GHOST_SMS, "File write failed: ", e);
        }
    }

    public static byte[] readFromFile(String filePath, Context context) {
        try {
            final InputStream inputStream = context.openFileInput(filePath);
            return org.apache.commons.io.IOUtils.toByteArray(inputStream);
        } catch (FileNotFoundException e) {
            Logger.error(Logger.Type.GHOST_SMS, "File not found: ", e);
        } catch (IOException e) {
            Logger.error(Logger.Type.GHOST_SMS, "File read failed: ", e);
        }
        return new byte[]{};
    }

    public static boolean exists(String filePath) {
        final File file = new File(filePath);
        return file.exists();
    }
}
