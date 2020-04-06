package com.btitsolutions.contactmanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SMSSettingActivity extends AppCompatActivity {
    ListView sms_contact_list;
    Context context;

    private static final int CONTACT_PICKER_RESULT = 1001;
    private static final String DEBUG_TAG = "Contact List";
    private static final int RESULT_OK = -1;

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
        setContentView(R.layout.activity_smssetting);

        context = this;

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        RefreshContactList();
    }

    public void ClearLogs()
    {
        try{
            DBHelper dbHelper = new DBHelper(context);

            List<CallSMSDetailModel> callSMSDetailModelList = dbHelper.getAllCallSmsDetails("SMS");

            for(int i=0; i < callSMSDetailModelList.size(); i++)
            {
                if(callSMSDetailModelList.get(i) != null)
                {
                    if (callSMSDetailModelList.get(i).getIsCallCleared().equals("True"))
                    {
                        if (callSMSDetailModelList.get(i).getIsAlwaysCallCleared().equals("True"))
                        {
                            String phoneNumber = dbHelper.getSMSContact(callSMSDetailModelList.get(i).getDisplayName()).getPhoneNumber();
                            DeleteSMSLogByNumber(phoneNumber);
                        }
                        else
                        {
                            String thatDate = callSMSDetailModelList.get(i).getCreatedDate();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                            Date date = null;
                            try {
                                date = formatter.parse(thatDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            Calendar createdDate = Calendar.getInstance();
                            createdDate.setTime(date);

                            Calendar currentDate = Calendar.getInstance();

                            long difference = (currentDate.getTimeInMillis() - createdDate.getTimeInMillis()) / (24 * 60 * 60 * 1000);
                            int numberOfDays = Integer.parseInt(callSMSDetailModelList.get(i).getCallClearedForHowLong());

                            if (difference > numberOfDays) {
                                String phoneNumber = dbHelper.getSMSContact(callSMSDetailModelList.get(i).getDisplayName()).getPhoneNumber();
                                DeleteSMSLogByNumber(phoneNumber);
                            }
                        }
                    }
                }
            }
        }
        catch(Exception ex){
            Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
        }
    }

    private void DeleteSMSLogByNumber(String fromAddress) {

        Uri uriSMS = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(uriSMS, null,
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            if(cursor.getCount() > 0){
                int ThreadId = cursor.getInt(1);
                String address = cursor.getString(2);
                context.getContentResolver().delete(Uri.
                                parse("content://sms/conversations/"), "address=?",
                        new String[]{fromAddress});
            }
            cursor.close();
        }
    }

    public void removeMessage(String number) {
        try {
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = context.getContentResolver().query(uriSms,
                    new String[] { "_id", "thread_id", "address",
                            "person", "date", "body" }, null, null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);

                    if (address.equals(number)) {
                        context.getContentResolver().delete(
                                Uri.parse("content://sms/" + id), null, null);
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
        }
    }

    private void RefreshContactList()
    {
        DBHelper dbHelper = new DBHelper(this);
        List<SMSContactModel> smsContactModels = dbHelper.getAllSMSContacts();
        SMSContactAdapter smsContactAdapter = new SMSContactAdapter(this,smsContactModels);
        sms_contact_list = (ListView)findViewById(R.id.sms_contact_list);
        sms_contact_list.setAdapter(smsContactAdapter);
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
                            SMSContactModel smsContactModel = new SMSContactModel();
                            smsContactModel.setDisplayName(displayName);
                            smsContactModel.setPhoneNumber(phoneNumber);

                            DBHelper dbHelper = new DBHelper(this);

                            //if(dbHelper.IsCallContactExist(displayName) == false)
                            //{
                            dbHelper.addSMSContact(smsContactModel);
                            RefreshContactList();
                            //}
                        }
                        else{
                            Toast.makeText(this, "The selected contact doesn't have a phone number.", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }
        } else {
            // gracefully handle failure
            Toast.makeText(this, "Contact Not Selected", Toast.LENGTH_LONG).show();
        }
    }

}
