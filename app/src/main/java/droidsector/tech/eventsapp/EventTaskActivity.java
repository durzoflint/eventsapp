package droidsector.tech.eventsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EventTaskActivity extends AppCompatActivity {
    String eventid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_task);
        Intent intent = getIntent();
        eventid = intent.getStringExtra("eventid");
    }

    private class FetchTasks extends AsyncTask<Void,Void,Void> {
        String webPage = "";
        String baseUrl = "https://whhc.in/aaa/eventsbuddy/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getApplicationContext(), "Please Wait!", "Fetching Tasks");
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
                    outerLinearLayout.setPadding(0,32,0,32);
                    LinearLayout linearLayout = new LinearLayout(context);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setPadding(32,8,32,8);
                    linearLayout.setBackgroundColor(Color.WHITE);
                    linearLayout.setLayoutParams(matchParams);
                    TextView nameTV = new TextView(context);
                    nameTV.setTextSize(22);
                    nameTV.setLayoutParams(matchParams);
                    nameTV.setText(taskName);
                    linearLayout.addView(nameTV);
                    TextView descriptionTV = new TextView(context);
                    descriptionTV.setLayoutParams(matchParams);
                    descriptionTV.setText(description);
                    linearLayout.addView(descriptionTV);
                    TextView completedTV = new TextView(context);
                    completedTV.setLayoutParams(matchParams);
                    completedTV.setText("Status : " + (completed.equals("y")?"Completed":"Pending"));
                    linearLayout.addView(completedTV);
                    outerLinearLayout.addView(linearLayout);
                    data.addView(outerLinearLayout);
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
                }
            }
        }
    }

}
