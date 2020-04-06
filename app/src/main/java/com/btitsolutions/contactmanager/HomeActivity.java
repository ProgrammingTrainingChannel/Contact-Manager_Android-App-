package com.btitsolutions.contactmanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnContacts, btnCalls, btnSetting, btnAbout;
    TextView lblShowHelpVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        lblShowHelpVideo = (TextView) findViewById(R.id.lblShowHelpVideo);
        lblShowHelpVideo.setOnClickListener(this);

        btnContacts = (Button) findViewById(R.id.btnContacts);
        btnCalls = (Button) findViewById(R.id.btnCalls);
        btnSetting = (Button) findViewById(R.id.btnSetting);
        btnAbout = (Button) findViewById(R.id.btnAbout);

        btnContacts.setOnClickListener(this);
        btnCalls.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        btnAbout.setOnClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            Intent serviceLauncher = new Intent(this, CallBlockerService.class);
            startService(serviceLauncher);
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALL_LOG}, 1010);//191919 is random number to use for callback
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1010: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent serviceLauncher = new Intent(this, CallBlockerService.class);
                    startService(serviceLauncher);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnContacts.getId())
        {
            Intent intent=new Intent(this, ContactManagerActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == btnCalls.getId())
        {
            Intent intent=new Intent(this, CallSettingActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == btnSetting.getId())
        {
            Intent intent=new Intent(this, SettingActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == btnAbout.getId())
        {
            Intent intent=new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == lblShowHelpVideo.getId())
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=Zilz8psIcj0"));
            startActivity(browserIntent);
        }
    }
}
