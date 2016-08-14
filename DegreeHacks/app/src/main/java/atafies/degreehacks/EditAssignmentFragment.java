package atafies.degreehacks;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Adriel on 1/4/2016.
 */
public class EditAssignmentFragment extends DialogFragment {
    public interface OnEditNoteListener{
        void onSaveNotePress(DialogFragment dialog, long id, int position);
        void onDeleteNotePress(DialogFragment dialog, long id, int position);
    }

    public static final String ARG_ID = "_ID";
    public static final String ARG_POSITION = "position";
    public static final String ARG_CONTENT = "content";



    OnEditNoteListener mListener;

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Bundle args = getArguments();
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        final String content = args.getString(ARG_CONTENT);
        final long id = args.getLong(ARG_ID);
        final int position = args.getInt(ARG_POSITION);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(atafies.degreehacks.R.layout.fragment_edit_note);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        EditText et = (EditText)dialog.findViewById(atafies.degreehacks.R.id.et_edit_note);

        Button saveIt = (Button)dialog.findViewById(atafies.degreehacks.R.id.button_save_note);
        Button deleteIt = (Button)dialog.findViewById(atafies.degreehacks.R.id.button_delete_note);
        et.setText(content);
        et.setSelection(et.getText().length());
        saveIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSaveNotePress(EditAssignmentFragment.this,id,position);
                dismiss();
            }
        });
        deleteIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDeleteNotePress(EditAssignmentFragment.this, id, position);
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
            mListener = (EditAssignmentFragment.OnEditNoteListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement OnAddButtonPressedListener");
        }

    }
}
