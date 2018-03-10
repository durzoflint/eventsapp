package droidsector.tech.eventsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userid");
        final String eventId = intent.getStringExtra("teamid");
        String name = intent.getStringExtra("name");
        String description = intent.getStringExtra("description");
        String location = intent.getStringExtra("location");
        String category = intent.getStringExtra("category");
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
                intent1.putExtra("eventid", eventId);
                startActivity(intent1);
            }
        });
        Button shoppingList = findViewById(R.id.shoppinglist);
        shoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        Button teamMembers = findViewById(R.id.teammembers);
        teamMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EventDetailsActivity.this, TeamMembersActivity.class));
            }
        });
        Button inviteeList = findViewById(R.id.inviteelist);
        inviteeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }
}