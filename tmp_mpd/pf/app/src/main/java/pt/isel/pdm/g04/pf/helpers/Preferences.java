package pt.isel.pdm.g04.pf.helpers;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Preferences {

    public static boolean useColoredNotifications(Context context) {
        return getDefault(context).getBoolean(Constants.Preferences.USE_COLORED_NOTIFICATIONS, true);
    }

    public static boolean useBatterySavingMode(Context context) {
        return getDefault(context).getBoolean(Constants.Preferences.USE_BATTERY_SAVING_MODE, false);
    }

    public static boolean isSetupComplete(Context context) {
        return getDefault(context).getBoolean(Constants.Preferences.PREF_SETUP_COMPLETE, false);
    }

    public static boolean showGeofences(Context context) {
        return getDefault(context).getBoolean(Constants.Preferences.SHOW_GEOFENCES, false);
    }

    public static void setLastAccount(Context context, Account account) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString("account", account.name)
                .putString("type", account.type).commit();
    }

    public static Account getLastAccount(Context context) {
        return new Account(
                getDefault(context).getString("account", "invalid_dummy"),
                getDefault(context).getString("type", "invalid_dummy"));
    }

    public static void setSetupComplete(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(Constants.Preferences.PREF_SETUP_COMPLETE, true).commit();
    }

    public static String getStudentsUrl(Context context) {
        return getDefault(context).getString(Constants.Preferences.STUDENT_SERVER_ADDRESS, Constants.Thoth.Urls.STUDENTS);
    }

    public static String getTeachersUrl(Context context) {
        return getDefault(context).getString(Constants.Preferences.TEACHER_SERVER_ADDRESS, Constants.Thoth.Urls.TEACHERS);
    }

    public static int getDefaultCacheSize() {
        int maxMemory;
        //use 25% of available heap size
        maxMemory = (int) (Runtime.getRuntime().maxMemory());
        // Use 1/8th of the available memory for this memory cache.
        return maxMemory / 8 / 1024;
    }

    public static int getMemoryCacheSize(Context context) {
        return getDefault(context).getInt(Constants.Preferences.MEMORY_CACHE_SIZE, getDefaultCacheSize());
    }

    public static int getDiskCacheSize(Context context) {
        return getDefault(context).getInt(Constants.Preferences.DISK_CACHE_SIZE, getDefaultCacheSize());
    }


    private static SharedPreferences getDefault(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

}
