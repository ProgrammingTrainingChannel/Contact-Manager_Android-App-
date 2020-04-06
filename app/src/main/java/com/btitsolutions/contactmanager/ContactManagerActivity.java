package com.btitsolutions.contactmanager;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SearchView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactManagerActivity extends AppCompatActivity {

    Context context;

    // ArrayList
    ArrayList<SelectContact> selectUsers;
    List<SelectContact> temp;
    // Contact List
    ListView listView;
    // Cursor to load contacts list
    Cursor phones, email;

    // Pop up
    ContentResolver resolver;
    SelectContactAdapter adapter;
    private InterstitialAd mInterstitialAd;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_backup_all_contacts:
                    OpenEmailDestinationDialog(true);
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    else {
                        //
                    }
                    break;
                case R.id.navigation_backup_selected_contacts:
                    OpenEmailDestinationDialog(false);
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    else {
                        //
                    }
                    break;
            }
            return false;
        }

    };

    public void OpenEmailDestinationDialog(final Boolean IsAllSelected) {
        final Dialog dialog = new Dialog(context); // Context, this, etc.
        dialog.setContentView(R.layout.email_destination_dialog);
        //dialog.setTitle(R.string.email_destination_confirmation);
        dialog.show();

        final EditText txtEmailDestination = (EditText) dialog.findViewById(R.id.txtEmailDestination);
        SharedPreferences sharedPreferences = this.getPreferences(MODE_PRIVATE);
        String EmailDestination = sharedPreferences.getString("EmailDestination", "");
        txtEmailDestination.setText(EmailDestination);

//        Toast.makeText(this, adapter.selectedUsers.get(0).getPhone(), Toast.LENGTH_LONG).show();

        Button btnDialogContinue = (Button) dialog.findViewById(R.id.btnDialogContinue);
        btnDialogContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtEmailDestination.getText().toString().equals(""))
                {
                    Toast.makeText(context, "Please enter destination email", Toast.LENGTH_LONG).show();
                }
                else
                {
                    String contactsList = "";

                    try{
                        if(IsAllSelected){
                            for (int i=0; i < selectUsers.size(); i++)
                            {
                                contactsList = contactsList + (i+1) + ") " + selectUsers.get(i).getName() + " ---> " + selectUsers.get(i).getPhone() + " \n";
                            }
                        }
                        else{
                            for (int i=0; i < adapter.selectedUsers.size(); i++)
                            {
                                contactsList = contactsList + (i+1) + ") " + adapter.selectedUsers.get(i).getName() + " ---> " + adapter.selectedUsers.get(i).getPhone() + " \n";
                            }
                        }

                        SendEmail(txtEmailDestination.getText().toString(), contactsList);
                    }
                    catch(Exception ex){
                        return;
                    }
                }
            }
        });

        Button btnDialogCancel = (Button) dialog.findViewById(R.id.btnDialogCancel);
        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    protected void SendEmail(String emailDestination, String emailContent) {
        String[] TO = {emailDestination};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contacts Backup - ");
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailContent);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void LoadContacts(){
        selectUsers = new ArrayList<SelectContact>();
        resolver = this.getContentResolver();
        listView = (ListView) findViewById(R.id.contact_list);

        phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        LoadContact loadContact = new LoadContact();
        loadContact.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_manager);

        context = this;
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                LoadContacts();
            }
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1011);//101010 is random number to use for callback
        }

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8168171128315421/6716141072");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1011: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LoadContacts();
                }
            }
        }
    }

    // Load data on background
    private class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            if (phones != null) {
                Log.e("count", "" + phones.getCount());
                if (phones.getCount() == 0) {
                    Toast.makeText(ContactManagerActivity.this, "No contacts in your contact list.", Toast.LENGTH_LONG).show();
                }

                while (phones.moveToNext()) {
                    Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String EmailAddr = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA2));
                    String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                    try {
                        if (image_thumb != null) {
                            bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
                        } else {
                            Log.e("No Image Thumb", "--------------");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    SelectContact selectUser = new SelectContact();
                    //selectUser.setThumb(bit_thumb);
                    selectUser.setName(name);
                    selectUser.setPhone(phoneNumber);
                    selectUser.setEmail(id);
                    selectUsers.add(selectUser);
                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
            //phones.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new SelectContactAdapter(selectUsers, ContactManagerActivity.this);
            listView.setAdapter(adapter);

            // Select item on listclick
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Log.e("search", "here---------------- listener");

                    SelectContact data = selectUsers.get(i);
                }
            });

            listView.setFastScrollEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        phones.close();
    }
}
