package com.btitsolutions.contactmanager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.android.internal.telephony.ITelephony;

public class DeviceStateListener extends PhoneStateListener {
    private ITelephony telephonyService;
    private Context context;

    public DeviceStateListener(Context context) {
        this.context = context;
        initializeTelephonyService();
    }

    private void initializeTelephonyService() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            Class clase = Class.forName(telephonyManager.getClass().getName());
            try {
                Method method = clase.getDeclaredMethod("getITelephony");
                method.setAccessible(true);
                telephonyService = (ITelephony) method.invoke(telephonyManager);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Date DateFormatter(String dateString) {
        DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        Date date = null;
        try {
            date = (Date) formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String formatedDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);

        return date;
    }

    @Override
    public void onCallStateChanged(int state, final String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                DBHelper dbHelper = new DBHelper(context);
                String displayName = dbHelper.getCallContactByPhoneNumber(incomingNumber).getDisplayName();

                CallSMSDetailModel callSMSDetailModel = new CallSMSDetailModel();
                callSMSDetailModel = dbHelper.getAllCallSmsDetailByDisplayName(displayName, "CALL");
                if (callSMSDetailModel != null) {
                    if (callSMSDetailModel.getIsBlocked().equals("True")) {
                        if (callSMSDetailModel.getIsAlwaysBlocked().equals("True")) {
                            try {
                                // This is the main code that block the incoming call.
                                telephonyService.endCall();
                                Thread t = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // You can run anything here lets say a notice to the user if a call is blocked
                                    }
                                });
                                t.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            String thatDate = callSMSDetailModel.getCreatedDate();
                            Date date = null;
                            date = DateFormatter(thatDate);

                            Calendar createdDate = Calendar.getInstance();
                            createdDate.setTime(date);

                            Calendar currentDate = Calendar.getInstance();

                            long difference = (currentDate.getTimeInMillis() - createdDate.getTimeInMillis()) / (24 * 60 * 60 * 1000);
                            int numberOfDays = Integer.parseInt(callSMSDetailModel.getBlockedForHowLong());

                            if (difference > numberOfDays) {
                                try {
                                    // This is the main code that block the incoming call.
                                    telephonyService.endCall();
                                    Thread t = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // You can run anything here lets say a notice to the user if a call is blocked
                                        }
                                    });
                                    t.start();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                try {
                    Thread timerThread = new Thread() {
                        public void run() {
                            try {
                                sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                ClearLogs();
                            }
                        }
                    };
                    timerThread.start();
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                break;
        }
    }


    public void DeleteCallLogByNumber(String numberTag) {
        char[] number = numberTag.trim().toCharArray();
        String n = "%";

        if (number.length <= 10) {
            for (int i = 1; i < number.length; i++) {
                if (number[i] != ' ') {
                    n = n + (number[i] + "%");
                }
            }
        } else {
            int starter = number.length - 9;
            for (int i = starter; i < number.length; i++) {
                if (number[i] != ' ') {
                    n = n + (number[i] + "%");
                }
            }
        }

        String queryString = CallLog.Calls.NUMBER.trim() + " LIKE '" + n + "'";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, queryString, null);
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
    }
}