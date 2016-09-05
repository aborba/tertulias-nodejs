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

package pt.isel.s1516v.ps.apiaccess.contentprovider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;

import pt.isel.s1516v.ps.apiaccess.syncadapter.TertuliasTableObserver;

public class TertuliasContentProvider extends ContentProvider {

    private static final String UNEXPECTED_MATCH = "Internal error: Unexpected matcher match";
    private static final String UNMATCHED_MATCH = "Internal error: Unmatched matcher match";

    protected static final int MYNOTIFICATIONS   = 100;
    protected static final int MYNOTIFICATION_ID = 101;
    protected static final int NOTIFICATIONS     = 200;
    protected static final int NOTIFICATION_ID   = 201;

    private static final String UNIT = "/#";

    protected static final UriMatcher URI_MATCHER;
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(TertuliasContract.AUTHORITY, TertuliasContract.MyNotifications.RESOURCE,        MYNOTIFICATIONS);
        URI_MATCHER.addURI(TertuliasContract.AUTHORITY, TertuliasContract.MyNotifications.RESOURCE + UNIT, MYNOTIFICATION_ID);
        URI_MATCHER.addURI(TertuliasContract.AUTHORITY, TertuliasContract.Notifications.RESOURCE,          NOTIFICATIONS);
        URI_MATCHER.addURI(TertuliasContract.AUTHORITY, TertuliasContract.Notifications.RESOURCE + UNIT,   NOTIFICATION_ID);
    }

    private Context ctx;
    private TertuliasOpenHelper dbHelper = null;
    private boolean isBatchMode;

    @Override
    public boolean onCreate() {
        ctx = getContext();
        dbHelper = new TertuliasOpenHelper(ctx);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case MYNOTIFICATIONS:   return TertuliasContract.MyNotifications.CONTENT_TYPE;
            case MYNOTIFICATION_ID: return TertuliasContract.MyNotifications.CONTENT_ITEM_TYPE;
            case NOTIFICATIONS:     return TertuliasContract.Notifications.CONTENT_TYPE;
            case NOTIFICATION_ID:   return TertuliasContract.Notifications.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table;
        boolean chgNotifications;
        chgNotifications = false;
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case MYNOTIFICATIONS:
                chgNotifications = true;
                table = TertuliasSchema.MyNotifications.TBL_NAME;
                break;
            case NOTIFICATIONS:
                chgNotifications = true;
                table = TertuliasSchema.Notifications.TBL_NAME;
                break;
            default:
                throw new IllegalArgumentException(UNMATCHED_MATCH);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(table, null, values);

        if (rowId != -1 && !isBatchMode) {
            ctx.getContentResolver().notifyChange(uri, null, true);
        }

        return rowId != -1 ? ContentUris.withAppendedId(uri, rowId) : null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder qryBuilder = new SQLiteQueryBuilder();
        int match = URI_MATCHER.match(uri);
        switch (match) {

            case MYNOTIFICATIONS:
                qryBuilder.setTables(TertuliasSchema.MyNotifications.TBL_NAME);
                if (TextUtils.isEmpty(sortOrder)) sortOrder = TertuliasContract.MyNotifications.DEFAULT_SORT_ORDER;
                break;
            case NOTIFICATIONS:
                qryBuilder.setTables(TertuliasSchema.Notifications.TBL_NAME);
                if (TextUtils.isEmpty(sortOrder)) sortOrder = TertuliasContract.Notifications.DEFAULT_SORT_ORDER;
                break;
            default:
                throw new IllegalStateException(UNMATCHED_MATCH);
        }
        Cursor cursor = qryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(ctx.getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        boolean chgNotifications;
        chgNotifications = false;
        String table;
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case NOTIFICATIONS:
            case NOTIFICATION_ID:
                chgNotifications = true;
                table = TertuliasSchema.Notifications.TBL_NAME;
                break;
            default:
                throw new IllegalArgumentException(UNMATCHED_MATCH);
        }

        if (selection == null) {
            selection = TertuliasContract.SELECTION_BY_ID;
            selectionArgs = new String[] {String.valueOf(values.get(TertuliasSchema.COL_ID))};
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.update(table, values, selection, selectionArgs);

        if (!isBatchMode)
            ctx.getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        boolean chgNotifications;
        chgNotifications = false;
        String table;
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case MYNOTIFICATIONS:
            case MYNOTIFICATION_ID:
                chgNotifications = true;
                table = TertuliasSchema.MyNotifications.TBL_NAME;
                break;
            case NOTIFICATIONS:
            case NOTIFICATION_ID:
                chgNotifications = true;
                table = TertuliasSchema.Notifications.TBL_NAME;
                break;
            default:
                throw new IllegalStateException(UNMATCHED_MATCH);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.delete(table, selection, selectionArgs);

        if (!isBatchMode)
            ctx.getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        SQLiteDatabase db = null;
        try {
            isBatchMode = true;
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            ContentProviderResult[] results = super.applyBatch(operations);
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
            isBatchMode = false;
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        String table = getTable(uri);

        int rowsCount = 0;
        SQLiteDatabase db = null;
        try {
            isBatchMode = true;
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            for (ContentValues value : values) {
                long rowId = db.replace(table, null, value);
                if (rowId != -1) rowsCount++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            isBatchMode = false;
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsCount;
    }

    @NonNull
    private String getTable(Uri uri) {
        String table;
        switch (URI_MATCHER.match(uri)) {
            case NOTIFICATIONS:
            case NOTIFICATION_ID:
                table = TertuliasContract.Notifications.RESOURCE;
                break;
            default:
                throw new IllegalArgumentException("Internal error: Unmatched matcher match");
        }
        return table;
    }

}
