package atafies.degreehacks;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Created by Adriel on 1/2/2016.
 */
public class ListViewLoader extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;

    // These are the Contacts rows that we will retrieve
    String[] PROJECTION = {
            DatabaseContract.ClassroomsTable._ID,
            DatabaseContract.ClassroomsTable.COL_CLASS_NAME,
    };
    static final String SELECTION = "((" +
            DatabaseContract.ClassroomsTable.COL_CLASS_NAME + " NOTNULL) AND (" +
            DatabaseContract.ClassroomsTable.COL_CLASS_NAME + " != '' ))";


    // This is the select criteria


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {


        View rootView = inflater.inflate(atafies.degreehacks.R.layout.activity_notes, container,
                false);


        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {DatabaseContract.ClassroomsTable.COL_CLASS_NAME};
        int[] toViews = {atafies.degreehacks.R.id.note_item}; // The TextView in simple_list_item_1

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new SimpleCursorAdapter(getActivity(),
                atafies.degreehacks.R.layout.note_list_item, null,
                fromColumns, toViews, 0);
        setListAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
        return rootView;
    }

    // Called when a new Loader needs to be created
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(getActivity(), null,
                PROJECTION, SELECTION, null, null);
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
        // Do something when a list item is clicked
    }
}