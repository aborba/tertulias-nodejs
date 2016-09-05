package pt.isel.pdm.g04.pf.data.parse.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Acknowledgements:
 * The code organization to implement the content provider was based on the ideas of
 * Wolfram Rittmeyer exposed in his blog [Grokking Android - Getting Down to the Nitty Gritty of Android Development]
 * and in the lectures of João Trindade who referred Rittmeyer\'s work and commented it suggesting improvements.
 * Blog is at https://www.grokkingandroid.com/android-tutorial-content-provider-basics
 */

public class ParseDbOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = ParseDbSchema.DB_NAME;
    private static final int DB_VERSION = ParseDbSchema.DB_VERSION;

    public ParseDbOpenHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDb(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteDb(db);
        createDb(db);
    }

    private void deleteDb(SQLiteDatabase db) {
        db.execSQL(ParseDbSchema.Locations.DDL_DROP_TABLE);
        db.execSQL(ParseDbSchema.Notifications.DDL_DROP_TABLE);
        db.execSQL(ParseDbSchema.Subscriptions.DDL_DROP_UNIQUE_INDEX);
        db.execSQL(ParseDbSchema.Subscriptions.DDL_DROP_TABLE);
    }

    private void createDb(SQLiteDatabase db) {
        db.execSQL(ParseDbSchema.Subscriptions.DDL_CREATE_TABLE);
        db.execSQL(ParseDbSchema.Subscriptions.DDL_CREATE_UNIQUE_INDEX);
        db.execSQL(ParseDbSchema.Notifications.DDL_CREATE_TABLE);
        db.execSQL(ParseDbSchema.Locations.DDL_CREATE_TABLE);
    }

}
