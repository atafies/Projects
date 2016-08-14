package atafies.degreehacks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.List;

public abstract class BaseDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    List<AsyncTask> runningTasks;

    protected void onCreateDrawer() {
        //super.setContentView(R.layout.activity_base_drawer);
        Toolbar toolbar = (Toolbar) findViewById(atafies.degreehacks.R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(atafies.degreehacks.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, atafies.degreehacks.R.string.navigation_drawer_open, atafies.degreehacks.R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(atafies.degreehacks.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void onStopAsyncTask() {
        for( AsyncTask task : runningTasks ) {
            task.cancel(true);
        }
    }

    public AsyncTask start( AsyncTask task ) {
        if (task != null) {
            runningTasks.add(task);
        }
        else
            return null;

        return task;
    }

    public void done( AsyncTask task ) {
        runningTasks.remove(task);
    }
    public static boolean isNumeric(String str)
    {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }

    public void giveError(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("Error!")
                .setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
        // create alert dialog
        alertDialogBuilder.create();
        alertDialogBuilder.show();
    }
    public void giveError(String title, String message) {
        Bundle args = new Bundle();
        args.putString(ErrorDialogFragment.TITLE, title);
        args.putString(ErrorDialogFragment.MESSAGE,message);
        ErrorDialogFragment error = new ErrorDialogFragment();
        error.setArguments(args);
        error.show(getFragmentManager(),"INPUT_ERROR");
    }


    @Override
    public void setContentView(@LayoutRes int layoutResID){
        super.setContentView(layoutResID);
        onCreateDrawer();
    }
    //---------------------------------------------------------------------------------
    ///Drawer Menu item Methods------------------------------------

    public void gotoGradeCalc(MenuItem item){
        Intent intent = new Intent(this, GradeTrackerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void gotoGPA(MenuItem item){
        Intent intent = new Intent(this, GPACalculatorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void gotoNotes(MenuItem item){
        Intent intent = new Intent(this, NotesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(atafies.degreehacks.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == atafies.degreehacks.R.id.grade_tracker) {
            // Handle the camera action
            gotoGradeCalc(item);
        }
        if (id == atafies.degreehacks.R.id.gpa_calculator) {
            // Handle the camera action
            gotoGPA(item);
        }
        if (id == atafies.degreehacks.R.id.notes_item) {
            // Handle the camera action
            gotoNotes(item);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(atafies.degreehacks.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
