package atafies.degreehacks;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by Adriel on 1/1/2016.
 */
public class AddClassroomFragment extends DialogFragment {

    public static final String CLASS_NAME = "CLASS_NAME";
    public static final String CLASS_GRADE = "COL_CLASS_GRADE";

    OnAddButtonPressedListener mListener;

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(atafies.degreehacks.R.layout.fragment_add_classroom);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        Button addIt = (Button)dialog.findViewById(atafies.degreehacks.R.id.button_add_classroom);
        addIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAddButtonPressed(AddClassroomFragment.this);
                dismiss();
            }
        });

        return dialog;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (OnAddButtonPressedListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement OnAddButtonPressedListener");
        }

    }


    public interface OnAddButtonPressedListener {
        void onAddButtonPressed(DialogFragment dialog);
    }
}
