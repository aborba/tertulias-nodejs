package pt.isel.pdm.g04.pf.data.thoth.database;

import android.database.Cursor;
import android.net.Uri;

import java.text.ParseException;

import pt.isel.pdm.g04.pf.data.thoth.models.Student;
import pt.isel.pdm.g04.pf.data.thoth.provider.ThothContract;

public class StudentTable extends Table<Student> {


    public StudentTable() {
        super(Student.class);
    }

    @Override
    protected Uri getUri() {
        return ThothContract.Students.CONTENT_URI;
    }


    @Override
    protected Student buildItem(Cursor cursor, int[] columnIds) throws ParseException {
        return new Student(cursor);
    }



}
