package droidsector.tech.eventsapp.Old;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import droidsector.tech.eventsapp.R;

public class BudgetPlannerActivity extends AppCompatActivity {
    String eventid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_planner);
        Intent intent = getIntent();
        eventid = intent.getStringExtra("eventid");
    }

    @Override
    protected void onResume() {
        super.onResume();
        new FetchBudget().execute();
    }

    private class FetchBudget extends AsyncTask<Void, Void, Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(BudgetPlannerActivity.this, "Please Wait!", "Fetching Budget Details");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                String myURL = baseUrl + "fetchbudget.php?eventid=" + eventid;
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
                int brI = webPage.indexOf("<br>");
                int budget = Integer.parseInt(webPage.substring(0, brI));
                webPage = webPage.substring(brI + 4);
                brI = webPage.indexOf("<br>");
                int shopping = Integer.parseInt(webPage.substring(0, brI));
                webPage = webPage.substring(brI + 4);
                brI = webPage.indexOf("<br>");
                int major = Integer.parseInt(webPage.substring(0, brI));
                webPage = webPage.substring(brI + 4);
                TextView budgetTV = findViewById(R.id.budget);
                budgetTV.setText("Budget : " + budget);
                TextView majorTV = findViewById(R.id.major);
                majorTV.setText("Major Expenses : " + major);
                TextView shoppingTV = findViewById(R.id.shopping);
                shoppingTV.setText("Shopping Expenses : " + (shopping - major));
                TextView balanceTV = findViewById(R.id.balance);
                balanceTV.setText("Balance : " + (budget - shopping));
            }
        }
    }
}
