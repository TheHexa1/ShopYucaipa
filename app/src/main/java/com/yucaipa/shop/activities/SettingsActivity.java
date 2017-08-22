package com.yucaipa.shop.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.yucaipa.shop.R;
import com.yucaipa.shop.model.Question;
import com.yucaipa.shop.services.GeofenceMonitorService;
import com.yucaipa.shop.services.GeofenceTransitionsIntentService;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;
    SwitchCompat switchCompat;
    SharedPreferences myPref;

    ArrayList<Question> questions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        questions = new ArrayList<>();
        questions = getIntent().getParcelableArrayListExtra("questions_obj");

        toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
/*
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
        upArrow.setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        switchCompat = (SwitchCompat) findViewById(R.id.mSwitch);

        myPref = getSharedPreferences("com.yucaipa.shop",MODE_PRIVATE);
        if(myPref.getBoolean("isNotificationTurnedOn", false)){
            switchCompat.setChecked(true);
        }else{
            switchCompat.setChecked(false);
        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent i = new Intent(SettingsActivity.this, GeofenceMonitorService.class);
                if(isChecked){
                    i.putParcelableArrayListExtra("questions_obj",questions);
                    startService(i);

                    myPref.edit().putBoolean("isNotificationTurnedOn", true).apply();
                    Toast.makeText(SettingsActivity.this,"You will get nearby businesses notifications",Toast.LENGTH_LONG).show();
                }else{

                    stopService(i);

                    stopService(new Intent(SettingsActivity.this, GeofenceTransitionsIntentService.class));
                    myPref.edit().putBoolean("isNotificationTurnedOn", false).apply();
                    Toast.makeText(SettingsActivity.this,"You will no longer get nearby businesses notifications",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}