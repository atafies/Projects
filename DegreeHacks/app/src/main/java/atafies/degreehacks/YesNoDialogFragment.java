package atafies.degreehacks;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Adriel on 12/30/2015.
 */
public class YesNoDialogFragment extends DialogFragment {

    public interface ContinueListener{
        void onContinue(boolean cont);
    }

    private ContinueListener mListener;

    public static final String TITLE = "TITLE";
    public static final String MESSAGE = "MESSAGE";


    public void setContinueListener(ContinueListener listener) {
        mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Bundle args = getArguments();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder
                .setCancelable(false)
                .setMessage(args.getString(MESSAGE))
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mListener.onContinue(true);
                            dismiss();
                            }
                        })
        .setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onContinue(false);
                        dismiss();
                    }
                });
        // create alert dialog
        return alertDialogBuilder.create();
    }
}
