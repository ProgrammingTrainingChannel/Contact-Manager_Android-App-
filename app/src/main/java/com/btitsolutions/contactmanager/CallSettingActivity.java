package com.btitsolutions.contactmanager;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CallSettingActivity extends AppCompatActivity implements View.OnClickListener {
    ListView call_contact_list;
    Context context;

    private static final int CONTACT_PICKER_RESULT = 1001;
    private static final String DEBUG_TAG = "Contact List";
    private static final int RESULT_OK = -1;
    private InterstitialAd mInterstitialAd;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_add_contact:
                    OpenContactList();
                    return true;
                case R.id.navigation_clear_log:
                    ClearLogs();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_setting);

        context = this;

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mInterstitialAd = new InterstitialAd(this);
        //ca-app-pub-3940256099942544/1033173712 -- test ad unit id
        mInterstitialAd.setAdUnitId("ca-app-pub-8168171128315421/6729826789");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        RefreshContactList();
    }

    public void DeleteCallLogByNumber(String numberTag) {
        char[] number = numberTag.trim().toCharArray();
        String n = "%";

        if(number.length <= 10) {
            for (int i = 1; i < number.length; i++) {
                if(number[i] != ' '){
                    n = n + (number[i] + "%");
                }
            }
        }
        else{
            int starter = number.length - 9;
            for (int i = starter; i < number.length; i++) {
                if(number[i] != ' '){
                    n = n + (number[i] + "%");
                }
            }
        }

        String queryString = CallLog.Calls.NUMBER.trim() + " LIKE '" + n + "'";
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.getContentResolver().delete(CallLog.Calls.CONTENT_URI, queryString, null);
    }

    public Date DateFormatter(String dateString) {
        DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        Date date = null;
        try {
            date = (Date)formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String formatedDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" +         cal.get(Calendar.YEAR);

        return date;
    }

    public void ClearLogs() {
        DBHelper dbHelper = new DBHelper(context);

        List<CallSMSDetailModel> callSMSDetailModelList = dbHelper.getAllCallSmsDetails("CALL");

        for(int i=0; i < callSMSDetailModelList.size(); i++)
        {
            if(callSMSDetailModelList.get(i) != null)
            {
                if (callSMSDetailModelList.get(i).getIsCallCleared().equals("True"))
                {
                    if (callSMSDetailModelList.get(i).getIsAlwaysCallCleared().equals("True"))
                    {
                        String phoneNumber = dbHelper.getCallContact(callSMSDetailModelList.get(i).getDisplayName()).getPhoneNumber();
                        DeleteCallLogByNumber(phoneNumber);
                    }
                    else
                    {
                        String thatDate = callSMSDetailModelList.get(i).getCreatedDate();
                        //Date expiredDate = stringToDate(date, "EEE MMM d HH:mm:ss zz yyyy");
                        Date date = null;
                        date = DateFormatter(thatDate);

                        Calendar createdDate = Calendar.getInstance();
                        createdDate.setTime(date);

                        Calendar currentDate = Calendar.getInstance();
                        long dateDiffInMillSeconds = (currentDate.getTimeInMillis() - createdDate.getTimeInMillis());
                        long difference = dateDiffInMillSeconds / (24 * 60 * 60 * 1000);
                        int numberOfDays = Integer.parseInt(callSMSDetailModelList.get(i).getCallClearedForHowLong());

                        if (difference > numberOfDays) {
                            String phoneNumber = dbHelper.getCallContact(callSMSDetailModelList.get(i).getDisplayName()).getPhoneNumber();
                            DeleteCallLogByNumber(phoneNumber);
                        }
                    }
                }
            }
        }

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        else {
            //
        }
    }

    public void RefreshContactList() {
        DBHelper dbHelper = new DBHelper(this);
        List<CallContactModel> callContactModels = dbHelper.getAllCallContacts();
        CallContactAdapter callContactAdapter = new CallContactAdapter(this, callContactModels);
        call_contact_list = (ListView)findViewById(R.id.call_contact_list);
        call_contact_list.setAdapter(callContactAdapter);
    }

    // a method to open your contact list
    private void OpenContactList() {
        Intent it = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(it, CONTACT_PICKER_RESULT);
    }

    // handle after selecting a contact from the list
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    // handle contact results
                    Uri contactData = data.getData();
                    Cursor c =  managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        String displayName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String phoneNumber = "Not Found";

                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (hasPhone.equalsIgnoreCase("1"))
                            hasPhone = "true";
                        else
                            hasPhone = "false";

                        if (Boolean.parseBoolean(hasPhone)) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                            + " = " + contactId, null, null);
                            if (phones != null) {
                                while (phones.moveToNext()) {
                                    phoneNumber = phones
                                            .getString(phones
                                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                }
                                phones.close();
                            }
                        }

                        if(phoneNumber != "Not Found")
                        {
                            CallContactModel callContactModel = new CallContactModel();
                            callContactModel.setDisplayName(displayName);
                            callContactModel.setPhoneNumber(phoneNumber);

                            DBHelper dbHelper = new DBHelper(this);

                            //if(dbHelper.IsCallContactExist(displayName) == false)
                            //{
                                dbHelper.addCallContact(callContactModel);
                                RefreshContactList();
                            //}
                        }
                        else{
                            Toast.makeText(this, "The selected contact doesn't have a phone number.", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
            else {
                //
            }
        } else {
            // gracefully handle failure
            Toast.makeText(this, "Contact Not Selected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this, "Hello Update", Toast.LENGTH_SHORT).show();
    }
}
