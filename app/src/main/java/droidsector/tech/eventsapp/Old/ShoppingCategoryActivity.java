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
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import droidsector.tech.eventsapp.R;

public class ShoppingCategoryActivity extends AppCompatActivity {
    String eventid = "", category = "", categoryUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_category);
        Intent intent = getIntent();
        eventid = intent.getStringExtra("eventid");
        category = intent.getStringExtra("category");
        categoryUser = intent.getStringExtra("categoryuser");
    }

    @Override
    protected void onResume() {
        super.onResume();
        new FetchItems().execute();
    }

    private class RemoveItem extends AsyncTask<String, Void, Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ShoppingCategoryActivity.this, "Please Wait!", "Removing Item");
        }

        @Override
        protected Void doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                String myURL = baseUrl + "removeitem.php?itemid=" + strings[0];
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
                Toast.makeText(ShoppingCategoryActivity.this, "Item Removed Successfully", Toast.LENGTH_SHORT).show();
                onResume();
            } else
                Toast.makeText(ShoppingCategoryActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    private class FetchItems extends AsyncTask<Void, Void, Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ShoppingCategoryActivity.this, "Please Wait!", "Fetching Cart");
            LinearLayout data = findViewById(R.id.data);
            data.removeAllViews();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                String myURL = baseUrl + "fetchitemsbycategory.php?eventid=" + eventid + "&category=" + category;
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
                Log.d("Abhinav", myURL);
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
            Log.d("Abhinav", webPage);
            if (!webPage.isEmpty()) {
                LinearLayout data = findViewById(R.id.data);
                data.removeAllViews();
                LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams matchParams = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                final Context context = ShoppingCategoryActivity.this;
                while (webPage.contains("<br>")) {
                    int brI = webPage.indexOf("<br>");
                    final String itemId = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String itemName = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String quantity = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String cost = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String bought = webPage.substring(0, brI);
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
                    nameTV.setText(itemName);
                    linearLayout.addView(nameTV);
                    TextView quantityTV = new TextView(context);
                    quantityTV.setLayoutParams(wrapParams);
                    quantityTV.setText("Quantity : " + quantity);
                    linearLayout.addView(quantityTV);
                    TextView costTV = new TextView(context);
                    costTV.setLayoutParams(wrapParams);
                    costTV.setText("Cost per Unit : " + cost);
                    linearLayout.addView(costTV);
                    TextView completedTV = new TextView(context);
                    completedTV.setLayoutParams(wrapParams);
                    final int viewId = View.generateViewId();
                    completedTV.setId(viewId);
                    completedTV.setText("Status : " + (bought.equals("y") ? "Bought" : "Pending"));
                    linearLayout.addView(completedTV);
                    outerLinearLayout.addView(linearLayout);
                    data.addView(outerLinearLayout);
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(ShoppingCategoryActivity.this);
                            dialog
                                    .setTitle("Confirm Completion")
                                    .setMessage("Are you sure that the task has been completed?")
                                    .setIcon(android.R.drawable.ic_menu_agenda)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            new BoughtItem().execute(itemId, viewId + "");
                                        }
                                    });
                            if (categoryUser.equals("admin")) {
                                dialog
                                        .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                new RemoveItem().execute(itemId);
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

    private class BoughtItem extends AsyncTask<String, Void, Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;
        TextView textView;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ShoppingCategoryActivity.this, "Please Wait!", "Adding Task");
        }

        @Override
        protected Void doInBackground(String... strings) {
            textView = findViewById(Integer.parseInt(strings[1]));
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                String myURL = baseUrl + "boughtitem.php?itemid=" + strings[0];
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
                textView.setText("Status : Bought");
            } else
                Toast.makeText(ShoppingCategoryActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }
}