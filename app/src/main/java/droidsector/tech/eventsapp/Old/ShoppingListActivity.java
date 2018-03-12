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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import droidsector.tech.eventsapp.R;

public class ShoppingListActivity extends AppCompatActivity {
    String eventid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        setTitle("Shopping List");
        Intent intent = getIntent();
        eventid = intent.getStringExtra("eventid");
        Button addTask = findViewById(R.id.addtasks);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(ShoppingListActivity.this);
                final View addTaskLayout = inflater.inflate(R.layout.layout_add_task, null);
                new AlertDialog.Builder(ShoppingListActivity.this)
                        .setTitle("Add Item")
                        .setIcon(android.R.drawable.ic_menu_agenda)
                        .setView(addTaskLayout)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText nameText = addTaskLayout.findViewById(R.id.taskname);
                                EditText descriptionText = addTaskLayout.findViewById(R.id.description);
                                EditText costText = addTaskLayout.findViewById(R.id.cost);
                                String name = nameText.getText().toString();
                                String description = descriptionText.getText().toString();
                                String cost = costText.getText().toString();
                                if (!name.isEmpty() && !description.isEmpty() && !cost.isEmpty())
                                {
                                    new AddItem().execute(name, description, cost);
                                }
                                else
                                    Toast.makeText(ShoppingListActivity.this, "Details cannot be empty", Toast.LENGTH_SHORT).show();
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
        new FetchItems().execute();
    }

    private class AddItem extends AsyncTask<String,Void,Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ShoppingListActivity.this, "Please Wait!", "Adding Item");
        }
        @Override
        protected Void doInBackground(String... strings){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"additem.php?eventid="+eventid+"&itemname="+strings[0]
                        +"&quantity="+strings[1]+"&cost="+strings[2];
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
                Toast.makeText(ShoppingListActivity.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();
                onResume();
            }
            else
                Toast.makeText(ShoppingListActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    private class FetchItems extends AsyncTask<Void,Void,Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ShoppingListActivity.this, "Please Wait!", "Fetching Cart");
            LinearLayout data = findViewById(R.id.data);
            data.removeAllViews();
        }
        @Override
        protected Void doInBackground(Void... voids){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"fetchitems.php?eventid="+eventid;
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
                Log.d("Abhinav", myURL);
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
                final Context context = ShoppingListActivity.this;
                while (webPage.contains("<br>"))
                {
                    int brI = webPage.indexOf("<br>");
                    final String itemId = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String itemName = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String quantity = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String cost = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String bought = webPage.substring(0, brI);
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
                    nameTV.setText(itemName);
                    linearLayout.addView(nameTV);
                    TextView descriptionTV = new TextView(context);
                    descriptionTV.setLayoutParams(wrapParams);
                    descriptionTV.setText("Quantity : " + quantity);
                    linearLayout.addView(descriptionTV);
                    TextView costTV = new TextView(context);
                    costTV.setLayoutParams(wrapParams);
                    costTV.setText("Cost : " + cost);
                    linearLayout.addView(costTV);
                    TextView completedTV = new TextView(context);
                    completedTV.setLayoutParams(wrapParams);
                    final int viewId = View.generateViewId();
                    completedTV.setId(viewId);
                    completedTV.setText("Status : " + (bought.equals("y")?"Bought":"Pending"));
                    linearLayout.addView(completedTV);
                    outerLinearLayout.addView(linearLayout);
                    data.addView(outerLinearLayout);
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new AlertDialog.Builder(ShoppingListActivity.this)
                                    .setTitle("Confirm Completion")
                                    .setMessage("Are you sure that the task has been completed?")
                                    .setIcon(android.R.drawable.ic_menu_agenda)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            new BoughtItem().execute(itemId, viewId+"");
                                        }
                                    })
                                    /*.setNeutralButton("Delete", null)
                                    * Todo: Get 'category' of the event and then add this button*/
                                    .setNegativeButton(android.R.string.no, null)
                                    .create().show();
                        }
                    });
                }
            }
        }
    }

    private class BoughtItem extends AsyncTask<String,Void,Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;
        TextView textView;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ShoppingListActivity.this, "Please Wait!", "Adding Task");
        }
        @Override
        protected Void doInBackground(String... strings){
            textView = findViewById(Integer.parseInt(strings[1]));
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"boughtitem.php?itemid="+strings[0];
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
                textView.setText("Status : Bought");
            }
            else
                Toast.makeText(ShoppingListActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }
}
