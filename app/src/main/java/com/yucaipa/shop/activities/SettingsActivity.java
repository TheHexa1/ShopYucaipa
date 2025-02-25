package com.yucaipa.shop.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.yucaipa.shop.R;
import com.yucaipa.shop.model.Question;
import com.yucaipa.shop.services.GeofenceMonitorService;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;
    SwitchCompat switchCompat;
    SharedPreferences myPref;

    ArrayList<Question> questions;

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;

    ImageView iv_calibration_img;

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

        if(getResources().getBoolean(R.bool.isTablet)){
            switchCompat.setScaleX(1.5f);
            switchCompat.setScaleY(1.5f);
        }

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

                    initGoogleAPIClient();
                    showSettingDialog();

                    myPref.edit().putBoolean("isNotificationTurnedOn", true).apply();
                    Toast.makeText(SettingsActivity.this,"You will get nearby businesses notifications",Toast.LENGTH_LONG).show();
                }else{

                    stopService(i);

//                    stopService(new Intent(SettingsActivity.this, GeofenceTransitionsIntentService.class));
                    myPref.edit().putBoolean("isNotificationTurnedOn", false).apply();
                    Toast.makeText(SettingsActivity.this,"You will no longer get nearby businesses notifications",Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.tv_calibrate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalibrationPopUp();
            }
        });

        findViewById(R.id.tv_send_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback();
            }
        });

    }

    private void showCalibrationPopUp(){

        View view = getLayoutInflater().inflate(R.layout.custom_calibration_popup,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        iv_calibration_img = (ImageView) view.findViewById(R.id.iv_calibration_img);

        builder.setView(view);

//        Glide.with(this).load(R.drawable.calibration_instructions).into(iv_calibration_img);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alert =  builder.create();
        alert.show();

        /*alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Glide.with(SettingsActivity.this).load(R.drawable.calibration_instructions).into(iv_calibration_img);
            }
        });*/
    }

    private void sendFeedback(){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
//        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "cyrano@cyranointeractive.com" });
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback from YF App");
//        intent.putExtra(Intent.EXTRA_TEXT, "mail body");
        startActivity(Intent.createChooser(intent, "Contact via"));
    }

    /* Initiate Google API Client  */
    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /* Show Location Access Dialog */
    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        Intent i = new Intent(SettingsActivity.this, GeofenceMonitorService.class);
                        i.putParcelableArrayListExtra("questions_obj",questions);
                        startService(i);

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(SettingsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");
                        //startLocationUpdates();
                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");
                        Intent i = new Intent(SettingsActivity.this, GeofenceMonitorService.class);
                        i.putParcelableArrayListExtra("questions_obj",questions);
                        startService(i);
                        break;
                }
                break;
        }
    }
}