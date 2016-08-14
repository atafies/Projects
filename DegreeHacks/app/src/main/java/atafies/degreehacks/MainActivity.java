package atafies.degreehacks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import atafies.degreehacks.StudyHacksDBHelper;

public class MainActivity extends AppCompatActivity {

    StudyHacksDBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(atafies.degreehacks.R.layout.activity_main_menu);
        //Use below to add elements to content view.
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)
                findViewById(atafies.degreehacks.R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = StudyHacksDBHelper.getInstance(this);

    }

    public void gotoGradeTracker(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, GradeTrackerActivity.class);
        startActivity(intent);
    }

    public void gotoGPA(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, GPACalculatorActivity.class);
        startActivity(intent);
    }
    public void gotoNotes(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, NotesActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(atafies.degreehacks.R.menu.base_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == atafies.degreehacks.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
