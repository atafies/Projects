package atafies.degreehacks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

public class GPACalculatorActivity extends BaseDrawerActivity {
    private EditText current_gpa,current_hours;
    private int classes = 0;//number of classes this semester
    private ViewGroup classlist;
    private boolean NextisPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(atafies.degreehacks.R.layout.activity_base_drawer);

        setTitle("GPA Calculator");

        LayoutInflater inflate = LayoutInflater.from(this);
        inflate.inflate(atafies.degreehacks.R.layout.activity_gpacalculator, (ViewGroup)
                findViewById(atafies.degreehacks.R.id.coord_layout));


        current_gpa = (EditText)findViewById(atafies.degreehacks.R.id.enter_gpa);
        current_hours = (EditText)findViewById(atafies.degreehacks.R.id.enter_hours);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public void onNextPress(View view){
        if (!NextisPressed) {

            if (!isNumeric(current_gpa.getText().toString())||current_gpa.getText()
                    .toString().isEmpty()) {
                giveError("Please enter a valid number for \"Current GPA\"");
                return;
            }
            if (!isNumeric(current_hours.getText().toString())||current_hours.getText()
                    .toString().isEmpty()) {
                giveError("Please enter a valid number for \"Total Credits Completed\"");
                return;
            }


            EditText editText = (EditText) findViewById(atafies.degreehacks.R.id.no_of_classes);
            if (editText.getText().toString().isEmpty()){
                giveError("Please enter a valid number");
                return;
            }
            else if (Integer.parseInt(editText.getText().toString())> 10){
                giveError("Give a number of classes from 1 to 10");
                return;
            }
            classes = Integer.parseInt(editText.getText().toString());
            LayoutInflater inflater = LayoutInflater.from(this);
            classlist = (ViewGroup) findViewById(atafies.degreehacks.R.id.class_list);
            ViewGroup mainview = (ViewGroup)findViewById(atafies.degreehacks.R.id.view_gpa_calculator);
            View v;
            for (int i = 0; i < classes; i++) {
                inflater.inflate(atafies.degreehacks.R.layout.classes_entry, classlist);
                v = classlist.getChildAt(i);
                final EditText et1 = (EditText) v.findViewById(atafies.degreehacks.R.id.letter_grade);
                final EditText et2 = (EditText) v.findViewById(atafies.degreehacks.R.id.credit_hours);
                if (i == classes - 1)
                    et2.setImeOptions(EditorInfo.IME_ACTION_DONE);

                et1.addTextChangedListener(new TextWatcher() {

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (et1.getText().toString().length() == 1) //size as per your requirement
                        {
                            et2.requestFocus();
                        }
                    }
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    public void afterTextChanged(Editable s) {}
                });
            }
            Button accept = (Button)mainview.findViewById(atafies.degreehacks.R.id.accept_button);
            accept.setVisibility(View.VISIBLE);
            NextisPressed = true;
        }
    }

    public void calculateGPA(View view){//function for calculate GPA button
        if (!isNumeric(current_gpa.getText().toString())||current_gpa.getText()
                .toString().isEmpty()) {
            giveError("Please enter a valid number for \"Current GPA\"");
            return;
        }
        if (!isNumeric(current_hours.getText().toString())||current_hours.getText()
                .toString().isEmpty()) {
            giveError("Please enter a valid number for \"Total Credits Completed\"");
            return;
        }
        EditText editText = (EditText) findViewById(atafies.degreehacks.R.id.no_of_classes);
        if (editText.getText().toString().isEmpty()){
            giveError("Please enter a valid number");
            return;
        }
        else if (Integer.parseInt(editText.getText().toString())> 10){
            giveError("Give a number of classes from 1 to 10");
            return;
        }

        double c_gpa = Double.parseDouble(current_gpa.getText().toString());//current gpa
        double s_gpa;//semester gpa
        int c_hours = Integer.parseInt(current_hours.getText().toString());//current credit hours
        int s_hours=0; //semester credit hours
        double q_points = c_gpa*c_hours;//current quality points
        double s_q_points = 0;//semester quality points
        double cum_gpa; //cumulative gps

        int[] gp = new int[classes];
        int[] cr = new int[classes];
        ViewGroup v;

        for (int i= 0; i < classes; i++){
            v = (ViewGroup)classlist.getChildAt(i);
            EditText et1 = (EditText)v.findViewById(atafies.degreehacks.R.id.letter_grade);
            EditText et2 = (EditText)v.findViewById(atafies.degreehacks.R.id.credit_hours);


            String g = et1.getText().toString();
            switch (g){
                case "A": gp[i] = 4;
                    break;
                case "B": gp[i] = 3;
                    break;
                case "C": gp[i] = 2;
                    break;
                case "D": gp[i] = 1;
                    break;
                case "F": gp[i] = 0;
                    break;
                default:
                    giveError("Please enter a valid letter grade \n(A through F)");
                    return;
            }
            if(!isNumeric(et2.getText().toString())||et2.getText().toString().isEmpty() ) {
                giveError("Please enter a valid number of credits");
                return;
            }
            cr[i]= Integer.parseInt(et2.getText().toString());
            s_hours+=cr[i];
        }

        for (int i = 0;i < classes; i++){
            s_q_points += gp[i]*cr[i];
            q_points += gp[i]*cr[i];
        }

        s_gpa = s_q_points/s_hours;
        cum_gpa = q_points/(c_hours + s_hours);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("Grade Point Averages ")
                .setMessage("Semester GPA: " + String.format("%.3f",s_gpa)
                        + "\nCumulative GPA: "+ String.format("%.3f",cum_gpa))
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }
}
