package atafies.degreehacks;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Created by Adriel on 1/2/2016.
 */
public class AssignmentListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface onListFragmentItemClickListener {
        void onListFragmentItemClick(View v, long id, int position);
    }

    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;
    onListFragmentItemClickListener mListener;

    // These are the Contacts rows that we will retrieve
    String[] PROJECTION = {
            DatabaseContract.AssignmentsTable._ID,
            DatabaseContract.AssignmentsTable.COL_ASSIGNMENT_NAME,
            DatabaseContract.AssignmentsTable.COL_ASSIGNMENT_WEIGHT
    };
    // This is the select criteria
    static final String SELECTION = "((" +
            DatabaseContract.AssignmentsTable.COL_ASSIGNMENT_NAME + " NOTNULL) AND (" +
            DatabaseContract.AssignmentsTable.COL_ASSIGNMENT_NAME + " != '' ))";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(atafies.degreehacks.R.layout.activity_classroom, container,
                false);
        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {DatabaseContract.AssignmentsTable.COL_ASSIGNMENT_NAME,};
        int[] toViews = {atafies.degreehacks.R.id.assignment_title}; // The TextView in simple_list_item_1

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new SimpleCursorAdapter(getActivity(),
                atafies.degreehacks.R.layout.fragment_assignment, null,
                fromColumns, toViews, 0);
        setListAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                mListener.onListFragmentItemClick(view,id, position);
            }
        });
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (onListFragmentItemClickListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement onListFragmentItemClickListener");
        }

    }

    // Called when a new Loader needs to be created
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(getActivity(), null,
                PROJECTION, SELECTION, null, null) {
            @Override
            public Cursor loadInBackground() {
                StudyHacksDBHelper dbHelper = StudyHacksDBHelper.getInstance(getActivity());
                //SQLiteDatabase db = dbHelper.getWritableDatabase();
                return dbHelper.query(DatabaseContract.ClassroomsTable.TABLE_NAME
                        ,getProjection()
                        ,getSelection()
                        ,getSelectionArgs()
                        ,null
                        ,null
                        ,getSortOrder()
                        ,null);

            }
        };
    }

    // Called when a previously created loader has finished loading
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
    }

    // Called when a previously created loader is reset, making the data unavailable
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mListener.onListFragmentItemClick(v,id,position);
    }
}