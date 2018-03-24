package droidsector.tech.eventsapp.Old;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import droidsector.tech.eventsapp.R;

public class EventTaskActivity extends AppCompatActivity {
    ArrayList<String> names;
    ArrayList<String> memberIds;
    String eventid = "", category = "", eventname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_task);
        setTitle("Tasks");
        Intent intent = getIntent();
        eventname = intent.getStringExtra("eventname");
        eventid = intent.getStringExtra("eventid");
        category = intent.getStringExtra("category");
        Button addTask = findViewById(R.id.addtasks);
        if (category.equals("admin"))
            addTask.setVisibility(View.VISIBLE);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(EventTaskActivity.this);
                final View addTaskLayout = inflater.inflate(R.layout.layout_add_task, null);
                final Spinner spinner = addTaskLayout.findViewById(R.id.spinner);
                names = new ArrayList<>();
                memberIds = new ArrayList<>();
                new FetchMembers().execute(spinner);
                new AlertDialog.Builder(EventTaskActivity.this)
                        .setTitle("Add Task")
                        .setIcon(android.R.drawable.ic_menu_agenda)
                        .setView(addTaskLayout)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText nameText = addTaskLayout.findViewById(R.id.taskname);
                                EditText descriptionText = addTaskLayout.findViewById(R.id.description);
                                String name = nameText.getText().toString();
                                String description = descriptionText.getText().toString();
                                String assignedTo = getIdsFromSpinnerValues(names, memberIds, spinner.getSelectedItem().toString());
                                if (!name.isEmpty() && !description.isEmpty())
                                {
                                    new AddTask().execute(name, description, assignedTo);
                                }
                                else
                                    Toast.makeText(EventTaskActivity.this, "Details cannot be empty", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .create().show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new FetchTasks().execute();
    }

    private class FetchMembers extends AsyncTask<Spinner, Void, Void> {
        Spinner spinner;
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";

        @Override
        protected Void doInBackground(Spinner... spinners) {
            spinner = spinners[0];
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                String myURL = baseUrl + "fetchteammembers.php?eventid=" + eventid;
                myURL = myURL.replaceAll(" ", "%20");
                myURL = myURL.replaceAll("\\+", "%2B");
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
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String data;
                while ((data = br.readLine()) != null)
                    webPage = webPage + data;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!webPage.isEmpty()) {
                while (webPage.contains("<br>")) {
                    int brI = webPage.indexOf("<br>");
                    final String memberId = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String memberName = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String phoneNumber = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String accepted = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String id = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    if (accepted.equals("y") || accepted.equals("admin")) {
                        memberIds.add(memberId);
                        names.add(memberName);
                    }
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(EventTaskActivity.this,
                        android.R.layout.simple_spinner_item, names);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);
            }
        }
    }

    private class AddTask extends AsyncTask<String,Void,Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        String userid;
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(EventTaskActivity.this, "Please Wait!", "Adding Task");
        }
        @Override
        protected Void doInBackground(String... strings){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                userid = strings[2];
                String myURL = baseUrl+"addtask.php?eventid="+eventid+"&taskname="+strings[0]
                        + "&description=" + strings[1] + "&assignedto=" + userid;
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
                Toast.makeText(EventTaskActivity.this, "Task Added Successfully", Toast.LENGTH_SHORT).show();
                new SendNotif().execute(userid, eventname, "A Task has been assigned to you");
                onResume();
            }
            else
                Toast.makeText(EventTaskActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    private class RemoveTask extends AsyncTask<String, Void, Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(EventTaskActivity.this, "Please Wait!", "Removing Task");
        }

        @Override
        protected Void doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                String myURL = baseUrl + "removetask.php?taskid=" + strings[0];
                myURL = myURL.replaceAll(" ", "%20");
                myURL = myURL.replaceAll("\\+", "%2B");
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
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String data;
                while ((data = br.readLine()) != null)
                    webPage = webPage + data;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (webPage.equals("success")) {
                Toast.makeText(EventTaskActivity.this, "Task Removed Successfully", Toast.LENGTH_SHORT).show();
                onResume();
            } else
                Toast.makeText(EventTaskActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    private class FetchTasks extends AsyncTask<Void,Void,Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(EventTaskActivity.this, "Please Wait!", "Fetching Tasks");
            LinearLayout data = findViewById(R.id.data);
            data.removeAllViews();
        }
        @Override
        protected Void doInBackground(Void... voids){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"fetchtasks.php?eventid="+eventid;
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
            if(!webPage.isEmpty())
            {
                LinearLayout data = findViewById(R.id.data);
                LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams matchParams = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                final Context context = EventTaskActivity.this;
                while (webPage.contains("<br>"))
                {
                    int brI = webPage.indexOf("<br>");
                    final String taskId = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String taskName = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String description = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String completed = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    LinearLayout outerLinearLayout = new LinearLayout(context);
                    outerLinearLayout.setLayoutParams(matchParams);
                    outerLinearLayout.setPadding(0,16,0,16);
                    LinearLayout linearLayout = new LinearLayout(context);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setPadding(32,16,32,16);
                    linearLayout.setBackgroundColor(Color.WHITE);
                    linearLayout.setLayoutParams(matchParams);
                    TextView nameTV = new TextView(context);
                    nameTV.setTextSize(22);
                    nameTV.setLayoutParams(wrapParams);
                    nameTV.setText(taskName);
                    linearLayout.addView(nameTV);
                    TextView descriptionTV = new TextView(context);
                    descriptionTV.setLayoutParams(wrapParams);
                    descriptionTV.setText("Description : " + description);
                    linearLayout.addView(descriptionTV);
                    TextView completedTV = new TextView(context);
                    completedTV.setLayoutParams(wrapParams);
                    final int viewId = View.generateViewId();
                    completedTV.setId(viewId);
                    completedTV.setText("Status : " + (completed.equals("y")?"Completed":"Pending"));
                    linearLayout.addView(completedTV);
                    outerLinearLayout.addView(linearLayout);
                    data.addView(outerLinearLayout);
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(EventTaskActivity.this);
                            dialog
                                    .setTitle("Confirm Completion")
                                    .setMessage("Are you sure that the task has been completed?")
                                    .setIcon(android.R.drawable.ic_menu_agenda)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            new CompleteTask().execute(taskId, viewId+"");
                                        }
                                    });
                            if (category.equals("admin")) {
                                dialog.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new RemoveTask().execute(taskId);
                                    }
                                });
                            }
                            dialog
                                    .setNegativeButton(android.R.string.no, null)
                                    .create().show();
                        }
                    });
                }
            }
        }
    }

    private class CompleteTask extends AsyncTask<String,Void,Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;
        TextView textView;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(EventTaskActivity.this, "Please Wait!", "Adding Task");
        }
        @Override
        protected Void doInBackground(String... strings){
            textView = findViewById(Integer.parseInt(strings[1]));
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"completetask.php?taskid="+strings[0];
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
                textView.setText("Status : Completed");
            }
            else
                Toast.makeText(EventTaskActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    String getIdsFromSpinnerValues(ArrayList<String> A, ArrayList<String> B, String string) {
        for (int i = 0; i < A.size(); i++) {
            if (A.get(i).equals(string))
                return B.get(i);
        }
        return "";
    }

    private class SendNotif extends AsyncTask<String, Void, Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";

        @Override
        protected Void doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                String myURL = baseUrl + "notificationtoone.php?userid=" + strings[0] + "&body=" + strings[2] + "&title=" + strings[1];
                myURL = myURL.replaceAll(" ", "%20");
                myURL = myURL.replaceAll("\\+", "%2B");
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
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String data;
                while ((data = br.readLine()) != null)
                    webPage = webPage + data;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }
    }
}