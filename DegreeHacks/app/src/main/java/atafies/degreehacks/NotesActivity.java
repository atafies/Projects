package atafies.degreehacks;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//Todo: Add edit and delet functionality to notes, as well as single activity management.
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Adriel on 12/30/2015.
 */
public class NotesActivity extends BaseDrawerActivity
        implements AddNoteFragment.OnAddButtonPressedListener
        ,EditNoteFragment.OnEditNoteListener
        ,NotesListFragment.onListFragmentItemClickListener {


    private final static String LOG_TAG = "Single Instance Check";

    private NotesListFragment listfragment = new NotesListFragment();

    ViewGroup main;

    StudyHacksDBHelper dbHelper;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = StudyHacksDBHelper.getInstance(this);

        setContentView(atafies.degreehacks.R.layout.activity_base_drawer);
        setTitle("Notes");
        LayoutInflater inflate = LayoutInflater.from(this);
        main = (ViewGroup) inflate.inflate(atafies.degreehacks.R.layout.activity_notes,
                (ViewGroup) findViewById(atafies.degreehacks.R.id.coord_layout));

        loadList();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        int toast_duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(getApplication(), "Touch list items to edit ", toast_duration);
        toast.show();


        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void loadList(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(atafies.degreehacks.R.id.notes_list_view, listfragment, "NOTE_LIST");
        fragmentTransaction.commit();
    }
    private void reloadList(){
        listfragment.getLoaderManager().restartLoader(0, null, listfragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadList();
    }

    @Override
    public void onAddButtonPressed(DialogFragment dialog) {
        Dialog dialogView = dialog.getDialog();
        String note = ((EditText) dialogView.findViewById(atafies.degreehacks.R.id.et_enter_note)).getText().toString();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.NotesTable.COL_NOTE, note);

        new databaseAsyncTask("insert",values).execute(dbHelper);

        reloadList();
    }
    @Override
    public void onSaveNotePress(DialogFragment dialog,long id, int position){
        String note = ((EditText)(dialog.getDialog()).findViewById(atafies.degreehacks.R.id.et_edit_note)).
                getText().toString();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.NotesTable.COL_NOTE, note);

        new databaseAsyncTask("edit",cv,id).execute(dbHelper);
        reloadList();
    }
    @Override
    public void onDeleteNotePress(DialogFragment dialog,long id, int position){
        new databaseAsyncTask("delete",null,id).execute(dbHelper);
        reloadList();
    }


    public void addNote(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        AddNoteFragment fragment = new AddNoteFragment();
        fragment.show(fragmentManager, "AddNoteFragment");
    }

    @Override
    public void onListFragmentItemClick(View v,long id,int position){
        FragmentManager fragmentManager = getFragmentManager();
        EditNoteFragment newFragment = new EditNoteFragment();
        String loadtext = ((TextView)v).getText().toString();
        Bundle args = new Bundle();
        args.putString(EditNoteFragment.ARG_CONTENT,loadtext);
        args.putInt(EditNoteFragment.ARG_POSITION, position);
        args.putLong(EditNoteFragment.ARG_ID, id);
        newFragment.setArguments(args);
        newFragment.show(fragmentManager, "EditNoteFragment");
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
    }


    private class databaseAsyncTask extends AsyncTask<StudyHacksDBHelper, Void, Void> {

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
                case "insert":
                    params[0].insert(DatabaseContract.NotesTable.TABLE_NAME
                            , null, cv);
                    break;
                case "edit":
                    params[0].update(DatabaseContract.NotesTable.TABLE_NAME
                            , cv, "_ID=" + id, null);
                    break;
                case "delete":
                    params[0].execSQL(DatabaseContract.NotesTable.DELETE_ENTRY + id);
                    break;
                case "create": //default case creates table
                    //db = ((StudyHacksDBHelper)params[0]).getWritableDatabase();
                    break;
            }

            return null;
        }
    }

}
