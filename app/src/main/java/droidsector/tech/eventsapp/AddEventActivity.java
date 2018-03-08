package droidsector.tech.eventsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel);
        setSupportActionBar(toolbar);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(AddEventActivity.this, R.color.colorPrimaryDark));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getTime(View view) {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View pickTime = inflater.inflate(R.layout.layout_time_pick, null);
        new AlertDialog.Builder(this)
                .setView(pickTime)
                .setIcon(android.R.drawable.ic_menu_agenda)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which)
                    {
                        TimePicker timePicker = pickTime.findViewById(R.id.timePicker);
                        String hour = timePicker.getCurrentHour()+"";
                        String minutes = timePicker.getCurrentMinute()+"";
                        if (hour.length() == 1)
                            hour = "0" + hour;
                        if (minutes.length() == 1)
                            minutes = "0" + minutes;
                        String time = hour +" : "+ minutes;
                        Log.d("Abhinav", time);
                        Toast.makeText(getBaseContext(), time, Toast.LENGTH_SHORT).show();
                    }
                })
                .create().show();
    }

    public void getDate(View view) {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View pickDate = inflater.inflate(R.layout.layout_date_pick, null);
        new AlertDialog.Builder(this)
                .setView(pickDate)
                .setIcon(android.R.drawable.ic_menu_agenda)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which)
                    {
                        DatePicker datePicker = pickDate.findViewById(R.id.pickdate);
                        String year = "" + datePicker.getYear();
                        String month = "" + (datePicker.getMonth() + 1);
                        String day = "" + datePicker.getDayOfMonth();
                        if (month.length() == 1)
                            month = "0" + month;
                        if (day.length() == 1)
                            day = "0" + day;
                        String date = year +" - "+ month +" - "+ day;
                        Log.d("Abhinav", date);
                        Toast.makeText(getApplicationContext(), date, Toast.LENGTH_SHORT).show();
                    }
                })
                .create().show();
    }
}