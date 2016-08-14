package atafies.degreehacks;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Adriel on 1/1/2016.
 */
public class GradeTrackerActivity extends BaseDrawerActivity implements AddClassroomFragment.OnAddButtonPressedListener,ClassroomListFragment.onListFragmentItemClickListener {


    private StudyHacksDBHelper dbHelper;
    private ClassroomListFragment listfragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(atafies.degreehacks.R.layout.activity_base_drawer);
        setTitle("Grade Tracker");
        LayoutInflater inflate = LayoutInflater.from(this);
        ViewGroup main = (ViewGroup)inflate.inflate(atafies.degreehacks.R.layout.activity_grade_tracker,
                (ViewGroup)findViewById(atafies.degreehacks.R.id.coord_layout));

        dbHelper = StudyHacksDBHelper.getInstance(this);
        listfragment = new ClassroomListFragment();
        loadList();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        int toast_duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(getApplication(), "Touch list items to edit \n Hold to delete "
                , toast_duration);
        toast.show();


        //todo: set up to populate list with database list of classes.

    }

    @Override
    protected void onResume(){
        super.onResume();
        reloadList();
    }
    public void addClassroom(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        AddClassroomFragment fragment = new AddClassroomFragment();
        fragment.show(fragmentManager, "AddClassroomFragment");
    }
    private void loadList(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(atafies.degreehacks.R.id.classroom_list_view, listfragment, "CLASSROOM_LIST");
        fragmentTransaction.commit();
    }
    private void reloadList(){
        listfragment.getLoaderManager().restartLoader(0, null, listfragment);
    }

    @Override
    public void onAddButtonPressed(DialogFragment dialog) {
        Dialog dialogView = dialog.getDialog();
        String classname = ((EditText) dialogView.findViewById(atafies.degreehacks.R.id.et_class_name))
                .getText().toString();
        ContentValues cn = new ContentValues();
        cn.put(DatabaseContract.ClassroomsTable.COL_CLASS_NAME, classname);
        cn.put(DatabaseContract.ClassroomsTable.COL_CLASS_GRADE,0);
        //db.insert(DatabaseContract.ClassroomsTable.TABLE_NAME, null, cn);
        new databaseAsyncTask(databaseAsyncTask.INSERT,cn).execute(dbHelper);
        reloadList();
    }
    @Override
    public void onListFragmentItemClick(View v,long id,int position){
        Intent intent = new Intent(this,ClassroomActivity.class);
        String className = ((TextView)v).getText().toString();
        intent.putExtra(AddClassroomFragment.CLASS_NAME,className);
        startActivity(intent);
    }
    public void onListFragmentItemLongClick(View v,long id,int position){
        Bundle args = new Bundle();
        final long classID = id;
        args.putString(YesNoDialogFragment.MESSAGE, "Delete Class?");
        YesNoDialogFragment confirm = new YesNoDialogFragment();
        confirm.setArguments(args);
        confirm.setContinueListener(new YesNoDialogFragment.ContinueListener() {
            @Override
            public void onContinue(boolean cont) {
                if (cont) {
                    new databaseAsyncTask(databaseAsyncTask.DELETE, null, classID).execute(dbHelper);
                    reloadList();
                }
            }
        });
        confirm.show(getFragmentManager(), null);
    }

    private class databaseAsyncTask extends AsyncTask<StudyHacksDBHelper, Void, Object> {

        public static final String INSERT = "insert";
        public static final String EDIT = "edit";
        public static final String DELETE = "delete";

        private ContentValues cv;
        private String operation = "";
        private long id = -1;

        protected databaseAsyncTask(){}

        protected databaseAsyncTask(String operation){
            this.operation = operation;
        }

        protected databaseAsyncTask(String operation,ContentValues cv){
            this.operation = operation;
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
                    params[0].insert(DatabaseContract.ClassroomsTable.TABLE_NAME, null,
                            cv);
                    break;
                case EDIT:
                    params[0].update(DatabaseContract.ClassroomsTable.TABLE_NAME
                            , cv, "_ID=" + id, null);
                    break;
                case DELETE:
                    //params[0].execSQL(DatabaseContract.ClassroomsTable.DELETE_ENTRY + id);
                    params[0].delete(DatabaseContract.ClassroomsTable.TABLE_NAME,"_ID=" + id,null);
                    break;
                //case "create": //default case creates table
                    //dbHelper = StudyHacksDBHelper.getInstance(getApplication());
                    //break;
            }
            return null;
        }
    }

}

