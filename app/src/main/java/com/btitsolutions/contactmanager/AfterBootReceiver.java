package com.btitsolutions.contactmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AfterBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent serviceLauncher = new Intent(context, CallBlockerService.class);
        context.startService(serviceLauncher);
    }
}