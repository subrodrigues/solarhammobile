package tp.solardospresuntos.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by filiperodrigues on 22/05/17.
 */

public class SettingsUtils {
    public static final String PREFERENCES_NAME = "preferences";
    private static final String PREFERENCE_PUSH = "pushNotificationEnabled";
    private static final String PREFERENCE_SOUND = "soundEnabled";
    private static final String PREFERENCE_FIRST_TIME = "firstTimeApplication";
    public static final String PREFERENCES_POPUP = "popup";

    public SettingsUtils() {
    }

    public static void savePushNotificationsSettings(Context context, boolean isEnabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences("preferences", 0).edit();
        editor.putBoolean("pushNotificationEnabled", isEnabled);
        editor.commit();
    }

    public static boolean isPushNotificationEnabled(Context context) {
        SharedPreferences editor = context.getSharedPreferences("preferences", 0);
        return editor.getBoolean("pushNotificationEnabled", true);
    }

    public static void saveSoundEnabledSettings(Context context, boolean isEnabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences("preferences", 0).edit();
        editor.putBoolean("soundEnabled", isEnabled);
        editor.commit();
    }

    public static boolean isSoundEnabled(Context context) {
        SharedPreferences editor = context.getSharedPreferences("preferences", 0);
        return editor.getBoolean("soundEnabled", true);
    }

    public static void saveFirstTimeApplication(Context context, boolean isFirstTime) {
        SharedPreferences.Editor editor = context.getSharedPreferences("preferences", 0).edit();
        editor.putBoolean("firstTimeApplication", isFirstTime);
        editor.commit();
    }

    public static boolean isFirstTimeApplication(Context context) {
        SharedPreferences editor = context.getSharedPreferences("preferences", 0);
        return editor.getBoolean("firstTimeApplication", true);
    }

    public static void setLastTimePopup(Context context, long time) {
        SharedPreferences.Editor editor = context.getSharedPreferences("preferences", 0).edit();
        editor.putLong("popup", time);
        editor.commit();
    }

    public static boolean showPopup(Context context, long interval) {
        SharedPreferences editor = context.getSharedPreferences("preferences", 0);
        long lastTime = editor.getLong("popup", 0L);
        return interval < System.currentTimeMillis() - lastTime;
    }

    public static <T> void saveObjectToSettings(Context context, String name, T object) {
        SharedPreferences mPrefs = context.getSharedPreferences("preferences", 0);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(object);
        prefsEditor.putString(name, json);
        prefsEditor.commit();
    }

    public static <T> T getObjectFromSettings(Context context, String name, Class<T> objectClass) {
        SharedPreferences mPrefs = context.getSharedPreferences("preferences", 0);
        Gson gson = new Gson();
        String json = mPrefs.getString(name, "");
        Object obj = gson.fromJson(json, objectClass);
        return (T) obj;
    }

    public static <T> T getObjectFromSettingsByType(Context context, String name, Type objectType) {
        SharedPreferences mPrefs = context.getSharedPreferences("preferences", 0);
        Gson gson = new Gson();
        String json = mPrefs.getString(name, "");
        Object obj = gson.fromJson(json, objectType);
        return (T) obj;
    }

}
