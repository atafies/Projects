package atafies.degreehacks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Adriel on 1/1/2016.
 */
public class StudyHacksDBHelper extends SQLiteOpenHelper {
    private static final String COMMA = ",";
    private static StudyHacksDBHelper mInstance = null;

    private SQLiteDatabase dbMain;

    private StudyHacksDBHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
        dbMain = getWritableDatabase();
    }

    public SQLiteDatabase getDbMain(){
        if (dbMain !=null){
        return dbMain;
        }
        else{throw new NullPointerException("dbMain is null!");}
    }

    public void insert(String table, String nullColumnHack, ContentValues values){
        dbMain.insert(table,nullColumnHack,values);
    }
    public void update(String table, ContentValues values, String whereClause, String[] whereArgs){
        dbMain.update(table, values, whereClause, whereArgs);
    }


    public static StudyHacksDBHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new StudyHacksDBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }
    public void execSQL(String sql){
        dbMain.execSQL(sql);
    }
    public void rawQuery(String sql, String[] selectionArgs){
        dbMain.rawQuery(sql, selectionArgs);
    }

    public void setClassGrade(long classroomID,double grade){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.AssignmentsTable.COL_ASSIGNMENT_AVERAGE,grade);

        dbMain.update(DatabaseContract.ClassroomsTable.TABLE_NAME
                , cv, "_ID=" + classroomID, null);
    }


    public void setAssignmentAverage(long assignID,double average){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.AssignmentsTable.COL_ASSIGNMENT_AVERAGE,average);

        dbMain.update(DatabaseContract.AssignmentsTable.TABLE_NAME
                , cv, "_ID=" + assignID, null);
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */

    @Override
    public void onOpen(SQLiteDatabase db){
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.ClassroomsTable.CREATE_TABLE);
        db.execSQL(DatabaseContract.NotesTable.CREATE_TABLE);
        db.execSQL(DatabaseContract.AssignmentsTable.CREATE_TABLE);
        db.execSQL(DatabaseContract.GradesTable.CREATE_TABLE);
    }

    // Method is called during an upgrade of the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.ClassroomsTable.DELETE_TABLE);
        db.execSQL(DatabaseContract.NotesTable.DELETE_TABLE);
        db.execSQL(DatabaseContract.AssignmentsTable.DELETE_TABLE);
        db.execSQL(DatabaseContract.GradesTable.DELETE_TABLE);
        onCreate(db);
    }
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit){
        return dbMain.query(table,columns,selection,selectionArgs,groupBy,having,orderBy,limit);
    }

    public void deleteGrades(long assignID){
        dbMain.execSQL("DELETE FROM " + DatabaseContract.GradesTable.TABLE_NAME + " WHERE "
                + DatabaseContract.GradesTable.COL_ASSIGNMENT_ID + " = " + assignID);
    }
   public int delete(String table, String whereClause, String[] whereArgs){
       return dbMain.delete(table, whereClause,whereArgs);
   }

    public long getMaxID(String tableName) {
        if (dbMain == null) {
            throw new NullPointerException("Database pointer is null!");
        }
        else if(!dbMain.isOpen()){
            throw new IllegalStateException("Database is not open!");
        }
        else{
            long maxID = 0;
            Cursor maxCursor = dbMain.query(tableName
                    , new String[]{"MAX(_id) as _id"}, null, null, null, null, null);
            for(maxCursor.moveToFirst();!maxCursor.isAfterLast();maxCursor.moveToNext()) {
                maxID = maxCursor.getInt(maxCursor
                        .getColumnIndex(DatabaseContract.AssignmentsTable._ID));
            }
            maxCursor.close();
            return maxID;
        }

    }

    public Classroom getClassroom(String className){
        if (dbMain == null) {
            throw new NullPointerException("Database pointer is null!");
        }
        else if(!dbMain.isOpen()){
            throw new IllegalStateException("Database is not open!");
        }
        else {
            String[] args = {className};
            String classname = "";


            Cursor classIDCursor = dbMain.rawQuery("SELECT " + DatabaseContract.ClassroomsTable._ID
                    + COMMA + DatabaseContract.ClassroomsTable.COL_CLASS_NAME
                    + COMMA + DatabaseContract.ClassroomsTable.COL_CLASS_GRADE
                    + " FROM " + DatabaseContract.ClassroomsTable.TABLE_NAME
                    + " WHERE " + DatabaseContract.ClassroomsTable.COL_CLASS_NAME + "= ?", args);

            int classID = 0, assignID;
            double assignmentWeight = 0, classGrade = 0;
            for (classIDCursor.moveToFirst(); !classIDCursor.isAfterLast(); classIDCursor.moveToNext()) {
                classID = classIDCursor.getInt(classIDCursor
                        .getColumnIndex(DatabaseContract.ClassroomsTable._ID));
                classname = classIDCursor.getString(classIDCursor
                        .getColumnIndex(DatabaseContract.ClassroomsTable.COL_CLASS_NAME));
                classGrade = classIDCursor.getDouble(classIDCursor
                        .getColumnIndex(DatabaseContract.ClassroomsTable.COL_CLASS_GRADE));
            }
            classIDCursor.close();
            Classroom classroom = new Classroom(classname, classID);

            Cursor ac = dbMain.rawQuery("SELECT " + DatabaseContract.AssignmentsTable._ID
                    + COMMA + DatabaseContract.AssignmentsTable.COL_ASSIGNMENT_NAME
                    + COMMA + DatabaseContract.AssignmentsTable.COL_ASSIGNMENT_WEIGHT
                    + " FROM " + DatabaseContract.AssignmentsTable.TABLE_NAME
                    + " WHERE " + DatabaseContract.AssignmentsTable.COL_CLASSROOM_ID + "= "
                    + classID, null);
            Cursor gc;
            for (ac.moveToFirst(); !ac.isAfterLast(); ac.moveToNext()) {
                String assName = ac.getString(ac
                        .getColumnIndex(DatabaseContract.AssignmentsTable.COL_ASSIGNMENT_NAME));
                assignID = ac.getInt(ac
                        .getColumnIndex(DatabaseContract.AssignmentsTable._ID));
                assignmentWeight = ac.getDouble(ac
                        .getColumnIndex(DatabaseContract.AssignmentsTable.COL_ASSIGNMENT_WEIGHT));
                classroom.addAssignment(assName, assignID, assignmentWeight);
                gc = dbMain.rawQuery("SELECT " + DatabaseContract.GradesTable.COL_GRADE_VALUE
                        + COMMA + DatabaseContract.GradesTable._ID
                        + " FROM " + DatabaseContract.GradesTable.TABLE_NAME
                        + " WHERE " + DatabaseContract.GradesTable.COL_ASSIGNMENT_ID + "= "
                        + assignID, null);
                for (gc.moveToFirst(); !gc.isAfterLast(); gc.moveToNext()) {
                    double grade = gc.getDouble(gc
                            .getColumnIndex(DatabaseContract.GradesTable.COL_GRADE_VALUE));
                    classroom.addGrade(assignID, grade);
                }
                gc.close();
            }
            classroom.findClassGrade();

            ac.close();
            return classroom;
        }

    }



}
