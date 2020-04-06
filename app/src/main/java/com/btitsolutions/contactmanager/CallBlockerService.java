package com.btitsolutions.contactmanager;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

public class CallBlockerService extends Service {

    public static final int notification_id = 111;

    // ---------------------------------------
    // Listening Services
    // ---------------------------------------
    private static ServiceReceiver service;
    private static SMSReceiver smsService;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        service = new ServiceReceiver(getApplicationContext());
        registerReceiver(service, new IntentFilter("android.intent.action.PHONE_STATE"));
    }

    @Override
    public void onDestroy() {
        service.stopListening();
        unregisterReceiver(service);
        service = null;
        cancelStatusBarNotification();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void cancelStatusBarNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(notification_id);
    }
}