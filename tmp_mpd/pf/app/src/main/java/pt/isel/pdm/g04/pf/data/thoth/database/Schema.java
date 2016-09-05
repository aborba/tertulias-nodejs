package pt.isel.pdm.g04.pf.data.thoth.database;

import pt.isel.pdm.g04.pf.data.thoth.models.Student;
import pt.isel.pdm.g04.pf.data.thoth.models.Teacher;

public class Schema {
    public static final String DB_NAME = "thoth.db";
    public static final int DB_VERSION = 2;

    public static final Table<Teacher> Teachers = new TeacherTable();
    public static final Table<Student> Students = new StudentTable();
}
