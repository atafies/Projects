package atafies.degreehacks;

import android.provider.BaseColumns;

/**
 * Created by Adriel on 1/1/2016.
 */
public final class DatabaseContract {
    public static final  int    DATABASE_VERSION   = 1;
    public static final  String DATABASE_NAME      = "studyhacks.db";
    private static final String TEXT_TYPE          = " TEXT";
    private static final String COMMA_SEP          = ",";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private DatabaseContract() {}

    public static abstract class NotesTable implements BaseColumns {
        public static final String TABLE_NAME       = "Notes";
        public static final String COL_NOTE = "Note";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COL_NOTE + TEXT_TYPE + " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String DELETE_ENTRY = "DELETE FROM " + TABLE_NAME + " WHERE _ID=";
    }

    public static abstract class ClassroomsTable implements BaseColumns {
        public static final String TABLE_NAME       = "Classrooms";
        public static final String COL_CLASS_NAME = "ClassName";
        public static final String COL_CLASS_GRADE = "ClassGrade";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COL_CLASS_GRADE + " REAL NOT NULL," +
                COL_CLASS_NAME + TEXT_TYPE + "NOT NULL )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String DELETE_ENTRY = "DELETE FROM " + TABLE_NAME + " WHERE _ID=";
    }

    public static abstract class AssignmentsTable implements BaseColumns {
        public static final String TABLE_NAME       = "Assignments";
        public static final String COL_ASSIGNMENT_NAME = "Assignment";
        public static final String COL_ASSIGNMENT_WEIGHT = "AssignmentWeight";
        public static final String COL_ASSIGNMENT_AVERAGE = "AssignmentAverage";
        public static final String COL_CLASSROOM_ID = "ClassroomID";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP + COL_ASSIGNMENT_NAME + TEXT_TYPE + COMMA_SEP
                + COL_ASSIGNMENT_WEIGHT + " REAL NOT NULL" + COMMA_SEP
                + COL_CLASSROOM_ID + " INTEGER NOT NULL"+ COMMA_SEP
                + COL_ASSIGNMENT_AVERAGE + " REAL NOT NULL "+ COMMA_SEP
                + "FOREIGN KEY (" + COL_CLASSROOM_ID + ") REFERENCES " +
                ClassroomsTable.TABLE_NAME +"("+ ClassroomsTable._ID+") ON DELETE CASCADE" + COMMA_SEP
                + "UNIQUE ("+_ID +COMMA_SEP
                + COL_CLASSROOM_ID + ")"
                + " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String DELETE_ENTRY = "DELETE FROM " + TABLE_NAME + " WHERE _ID=";
    }
    public static abstract class GradesTable implements BaseColumns {
        public static final String TABLE_NAME       = "Grades";
        public static final String COL_ASSIGNMENT_ID = "AssignmentID";
        public static final String COL_GRADE_VALUE = "Grade";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT"+ COMMA_SEP
                + COL_ASSIGNMENT_ID + " INTEGER NOT NULL" + COMMA_SEP
                + COL_GRADE_VALUE +" REAL"+ COMMA_SEP
                + "FOREIGN KEY ("+ COL_ASSIGNMENT_ID + ") REFERENCES "
                + AssignmentsTable.TABLE_NAME +"("+ AssignmentsTable._ID +") ON DELETE CASCADE"+COMMA_SEP
                + "UNIQUE ("+ _ID +COMMA_SEP
                + COL_ASSIGNMENT_ID + " )"
                + " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String DELETE_ENTRY = "DELETE FROM " + TABLE_NAME + " WHERE _ID=";
    }//todo: give grade table database contract two foreign keys for assignment and class...(maybe a
    //todo: a single table????
}
