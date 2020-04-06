package com.btitsolutions.contactmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.Locale;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        MobileAds.initialize(this, "ca-app-pub-8168171128315421~1261877682");

        final Context context = this;
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
                finally{
                    SetupProperLanguage(context);

                    finish();
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    public void SetupProperLanguage(Context context)
    {
        SharedPreferences GeneralSettings = getSharedPreferences("GeneralSettings", MODE_PRIVATE);
        String languageSetting = GeneralSettings.getString("Language", "English");
        if(languageSetting.equals("Amharic"))
        {
            String language_code = "am";
            Resources res = getApplicationContext().getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.setLocale(new Locale(language_code.toLowerCase())); // API 17+ only.
            // Use conf.locale = new Locale(...) if targeting lower versions
            res.updateConfiguration(conf, dm);
        }
        else
        {
            String language_code = "en-US";
            Resources res = getApplicationContext().getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.setLocale(new Locale(language_code.toLowerCase())); // API 17+ only.
            // Use conf.locale = new Locale(...) if targeting lower versions
            res.updateConfiguration(conf, dm);
        }
    }
}
