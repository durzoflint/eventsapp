package droidsector.tech.eventsapp.Old;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import droidsector.tech.eventsapp.R;

public class EventDetailsActivity extends AppCompatActivity {
    String eventId, eventname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userid");
        eventId = intent.getStringExtra("teamid");
        eventname = intent.getStringExtra("name");
        String description = intent.getStringExtra("description");
        String location = intent.getStringExtra("location");
        final String category = intent.getStringExtra("category");
        String from = intent.getStringExtra("from");
        String to = intent.getStringExtra("to");
        String teamMemberCount = intent.getStringExtra("teamMemberCount");
        setTitle(eventname);
        TextView about = findViewById(R.id.about);
        about.setText(description);
        TextView date = findViewById(R.id.date);
        date.setText("Date : " + from);
        TextView venue = findViewById(R.id.venue);
        venue.setText("Venue : "+location);
        Button eventTasks = findViewById(R.id.eventtask);
        eventTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(EventDetailsActivity.this, EventTaskActivity.class);
                intent1.putExtra("category", category);
                intent1.putExtra("eventname", eventname);
                intent1.putExtra("eventid", eventId);
                startActivity(intent1);
            }
        });
        Button shoppingList = findViewById(R.id.shoppinglist);
        shoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(EventDetailsActivity.this, ShoppingListActivity.class);
                intent1.putExtra("category", category);
                intent1.putExtra("eventid", eventId);
                startActivity(intent1);
            }
        });
        Button teamMembers = findViewById(R.id.teammembers);
        teamMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(EventDetailsActivity.this, TeamMembersActivity.class);
                intent1.putExtra("category", category);
                intent1.putExtra("eventid", eventId);
                startActivity(intent1);
            }
        });
        Button inviteeList = findViewById(R.id.inviteelist);
        inviteeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(EventDetailsActivity.this, InviteeActivity.class);
                intent1.putExtra("eventid", eventId);
                intent1.putExtra("eventname", eventname);
                startActivity(intent1);
            }
        });
        Button budgetPlanner = findViewById(R.id.budgetplanner);
        budgetPlanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(EventDetailsActivity.this, BudgetPlannerActivity.class);
                intent1.putExtra("eventid", eventId);
                startActivity(intent1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.share) {
            LayoutInflater inflater = LayoutInflater.from(this);
            final View inviteeNameLayout = inflater.inflate(R.layout.layout_invitee_name, null);
            new AlertDialog.Builder(this)
                    .setTitle("Enter Details")
                    .setView(inviteeNameLayout)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText nameET = inviteeNameLayout.findViewById(R.id.name);
                            EditText numberET = inviteeNameLayout.findViewById(R.id.number);
                            String name = nameET.getText().toString();
                            String number = numberET.getText().toString();
                            new AddInvitee().execute(name, number);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    private class AddInvitee extends AsyncTask<String, Void, Void> {
        String webPage = "", name = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(EventDetailsActivity.this, "Please Wait!", "Generating Link");
        }

        @Override
        protected Void doInBackground(String... strings) {
            name = strings[0];
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                String myURL = baseUrl + "addinvitee.php?eventid=" + eventId + "&name=" + name + "&plusallowed=" + strings[1];
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
            if (webPage.contains("success<br>")) {
                Toast.makeText(EventDetailsActivity.this, "Link Generated Successfully", Toast.LENGTH_SHORT).show();
                int brI = webPage.indexOf("<br>");
                webPage = webPage.substring(brI + 4);
                brI = webPage.indexOf("<br>");
                String id = webPage.substring(0, brI);
                webPage = webPage.substring(brI + 4);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hey " + name + ", you have been invited to " + eventname + ". Click on this link to RSVP.\n" +
                        "http://eventsapp.co.in/eventsbuddy/index.php?id=" + id;
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        }
    }
}