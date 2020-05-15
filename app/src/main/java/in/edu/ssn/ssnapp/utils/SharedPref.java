package in.edu.ssn.ssnapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    public static SharedPreferences getSharePref(Context context) {
        return context.getSharedPreferences("status", Context.MODE_PRIVATE);
    }

    public static SharedPreferences getSharePref(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /********************************************************************************/

    public static String getString(Context context, String key) {
        return getSharePref(context).getString(key, null);
    }

    public static String getString(Context context, String name, String key) {
        return getSharePref(context, name).getString(key, null);
    }

    /*********************************************************/

    public static int getInt(Context context, String key) {
        return getSharePref(context).getInt(key, 0);
    }

    public static int getInt(Context context, String name, String key) {
        return getSharePref(context, name).getInt(key, 0);
    }

    /*********************************************************/

    public static long getLong(Context context, String key) {
        return getSharePref(context).getLong(key, 0);
    }

    public static long getLong(Context context, String name, String key) {
        return getSharePref(context, name).getLong(key, 0);
    }

    /*********************************************************/

    public static Boolean getBoolean(Context context, String key) {
        return getSharePref(context).getBoolean(key, false);
    }

    public static Boolean getBoolean(Context context, String name, String key) {
        return getSharePref(context, name).getBoolean(key, false);
    }

    /********************************************************************************/

    public static void putString(Context context, String key, String value) {
        getSharePref(context).edit().putString(key, value).apply();
    }

    public static void putString(Context context, String name, String key, String value) {
        getSharePref(context, name).edit().putString(key, value).apply();
    }

    /*********************************************************/

    public static void putInt(Context context, String key, int value) {
        getSharePref(context).edit().putInt(key, value).apply();
    }

    public static void putInt(Context context, String name, String key, int value) {
        getSharePref(context, name).edit().putInt(key, value).apply();
    }

    /*********************************************************/

    public static void putLong(Context context, String key, long value) {
        getSharePref(context).edit().putLong(key, value).apply();
    }

    public static void putLong(Context context, String name, String key, long value) {
        getSharePref(context, name).edit().putLong(key, value).apply();
    }

    /*********************************************************/

    public static void putBoolean(Context context, String key, Boolean value) {
        getSharePref(context).edit().putBoolean(key, value).apply();
    }

    public static void putBoolean(Context context, String name, String key, Boolean value) {
        getSharePref(context, name).edit().putBoolean(key, value).apply();
    }

    /********************************************************************************/

    public static void remove(Context context, String key) {
        getSharePref(context).edit().remove(key).apply();
    }

    public static void remove(Context context, String name, String key) {
        getSharePref(context, name).edit().remove(key).apply();
    }

    /*********************************************************/

    public static void removeAll(Context context) {
        getSharePref(context).edit().clear().apply();
    }

    public static void removeAll(Context context, String name) {
        getSharePref(context, name).edit().clear().apply();
    }


}