package droidsector.tech.eventsapp;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Abhinav on 21-Mar-18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Abhinav", refreshedToken);
        SharedPreferences notifPrefs = getSharedPreferences("notifPrefs", MODE_PRIVATE);
        SharedPreferences.Editor notifPrefsEditor = notifPrefs.edit();
        notifPrefsEditor.putString("notifToken", refreshedToken);
        notifPrefsEditor.apply();
    }
}