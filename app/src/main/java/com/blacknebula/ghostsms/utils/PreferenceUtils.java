package com.blacknebula.ghostsms.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.blacknebula.ghostsms.GhostSmsApplication;

/**
 * @author hazem
 */

public class PreferenceUtils {

    private static SharedPreferences sharedPreferences;

    public static SharedPreferences getPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(GhostSmsApplication.getAppContext());
        }
        return sharedPreferences;
    }

    public static String getString(String key, String defaultValue) {
        return getPreferences().getString(key, defaultValue);
    }

    public static Boolean getBoolean(String key, Boolean defaultValue) {
        return getPreferences().getBoolean(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        return getPreferences().getInt(key, defaultValue);
    }

    public static int getIntFromString(String key, int defaultValue) {
        return Integer.parseInt(getPreferences().getString(key, "" + defaultValue));
    }
}
