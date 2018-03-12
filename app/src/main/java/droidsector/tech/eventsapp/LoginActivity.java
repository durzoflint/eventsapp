package droidsector.tech.eventsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import droidsector.tech.eventsapp.Old.DashboardActivity;
import droidsector.tech.eventsapp.Old.RegisterUserActivity;

public class LoginActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static FirebaseUser user;
    String userid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    onSignedInInitialize(user.getPhoneNumber());
                }
                else
                {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setLogo(R.mipmap.ic_launcher_round)
                                    .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.PhoneBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.logout:
                AuthUI.getInstance().signOut(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSignedInInitialize(String phoneNumber) {
        new CheckLogin().execute(phoneNumber);
    }

    private void onSignedOutCleanup() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN)
        {
            if (resultCode == RESULT_OK)
            {
                Toast.makeText(this, "Signed In!", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign In Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private class CheckLogin extends AsyncTask<String,Void,Void> {
        String webPage = "", number = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(LoginActivity.this, "Please Wait!","Validating!");
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... strings){
            number = strings[0];
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"firsttimelogin.php?number="+number;
                myURL = myURL.replaceAll(" ","%20");
                myURL = myURL.replaceAll("\\+","%2B");
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
            if(webPage.equals("y"))
            {
                Intent i = new Intent(LoginActivity.this, RegisterUserActivity.class);
                i.putExtra("number", number);
                startActivity(i);
            }
            else
            {
                new FetchUserId().execute(user.getPhoneNumber());
            }
        }
    }

    private class FetchUserId extends AsyncTask<String,Void,Void> {
        String webPage = "", number = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(LoginActivity.this, "Please Wait!","Validating!");
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... strings){
            number = strings[0];
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"fetchuserid.php?number="+number;
                myURL = myURL.replaceAll(" ","%20");
                myURL = myURL.replaceAll("\\+","%2B");
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
            if (!webPage.isEmpty())
            {
                userid = webPage;
                Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
                i.putExtra("userid", userid);
                startActivity(i);
            }
            else
            {
                Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}