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
 */

package pt.isel.s1516v.ps.apiaccess.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.microsoft.windowsazure.messaging.NotificationHub;

import pt.isel.s1516v.ps.apiaccess.helpers.Util;

public class NotificationsToken {
    public static void regenerate(Context ctx) {
        new AsyncTask<Context, Void, Void>() {
            @Override
            protected Void doInBackground(Context... params) {
                Context ctx = params[0];
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
                String registrationId = null;
                InstanceID instanceID = InstanceID.getInstance(ctx);
                NotificationHub notificationHub = new NotificationHub(NotificationSettings.HubName, NotificationSettings.HubListenConnectionString, ctx);
                try {
                    String token = instanceID.getToken(NotificationSettings.SenderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                    String[] tags = RegistrationIntentService.getNotificationTags();
                    registrationId = notificationHub.register(token, tags).getRegistrationId();
                    sharedPreferences.edit().putString("registrationID", registrationId).apply();
                } catch (Exception e) {
                    Util.logd("Failed to complete token refresh");
                }
                return null;
            }
        }.execute(ctx);
    }
}
