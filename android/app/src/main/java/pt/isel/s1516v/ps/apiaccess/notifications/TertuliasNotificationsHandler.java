/*
 * Copyright (c) 2016 António Borba da Silva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * source: https://azure.microsoft.com/en-us/documentation/articles/app-service-mobile-android-get-started-push
 *
 */

package pt.isel.s1516v.ps.apiaccess.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;

import pt.isel.s1516v.ps.apiaccess.MainActivity;
import pt.isel.s1516v.ps.apiaccess.R;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.memberinvitation.ViewMembersActivity;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaListItem;
import pt.isel.s1516v.ps.apiaccess.support.remote.ApiLinks;
import pt.isel.s1516v.ps.apiaccess.tertuliadetails.TertuliaDetailsActivity;

public class TertuliasNotificationsHandler extends com.microsoft.windowsazure.notifications.NotificationsHandler {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    Context ctx;

    @Override
    public void onReceive(Context ctx, Bundle bundle) {
        this.ctx = ctx;
        String message = bundle.getString("message");
        if (message == null)
            return;
        ApiNotification apiNotification = new Gson().fromJson(message, ApiNotification.class);
        if (apiNotification == null || Util.getMyKey().equals(apiNotification.myKey)) {
            Util.logd("Notification due to my own action - returning");
            return;
        }
        sendNotification(ctx, apiNotification);
    }

    private void sendNotification(Context ctx, ApiNotification apiNotification) {
        Util.logd("NOTIFICATION RECEIVED: " + apiNotification.toString());
        Class clazz = null;
        String apiLinksLabel = null;
        ApiLinks apiLinks = null;
        String message = null;
        int tr_id, index;
        TertuliaListItem[] tertulias = MainActivity.tertulias;
        TertuliaListItem tertulia;
        switch (apiNotification.action) {
            case "message":
                tr_id = apiNotification.tertulia;
                if ((index = getIndex(tertulias, tr_id)) == -1)
                    return;
                clazz = TertuliaDetailsActivity.class;
                apiLinksLabel = TertuliaDetailsActivity.INTENT_LINKS;
                tertulia = tertulias[index];
                apiLinks = new ApiLinks(tertulia.links);
                message = String.format("Received a new post to Tertulia %s", tertulia.name); // TODO: Strings
                ContentResolver.requestSync(MainActivity.account, "pt.isel.s1516v.ps.apiaccess.contentprovider.TertuliasContentProvider", null);
                break;
            case "update":
                tr_id = apiNotification.tertulia;
                if ((index = getIndex(tertulias, tr_id)) == -1)
                    return;
                clazz = TertuliaDetailsActivity.class;
                apiLinksLabel = TertuliaDetailsActivity.INTENT_LINKS;
                tertulia = tertulias[index];
                apiLinks = new ApiLinks(tertulia.links);
                message = String.format("Tertulia %s was edited", tertulia.name); // TODO: Strings
                break;
            case "subscribe":
                tr_id = apiNotification.tertulia;
                if ((index = getIndex(tertulias, tr_id)) == -1)
                    return;
                clazz = ViewMembersActivity.class;
                apiLinksLabel = ViewMembersActivity.INTENT_LINKS;
                tertulia = tertulias[index];
                apiLinks = new ApiLinks(tertulia.links);
                message = String.format("A new member has joined Tertulia %s", tertulia.name); // TODO: Strings
                break;
            case "unsubscribe":
                tr_id = apiNotification.tertulia;
                if ((index = getIndex(tertulias, tr_id)) == -1)
                    return;
                clazz = ViewMembersActivity.class;
                apiLinksLabel = ViewMembersActivity.INTENT_LINKS;
                tertulia = tertulias[index];
                apiLinks = new ApiLinks(tertulia.links);
                message = String.format("A member has left Tertulia %s", tertulia.name); // TODO: Strings
                break;
            default:
                    break;
        }
        Intent intent = new Intent(ctx, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(apiLinksLabel, apiLinks);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.mipmap.tertulias)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(ctx.getResources().getString(R.string.tertulias_notification_title))
                .setContentText(message)
                .setContentIntent(contentIntent);
        notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private int getIndex(TertuliaListItem[] tertulias, int id) {
        if (tertulias == null)
            return -1;
        for (int i = 0; i < tertulias.length; i++)
            if (tertulias[i].id == id)
                return i;
        return -1;
    }

}