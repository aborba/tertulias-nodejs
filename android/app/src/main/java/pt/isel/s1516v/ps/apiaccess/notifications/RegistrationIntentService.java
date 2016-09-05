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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.microsoft.windowsazure.messaging.NotificationHub;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import pt.isel.s1516v.ps.apiaccess.MainActivity;
import pt.isel.s1516v.ps.apiaccess.helpers.Util;
import pt.isel.s1516v.ps.apiaccess.support.domain.TertuliaListItem;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    private NotificationHub hub;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Registration registration = null;
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(NotificationSettings.SenderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            Util.logd("FCM Registration Token: " + token);
            String[] tags = getNotificationTags();

            // Storing the registration id that indicates whether the generated token has been
            // sent to your server. If it is not stored, send the token to your server,
            // otherwise your server should have already received the token.
            if ((registration = loadRegistration(this)) == null) {
                Util.logd("Trying to register on the Notification Hub with: " + token);
                String registrationId = notificationHubRegistration(this, token, tags, false);

                // If you want to use tags...
                // Refer to : https://azure.microsoft.com/en-us/documentation/articles/notification-hubs-routing-tag-expressions/
                // regID = hub.register(token, "tag1", "tag2").getRegistrationId();

                registration = new Registration(registrationId, tags);
                saveRegistration(this, registration);
                Util.logd("Registered Successfully - RegId : " + registration.toString());
            } else {
                if (registration.isTagsEquals(tags)) {
                    Util.logd("Previously Registered Successfully - RegId : " + registration.id);
                    return;
                }
                Util.logd("Trying to register on the Notification Hub with: " + token);
                String registrationId = notificationHubRegistration(this, token, tags, true);
                registration = new Registration(registrationId, tags);
                saveRegistration(this, registration);
                Util.logd("Registered Successfully - RegId : " + registration.toString());
            }
        } catch (Exception e) {
            Util.logd("Failed to complete registration token refresh");
        }
    }

    public final static String REGISTRATION = "registration";

    private static String notificationHubRegistration(Context ctx, String token, String[] tags, boolean unregisterFirst) throws Exception {
        NotificationHub notificationHub = new NotificationHub(NotificationSettings.HubName, NotificationSettings.HubListenConnectionString, ctx);
        if (unregisterFirst)
            notificationHub.unregister();
        String registrationId = notificationHub.register(token, tags).getRegistrationId();
        return registrationId;
    }

    private static class Registration {
        @com.google.gson.annotations.SerializedName("id")
        final String id;
        @com.google.gson.annotations.SerializedName("tags")
        final String[] tags;

        public Registration(String id, String[] tags) {
            this.id = id;
            this.tags = tags;
        }

        public Registration(String registrationStr) {
            Registration registration = new Gson().fromJson(registrationStr, Registration.class);
            id = registration.id;
            tags = registration.tags;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (id != null)
                sb.append("RegId : " + id);
            if (tags != null && tags.length > 0) {
                if (sb.length() > 0)
                    sb.append(", ");
                sb.append("tags: ");
                for (int i = 0; i < tags.length; i++)
                    sb.append(tags[i] + ", ");
            }
            String result = sb.toString();
            result = result.substring(0, result.length() - 2);
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || ! (o instanceof Registration))
                return false;
            Registration other = (Registration) o;
            if (id == null && other.id != null || id != null && other.id == null)
                return false;
            if (id != null && other.id != null && ! id.equals(other.id))
                return false;
            return isTagsEquals(other.tags);
        }

        private Set<String> controlSet = null;

        public boolean isTagsEquals(String[] otherTags) {
            if (tags == null && otherTags == null)
                return true;
            if (tags == null && otherTags != null ||
                    tags != null &&
                            (otherTags == null || tags.length != otherTags.length))
                return false;
            if (controlSet == null)
                controlSet = new HashSet<>(Arrays.asList(tags));
            return controlSet.containsAll(Arrays.asList(otherTags));
        }

    }

    private static Registration loadRegistration(Context ctx) {
        String registrationStr = PreferenceManager.getDefaultSharedPreferences(ctx)
                .getString(REGISTRATION, null);
        if (registrationStr == null)
            return null;
        Registration registration = new Gson().fromJson(registrationStr, Registration.class);
        return registration;
    }

    private static void saveRegistration(Context ctx, Registration registration) {
        String registrationStr = new Gson().toJson(registration);
        PreferenceManager.getDefaultSharedPreferences(ctx)
                .edit()
                .putString(REGISTRATION, registrationStr)
                .apply();
    }

    public static String[] getNotificationTags() {
        TertuliaListItem[] tertulias = MainActivity.tertulias;
        String[] tags = new String[tertulias.length];
        for (int i = 0; i < tertulias.length; i++)
            tags[i] = String.format("tertulia_%d", tertulias[i].id);
        return tags;
    }

}