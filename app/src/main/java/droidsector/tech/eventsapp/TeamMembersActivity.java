package droidsector.tech.eventsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class TeamMembersActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_members);
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
                if (!contactNumber.contains(" "))
                {
                    RadioGroup data = numberListLayout.findViewById(R.id.data);
                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setPadding(0,8,0,8);
                    radioButton.setText(contactNumber);
                    data.addView(radioButton);
                }
            }
            builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Number")
                    .setView(numberListLayout)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            RadioGroup data = numberListLayout.findViewById(R.id.data);
                            int id = data.getCheckedRadioButtonId();
                            if (id != -1)
                            {
                                RadioButton radioButton = data.findViewById(id);
                                sendToServerAndAddOnTheScreen(radioButton.getText().toString());
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create().show();
            cursorPhone.close();
        }
    }

    void sendToServerAndAddOnTheScreen(String number)
    {
        Log.d("Abhinav", number);
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
