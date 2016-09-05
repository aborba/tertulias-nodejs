package pt.isel.pdm.g04.pf.data.parse.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.util.ArrayList;

import pt.isel.pdm.g04.pf.presentation.MainActivity;
import pt.isel.pdm.g04.pf.syncadapter.parse.ParseSyncAdapter;

/**
 * Acknowledgements:
 * The code organization to implement the content provider was based on the ideas of
 * Wolfram Rittmeyer exposed in his blog [Grokking Android - Getting Down to the Nitty Gritty of Android Development]
 * and in the lectures of João Trindade who referred Rittmeyer\'s work and commented it suggesting improvements.
 * Blog is at https://www.grokkingandroid.com/android-tutorial-content-provider-basics
 */

public class ParseProvider extends ContentProvider {

    private final ThreadLocal<BatchBag> mBatchBag = new ThreadLocal<BatchBag>() {
        public BatchBag batchBag;

        @Override
        protected BatchBag initialValue() {
            batchBag = new BatchBag();
            return batchBag;
        }
    };
    private BatchBag mBag = mBatchBag.get();

    private static final String UNEXPECTED_MATCH = "Internal error: Unexpected matcher match";
    private static final String UNMATCHED_MATCH = "Internal error: Unmatched matcher match";

    private Context mContext;

    protected static final int SUBSCRIPTIONS_LST = 100;
    protected static final int SUBSCRIPTIONS_OBJ = 101;
    protected static final int LOCATIONS_LST = 200;
    protected static final int LOCATIONS_OBJ = 201;
    protected static final int NOTIFICATIONS_LST = 300;
    protected static final int NOTIFICATIONS_OBJ = 301;

    private static final String UNIT = "/#";

    protected static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(ParseContract.AUTHORITY, ParseContract.Subscriptions.RESOURCE, SUBSCRIPTIONS_LST);
        sUriMatcher.addURI(ParseContract.AUTHORITY, ParseContract.Subscriptions.RESOURCE + UNIT, SUBSCRIPTIONS_OBJ);
        sUriMatcher.addURI(ParseContract.AUTHORITY, ParseContract.Locations.RESOURCE, LOCATIONS_LST);
        sUriMatcher.addURI(ParseContract.AUTHORITY, ParseContract.Locations.RESOURCE + UNIT, LOCATIONS_OBJ);
        sUriMatcher.addURI(ParseContract.AUTHORITY, ParseContract.Notifications.RESOURCE, NOTIFICATIONS_LST);
        sUriMatcher.addURI(ParseContract.AUTHORITY, ParseContract.Notifications.RESOURCE + UNIT, NOTIFICATIONS_OBJ);
    }

    private ParseDbOpenHelper mDbHelper = null;

    @Override
    public boolean onCreate() {
        mContext = getContext();
        mDbHelper = new ParseDbOpenHelper(mContext);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case SUBSCRIPTIONS_LST:
                return ParseContract.Subscriptions.CONTENT_TYPE;
            case SUBSCRIPTIONS_OBJ:
                return ParseContract.Subscriptions.CONTENT_ITEM_TYPE;
            case LOCATIONS_LST:
                return ParseContract.Locations.CONTENT_TYPE;
            case LOCATIONS_OBJ:
                return ParseContract.Locations.CONTENT_ITEM_TYPE;
            case NOTIFICATIONS_LST:
                return ParseContract.Notifications.CONTENT_TYPE;
            case NOTIFICATIONS_OBJ:
                return ParseContract.Notifications.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] select, String where, String[] whereArgs, String orderBy) {
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        final String groupBy, having;
        groupBy = having = null;
        switch (sUriMatcher.match(uri)) {
            case SUBSCRIPTIONS_LST:
                sqlBuilder.setTables(ParseDbSchema.Subscriptions.TBL_NAME);
                if (TextUtils.isEmpty(orderBy))
                    orderBy = ParseContract.Subscriptions.DEFAULT_ORDER_BY;
                break;
            case SUBSCRIPTIONS_OBJ:
                sqlBuilder.setTables(ParseDbSchema.Subscriptions.TBL_NAME);
                sqlBuilder.appendWhere(BaseColumns._ID + " = " + uri.getLastPathSegment());
                break;
            case LOCATIONS_LST:
                sqlBuilder.setTables(ParseDbSchema.Locations.TBL_NAME);
                if (TextUtils.isEmpty(orderBy)) orderBy = ParseContract.Locations.DEFAULT_ORDER_BY;
                break;
            case LOCATIONS_OBJ:
                sqlBuilder.setTables(ParseDbSchema.Locations.TBL_NAME);
                sqlBuilder.appendWhere(BaseColumns._ID + " = " + uri.getLastPathSegment());
                break;
            case NOTIFICATIONS_LST:
                sqlBuilder.setTables(ParseDbSchema.Notifications.TBL_NAME);
                if (TextUtils.isEmpty(orderBy))
                    orderBy = ParseContract.Notifications.DEFAULT_ORDER_BY;
                break;
            case NOTIFICATIONS_OBJ:
                sqlBuilder.setTables(ParseDbSchema.Notifications.TBL_NAME);
                sqlBuilder.appendWhere(BaseColumns._ID + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalStateException(UNMATCHED_MATCH);
        }
        Cursor cursor = sqlBuilder.query(mDbHelper.getReadableDatabase(),
                select, where, whereArgs, groupBy, having, orderBy);
        cursor.setNotificationUri(mContext.getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table;
        boolean subscriptions, locations, notifications;
        subscriptions = locations = notifications = false;
        Bundle requestSyncExtras = new Bundle();
        switch (sUriMatcher.match(uri)) {
            case NOTIFICATIONS_LST:
                table = ParseContract.Notifications.RESOURCE;
                notifications = true;
                break;
            case LOCATIONS_LST:
                table = ParseContract.Locations.RESOURCE;
                locations = true;
                putLocation(values, requestSyncExtras);
                break;
            case SUBSCRIPTIONS_LST:
                table = ParseContract.Subscriptions.RESOURCE;
                subscriptions = true;
                requestSyncExtras.putInt(ParseSyncAdapter.ACTION, ParseSyncAdapter.ACTION_SUBSCRIBE);
                subscriptions = true;
                break;
            default:
                throw new IllegalStateException(UNMATCHED_MATCH);
        }
        long rowId = mDbHelper.getWritableDatabase().insert(table, null, values);
        if (rowId != -1) {
            mBag.update(subscriptions, locations, notifications);
            if (!mBag.isBatchMode) {
                if (locations || subscriptions)
                    ContentResolver.requestSync(MainActivity.account, ParseContract.AUTHORITY, requestSyncExtras);
                mContext.getContentResolver().notifyChange(uri, null);
                handleNotifications();
            }
            return ContentUris.withAppendedId(uri, rowId);
        }
        return null;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String table;
        boolean subscriptions, locations, notifications;
        subscriptions = locations = notifications = false;
        Bundle requestSyncExtras = new Bundle();
        switch (sUriMatcher.match(uri)) {
            case NOTIFICATIONS_LST:
            case NOTIFICATIONS_OBJ:
                table = ParseContract.Notifications.RESOURCE;
                notifications = true;
                break;
            case LOCATIONS_LST:
            case LOCATIONS_OBJ:
                table = ParseContract.Locations.RESOURCE;
                locations = true;
                putLocation(values, requestSyncExtras);
                break;
            default:
                throw new IllegalArgumentException(UNMATCHED_MATCH);
        }
        if (selection == null) {
            selection = ParseContract.SELECTION_BY_ID;
            selectionArgs = new String[]{String.valueOf(values.get(ParseDbSchema.COL_ID))};
        }
        int count = mDbHelper.getWritableDatabase().update(table, values, selection, selectionArgs);
        if (count > 0) {
            mBag.update(subscriptions, locations, notifications);
            if (!mBag.isBatchMode) {
                if (locations)
                    ContentResolver.requestSync(MainActivity.account, ParseContract.AUTHORITY, requestSyncExtras);
                mContext.getContentResolver().notifyChange(uri, null);
                handleNotifications();
            }
        }
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table;
        boolean subscriptions, locations, notifications;
        subscriptions = locations = notifications = false;
        Bundle requestSyncExtras = new Bundle();
        switch (sUriMatcher.match(uri)) {
            case NOTIFICATIONS_LST:
            case NOTIFICATIONS_OBJ:
                table = ParseDbSchema.Notifications.TBL_NAME;
                notifications = true;
                break;
            case LOCATIONS_LST:
            case LOCATIONS_OBJ:
                table = ParseDbSchema.Locations.TBL_NAME;
                locations = true;
                break;
            case SUBSCRIPTIONS_LST:
            case SUBSCRIPTIONS_OBJ:
                table = ParseDbSchema.Subscriptions.TBL_NAME;
                subscriptions = true;
                requestSyncExtras.putInt(ParseSyncAdapter.ACTION, ParseSyncAdapter.ACTION_SUBSCRIBE);
                break;
            default:
                throw new IllegalStateException(UNMATCHED_MATCH);
        }
        int count = mDbHelper.getWritableDatabase().delete(table, selection, selectionArgs);
        if (count > 0) {
            mBag.update(subscriptions, locations, notifications);
            if (!mBag.isBatchMode) {
                if (subscriptions)
                    ContentResolver.requestSync(MainActivity.account, ParseContract.AUTHORITY, requestSyncExtras);
                mContext.getContentResolver().notifyChange(uri, null);
                handleNotifications();
            }
        }
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        String table;
        switch (sUriMatcher.match(uri)) {
            case NOTIFICATIONS_LST:
                table = ParseDbSchema.Notifications.TBL_NAME;
                mBag.isNotifyNotifications = true;
                break;
            case LOCATIONS_LST:
                table = ParseDbSchema.Locations.TBL_NAME;
                mBag.isNotifyLocations = true;
                break;
            case SUBSCRIPTIONS_LST:
                table = ParseDbSchema.Subscriptions.TBL_NAME;
                mBag.isNotifySubscribers = true;
                break;
            default:
                throw new IllegalArgumentException(UNMATCHED_MATCH);
        }
        int rowsCount = 0;
        SQLiteDatabase db = null;
        try {
            mBag.isBatchMode = true;
            db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            for (ContentValues contentValues : values) {
                long rowId = db.replace(table, null, contentValues);
                if (rowId != -1) rowsCount++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            handleNotifications();
            mBag.isBatchMode = false;
        }
        return rowsCount;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        SQLiteDatabase db = null;
        try {
            mBag.isBatchMode = true;
            db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            ContentProviderResult[] contentProviderResults = super.applyBatch(operations);
            db.setTransactionSuccessful();
            return contentProviderResults;
        } finally {
            db.endTransaction();
            handleNotifications();
            mBag.isBatchMode = false;
        }
    }

    private void putLocation(ContentValues values, Bundle requestSyncExtras) {
        requestSyncExtras.putInt(ParseSyncAdapter.ACTION, ParseSyncAdapter.ACTION_NOTIFY);
        requestSyncExtras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        requestSyncExtras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        requestSyncExtras.putString(ParseContract.Locations.EMAIL, values.getAsString(ParseContract.Locations.EMAIL));
        requestSyncExtras.putDouble(ParseContract.Locations.LATITUDE, values.getAsDouble(ParseContract.Locations.LATITUDE));
        requestSyncExtras.putDouble(ParseContract.Locations.LONGITUDE, values.getAsDouble(ParseContract.Locations.LONGITUDE));
        requestSyncExtras.putString(ParseContract.Locations.LOCATION, values.getAsString(ParseContract.Locations.LOCATION));
        requestSyncExtras.putLong(ParseContract.Locations.TIMESTAMP, values.getAsLong(ParseContract.Locations.TIMESTAMP));
    }

    // region Private

    private void handleNotifications() {
//        if (mBag.isNotifySubscribers) Util.notifyChange(mContext, ParseContract.Subscriptions.CONTENT_URI);
//        if (mBag.isNotifyLocations) Util.notifyChange(mContext, ParseContract.Locations.CONTENT_URI);
//        if (mBag.isNotifyNotifications) Util.notifyChange(mContext, ParseContract.Notifications.CONTENT_URI);
    }

    class BatchBag {
        public boolean isBatchMode = false,
                isNotifySubscribers = false, isNotifyLocations = false, isNotifyNotifications = false;

        public void update(boolean isNotifySubscribers, boolean isNotifyLocations, boolean isNotifyNotifications) {
            this.isNotifySubscribers = isNotifySubscribers;
            this.isNotifyLocations = isNotifyLocations;
            this.isNotifyNotifications = isNotifyNotifications;
        }
    }

    // endregion
}
