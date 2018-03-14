package droidsector.tech.eventsapp.Old;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import droidsector.tech.eventsapp.R;

public class InviteeActivity extends AppCompatActivity {
    String eventid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitee);
        Intent intent = getIntent();
        eventid = intent.getStringExtra("eventid");
    }

    @Override
    protected void onResume() {
        super.onResume();
        new FetchMembers().execute();
    }

    private class FetchMembers extends AsyncTask<Void, Void, Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(InviteeActivity.this, "Please Wait!", "Fetching Invitees");
            LinearLayout data = findViewById(R.id.members);
            data.removeAllViews();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                String myURL = baseUrl + "fetchinvitees.php?eventid=" + eventid;
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
            if (!webPage.isEmpty()) {
                LinearLayout data = findViewById(R.id.members);
                LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams matchParams = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                final Context context = InviteeActivity.this;
                while (webPage.contains("<br>")) {
                    int brI = webPage.indexOf("<br>");
                    final String name = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String number = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String plus = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String going = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    LinearLayout outerLinearLayout = new LinearLayout(context);
                    outerLinearLayout.setLayoutParams(matchParams);
                    outerLinearLayout.setPadding(0, 16, 0, 16);
                    LinearLayout linearLayout = new LinearLayout(context);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setPadding(32, 16, 32, 16);
                    linearLayout.setBackgroundColor(Color.WHITE);
                    linearLayout.setLayoutParams(matchParams);
                    TextView nameTV = new TextView(context);
                    nameTV.setTextSize(22);
                    nameTV.setLayoutParams(wrapParams);
                    nameTV.setText(name);
                    linearLayout.addView(nameTV);
                    TextView descriptionTV = new TextView(context);
                    descriptionTV.setLayoutParams(wrapParams);
                    descriptionTV.setText("Contact : " + number);
                    linearLayout.addView(descriptionTV);
                    TextView costTV = new TextView(context);
                    costTV.setLayoutParams(wrapParams);
                    costTV.setText("Status : " + going);
                    if (Integer.parseInt(plus) > 0 && going.equals("going"))
                        costTV.setText("Status : " + going + "(+" + plus + ")");
                    linearLayout.addView(costTV);
                    outerLinearLayout.addView(linearLayout);
                    data.addView(outerLinearLayout);
                }
            }
        }
    }
}
