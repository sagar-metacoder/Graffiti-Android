package com.app.graffiti.utils;


import android.util.Log;

import com.app.graffiti.BuildConfig;

/**
 * {@link Logger} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 5/4/18
 */
public class Logger {

    public static void log(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message);
        } else {
            Log.v(TAG, message);
        }
    }

    public static void log(String TAG, String message, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message, throwable);
        } else {
            Log.e(TAG, message, throwable);
        }
    }

    public static void log(String TAG, String message, Level level) {
        if (BuildConfig.DEBUG) {
            switch (level) {
                case VERBOSE: {
                    Log.v(TAG, message);
                    break;
                }
                case DEBUG: {
                    Log.d(TAG, message);
                    break;
                }
                case INFO: {
                    Log.i(TAG, message);
                    break;
                }
                case WARN: {
                    Log.w(TAG, message);
                    break;
                }
                case ERROR: {
                    Log.e(TAG, message);
                    break;
                }
                default: {
                    Log.v(TAG, message);
                    break;
                }
            }
        }
    }

    public enum Level {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }
}
