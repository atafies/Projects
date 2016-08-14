package atafies.degreehacks;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Adriel on 1/1/2016.
 */
public class AddClassTrackerFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(atafies.degreehacks.R.layout.fragment_class_tracker, container, false);
    }
}
