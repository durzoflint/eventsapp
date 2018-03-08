package droidsector.tech.eventsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        Intent intent = getIntent();
        final String number = intent.getStringExtra("number");
        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nameEditText = findViewById(R.id.name);
                String name = nameEditText.getText().toString();
                if (!name.isEmpty())
                {
                    new RegisterUser().execute(name, number);
                }
            }
        });
    }

    private class RegisterUser extends AsyncTask<String,Void,Void> {
        String webPage = "",name="", number = "";
        String baseUrl = "https://whhc.in/aaa/eventsbuddy/";
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
                url = new URL(baseUrl+"registeruser.php?name="+name+"&number="+number);
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
                startActivity(intent);
            }
            else
                Toast.makeText(RegisterUserActivity.this, "Some error occurred:"+webPage, Toast.LENGTH_LONG).show();
        }
    }

}
