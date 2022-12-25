package com.hcmus.picbox.utils;

import static com.hcmus.picbox.models.AbstractModel.GROUP_MODE_OPTION_3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created on 21/11/2022 by Minh Kha
 */
public class SharedPreferencesUtils {

    public static final String KEY_SPAN_COUNT = "num_columns_of_row";
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_GROUP_MODE = "group_mode";
    public static final String KEY_GRID_MODE = "grid_mode";
    public static final String KEY_BACKGROUND_IMAGE = "background_image";
    private static final String PREF_APP = "pref_app";

    public static final String LANGUAGE_OPTION_1 = "english";
    public static final String LANGUAGE_OPTION_2 = "vietnamese";

    public static final int SPAN_COUNT_DEFAULT = 4;
    public static final String GROUP_MODE_DEFAULT = GROUP_MODE_OPTION_3;
    public static final String LANGUAGE_DEFAULT = LANGUAGE_OPTION_2;

    private SharedPreferencesUtils() {
        throw new UnsupportedOperationException(
                "Should not create instance of Util class. Please use as static..");
    }

    /**
     * check key exist.
     *
     * @param context the context
     * @param key     the key
     * @return the boolean if key exists in Shared Preferences
     */
    static public boolean checkKeyExist(Context context, String key) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).contains(key);
    }

    /**
     * Gets boolean data.
     *
     * @param context the context
     * @param key     the key
     * @return the boolean data
     */
    static public boolean getBooleanData(Context context, String key) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getBoolean(key, false);
    }

    /**
     * Gets int data.
     *
     * @param context the context
     * @param key     the key
     * @return the int data
     */
    static public int getIntData(Context context, String key) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getInt(key, 0);
    }

    /**
     * Gets string data.
     *
     * @param context the context
     * @param key     the key
     * @return the string data
     */
    // Get Data
    static public String getStringData(Context context, String key) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getString(key, null);
    }

    /**
     * Save data.
     *
     * @param context the context
     * @param key     the key
     * @param val     the val
     */
    // Save Data
    static public void saveData(Context context, String key, String val) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().putString(key, val).apply();
    }

    /**
     * Save data.
     *
     * @param context the context
     * @param key     the key
     * @param val     the val
     */
    static public void saveData(Context context, String key, int val) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().putInt(key, val).apply();
    }

    /**
     * Save data.
     *
     * @param context the context
     * @param key     the key
     * @param val     the val
     */
    static public void saveData(Context context, String key, boolean val) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(key, val)
                .apply();
    }

    static public SharedPreferences.Editor getSharedPrefEditor(Context context, String pref) {
        return context.getSharedPreferences(pref, Context.MODE_PRIVATE).edit();
    }

    static public void saveData(SharedPreferences.Editor editor) {
        editor.apply();
    }

    /**
     * remove key and value.
     *
     * @param context the context
     * @param key     the key
     */
    @SuppressLint("CommitPrefEdits")
    static public void removeData(Context context, String key) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE)
                .edit()
                .remove(key)
                .apply();
    }
}
