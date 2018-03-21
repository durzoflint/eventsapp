package droidsector.tech.eventsapp.Old;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import droidsector.tech.eventsapp.R;

public class RegisterUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        Intent intent = getIntent();
        final String number = intent.getStringExtra("number");
        SharedPreferences notifPrefs = getSharedPreferences("notifPrefs", MODE_PRIVATE);
        final String token = notifPrefs.getString("notifToken", "");
        Log.d("Abhinav", "In register activity : " + token);
        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nameEditText = findViewById(R.id.taskname);
                String name = nameEditText.getText().toString();
                if (!name.isEmpty())
                {
                    new RegisterUser().execute(name, number, token);
                }
            }
        });
    }

    private class RegisterUser extends AsyncTask<String,Void,Void> {
        String webPage = "",name="", number = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(RegisterUserActivity.this, "Please Wait!","Registration In Process!");
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... strings){
            name = strings[0];
            number = strings[1];
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl + "registeruser.php?name=" + name + "&number=" + number + "&token=" + strings[2];
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
            if(webPage.contains("success<br>"))
            {
                Toast.makeText(RegisterUserActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                int brI = webPage.indexOf("<br>");
                webPage = webPage.substring(brI+4);
                brI = webPage.indexOf("<br>");
                String id = webPage.substring(0, brI);
                Intent intent = new Intent(RegisterUserActivity.this, DashboardActivity.class);
                intent.putExtra("userid", id);
                intent.putExtra("number", number);
                startActivity(intent);
            }
            else
                Toast.makeText(RegisterUserActivity.this, "Some error occurred:"+webPage, Toast.LENGTH_LONG).show();
        }
    }

}
