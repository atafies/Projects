package atafies.degreehacks;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import atafies.degreehacks.AddAssignmentFragment;

/**
 * Created by Adriel on 1/9/2016.
 */
public class ClassroomActivity extends BaseDrawerActivity implements
        AddAssignmentFragment.OnAddButtonPressedListener
        ,AssignmentDisplayFragment.OnButtonPressedListener{

    Classroom classroom;
    private String className;
    private long classroomID;
    ArrayList<AssignmentDisplayFragment> displayFrags;


    private StudyHacksDBHelper dbHelper;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(atafies.degreehacks.R.layout.activity_base_drawer);
        LayoutInflater inflate = LayoutInflater.from(this);
        ViewGroup main = (ViewGroup) inflate.inflate(atafies.degreehacks.R.layout.activity_classroom,
                (ViewGroup) findViewById(atafies.degreehacks.R.id.coord_layout));
        // Get the database. If it does not exist, this is where it will
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                className = null;
            } else {
                className = extras.getString("CLASS_NAME");
            }
        } else {
            className = (String) savedInstanceState.getSerializable("CLASS_NAME");
        }
        setTitle(className);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dbHelper = StudyHacksDBHelper.getInstance(this);
        displayFrags = new ArrayList<>();

        new loadClassroomTask(this,className).execute(dbHelper);

        //classroom = new Classroom() todo: add loop to collect Classroom from

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    @Override
    public void onAddButtonPressed(DialogFragment dialog) {
        try {


            Dialog dialogView = dialog.getDialog();
            String assignName = ((EditText) dialogView.findViewById(atafies.degreehacks.R.id.et_assignment_name))
                    .getText().toString();
            String grades = ((EditText) dialogView.findViewById(atafies.degreehacks.R.id.et_grades))
                    .getText().toString();
            String weight = ((EditText) dialogView.findViewById(atafies.degreehacks.R.id.et_assignment_weight))
                    .getText().toString();

            if (assignName.isEmpty() || grades.isEmpty() || weight.isEmpty()) {
                Bundle args = new Bundle();
                args.putString(ErrorDialogFragment.TITLE, "Error");
                args.putString(ErrorDialogFragment.MESSAGE, "Please fill out all fields!");
                ErrorDialogFragment error = new ErrorDialogFragment();
                error.setArguments(args);
                error.show(getFragmentManager(), "INPUT_ERROR");
            } else {
                double d_weight = Double.parseDouble(weight);

                Scanner f = new Scanner(grades);
                f.useLocale(Locale.US); //without this line the program wouldn't work
                //on machines with different locales
                f.useDelimiter(",\\s*");
                ArrayList<Double> dd = new ArrayList<Double>();

                while (f.hasNextDouble()) {
                    dd.add(f.nextDouble());
                }
                double gradesSum = 0;
                double[] gradesArray = new double[dd.size()];
                for (int i = 0; i < gradesArray.length; i++) {
                    gradesArray[i] = dd.get(i);
                    gradesSum += gradesArray[i];
                }
                double assAvg = gradesSum / gradesArray.length;

                ContentValues cn = new ContentValues();
                cn.put(DatabaseContract.AssignmentsTable.COL_ASSIGNMENT_NAME, assignName);
                cn.put(DatabaseContract.AssignmentsTable.COL_CLASSROOM_ID, classroomID);
                cn.put(DatabaseContract.AssignmentsTable.COL_ASSIGNMENT_WEIGHT, d_weight);
                cn.put(DatabaseContract.AssignmentsTable.COL_ASSIGNMENT_AVERAGE, assAvg);
                dbHelper.insert(DatabaseContract.AssignmentsTable.TABLE_NAME, null, cn);
                long assignID = dbHelper.getMaxID(DatabaseContract.AssignmentsTable.TABLE_NAME);
                classroom.addAssignment(assignName, assignID, d_weight);
                for (Double d : dd) {
                    ContentValues grds = new ContentValues();
                    grds.put(DatabaseContract.GradesTable.COL_GRADE_VALUE, d);
                    grds.put(DatabaseContract.GradesTable.COL_ASSIGNMENT_ID, assignID);
                    //dbHelper.insert(DatabaseContract.GradesTable.TABLE_NAME, null, grds);
                    new databaseAsyncTask(databaseAsyncTask.INSERT_GRADE, grds).execute(dbHelper);
                    classroom.addGrade(assignID, d);
                }
                //create new fragment and add it to existing stack
                AssignmentDisplayFragment newFragment = new AssignmentDisplayFragment();
                Bundle newArgs = new Bundle();
                newArgs.putString(AssignmentDisplayFragment.TITLE, assignName);
                newArgs.putLong(AssignmentDisplayFragment.ASSIGNMENTID, assignID);
                newArgs.putDoubleArray(AssignmentDisplayFragment.GRADES, gradesArray);
                newArgs.putDouble(AssignmentDisplayFragment.GRADES_AVG, assAvg);
                newArgs.putDouble(AssignmentDisplayFragment.WEIGHT, d_weight);
                newFragment.setArguments(newArgs);

                getFragmentManager().beginTransaction().add(atafies.degreehacks.R.id.assignment_fragments, newFragment
                        , "ASSIGNMENT_FRAGMENT" + assignID).commit();
            }

            if (classroom.getWeightTotal() > 100) {
                giveError("WARNING", "Weight values add up to over 100%, " +
                        "class average value not shown");
            } else {
                refreshAverage();
            }
        }
        catch (Exception ex)
        {

        }
    }

    public void refreshAverage(){
        setTitle(className + " | Average: "
                + String.format("%.2f", classroom.findClassGrade()));
    }
    public void addAssignment(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        AddAssignmentFragment fragment = new AddAssignmentFragment();
        fragment.show(fragmentManager, "AddAssignmentFragment");
    }
    @Override
    public void onEditButtonPressed(AssignmentDisplayFragment fragment, long assignID){
        EditText et_grades = (EditText)fragment.getView().findViewById(atafies.degreehacks.R.id.et_grades_display);
        TextView tv = (TextView)fragment.getView().findViewById(atafies.degreehacks.R.id.grades_display);
        Button edit = (Button)fragment.getView().findViewById(atafies.degreehacks.R.id.edit_assign_button);
        Button save = (Button)fragment.getView().findViewById(atafies.degreehacks.R.id.save_assign_button);
        //make save button appear over edit button, and replace textviews with EditText
        edit.setVisibility(View.INVISIBLE);
        save.setVisibility(View.VISIBLE);
        tv.setVisibility(View.GONE);
        et_grades.setVisibility(View.VISIBLE);
        et_grades.setText(tv.getText().toString());
    }

    @Override
    public void onSaveButtonPressed(AssignmentDisplayFragment fragment, long assignID){
        EditText et_grades = null;
        String grades = "";
        TextView avg = null;
    try
    {
        et_grades = (EditText) fragment.getView().findViewById(atafies.degreehacks.R.id.et_grades_display);
        grades = et_grades.getText().toString();
        avg = (TextView) fragment.getView().findViewById(atafies.degreehacks.R.id.average_display);
    }
    catch(Exception ex)
    {
        giveError("Error","Item could not be saved." + ex.getMessage());
        return;
    }

        if (grades.isEmpty()) {
            Bundle args = new Bundle();
            args.putString(ErrorDialogFragment.TITLE,"Error");
            args.putString(ErrorDialogFragment.MESSAGE,"Please enter valid values!");
            ErrorDialogFragment error = new ErrorDialogFragment();
            error.setArguments(args);
            error.show(getFragmentManager(),"INPUT_ERROR");
        }
        else {
            Scanner f = new Scanner(grades);
            f.useLocale(Locale.US); //without this line the program wouldn't work
            //on machines with different locales
            f.useDelimiter(",\\s*");
            ArrayList<Double> dd = new ArrayList<Double>();

            while (f.hasNextDouble()) {
                dd.add(f.nextDouble());
            }
            double gradesSum = 0;
            double[] gradesArray = new double[dd.size()];
            for (int i = 0; i < gradesArray.length; i++) {
                gradesArray[i] = dd.get(i);
                gradesSum += gradesArray[i];
            }
            double assAvg = gradesSum / gradesArray.length;
            avg.setText(String.valueOf(assAvg));

            new databaseAsyncTask(databaseAsyncTask.DELETE_GRADES,null,assignID).execute(dbHelper);

            ContentValues cn = new ContentValues();
            cn.put(DatabaseContract.GradesTable.COL_ASSIGNMENT_ID, assignID);
            cn.put(DatabaseContract.AssignmentsTable.COL_ASSIGNMENT_AVERAGE, assAvg);
            new databaseAsyncTask(databaseAsyncTask.EDIT,cn,assignID);
            for (Double d : dd) {
                ContentValues grds = new ContentValues();
                grds.put(DatabaseContract.GradesTable.COL_GRADE_VALUE, d);
                grds.put(DatabaseContract.GradesTable.COL_ASSIGNMENT_ID, assignID);
                new databaseAsyncTask(databaseAsyncTask.INSERT_GRADE,grds).execute(dbHelper);
            }
            classroom.setGrades(assignID,dd);
            refreshAverage();

            TextView tv = (TextView)fragment.getView().findViewById(atafies.degreehacks.R.id.grades_display);
            Button edit = (Button)fragment.getView().findViewById(atafies.degreehacks.R.id.edit_assign_button);
            Button save = (Button)fragment.getView().findViewById(atafies.degreehacks.R.id.save_assign_button);
            tv.setText(et_grades.getText().toString());
            edit.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
            et_grades.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDeleteButtonPressed(final AssignmentDisplayFragment fragment, final long assignID){
        Bundle args = new Bundle();
        args.putString(YesNoDialogFragment.MESSAGE, "Delete Assignment?");
        YesNoDialogFragment confirm = new YesNoDialogFragment();
        confirm.setArguments(args);
        confirm.setContinueListener(new YesNoDialogFragment.ContinueListener() {
            @Override
            public void onContinue(boolean cont) {
                if (cont) {
                    getFragmentManager().beginTransaction().remove(fragment).commit();
                    new databaseAsyncTask("delete", null, assignID).execute(dbHelper);
                    classroom.deleteAssignment(assignID);
                    refreshAverage();
                }
            }
        });
        confirm.show(getFragmentManager(), null);
    }


    private static class databaseAsyncTask extends AsyncTask<StudyHacksDBHelper, Void, Void> {

        public static final String INSERT = "insert";
        public static final String INSERT_GRADE = "insert_grade";
        public static final String DELETE_ASSIGN = "delete";
        public static final String EDIT = "edit";
        public static final String DELETE_GRADES = "delete_grades";

        private ContentValues cv;
        private String operation = "";
        private long id = -1;

        protected databaseAsyncTask(String operation,ContentValues cv){
            this.operation = operation;
            if(cv != null)
                this.cv = cv;
        }

        protected databaseAsyncTask(String operation,ContentValues cv,long id){
            this.operation = operation;
            if(cv != null)
                this.cv = cv;
            this.id = id;
        }

        @Override
        protected Void doInBackground(StudyHacksDBHelper... params) {
            switch (operation) {
                case INSERT:
                    params[0].insert(DatabaseContract.AssignmentsTable.TABLE_NAME
                            , null, cv);
                    break;
                case EDIT:
                    params[0].update(DatabaseContract.AssignmentsTable.TABLE_NAME
                            , cv, "_ID=" + id, null);
                    break;
                case DELETE_ASSIGN:
                    params[0].execSQL(DatabaseContract.AssignmentsTable.DELETE_ENTRY + id);
                    break;
                case INSERT_GRADE:
                    params[0].insert(DatabaseContract.GradesTable.TABLE_NAME
                            , null, cv);
                    break;
                case DELETE_GRADES:
                    params[0].deleteGrades(id);
                    break;
            }

            return null;
        }
    }

    private static class loadClassroomTask extends AsyncTask<StudyHacksDBHelper, Void, Classroom> {

        private ArrayList<Assignment> assignments;
        private String className;
        private ClassroomActivity activity;

        public loadClassroomTask(ClassroomActivity activity,String className){
            this.className = className;
            this.activity = activity;
        }

        @Override
        protected Classroom doInBackground(StudyHacksDBHelper... params) {
            Classroom cr = params[0].getClassroom(className);
            activity.classroomID = cr.getClassroomID();
            assignments = cr.getAssignments();
            return cr;
        }
        @Override
        protected void onPostExecute(Classroom c){
            if (c != null) {
                activity.classroom = c;
                FragmentManager fragmentManager = activity.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                for (int i = 0; i < assignments.size(); i++) {
                    AssignmentDisplayFragment assignmentFragment = new AssignmentDisplayFragment();
                    Bundle args = new Bundle();
                    String assignmentName = assignments.get(i).getAssignmentName();
                    double[] grades = assignments.get(i).getGradesArray();
                    double average = assignments.get(i).getAverage();
                    double weight = assignments.get(i).getWeight();

                    long assignID = assignments.get(i).getAssignmentID();
                    args.putDouble(AssignmentDisplayFragment.WEIGHT,weight);
                    args.putLong(AssignmentDisplayFragment.ASSIGNMENTID, assignID);
                    args.putString(AssignmentDisplayFragment.TITLE, assignmentName);
                    args.putDoubleArray(AssignmentDisplayFragment.GRADES, grades);
                    args.putDouble(AssignmentDisplayFragment.GRADES_AVG,average);
                    assignmentFragment.setArguments(args);
                    fragmentTransaction.add(atafies.degreehacks.R.id.assignment_fragments, assignmentFragment
                            , "ASSIGNMENT_FRAGMENT" + assignID);
                }
                fragmentTransaction.commit();

                if (c.getWeightTotal() > 100){
                    activity.giveError("WARNING","Weight values add up to over 100%, " +
                            "class average may not be accurate");
                }
                else if (c.getWeightTotal() < 100){
                    activity.giveError("WARNING","Sum of weight values is under 100%, " +
                            "class average may not be accurate");
                }

                activity.setTitle(className + " | Average: "
                        + String.format("%.2f", c.findClassGrade()));


            } else {
                throw new NullPointerException("Classroom is null!");
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Classroom Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://atafies.degreehacks/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Classroom Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://atafies.degreehacks/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

}
