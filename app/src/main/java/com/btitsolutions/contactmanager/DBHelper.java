package com.btitsolutions.contactmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactManagement";
    private static final String TABLE_CALL_SETTING = "callSetting";
    private static final String TABLE_SMS_SETTING = "smsSetting";
    private static final String TABLE_CALL_SMS_DETAIL = "callSmsDetail";

    private static final String KEY_DISPLAY_NAME = "displayName";
    private static final String KEY_PHONE_NUMBER = "phoneNumber";

    private static final String IS_CALL_OR_SMS = "IsCallOrSms";
    private static final String IS_BLOCKED = "IsBlocked";
    private static final String IS_ALWAYS_BLOCKED = "IsAlwaysBlocked";
    private static final String BLOCKED_FOR_HOW_LONG = "BlockedForHowLong";
    private static final String IS_CALL_CLEARED = "IsCallCleared";
    private static final String IS_ALWAYS_CALL_CLEARED = "IsAlwaysCallCleared";
    private static final String CALL_CLEARED_FOR_HOW_LONG = "CallClearedForHowLong";
    private static final String CREATED_DATE = "CreatedDate";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CALL_SETTING_TABLE = "CREATE TABLE " + TABLE_CALL_SETTING + "("
        + KEY_DISPLAY_NAME + " TEXT PRIMARY KEY,"
        + KEY_PHONE_NUMBER + " TEXT" + ")";

        db.execSQL(CREATE_CALL_SETTING_TABLE);

        String CREATE_SMS_SETTING_TABLE = "CREATE TABLE " + TABLE_SMS_SETTING + "("
                + KEY_DISPLAY_NAME + " TEXT PRIMARY KEY, "
                + KEY_PHONE_NUMBER + " TEXT" + ")";

        db.execSQL(CREATE_SMS_SETTING_TABLE);

        String CREATE_CALL_SMS_DETAIL_TABLE = "CREATE TABLE " + TABLE_CALL_SMS_DETAIL + "("
                + KEY_DISPLAY_NAME + " TEXT, "
                + IS_CALL_OR_SMS + " TEXT, "
                + IS_BLOCKED + " TEXT , "
                + IS_ALWAYS_BLOCKED + " TEXT , "
                + BLOCKED_FOR_HOW_LONG + " TEXT , "
                + IS_CALL_CLEARED + " TEXT , "
                + IS_ALWAYS_CALL_CLEARED + " TEXT , "
                + CALL_CLEARED_FOR_HOW_LONG + " TEXT, "
                + CREATED_DATE + " TEXT, "
                + " PRIMARY KEY ( " + KEY_DISPLAY_NAME + "," + IS_CALL_OR_SMS + " ) )";

        db.execSQL(CREATE_CALL_SMS_DETAIL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALL_SETTING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS_SETTING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALL_SMS_DETAIL);
        onCreate(db);
    }

    //for callContact table
    public void addCallContact(CallContactModel callContactModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DISPLAY_NAME, callContactModel.getDisplayName());
        values.put(KEY_PHONE_NUMBER, callContactModel.getPhoneNumber());

        db.insert(TABLE_CALL_SETTING, null, values);
        db.close();
    }

    public CallContactModel getCallContact(String displayName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CALL_SETTING, new String[] { KEY_DISPLAY_NAME,
                        KEY_PHONE_NUMBER }, KEY_DISPLAY_NAME + "=?",
                new String[] { String.valueOf(displayName) }, null, null, null, null);

        if (cursor != null)
        {
            cursor.moveToFirst();
        }

        CallContactModel callContact = new CallContactModel(cursor.getString(0), cursor.getString(1));
        return callContact;
    }

    public CallContactModel getCallContactByPhoneNumber(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CALL_SETTING, new String[] { KEY_DISPLAY_NAME,
                        KEY_PHONE_NUMBER }, KEY_PHONE_NUMBER + "=?",
                new String[] { String.valueOf(phoneNumber) }, null, null, null, null);

        if (cursor != null)
        {
            cursor.moveToFirst();
        }

        CallContactModel callContact = new CallContactModel(cursor.getString(0), cursor.getString(1));
        return callContact;
    }

    public boolean IsCallContactExist(String displayName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CALL_SETTING, new String[] { KEY_DISPLAY_NAME,
                        KEY_PHONE_NUMBER }, KEY_DISPLAY_NAME + "=?",
                new String[] { String.valueOf(displayName) }, null, null, null, null);

        return cursor != null;
    }

    public List<CallContactModel> getAllCallContacts() {
        List<CallContactModel> callContactModelList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_CALL_SETTING;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                CallContactModel callContactModel = new CallContactModel();
                callContactModel.setDisplayName(cursor.getString(0));
                callContactModel.setPhoneNumber(cursor.getString(1));

                callContactModelList.add(callContactModel);
            } while (cursor.moveToNext());
        }

        return callContactModelList;
    }

    public int getCallContactsCount() {
        String countQuery = "SELECT * FROM " + TABLE_CALL_SETTING;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public int updateCallContact(CallContactModel callContactModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DISPLAY_NAME, callContactModel.getDisplayName());
        values.put(KEY_PHONE_NUMBER, callContactModel.getPhoneNumber());

        return db.update(TABLE_CALL_SETTING, values, KEY_DISPLAY_NAME + " = ?",
                new String[]{String.valueOf(callContactModel.getDisplayName())});
    }

    public void deleteCallContact(CallContactModel callContactModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CALL_SETTING, KEY_DISPLAY_NAME + " = ?",
                new String[] { callContactModel.getDisplayName() });

        db.close();
    }

    //for smsContact table
    public void addSMSContact(SMSContactModel smsContactModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DISPLAY_NAME, smsContactModel.getDisplayName());
        values.put(KEY_PHONE_NUMBER, smsContactModel.getPhoneNumber());

        db.insert(TABLE_SMS_SETTING, null, values);
        db.close();
    }

    public SMSContactModel getSMSContact(String code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SMS_SETTING, new String[] { KEY_DISPLAY_NAME,
                        KEY_PHONE_NUMBER }, KEY_DISPLAY_NAME + "=?",
                new String[] { String.valueOf(code) }, null, null, null, null);

        if (cursor != null)
        {
            cursor.moveToFirst();
        }

        SMSContactModel smsContact = new SMSContactModel(cursor.getString(0), cursor.getString(1));
        return smsContact;
    }

    public List<SMSContactModel> getAllSMSContacts() {
        List<SMSContactModel> smsContactModelList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_SMS_SETTING;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                SMSContactModel smsContactModel = new SMSContactModel();
                smsContactModel.setDisplayName(cursor.getString(0));
                smsContactModel.setPhoneNumber(cursor.getString(1));

                smsContactModelList.add(smsContactModel);
            } while (cursor.moveToNext());
        }

        return smsContactModelList;
    }

    public int getSMSContactsCount() {
        String countQuery = "SELECT * FROM " + TABLE_SMS_SETTING;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public int updateSMSContact(SMSContactModel smsContactModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DISPLAY_NAME, smsContactModel.getDisplayName());
        values.put(KEY_PHONE_NUMBER, smsContactModel.getPhoneNumber());

        return db.update(TABLE_SMS_SETTING, values, KEY_DISPLAY_NAME + " = ?",
                new String[]{String.valueOf(smsContactModel.getDisplayName())});
    }

    public void deleteSMSContact(SMSContactModel smsContactModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SMS_SETTING, KEY_DISPLAY_NAME + " = ?",
                new String[] { smsContactModel.getDisplayName() });

        db.close();
    }

    //for callSmsDetail table
    public void addCallSmsDetail(CallSMSDetailModel callSMSDetailModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DISPLAY_NAME, callSMSDetailModel.getDisplayName());
        values.put(IS_CALL_OR_SMS, callSMSDetailModel.getIsCallOrSms());
        values.put(IS_BLOCKED, callSMSDetailModel.getIsBlocked());
        values.put(IS_ALWAYS_BLOCKED, callSMSDetailModel.getIsAlwaysBlocked());
        values.put(BLOCKED_FOR_HOW_LONG, callSMSDetailModel.getBlockedForHowLong());
        values.put(IS_CALL_CLEARED, callSMSDetailModel.getIsCallCleared());
        values.put(IS_ALWAYS_CALL_CLEARED, callSMSDetailModel.getIsAlwaysCallCleared());
        values.put(CALL_CLEARED_FOR_HOW_LONG, callSMSDetailModel.getCallClearedForHowLong());
        values.put(CREATED_DATE, callSMSDetailModel.getCreatedDate());

        db.insert(TABLE_CALL_SMS_DETAIL, null, values);
        db.close();
    }

    public CallSMSDetailModel getAllCallSmsDetailByDisplayName(String displayName, String IsCallOrSms) {
        try{
            List<CallSMSDetailModel> callContactModelList = new ArrayList<>();

            String selectQuery = "SELECT * FROM " + TABLE_CALL_SMS_DETAIL + " WHERE displayName = '" + displayName + "'";
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    CallSMSDetailModel callSMSDetailModel = new CallSMSDetailModel();
                    callSMSDetailModel.setDisplayName(cursor.getString(0));
                    callSMSDetailModel.setIsCallOrSms(cursor.getString(1));
                    callSMSDetailModel.setIsBlocked(cursor.getString(2));
                    callSMSDetailModel.setIsAlwaysBlocked(cursor.getString(3));
                    callSMSDetailModel.setBlockedForHowLong(cursor.getString(4));

                    callSMSDetailModel.setIsCallCleared(cursor.getString(5));
                    callSMSDetailModel.setIsAlwaysCallCleared(cursor.getString(6));
                    callSMSDetailModel.setCallClearedForHowLong(cursor.getString(7));
                    callSMSDetailModel.setCreatedDate(cursor.getString(8));

                    callContactModelList.add(callSMSDetailModel);
                } while (cursor.moveToNext());
            }

            for(int i=0; i < callContactModelList.size(); i++)
            {
                if(callContactModelList.get(i).getIsCallOrSms().equals(IsCallOrSms))
                {
                    return callContactModelList.get(i);
                }
            }

            return null;
        }
        catch(Exception ex){
            return null;
        }
    }

    public List<CallSMSDetailModel> getAllCallSmsDetails(String IsCallOrSms) {
        List<CallSMSDetailModel> callContactModelList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_CALL_SMS_DETAIL;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                CallSMSDetailModel callSMSDetailModel = new CallSMSDetailModel();
                callSMSDetailModel.setDisplayName(cursor.getString(0));
                callSMSDetailModel.setIsCallOrSms(cursor.getString(1));
                callSMSDetailModel.setIsBlocked(cursor.getString(2));
                callSMSDetailModel.setIsAlwaysBlocked(cursor.getString(3));
                callSMSDetailModel.setBlockedForHowLong(cursor.getString(4));

                callSMSDetailModel.setIsCallCleared(cursor.getString(5));
                callSMSDetailModel.setIsAlwaysCallCleared(cursor.getString(6));
                callSMSDetailModel.setCallClearedForHowLong(cursor.getString(7));
                callSMSDetailModel.setCreatedDate(cursor.getString(8));

                callContactModelList.add(callSMSDetailModel);
            } while (cursor.moveToNext());
        }

        List<CallSMSDetailModel> filteredCallContactModelList = new ArrayList<>();
        for(int i=0; i < callContactModelList.size(); i++)
        {
            if(callContactModelList.get(i).getIsCallOrSms().equals(IsCallOrSms))
            {
                filteredCallContactModelList.add(callContactModelList.get(i));
            }
        }

        return filteredCallContactModelList;
    }

    public int getCallSmsDetailsCount() {
        String countQuery = "SELECT * FROM " + TABLE_CALL_SMS_DETAIL;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public void deleteCallSmsDetail(CallSMSDetailModel callSMSDetailModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CALL_SMS_DETAIL, KEY_DISPLAY_NAME + " = ? AND " + IS_CALL_OR_SMS + " = ?",
                new String[] { callSMSDetailModel.getDisplayName(), callSMSDetailModel.getIsCallOrSms() });

        db.close();
    }
}
