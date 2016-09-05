package pt.isel.pdm.g04.pf.data.thoth.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;

import pt.isel.pdm.g04.pf.data.thoth.DatabaseHelper;

public class ThothProvider extends ContentProvider {

    // used for the UriMacher
    private static final int TEACHERS = 10;
    private static final int TEACHER_ID = 20;
    private static final int STUDENTS = 30;
    private static final int STUDENT_ID = 40;

    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(ThothContract.AUTHORITY, ThothContract.Teachers.RESOURCE, TEACHERS);
        URI_MATCHER.addURI(ThothContract.AUTHORITY, ThothContract.Teachers.RESOURCE + "/#", TEACHER_ID);
        URI_MATCHER.addURI(ThothContract.AUTHORITY, ThothContract.Students.RESOURCE, STUDENTS);
        URI_MATCHER.addURI(ThothContract.AUTHORITY, ThothContract.Students.RESOURCE + "/#", STUDENT_ID);
    }

    // database
    private DatabaseHelper database;
    private boolean isBatchMode;

    @Override
    public boolean onCreate() {
        database = new DatabaseHelper(getContext());
        return false;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        SQLiteDatabase sqLiteDatabase = null;
        try {
            isBatchMode = true;
            sqLiteDatabase = database.getWritableDatabase();
            sqLiteDatabase.beginTransaction();
            ContentProviderResult[] results = super.applyBatch(operations);
            sqLiteDatabase.setTransactionSuccessful();
            return results;
        } finally {
            sqLiteDatabase.endTransaction();
            isBatchMode = false;
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        String table = getTable(uri);

        int rowsCount = 0;
        SQLiteDatabase sqLiteDatabase = null;
        try {
            isBatchMode = true;
            sqLiteDatabase = database.getWritableDatabase();
            sqLiteDatabase.beginTransaction();
            for (ContentValues contentValues : values) {
                long rowId = sqLiteDatabase.replace(table, null, contentValues);
                if (rowId != -1) rowsCount++;
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
            isBatchMode = false;
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsCount;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Set the table
        String table = getTable(uri);
        queryBuilder.setTables(table);

        int uriType = URI_MATCHER.match(uri);
        switch (uriType) {
            case TEACHERS:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = ThothContract.Teachers.DEFAULT_SORT_ORDER;
                }
                break;
            case STUDENTS:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = ThothContract.Students.DEFAULT_SORT_ORDER;
                }
                break;
            case TEACHER_ID:
            case STUDENT_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(ThothContract.Users._ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        Uri res;
        String table = getTable(uri);
        long id = sqlDB.insert(table, null, values);
        switch (uriType) {
            case TEACHERS:
            case STUDENTS:
                res = Uri.parse(table + "/" + id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (!isBatchMode) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return res;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        String table = getTable(uri);
        int rowsDeleted;
        switch (uriType) {
            case TEACHERS:
            case STUDENTS:
                rowsDeleted = sqlDB.delete(table, selection,
                        selectionArgs);
                break;
            case STUDENT_ID:
            case TEACHER_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(table,
                            ThothContract.Users._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(table,
                            ThothContract.Users._ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (!isBatchMode) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int uriType = URI_MATCHER.match(uri);
        String table = getTable(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated;
        switch (uriType) {
            case TEACHERS:
            case STUDENTS:
                rowsUpdated = sqlDB.update(table,
                        values,
                        selection,
                        selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case STUDENT_ID:
            case TEACHER_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(table,
                            values,
                            ThothContract.Users._ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(table,
                            values,
                            ThothContract.Users._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return rowsUpdated;
    }

    //region Private Methods

    @NonNull
    private String getTable(Uri uri) {
        String table;
        switch (URI_MATCHER.match(uri)) {
            case TEACHER_ID:
            case TEACHERS:
                table = ThothContract.Teachers.RESOURCE;
                break;
            case STUDENT_ID:
            case STUDENTS:
                table = ThothContract.Students.RESOURCE;
                break;
            default:
                throw new IllegalArgumentException("Internal error: Unmatched matcher match");
        }
        return table;
    }
    //endregion

}