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

import droidsector.tech.eventsapp.R;

public class ShoppingListActivity extends AppCompatActivity {
    String eventid = "", categoryUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        setTitle("Shopping List");
        Intent intent = getIntent();
        eventid = intent.getStringExtra("eventid");
        categoryUser = intent.getStringExtra("category");
        Button addTask = findViewById(R.id.addtasks);
        Button addmajor = findViewById(R.id.addmajor);
        if (categoryUser.equals("admin")) {
            addTask.setVisibility(View.VISIBLE);
            addmajor.setVisibility(View.VISIBLE);
        }
        addmajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(ShoppingListActivity.this);
                final View addTaskLayout = inflater.inflate(R.layout.layout_add_item, null);
                TextView categoryTV = addTaskLayout.findViewById(R.id.category);
                final Spinner categoryText = addTaskLayout.findViewById(R.id.itemcategory);
                categoryTV.setVisibility(View.GONE);
                categoryText.setVisibility(View.GONE);
                final EditText costText = addTaskLayout.findViewById(R.id.cost);
                costText.setHint("Cost");
                final EditText quantityText = addTaskLayout.findViewById(R.id.quantity);
                quantityText.setVisibility(View.GONE);
                new AlertDialog.Builder(ShoppingListActivity.this)
                        .setTitle("Add Item")
                        .setIcon(android.R.drawable.ic_menu_agenda)
                        .setView(addTaskLayout)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText nameText = addTaskLayout.findViewById(R.id.itemname);
                                String name = nameText.getText().toString();
                                String cost = costText.getText().toString();
                                if (!name.isEmpty() && !cost.isEmpty()) {
                                    new AddItem().execute(name, "major", "1", cost);
                                } else
                                    Toast.makeText(ShoppingListActivity.this, "Details cannot be empty", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .create().show();
            }
        });
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(ShoppingListActivity.this);
                final View addTaskLayout = inflater.inflate(R.layout.layout_add_item, null);
                new AlertDialog.Builder(ShoppingListActivity.this)
                        .setTitle("Add Item")
                        .setIcon(android.R.drawable.ic_menu_agenda)
                        .setView(addTaskLayout)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Spinner categoryText = addTaskLayout.findViewById(R.id.itemcategory);
                                EditText nameText = addTaskLayout.findViewById(R.id.itemname);
                                EditText quantityText = addTaskLayout.findViewById(R.id.quantity);
                                EditText costText = addTaskLayout.findViewById(R.id.cost);
                                String name = nameText.getText().toString();
                                String category = categoryText.getSelectedItem().toString();
                                String quantity = quantityText.getText().toString();
                                String cost = costText.getText().toString();
                                if (!name.isEmpty() && !quantity.isEmpty() && !cost.isEmpty())
                                {
                                    new AddItem().execute(name, category, quantity, cost);
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
                        + "&category=" + strings[1] + "&quantity=" + strings[2] + "&cost=" + strings[3];
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

    private class RemoveItem extends AsyncTask<String, Void, Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ShoppingListActivity.this, "Please Wait!", "Removing Item");
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
                Toast.makeText(ShoppingListActivity.this, "Item Removed Successfully", Toast.LENGTH_SHORT).show();
                onResume();
            } else
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
                boolean sameCategory;
                String previousCategory = "";
                int previousCost = 0;
                TextView previousName = null, previousCostTV = null;
                LinearLayout data = findViewById(R.id.data);
                LinearLayout majorData = findViewById(R.id.majordata);
                majorData.removeAllViews();
                data.removeAllViews();
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
                    final String category = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String quantity = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String cost = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String bought = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    sameCategory = category.equals(previousCategory);
                    previousCategory = category;
                    if (category.equals("major")) {
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
                        TextView categoryTV = new TextView(context);
                        categoryTV.setLayoutParams(wrapParams);
                        categoryTV.setText("Category : " + category);
                        linearLayout.addView(categoryTV);
                        TextView costTV = new TextView(context);
                        costTV.setLayoutParams(wrapParams);
                        costTV.setText("Cost : " + cost);
                        linearLayout.addView(costTV);
                        TextView completedTV = new TextView(context);
                        completedTV.setLayoutParams(wrapParams);
                        final int viewId = View.generateViewId();
                        completedTV.setId(viewId);
                        completedTV.setText("Status : " + (bought.equals("y") ? "Bought" : "Pending"));
                        linearLayout.addView(completedTV);
                        outerLinearLayout.addView(linearLayout);
                        majorData.addView(outerLinearLayout);
                        linearLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(ShoppingListActivity.this);
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
                    } else {
                        if (sameCategory) {
                            previousName.setText(previousName.getText() + ", " + itemName);
                            previousCost = previousCost + Integer.parseInt(cost) * Integer.parseInt(quantity);
                            previousCostTV.setText("Cost : " + previousCost);
                        } else {
                            LinearLayout outerLinearLayout = new LinearLayout(context);
                            outerLinearLayout.setLayoutParams(matchParams);
                            outerLinearLayout.setPadding(0, 16, 0, 16);
                            LinearLayout linearLayout = new LinearLayout(context);
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            linearLayout.setPadding(32, 16, 32, 16);
                            linearLayout.setBackgroundColor(Color.WHITE);
                            linearLayout.setLayoutParams(matchParams);
                            TextView categoryTV = new TextView(context);
                            categoryTV.setLayoutParams(wrapParams);
                            categoryTV.setTextSize(22);
                            categoryTV.setText(category);
                            linearLayout.addView(categoryTV);
                            TextView nameTV = new TextView(context);
                            nameTV.setLayoutParams(wrapParams);
                            nameTV.setText("Items : " + itemName);
                            linearLayout.addView(nameTV);
                            int finalCost = Integer.parseInt(quantity) * Integer.parseInt(cost);
                            TextView costTV = new TextView(context);
                            costTV.setLayoutParams(wrapParams);
                            costTV.setText("Cost : " + finalCost);
                            linearLayout.addView(costTV);
                            outerLinearLayout.addView(linearLayout);
                            data.addView(outerLinearLayout);
                            previousName = nameTV;
                            previousCost = finalCost;
                            previousCostTV = costTV;
                            linearLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(ShoppingListActivity.this, ShoppingCategoryActivity.class);
                                    intent.putExtra("eventid", eventid);
                                    intent.putExtra("category", category);
                                    intent.putExtra("categoryuser", categoryUser);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
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