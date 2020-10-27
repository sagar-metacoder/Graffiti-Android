package com.app.graffiti.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * {@link SharedPrefs} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 14/10/17
 */

public class SharedPrefs {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static void init(Context context, String fileName) {
        sharedPreferences = context.getSharedPreferences(fileName, context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static void putValue(String key, Object value) {
        if (value instanceof String)
            editor.putString(key, (String) value);
        else if (value instanceof Boolean)
            editor.putBoolean(key, (Boolean) value);
        else if (value instanceof Integer)
            editor.putInt(key, (int) value);
        else if (value instanceof Float)
            editor.putFloat(key, (Float) value);
        else if (value instanceof Long)
            editor.putLong(key, (Long) value);
        else if (value instanceof Set)
            editor.putStringSet(key, (Set) value);
        else
            editor.putString(key, String.valueOf(value));
        editor.commit();
    }

    public static void clear() {
        editor.clear();
        editor.commit();
    }

    public static String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public static Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public static int getInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public static float getFloat(String key) {
        return sharedPreferences.getFloat(key, -1);
    }

    public static long getLong(String key) {
        return sharedPreferences.getLong(key, -1);
    }

    public static Set getStringSet(String key) {
        return sharedPreferences.getStringSet(key, null);
    }
}