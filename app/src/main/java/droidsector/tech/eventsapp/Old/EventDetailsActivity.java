package droidsector.tech.eventsapp.Old;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import droidsector.tech.eventsapp.R;

public class EventDetailsActivity extends AppCompatActivity {
    String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userid");
        eventId = intent.getStringExtra("teamid");
        final String name = intent.getStringExtra("name");
        String description = intent.getStringExtra("description");
        String location = intent.getStringExtra("location");
        final String category = intent.getStringExtra("category");
        String from = intent.getStringExtra("from");
        String to = intent.getStringExtra("to");
        String teamMemberCount = intent.getStringExtra("teamMemberCount");
        setTitle(name);
        TextView about = findViewById(R.id.about);
        about.setText(description);
        TextView date = findViewById(R.id.date);
        date.setText("Date : "+from.substring(0, from.indexOf(' ')));
        TextView venue = findViewById(R.id.venue);
        venue.setText("Venue : "+location);
        Button eventTasks = findViewById(R.id.eventtask);
        eventTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(EventDetailsActivity.this, EventTaskActivity.class);
                intent1.putExtra("category", category);
                intent1.putExtra("eventname", name);
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
            //Todo: Get the name of the person and the number of people he/she is allowed to invite
            //Todo: Also add it in the database
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "http://eventsapp.co.in/eventsbuddy/index.php?id=" + eventId;
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }

        return super.onOptionsItemSelected(item);
    }
}