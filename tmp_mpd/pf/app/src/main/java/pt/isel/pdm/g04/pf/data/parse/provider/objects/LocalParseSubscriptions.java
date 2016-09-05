package pt.isel.pdm.g04.pf.data.parse.provider.objects;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import pt.isel.pdm.g04.pf.data.parse.provider.ParseContract;

public class LocalParseSubscriptions {
    protected final Context ctx;
    protected final ContentResolver contentResolver;
    protected final Uri uri;

    public LocalParseSubscriptions(Context ctx) {
        this.ctx = ctx;
        this.contentResolver = ctx.getContentResolver();
        uri = ParseContract.Subscriptions.CONTENT_URI;
    }

    public Cursor queryAll() {
        return contentResolver.query(uri, getQueryProjection(),
                null, null,
                getSort());
    }

    public Cursor query(String item) {
        return contentResolver.query(uri, getQueryProjection(),
                getQuerySelection(), getQuerySelectionArgs(item),
                getSort());
    }

    public boolean exists(String item) {
        Cursor cursor =  query(item);
        boolean result = cursor.moveToNext();
        cursor.close();
        return result;
    }

    public Uri insert(String item) {
        ContentValues values = toContentValues(item);
        return contentResolver.insert(uri, values);
    }

    public Uri insert(String name, String email) {
        ContentValues values = toContentValues(name, email);
        return contentResolver.insert(uri, values);
    }

    public int update(String... items) {
        ContentValues values = toContentValues(items[1]);
        return contentResolver.update(uri,
                values,
                getQuerySelection(),
                getQuerySelectionArgs(items[0]));
    }

    public int delete(String item) {
        return contentResolver.delete(uri,
                getQuerySelection(),
                getQuerySelectionArgs(item));
    }

    public int deleteAll() {
        return contentResolver.delete(uri,
                "_ID IS NOT NULL",
                null);
    }

    protected Uri getUri() {
        return uri;
    }

    protected ContentValues toContentValues(String item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ParseContract.Subscriptions.EMAIL, item);
        return contentValues;
    }

    protected ContentValues toContentValues(String name, String email) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ParseContract.Subscriptions.EMAIL, email);
        return contentValues;
    }

    protected String[] getQueryProjection() {
        return new String[]{
                ParseContract.Subscriptions.EMAIL
        };
    }

    protected String getQuerySelection() {
        return String.format("%s = ?", ParseContract.Subscriptions.EMAIL);
    }

    protected String[] getQuerySelectionArgs(String item) {
        return new String[]{item};
    }

    protected String getSort() {
        return String.format("%s ASC", ParseContract.Subscriptions.EMAIL);
    }

}
