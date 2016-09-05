package pt.isel.pdm.g04.pf.syncadapter.parse;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pt.isel.pdm.g04.pf.data.Notification;
import pt.isel.pdm.g04.pf.data.parse.localhelpers.ParseEndPoint;
import pt.isel.pdm.g04.pf.data.parse.provider.ParseContract;
import pt.isel.pdm.g04.pf.data.thoth.database.Schema;
import pt.isel.pdm.g04.pf.data.thoth.models.Teacher;
import pt.isel.pdm.g04.pf.helpers.Logger;
import pt.isel.pdm.g04.pf.helpers.Utils;

public class ParseSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String CLASS_NAME = "ParseSyncAdapter";

    public static final String ACTION = "action";
    public static final int ACTION_SUBSCRIBE = 1; // Subscribe to location pushes of a teacher
    public static final int ACTION_UNSUBSCRIBE = 2; // Unsubscribe to location pushes of a teacher
    public static final int ACTION_NOTIFY = 3; // Push location to subscribers

    private Context ctx;

    public ParseSyncAdapter(Context ctx, boolean autoInitialize) {
        super(ctx, autoInitialize);
        this.ctx = ctx;
    }

    public ParseSyncAdapter(Context ctx, boolean autoInitialize, boolean allowParallelSyncs) {
        super(ctx, autoInitialize, allowParallelSyncs);
        this.ctx = ctx;
    }

    // region Life Cycle

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Logger.c(CLASS_NAME, "onPerformSync");
        if (extras == null) {
            Logger.w("No extras to synchronise.");
            return;
        }
        int action = extras.getInt(ACTION);
        if (!isEmailVerified())
            return;

        switch (action) {
            case ACTION_NOTIFY:
                Teacher teacher = Schema.Teachers.selectByEmail(ctx.getContentResolver(), extras.getString(ParseContract.Locations.EMAIL));
                Notification notification = new Notification(teacher)
                        .withLatLon(extras.getDouble(ParseContract.Locations.LATITUDE), extras.getDouble(ParseContract.Locations.LONGITUDE))
                        .withTime(extras.getLong(ParseContract.Locations.TIMESTAMP))
                        .withLocation(extras.getString(ParseContract.Locations.LOCATION));
                pushLocation(notification);
                break;
            case ACTION_SUBSCRIBE:
                updateSubscribedChannels(extras);
                break;
            case ACTION_UNSUBSCRIBE:
                updateSubscribedChannels(extras);
                break;
            default:
                Logger.i("Nothing to synchronise.");
        }
        Logger.i("Sync done.");
    }


    private void updateSubscribedChannels(Bundle extras) {
        String extra = extras.getString(ParseContract.Subscriptions.EMAIL);
        ArrayList<String> emails = TextUtils.isEmpty(extra) ? new ArrayList<>() : new ArrayList(Arrays.asList(extra.split("-")));
        List<String> channels = ParseInstallation.getCurrentInstallation().getList("channels");
        if (channels != null) {
            for (String c : channels) {
                if (!emails.contains(c)) {
                    ParsePush.unsubscribeInBackground(c);
                }
            }
        }
        for (String c : emails) {
            if (channels == null || !channels.contains(c)) {
                ParsePush.subscribeInBackground(c);
            }
        }
    }

    // endregion

    // region Private


    private void pushLocation(Notification notification) {
        if (notification.getTeacherEmail() == null) {
            Logger.e("Error no email to push", new RuntimeException());
            return;
        }
        if (!isEmailVerified()) return;
        try {
            Gson gson = new Gson();
            String json = gson.toJson(notification, notification.getClass());
            JSONObject jsonObject = new JSONObject(json);
            Logger.d("[JSON] Location: " + notification.getLocation() + ", Pushing: " + json);
            ParsePush parsePush = new ParsePush();
            parsePush.setChannel(Utils.mangleEmail(notification.getTeacherEmail()));
            parsePush.setData(jsonObject);
            parsePush.sendInBackground();
        } catch (JSONException e) {
            Logger.e(e.getMessage(), e);
        }
    }

    private boolean isEmailVerified() {
        try {
            return ParseEndPoint.isMyEmailVerified();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    // endregion
}
