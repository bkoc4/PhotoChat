package com.photo.advanced.photochat.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesHelper {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static final String defaultString = "nothing";

    public static void initialize(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public static String getECKeyS() {
        return sharedPreferences.getString(Keys.ECKeyS,defaultString);
    }

    public static void setECKeyS(String value) {
        editor.putString(Keys.ECKeyS,value);
        editor.commit();
    }

    public class Keys {
        public static final String ECKeyS = "eckeys";
    }

}
