package atafies.degreehacks;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Adriel on 12/30/2015.
 */
public class ErrorDialogFragment extends DialogFragment {

    public interface ContinueListener{
        void onContinue(boolean cont);
    }


    private ContinueListener mListener;

    public static final String TITLE = "TITLE";
    public static final String MESSAGE = "MESSAGE";


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Bundle args = getArguments();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder
                .setCancelable(false)
                .setTitle(args.getString(TITLE))
                .setMessage(args.getString(MESSAGE))
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            dismiss();
                            }
                        });
        // create alert dialog
        return alertDialogBuilder.create();
    }
}
