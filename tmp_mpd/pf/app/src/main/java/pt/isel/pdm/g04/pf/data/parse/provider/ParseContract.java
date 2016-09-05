package pt.isel.pdm.g04.pf.data.parse.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Acknowledgements:
 * The code organization to implement the content provider was based on the ideas of
 * Wolfram Rittmeyer exposed in his blog [Grokking Android - Getting Down to the Nitty Gritty of Android Development]
 * and in the lectures of João Trindade who referred Rittmeyer\'s work and commented it suggesting improvements.
 * Blog is at https://www.grokkingandroid.com/android-tutorial-content-provider-basics
 */

public interface ParseContract {
    String AUTHORITY = "pt.isel.pdm.g04.pf.parse.provider";
    Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    String SELECTION_BY_ID = BaseColumns._ID + " = ?";
    String MEDIA_BASE_SUBTYPE = "/vnd.g04.pf.parse.";

    // For teacher subsriptions -> to feed parse channels
    interface Subscriptions extends BaseColumns {
        String RESOURCE = "subscriptions";
        String EMAIL = "email";
        Uri CONTENT_URI = Uri.withAppendedPath(ParseContract.CONTENT_URI, RESOURCE);
        String[] PROJECTION_ALL = {_ID, EMAIL};
        String DEFAULT_ORDER_BY = EMAIL + " ASC";
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
        String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
    }

    // For logged teacher current location -> to feed parse push notifications
    interface Locations extends BaseColumns {
        String RESOURCE = "locations";
        String EMAIL = "email";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String LOCATION = "location";
        String TIMESTAMP = "timestamp";
        Uri CONTENT_URI = Uri.withAppendedPath(ParseContract.CONTENT_URI, RESOURCE);
        String[] PROJECTION_ALL = {_ID, EMAIL, LATITUDE, LONGITUDE, LOCATION, TIMESTAMP};
        String DEFAULT_ORDER_BY = EMAIL + " ASC" + ", " + TIMESTAMP + " DESC";
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
        String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
    }

    // For subscribed teachers locations -> from parse push notifications
    interface Notifications extends BaseColumns {
        String RESOURCE = "notifications";
        String NAME = "name";
        String EMAIL = "email";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String LOCATION = "location";
        String PHOTO = "photo";
        String TIMESTAMP = "timestamp";
        Uri CONTENT_URI = Uri.withAppendedPath(ParseContract.CONTENT_URI, RESOURCE);
        String[] PROJECTION_ALL = {_ID, NAME, EMAIL, LATITUDE, LONGITUDE, LOCATION, PHOTO, TIMESTAMP};
        String DEFAULT_ORDER_BY = TIMESTAMP + " DESC, " + NAME + " ASC";
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
        String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
    }

}
