package pt.isel.pdm.g04.pf.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import pt.isel.pdm.g04.pf.R;
import pt.isel.pdm.g04.pf.TeacherLocatorApplication;
import pt.isel.pdm.g04.pf.data.parse.provider.ParseContract;
import pt.isel.pdm.g04.pf.helpers.Constants;
import pt.isel.pdm.g04.pf.helpers.Logger;
import pt.isel.pdm.g04.pf.helpers.Utils;
import pt.isel.pdm.g04.pf.presentation.MainActivity;
import pt.isel.pdm.g04.pf.workers.Task;

public class ParseReceiver extends BroadcastReceiver {
    private static final String CLASS_NAME = "ParseReceiver";

    public static final String ACTION = "pt.isel.pdm.g04.pf.ParsePushAction"; // Must match intent filter in the Manifest

    private static final String PARSE_ALERT_KEY = "alert";
    private static final String PARSE_CHANNELS_KEY = "com.parse.Channel";
    private static final String PARSE_DATA_KEY = "com.parse.Data";


    @Override
    public void onReceive(Context ctx, Intent intent) {
        Logger.c(CLASS_NAME, "onReceive");
        try {
            // extract the action from the intent to prepare notification
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String jsonRawData = intent.getExtras().getString(PARSE_DATA_KEY);
            Logger.d("[JSON] Receiving: " + jsonRawData);
            Gson gson = new Gson();
            pt.isel.pdm.g04.pf.data.Notification notification = gson.fromJson(jsonRawData, pt.isel.pdm.g04.pf.data.Notification.class);

            Bundle bundle = new Bundle();
            bundle.putString(ParseContract.Notifications.NAME, notification.getTeacherName());
            bundle.putString(ParseContract.Notifications.EMAIL, notification.getTeacherEmail());
            bundle.putDouble(ParseContract.Notifications.LATITUDE, notification.getLatitude());
            bundle.putDouble(ParseContract.Notifications.LONGITUDE, notification.getLongitude());
            bundle.putString(ParseContract.Notifications.LOCATION, notification.getLocation());
            bundle.putLong(ParseContract.Notifications.TIMESTAMP, notification.getTime());
            if (notification.getLocation().equals(Constants.Isel.Locations.OUTSIDE)) {
                ctx.getContentResolver().delete(ParseContract.Notifications.CONTENT_URI, ParseContract.Notifications.EMAIL + " = ?", new String[]{Utils.getEmail()});
            } else {
                storeNotification(ctx, notification);
                if (!Utils.isActivityVisible())
                {
                    publishNotification(ctx, intent, notification);
                }

            }
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    private void storeNotification(Context ctx, pt.isel.pdm.g04.pf.data.Notification notification) {
        ContentValues values = getNotificationContentValues(ctx, notification);
        if (!exists(ctx, notification.getTeacherEmail())) {
            ctx.getContentResolver().insert(ParseContract.Notifications.CONTENT_URI, values);
        } else {
            ctx.getContentResolver().update(ParseContract.Notifications.CONTENT_URI, values,
                    ParseContract.Notifications.EMAIL + " = ?",
                    new String[]{notification.getTeacherEmail()});

        }
    }

    private boolean exists(Context ctx, String email) {
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = ctx.getContentResolver();
            cursor = contentResolver.query(ParseContract.Notifications.CONTENT_URI,
                    new String[]{ParseContract.Notifications.EMAIL},
                    ParseContract.Notifications.EMAIL + " = ?",
                    new String[]{email},
                    null);
            return cursor.moveToFirst();
        } finally {
            if (cursor != null) cursor.close();
        }
    }


    private ContentValues getNotificationContentValues(Context ctx, pt.isel.pdm.g04.pf.data.Notification notification) {
        if (notification.getTeacherEmail() == null)
            throw new RuntimeException("Email value should exist in the bundle");
        ContentValues values = new ContentValues();
        values.put(ParseContract.Notifications.NAME, notification.getTeacherName());
        values.put(ParseContract.Notifications.EMAIL, notification.getTeacherEmail());
        values.put(ParseContract.Notifications.LATITUDE, notification.getLatitude());
        values.put(ParseContract.Notifications.LONGITUDE, notification.getLongitude());
        values.put(ParseContract.Notifications.LOCATION, notification.getLocation());
        values.put(ParseContract.Notifications.PHOTO, notification.getTeacherAvatarUrl());
        values.put(ParseContract.Notifications.TIMESTAMP, notification.getTime());

        return values;
    }

    private void publishNotification(final Context ctx, final Intent intent, final pt.isel.pdm.g04.pf.data.Notification notification) {
        Intent newIntent = new Intent(ctx, MainActivity.class); // TODO: Set to the correct activity
        newIntent.putExtra(PARSE_DATA_KEY, intent.getExtras().getString(PARSE_DATA_KEY));
        final PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        final Task<Bitmap> task = new Task<Bitmap>() {

            @Override
            public void run() {
                Notification n = new NotificationCompat.Builder(ctx)
                        .setContentIntent(pendingIntent)
                        .setTicker(notification.getTeacherName())
                        .setContentTitle(notification.getTeacherName())
                        .setContentText(String.format("%s, %s", notification.getFriendlyLocation(ctx), Utils.getFriendlyDate(ctx, new Date(notification.getTime()))))
                        .setWhen(notification.getTime())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(res)
                        .setAutoCancel(true)
                        .setExtras(intent.getExtras())
                        .build();
                ((NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, n);
            }
        };
        task.url = notification.getTeacherAvatarUrl();
        TeacherLocatorApplication.sIOThread.queueImageRead(task);
    }
}
