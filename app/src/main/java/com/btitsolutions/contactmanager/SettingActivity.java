package com.btitsolutions.contactmanager;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.Locale;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnSave;
    RadioButton rdbtnAmharic, rdbtnEnglish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        rdbtnAmharic = (RadioButton) findViewById(R.id.rdbtnAmharic);
        rdbtnEnglish = (RadioButton) findViewById(R.id.rdbtnEnglish);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        SharedPreferences GeneralSettings = getSharedPreferences("GeneralSettings", MODE_PRIVATE);
        String languageSetting = GeneralSettings.getString("Language", "English");
        if (languageSetting.equals("Amharic")) {
            rdbtnAmharic.setChecked(true);
            rdbtnEnglish.setChecked(false);
        }
        else {
            rdbtnAmharic.setChecked(false);
            rdbtnEnglish.setChecked(true);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnSave.getId())
        {
            SharedPreferences GeneralSettings = getSharedPreferences("GeneralSettings", MODE_PRIVATE);
            SharedPreferences.Editor editor;

            //set currently selected subject to preferences for further use
            editor = GeneralSettings.edit();
            String language_code = "am";
            if (rdbtnEnglish.isChecked())
            {
                language_code = "en-US";
                editor.putString("Language", "English");
            }
            else
            {
                language_code = "am";
                editor.putString("Language", "Amharic");
            }
            editor.apply();

            Toast.makeText(this, "Please close and re-open the app to see language changes", Toast.LENGTH_LONG).show();

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
