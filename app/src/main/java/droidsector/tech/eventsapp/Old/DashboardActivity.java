package droidsector.tech.eventsapp.Old;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

public class
DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String userid, number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
        number = intent.getStringExtra("number");
    }

    @Override
    protected void onResume() {
        super.onResume();
        CoordinatorLayout coordinatorLayout = findViewById(R.id.include);
        LinearLayout adminEvents = coordinatorLayout.findViewById(R.id.adminevents);
        adminEvents.removeAllViews();
        LinearLayout teamEvents = coordinatorLayout.findViewById(R.id.teamevents);
        teamEvents.removeAllViews();
        new FetchEvent().execute();
        new CheckForInvitations().execute(number.substring(3));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addevent) {
            Intent intent = new Intent(DashboardActivity.this, AddEventActivity.class);
            intent.putExtra("userid", userid);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class FetchEvent extends AsyncTask<Void,Void,Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        @Override
        protected Void doInBackground(Void... voids){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"fetchevents.php?userid="+userid;
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
            if(!webPage.isEmpty())
            {
                CoordinatorLayout coordinatorLayout = findViewById(R.id.include);
                LinearLayout adminEvents = coordinatorLayout.findViewById(R.id.adminevents);
                LinearLayout teamEvents = coordinatorLayout.findViewById(R.id.teamevents);
                adminEvents.removeAllViews();
                teamEvents.removeAllViews();
                LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams matchParams = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                final Context context = DashboardActivity.this;
                while (webPage.contains("<br>"))
                {
                    int brI = webPage.indexOf("<br>");
                    final String category = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String id = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String name = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String description = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String location = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String from = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String to = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String teamMemberCount = webPage.substring(0, brI);
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
                    nameTV.setLayoutParams(wrapParams);
                    nameTV.setText(name);
                    linearLayout.addView(nameTV);
                    TextView fromTV = new TextView(context);
                    fromTV.setLayoutParams(wrapParams);
                    fromTV.setText("Date : " + from.substring(0, from.indexOf(':') - 3));
                    linearLayout.addView(fromTV);
                    outerLinearLayout.addView(linearLayout);
                    if (category.equals("admin"))
                    {
                        TextView adminTV = coordinatorLayout.findViewById(R.id.admintv);
                        adminTV.setVisibility(View.VISIBLE);
                        adminEvents.addView(outerLinearLayout);
                        adminEvents.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        TextView teamTV = coordinatorLayout.findViewById(R.id.teamtv);
                        teamTV.setVisibility(View.VISIBLE);
                        teamEvents.addView(outerLinearLayout);
                        teamEvents.setVisibility(View.VISIBLE);
                    }
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, EventDetailsActivity.class);
                            intent.putExtra("userid",userid);
                            intent.putExtra("teamid",id);
                            intent.putExtra("name",name);
                            intent.putExtra("description",description);
                            intent.putExtra("location",location);
                            intent.putExtra("category",category);
                            intent.putExtra("from",from);
                            intent.putExtra("to",to);
                            intent.putExtra("teamMemberCount",teamMemberCount);
                            startActivity(intent);
                        }
                    });
                }
            }
        }
    }

    private class CheckForInvitations extends AsyncTask<String, Void, Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";

        @Override
        protected Void doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                String myURL = baseUrl + "checkinvitation.php?number=" + strings[0];
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
                final Context context = DashboardActivity.this;
                while (webPage.contains("<br>")) {
                    int brI = webPage.indexOf("<br>");
                    final String id = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String eventId = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String name = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String description = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String location = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String from = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String to = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String teamMemberCount = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    new AlertDialog.Builder(context)
                            .setTitle("Team Member Invitation")
                            .setMessage("Event Name: " + name + "\nDescription: " + description + "\nLocation: " + location +
                                    "\nFrom: " + from + "\nTo: " + to)
                            .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new InvitationResponse().execute(id, "y", userid);
                                }
                            })
                            .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new InvitationResponse().execute(id, "n", userid);
                                }
                            })
                            .create().show();
                }
            }
        }
    }

    private class InvitationResponse extends AsyncTask<String, Void, Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";

        @Override
        protected Void doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                String myURL = baseUrl + "invitationresponse.php?invitationid=" + strings[0] +
                        "&response=" + strings[1] + "&memberid=" + strings[2];
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
            if (webPage.equals("success")) {
                Toast.makeText(DashboardActivity.this, "Response Recorded Successfully", Toast.LENGTH_SHORT).show();
                new FetchEvent().execute();
            }
            else
                Toast.makeText(DashboardActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }
}
