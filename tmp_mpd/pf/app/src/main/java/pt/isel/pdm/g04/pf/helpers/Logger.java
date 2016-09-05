package pt.isel.pdm.g04.pf.helpers;

import android.util.Log;

import pt.isel.pdm.g04.pf.BuildConfig;

public class Logger {

    public static final String LOG_TAG = "PDM-PROJETO_FINAL";

    public static void d(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, message);
        }
    }

    public static void i(String message) {
        if (BuildConfig.DEBUG) {
            Log.i(LOG_TAG, message);
        }
    }

    public static void e(Exception message) {
        if (BuildConfig.DEBUG) {
            message.printStackTrace();
        }
        Log.e(LOG_TAG, message.getMessage());
    }

    public static void e(String message, Exception e) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
        Log.e(LOG_TAG, message, e);
    }

    public static void w(String message) {
        Log.w(LOG_TAG, message);
    }

    public static void c(String className, String methodName) {
        d(String.format("%s#%s called", className, methodName));
    }
}