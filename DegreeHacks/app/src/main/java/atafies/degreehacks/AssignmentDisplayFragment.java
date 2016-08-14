package atafies.degreehacks;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Adriel on 1/9/2016.
 */
public class AssignmentDisplayFragment extends Fragment {

    public static final String TITLE = "ASSIGNMENT_NAME";
    public static final String GRADES = "GRADES";
    public static final String ASSIGNMENTID = "ASSIGNMENT_ID";
    public static final String GRADES_AVG = "GRADES_AVERAGE";
    public static final String WEIGHT = "WEIGHT";

    public interface OnButtonPressedListener {
        void onDeleteButtonPressed(AssignmentDisplayFragment fragment, long id);
        void onEditButtonPressed(AssignmentDisplayFragment fragment,long id);
        void onSaveButtonPressed(AssignmentDisplayFragment fragment,long id);
    }

    OnButtonPressedListener mListener;

    private String title;
    private double[] grades;
    private long assignID;
    private double average;
    private double weight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(atafies.degreehacks.R.layout.fragment_assignment, container, false);

        LinearLayout frame = (LinearLayout)v.findViewById(atafies.degreehacks.R.id.assignment_fragments);
        Bundle args = getArguments();
        assignID = args.getLong(ASSIGNMENTID);
        title = args.getString(TITLE);
        grades = args.getDoubleArray(GRADES);
        average = args.getDouble(GRADES_AVG);
        weight = args.getDouble(WEIGHT);
        String arrayString="";
        for(double d:grades){
            arrayString+= String.format("%.2f", d) + ", ";
        }
        TextView tv_title = (TextView)v.findViewById(atafies.degreehacks.R.id.assignment_title);
        tv_title.setText(title);
        TextView tv_grades = (TextView)v.findViewById(atafies.degreehacks.R.id.grades_display);
        tv_grades.setText(arrayString);
        TextView avg = (TextView)v.findViewById(atafies.degreehacks.R.id.average_display);
        avg.setText(String.format("%.2f", average));
        TextView tv_weight = (TextView)v.findViewById(atafies.degreehacks.R.id.weight_display);
        tv_weight.setText(String.valueOf(weight) + "% of total grade");


        Button delete = (Button)v.findViewById(atafies.degreehacks.R.id.delete_assign_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDeleteButtonPressed(AssignmentDisplayFragment.this, assignID);
            }
        });
        Button edit = (Button)v.findViewById(atafies.degreehacks.R.id.edit_assign_button);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onEditButtonPressed(AssignmentDisplayFragment.this, assignID);
            }
        });

        Button save = (Button)v.findViewById(atafies.degreehacks.R.id.save_assign_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSaveButtonPressed(AssignmentDisplayFragment.this,assignID);
            }
        });
        return v;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (OnButtonPressedListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement OnButtonPressedListener");
        }

    }
}
