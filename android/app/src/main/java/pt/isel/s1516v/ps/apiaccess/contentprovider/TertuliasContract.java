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
 * Acknowledgements:
 * The code organization to implement the content provider was based on the ideas of Wolfram Rittmeyer
 * exposed in his blog [Grokking Android - Getting Down to the Nitty Gritty of Android Development]
 * and in the lectures of João Trindade who referred Rittmeyer\'s work and commented it suggesting improvements.
 * Blog articles:
 *      http://www.grokkingandroid.com/android-tutorial-content-provider-basics
 *      http://www.grokkingandroid.com/android-tutorial-writing-your-own-content-provider
 *
 */

package pt.isel.s1516v.ps.apiaccess.contentprovider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public interface TertuliasContract {

    String AUTHORITY = "pt.isel.s1516v.ps.apiaccess";
    Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    String SELECTION_BY_ID = BaseColumns._ID + " = ? ";
    String MEDIA_BASE_SUBTYPE = "/vnd.isel.tertulias.";

    interface MyNotifications extends BaseColumns {
        String RESOURCE = "my_notifications";
        String TERTULIA = "mn_tertulia";
        String TIMESTAMP = "mn_timestamp";
        String TAG = "mn_tag";
        String MESSAGE = "mn_message";
        String MYKEY = "mn_mykey";
        Uri CONTENT_URI = Uri.withAppendedPath(TertuliasContract.CONTENT_URI, RESOURCE);
        String[] PROJECTION_ALL = { _ID, TERTULIA, TIMESTAMP, TAG, MESSAGE, MYKEY };
        String DEFAULT_SORT_ORDER = TIMESTAMP + " ASC, " + TAG + " ASC, " + TERTULIA + " ASC";
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
        String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
    }

    interface Notifications extends BaseColumns {
        String RESOURCE = "notifications";
        String REMOTE_ID = "no_id";
        String TERTULIA = "no_tertulia";
        String TIMESTAMP = "no_timestamp";
        String TAG = "no_tag";
        String MESSAGE = "no_message";
        String MYKEY = "mykey";
        String IS_READ = "isread";
        Uri CONTENT_URI = Uri.withAppendedPath(TertuliasContract.CONTENT_URI, RESOURCE);
        String[] PROJECTION_ALL = { _ID, REMOTE_ID, TERTULIA, TIMESTAMP, TAG, MESSAGE, MYKEY, IS_READ };
        String DEFAULT_FILTER = IS_READ + " = 0";
        String DEFAULT_SORT_ORDER = REMOTE_ID + " DESC";
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
        String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
    }

}
