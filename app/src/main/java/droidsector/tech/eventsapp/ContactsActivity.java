package droidsector.tech.eventsapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ContactsActivity extends AppCompatActivity {

    private static final String TAG = "Abhinav";
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;     // contacts unique ID
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
    }

    public void onClickSelectContact(View btnSelectContact) {

        // using native contacts selection
        // Intent.ACTION_PICK = Pick an item from the data, returning what was selected.
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();

            //retrieveContactName();
            retrieveContactNumbers();
            //retrieveContactPhoto();

        }
    }

    private void retrieveContactNumbers() {

        String contactNumber = null;
        // getting contacts ID
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
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Using the contact ID now we will get contact phone number
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

            while (cursorPhone.moveToNext()){
                contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                Log.d(TAG, "Contact Phone Number: " + contactNumber);
            }
            cursorPhone.close();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                retrieveContactNumbers();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

}