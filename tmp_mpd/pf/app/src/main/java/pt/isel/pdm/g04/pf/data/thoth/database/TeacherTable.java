package pt.isel.pdm.g04.pf.data.thoth.database;

import android.database.Cursor;
import android.net.Uri;

import java.text.ParseException;

import pt.isel.pdm.g04.pf.data.thoth.models.Teacher;
import pt.isel.pdm.g04.pf.data.thoth.provider.ThothContract;

public class TeacherTable extends Table<Teacher> {


    public TeacherTable() {
        super(Teacher.class);
    }

    @Override
    protected Uri getUri() {
        return ThothContract.Teachers.CONTENT_URI;
    }

    @Override
    protected Teacher buildItem(Cursor cursor, int[] columnIds) throws ParseException {
        return new Teacher(cursor);
    }


}
