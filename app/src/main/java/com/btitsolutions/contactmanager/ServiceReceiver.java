package com.btitsolutions.contactmanager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class ServiceReceiver extends BroadcastReceiver
{
    private static TelephonyManager telephony;
    private static DeviceStateListener phoneListener;
    private static boolean firstTime=true;

    public ServiceReceiver(Context context)
    {
        telephony=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        phoneListener = new DeviceStateListener(context);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(firstTime)
        {
            telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            firstTime = false;
        }
    }

    // You can use this in the future to stop the call blocker feature.
    public void stopListening() {
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
        firstTime=true;
    }
}