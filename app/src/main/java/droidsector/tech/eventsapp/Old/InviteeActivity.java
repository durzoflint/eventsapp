package droidsector.tech.eventsapp.Old;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
}
