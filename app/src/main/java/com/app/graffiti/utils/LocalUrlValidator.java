package com.app.graffiti.utils;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;

/**
 * {@link LocalUrlValidator} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 14/12/17
 */

public class LocalUrlValidator {
    private static final String TAG = LocalUrlValidator.class.getSimpleName();

    public static boolean isValidLocalUrl(
            Context context,
            String localPath
    ) {
        String splitPath[] = getSplitPath(localPath);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Log.wtf(
                    TAG,
                    " isValidLocalUrl : Permission to external storage is not granted !",
                    new SecurityException("Required permission " + Manifest.permission.WRITE_EXTERNAL_STORAGE + "is not allowed, have you forgot adding it to manifest !?\nNote:Handle runtime permission properly")
            );
            return false;
        } else {
            StringBuilder dirPath = new StringBuilder("/" + splitPath[1]);
            for (int index = 1; index < splitPath.length; index++) {
                if (index < splitPath.length - 1) {
                    if (isValidDir(dirPath.toString())) {
                        dirPath.append("/").append(splitPath[index + 1]);
                    } else {
                        return false;
                    }
                } else {
                    return isValidFile(dirPath.toString());
                }
            }
            return false;
        }
    }

    private static String[] getSplitPath(String localPath) {
        return localPath.split("/");
    }

    private static boolean isValidDir(String directory) {
        File file = new File(directory);
        return file.isDirectory() && file.exists();
    }

    private static boolean isValidFile(String filePath) {
        File file = new File(filePath);
        return file.isFile() && file.exists();
    }
}
