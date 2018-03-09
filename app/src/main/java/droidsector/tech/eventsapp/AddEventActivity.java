package droidsector.tech.eventsapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddEventActivity extends AppCompatActivity {
    String userid = "", eventName, description, location, startDate, endDate;

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
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
        Button saveEvent = findViewById(R.id.save);
        saveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText eventNameEditText = findViewById(R.id.eventname);
                EditText descriptionEditText = findViewById(R.id.description);
                EditText locationEditText = findViewById(R.id.location);
                TextView fromDateEditText = findViewById(R.id.fromdate);
                TextView fromTimeEditText = findViewById(R.id.fromtime);
                TextView toDateEditText = findViewById(R.id.todate);
                TextView toTimeEditText = findViewById(R.id.totime);
                eventName = eventNameEditText.getText().toString();
                description = descriptionEditText.getText().toString();
                location = locationEditText.getText().toString();
                startDate = fromDateEditText.getText().toString() + " " + fromTimeEditText.getText().toString();
                endDate = toDateEditText.getText().toString() + " " + toTimeEditText.getText().toString();
                new AddEvent().execute();
            }
        });
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

    public void getTime(final View view) {
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
                        TextView textView = (TextView) view;
                        textView.setText(time);
                    }
                })
                .create().show();
    }

    public void getDate(final View view) {
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
                        TextView textView = (TextView) view;
                        textView.setText(date);
                    }
                })
                .create().show();
    }

    private class AddEvent extends AsyncTask<Void,Void,Void> {
        String webPage = "";
        String baseUrl = "https://whhc.in/aaa/eventsbuddy/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(AddEventActivity.this, "Please Wait!","Validating!");
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... voids){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"addevent.php?userid="+userid+"&eventname="+eventName
                        +"&description="+description+"&address="+location+"&startdate="+startDate+"&enddate="+endDate;
                myURL = myURL.replaceAll(" ","%20");
                myURL = myURL.replaceAll("\\+","%2B");
                myURL = myURL.replaceAll("\'", "%27");
                myURL = myURL.replaceAll("\'", "%22");
                myURL = myURL.replaceAll("\\(", "%28");
                myURL = myURL.replaceAll("\\)", "%29");
                myURL = myURL.replaceAll("\\{", "%7B");
                myURL = myURL.replaceAll("\\}", "%7B");
                myURL = myURL.replaceAll("\\]", "%22");
                myURL = myURL.replaceAll("\\[", "%22");
                url = new URL(myURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String data;
                while ((data=br.readLine()) != null)
                    webPage=webPage+data;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if(webPage.equals("success"))
            {
                Toast.makeText(AddEventActivity.this, "Event Save Success", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            else
            {
                Toast.makeText(AddEventActivity.this, "Error Occurred: "+webPage, Toast.LENGTH_SHORT).show();
            }
        }
    }
}