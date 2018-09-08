package com.photo.advanced.photochat.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ShareActionProvider;

import java.util.PropertyResourceBundle;

public class PreferencesHelper {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static final String defaultString = "nothing";

    public static void initialize(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public static String getECKey() {
        return sharedPreferences.getString(Keys.ECKey,defaultString);
    }

    public static void setECKey(String value) {
        editor.putString(Keys.ECKey,value);
    }

    public class Keys {
        public static final String ECKey = "eckey";
    }

}
