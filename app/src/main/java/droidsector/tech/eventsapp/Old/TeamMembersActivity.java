package droidsector.tech.eventsapp.Old;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import droidsector.tech.eventsapp.R;

public class TeamMembersActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    String eventid = "", category = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_members);
        Intent intent = getIntent();
        eventid = intent.getStringExtra("eventid");
        category = intent.getStringExtra("category");
        if (category.equals("admin")) {
            Button addTeamMember = findViewById(R.id.addteammember);
            addTeamMember.setVisibility(View.VISIBLE);
        }
    }

    public void onClickSelectContact(View btnSelectContact) {
        startActivityForResult(new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            uriContact = data.getData();
            retrieveContactNumbers();
        }
    }

    private void retrieveContactNumbers() {
        String contactNumber;
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);
        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }
        cursorID.close();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else {
            Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME +
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE +
                            ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK +
                            ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME +
                            ContactsContract.CommonDataKinds.Phone.TYPE_PAGER +
                            ContactsContract.CommonDataKinds.Phone.TYPE_OTHER +
                            ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK +
                            ContactsContract.CommonDataKinds.Phone.TYPE_CAR +
                            ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN +
                            ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX +
                            ContactsContract.CommonDataKinds.Phone.TYPE_RADIO +
                            ContactsContract.CommonDataKinds.Phone.TYPE_TELEX +
                            ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD +
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE +
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER +
                            ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT +
                            ContactsContract.CommonDataKinds.Phone.TYPE_MMS,
                    new String[]{contactID},
                    null);
            LayoutInflater inflater = LayoutInflater.from(this);
            final View numberListLayout = inflater.inflate(R.layout.layout_number_list, null);
            while (cursorPhone.moveToNext()){
                contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                //if (!contactNumber.contains(" "))
                {
                    contactNumber = contactNumber.replaceAll(" ", "");
                    RadioGroup data = numberListLayout.findViewById(R.id.data);
                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setPadding(0,8,0,8);
                    radioButton.setText(contactNumber);
                    data.addView(radioButton);
                }
            }
            new AlertDialog.Builder(this)
                    .setTitle("Select Number")
                    .setView(numberListLayout)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            RadioGroup data = numberListLayout.findViewById(R.id.data);
                            int id = data.getCheckedRadioButtonId();
                            if (id != -1)
                            {
                                RadioButton radioButton = data.findViewById(id);
                                new AddTeamMember().execute(radioButton.getText().toString());
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create().show();
            cursorPhone.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new FetchMembers().execute();
    }

    private class FetchMembers extends AsyncTask<Void,Void,Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(TeamMembersActivity.this, "Please Wait!", "Fetching Members");
            LinearLayout data = findViewById(R.id.members);
            data.removeAllViews();
        }
        @Override
        protected Void doInBackground(Void... voids){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"fetchteammembers.php?eventid="+eventid;
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
                LinearLayout data = findViewById(R.id.members);
                LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams matchParams = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                final Context context = TeamMembersActivity.this;
                while (webPage.contains("<br>"))
                {
                    int brI = webPage.indexOf("<br>");
                    final String memberId = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String memberName = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String phoneNumber = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String accepted = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    final String id = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    LinearLayout outerLinearLayout = new LinearLayout(context);
                    outerLinearLayout.setLayoutParams(matchParams);
                    outerLinearLayout.setPadding(0,16,0,16);
                    LinearLayout linearLayout = new LinearLayout(context);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setPadding(32,16,32,16);
                    linearLayout.setBackgroundColor(Color.WHITE);
                    linearLayout.setLayoutParams(matchParams);
                    TextView nameTV = new TextView(context);
                    nameTV.setTextSize(22);
                    nameTV.setLayoutParams(wrapParams);
                    nameTV.setText(memberName);
                    linearLayout.addView(nameTV);
                    TextView descriptionTV = new TextView(context);
                    descriptionTV.setLayoutParams(wrapParams);
                    descriptionTV.setText("Contact : " + phoneNumber);
                    linearLayout.addView(descriptionTV);
                    TextView costTV = new TextView(context);
                    costTV.setLayoutParams(wrapParams);
                    costTV.setText("Status : Accepted");
                    linearLayout.addView(costTV);
                    if (memberId.equals("0"))
                    {
                        nameTV.setVisibility(View.GONE);
                        descriptionTV.setText("Contact : " + accepted);
                        costTV.setText("Status : Pending");
                    }
                    if(accepted.equals("admin"))
                        costTV.setText("Status : Organiser");
                    if (category.equals("admin")) {
                        linearLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(TeamMembersActivity.this);
                                dialog
                                        .setTitle("Remove Team Member")
                                        .setMessage("Are you sure that the you want to remove the team member?")
                                        .setIcon(android.R.drawable.ic_menu_agenda)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                new RemoveTeamMember().execute(id);
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, null)
                                        .create().show();
                            }
                        });
                    }
                    outerLinearLayout.addView(linearLayout);
                    data.addView(outerLinearLayout);
                }
            }
        }
    }

    private class AddTeamMember extends AsyncTask<String,Void,Void> {
        String webPage = "", number = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(TeamMembersActivity.this, "Please Wait!","Validating!");
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... strings){
            number = strings[0];
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"addteammember.php?eventid="+eventid+"&number="+strings[0];
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
            if(webPage.equals("success"))
            {
                Toast.makeText(TeamMembersActivity.this, "Member Added Successfully", Toast.LENGTH_SHORT).show();
                onResume();
            }
            else
                Toast.makeText(TeamMembersActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    private class RemoveTeamMember extends AsyncTask<String, Void, Void> {
        String webPage = "";
        String baseUrl = "http://eventsapp.co.in/eventsbuddy/";
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(TeamMembersActivity.this, "Please Wait!", "Removing Team Member");
        }

        @Override
        protected Void doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                String myURL = baseUrl + "removeteammember.php?memberid=" + strings[0];
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
                Toast.makeText(TeamMembersActivity.this, "Member Removed Successfully", Toast.LENGTH_SHORT).show();
                onResume();
            } else
                Toast.makeText(TeamMembersActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                retrieveContactNumbers();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
