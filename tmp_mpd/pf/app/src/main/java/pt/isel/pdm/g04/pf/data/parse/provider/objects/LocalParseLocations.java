/*
package pt.isel.pdm.g04.pf.data.parse.provider.objects;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import pt.isel.pdm.g04.pf.data.parse.provider.ParseContract;

public class LocalParseLocations {
    protected final Context ctx;
    protected final ContentResolver contentResolver;
    protected final Uri uri;

    public LocalParseLocations(Context ctx) {
        this.ctx = ctx;
        this.contentResolver = ctx.getContentResolver();
        uri = ParseContract.Locations.CONTENT_URI;
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

    public Uri insert(ContentValues values) {
        return contentResolver.insert(uri, values);
    }

    public int update(ContentValues values) {
        return contentResolver.update(uri,
                values,
                getQuerySelection(),
                getQuerySelectionArgs(values));
    }

    public int delete(String item) {
        return contentResolver.delete(uri,
                getQuerySelection(),
                getQuerySelectionArgs(item));
    }

    protected Uri getUri() {
        return uri;
    }

    protected String[] getQueryProjection() {
        return new String[]{
                ParseContract.Locations._ID,
                ParseContract.Locations.EMAIL,
                ParseContract.Locations.LATITUDE,
                ParseContract.Locations.LONGITUDE,
                ParseContract.Locations.LOCATION,
                ParseContract.Locations.TIMESTAMP
        };
    }

    protected String getQuerySelection() {
        return String.format("%s = ?", ParseContract.Locations.EMAIL);
    }

    protected String[] getQuerySelectionArgs(String item) {
        return new String[]{item};
    }

    protected String[] getQuerySelectionArgs(ContentValues values) {
        return new String[]{values.getAsString(ParseContract.Locations.EMAIL)};
    }

    protected String getSort() {
        return String.format("%s ASC", ParseContract.Locations.EMAIL);
    }

}
*/
