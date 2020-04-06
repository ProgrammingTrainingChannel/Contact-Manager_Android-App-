package com.btitsolutions.contactmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SMSReceiver extends BroadcastReceiver
{
    public void onReceive(Context context, Intent intent)
    {
        boolean conditionMatches = true;
        if(conditionMatches){
            abortBroadcast();
        }
    }
}