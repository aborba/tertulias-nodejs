package pt.isel.pdm.g04.pf.data.thoth;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import pt.isel.pdm.g04.pf.data.thoth.database.Schema;

/**
 * Created by Pedro on 31/05/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, Schema.DB_NAME, null, Schema.DB_VERSION);
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

    private void createDb(SQLiteDatabase db) {
        db.execSQL(Schema.Teachers.getCreateTableDDL());
        db.execSQL(Schema.Students.getCreateTableDDL());
    }

    private void deleteDb(SQLiteDatabase db) {
        db.execSQL(Schema.Teachers.getDropTableDDL());
    }
}
