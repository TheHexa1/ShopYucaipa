package com.yucaipa.shop.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yucaipa.shop.R;
import com.yucaipa.shop.model.Location;
import com.yucaipa.shop.model.Question;
import com.yucaipa.shop.services.GeofenceTransitionsIntentService;
import com.yucaipa.shop.utils.Constants;
import com.yucaipa.shop.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeaturedPhotoQuiz extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    TextView tv_show_hint,tv_ans_box,tv_hint;
    RadioGroup rg_ans;
    RadioButton rd_choice_1,rd_choice_2,rd_choice_3;
    LinearLayout ll_answers,ll_hint;
    RelativeLayout rl_hint_ans;
    ImageView iv_que_img,iv_yucaipa_logo;
    Button btn_prev, btn_next;
    Question question;
    List<Question> questions;
    int current_que_no = 0;
    Utils utils;
    RadioGroup.OnCheckedChangeListener checkedChangeListener;
    boolean exit = false;
    AlertDialog dialogInterface;
    String message = "", btn_label = "";

    LocationManager locationManager;

    boolean permission_flag = false;

    int PERMISSION_ALL = 321;

    protected ArrayList<Geofence> mGeofenceList;
    protected GoogleApiClient mGoogleApiClient;

    String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_photo_quiz);

        utils = Utils.getInstance(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Question>>(){}.getType();
        questions = gson.fromJson(loadJSONFromAsset("questions.json"), listType);

        //load first que
        question = questions.get(current_que_no);

        tv_ans_box = (TextView) findViewById(R.id.tv_ans_box);
        tv_ans_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHintAnsChoicePopUp(1);
            }
        });

        tv_show_hint = (TextView) findViewById(R.id.tv_show_hint);

        tv_show_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHintAnsChoicePopUp(0);
            }
        });

        iv_que_img = (ImageView) findViewById(R.id.iv_que_img);
        iv_yucaipa_logo = (ImageView) findViewById(R.id.iv_yucaipa_logo);

        iv_yucaipa_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent accessIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://yucaipachamber.org/"));
                startActivity(accessIntent);
            }
        });

        Glide.with(this).load(R.drawable.yucaipa_logo_quiz).into(iv_yucaipa_logo);
/*
        Glide.with(this)
                .load(utils.getDrawableResId(question.getQueImgDrawable()))
                .into(iv_que_img);*/

        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_que_no++;// increment index to next que
                if(current_que_no < questions.size()){
                    question = questions.get(current_que_no);
                    loadQueImage();
                }else{
                    current_que_no = questions.size()-1;
                    Toast.makeText(getApplicationContext(),"Done!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_prev = (Button) findViewById(R.id.btn_prev);
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_que_no--; //decrement index to previous question
                if(current_que_no >= 0){
                    question = questions.get(current_que_no);
                    loadQueImage();
                }else{
                    current_que_no = 0;
                    Toast.makeText(getApplicationContext(),"You are at 1st question!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //************************************************
        //        ask for permissions in newer version of android.
        permission_flag = hasPermissions(this, PERMISSIONS);

        if(!permission_flag){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        else{

//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
                    // Empty list for storing geofences.
                    mGeofenceList = new ArrayList<Geofence>();

                    // Kick off the request to build GoogleApiClient.
                    buildGoogleApiClient();

                    // Get the geofences used. Geofence data is hard coded in this sample.
                    populateGeofenceList();

//                }
//            },500);
            loadQueImage();
        }

    }

    //**************************
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            //for Android M and above versions
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    // Permission Denied
//                    Toast.makeText(context,"You need to allow all the required permissions to access this app!", Toast.LENGTH_LONG)
//                            .show();
                    return false;
                }
            }
        }
        return true;
    }

    private void initViews(View view){

        tv_hint = (TextView) view.findViewById(R.id.tv_hint);
//        tv_que_text = (TextView) findViewById(R.id.tv_que_text);

        rg_ans = (RadioGroup) view.findViewById(R.id.rg_ans);
        checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if (checkedId == R.id.rd_choice_1) {
                    showAnsPopUp(1);
                    rd_choice_1.setChecked(true);
                } else if (checkedId == R.id.rd_choice_2) {
                    showAnsPopUp(2);
                    rd_choice_2.setChecked(true);
                } else if (checkedId == R.id.rd_choice_3) {
                    showAnsPopUp(3);
                    rd_choice_3.setChecked(true);
                }
            }
        };

        rg_ans.setOnCheckedChangeListener(checkedChangeListener);

        rd_choice_1 = (RadioButton) view.findViewById(R.id.rd_choice_1);
        rd_choice_2 = (RadioButton) view.findViewById(R.id.rd_choice_2);
        rd_choice_3 = (RadioButton) view.findViewById(R.id.rd_choice_3);

        ll_answers = (LinearLayout) view.findViewById(R.id.ll_answers);
        ll_hint = (LinearLayout) view.findViewById(R.id.ll_hint);

        rl_hint_ans = (RelativeLayout) view.findViewById(R.id.rl_hint_ans);
    }

    private void loadQueImage(){

        Glide.with(this)
                .load(utils.getDrawableResId(question.getQueImgDrawable()))
                .into(iv_que_img);
//        rg_ans.setOnCheckedChangeListener(null);
/*
        rd_choice_1.setChecked(false);
        rd_choice_2.setChecked(false);
        rd_choice_3.setChecked(false);

        rg_ans.setOnCheckedChangeListener(checkedChangeListener);*/

        tv_show_hint.setTextColor(getResources().getColor(R.color.accent));
        tv_ans_box.setTextColor(getResources().getColor(R.color.accent));
        /*ll_answers.setVisibility(View.GONE);
        ll_hint.setVisibility(View.GONE);
        rl_hint_ans.setVisibility(View.GONE);*/

//        tv_que_text.setText(question.getQueText());
    }

    private void showHint(){
        tv_show_hint.setTextColor(getResources().getColor(R.color.primary));
        tv_ans_box.setTextColor(getResources().getColor(R.color.accent));
        ll_answers.setVisibility(View.GONE);
        ll_hint.setVisibility(View.VISIBLE);
        rl_hint_ans.setVisibility(View.VISIBLE);

        tv_hint.setText(question.getHint());
    }

    private void showAnswerChoices(){
        tv_show_hint.setTextColor(getResources().getColor(R.color.accent));
        tv_ans_box.setTextColor(getResources().getColor(R.color.primary));
        ll_answers.setVisibility(View.VISIBLE);
        ll_hint.setVisibility(View.GONE);
        rl_hint_ans.setVisibility(View.VISIBLE);

        rd_choice_1.setText(question.getChoice1());
        rd_choice_2.setText(question.getChoice2());
        rd_choice_3.setText(question.getChoice3());
    }

    private boolean isAnswerCorrect(int ansChoice, int selectedChoice){
        return ansChoice == selectedChoice;
    }

    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {

            InputStream is = getAssets().open(filename);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;

    }

    public void showAnsPopUp(int selectedChoice){

        View view = getLayoutInflater().inflate(R.layout.image_layout,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(isAnswerCorrect(question.getAnsChoice(), selectedChoice)){

            dialogInterface.dismiss();

            message = "Correct!";
            btn_label = "Navigate";
            builder.setView(view);

            if(question.getQueNo() == 2){
                view.findViewById(R.id.iv_ans_logo).setBackgroundColor(Color.BLACK);
            }else{
                view.findViewById(R.id.iv_ans_logo).setBackgroundColor(Color.WHITE);
            }

            Glide.with(this)
                    .load(utils.getDrawableResId(question.getAnsImgDrawable()))
                    .into((ImageView) view.findViewById(R.id.iv_ans_logo));

            builder.setNegativeButton("CALL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String phone_no = utils.getTelephoneNumber(utils.getDrawableResId(question.getAnsImgDrawable()));
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone_no));
                    startActivity(callIntent);
                }
            });

            builder.setPositiveButton("OFFER", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String url = utils.getWebsiteUrl(utils.getDrawableResId(question.getAnsImgDrawable()));
                    Intent callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(callIntent);
                }
            });
        }else{
            message = "Wrong!";
            btn_label = "Try Again!";
        }
        builder.setMessage(message);

        builder.setNeutralButton(btn_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(message.equals("Correct!")){
                    Intent guidanceIntent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/maps/dir/?api=1&origin=my location&destination=Frisch's Clock Chalet & Gift Shop&travelmode=driving"));
        /*https://www.google.com/maps/dir/?api=1&origin=Uptown Pets, 35039 Yucaipa Blvd, Yucaipa, CA 92399&destination=Frisch's Clock Chalet & Gift Shop&travelmode=driving*/
                    guidanceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(guidanceIntent);
                }else {
                    dialog.dismiss();
                }
            }
        });

        final AlertDialog alert =  builder.create();

        alert.show();
    }

    public void showHintAnsChoicePopUp(int flag){

        View view = getLayoutInflater().inflate(R.layout.custom_quiz_popup_layout,null);

        initViews(view);

        if(flag == 0){
            showHint();
        }else if(flag == 1){
            showAnswerChoices();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String message = "", btn_label = "";

        builder.setView(view);

        /*if(isAnswerCorrect(question.getAnsChoice(), selectedChoice)){
            message = "Correct!";
            btn_label = "Navigate";
            builder.setView(view);

            if(question.getQueNo() == 2){
                view.findViewById(R.id.iv_ans_logo).setBackgroundColor(Color.BLACK);
            }else{
                view.findViewById(R.id.iv_ans_logo).setBackgroundColor(Color.WHITE);
            }

            Glide.with(this)
                    .load(utils.getDrawableResId(question.getAnsImgDrawable()))
                    .into((ImageView) view.findViewById(R.id.iv_ans_logo));
        }else{
            message = "Wrong!";
            btn_label = "Try Again!";
        }
        builder.setMessage(message);*/

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alert =  builder.create();

        dialogInterface = alert;

        alert.show();
    }

    @Override
    public void onBackPressed() {

        if (exit) {
            ActivityCompat.finishAffinity(this); // finish activity and exit from the app
        } else {
            Toast.makeText(this, "Press Back again to exit",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }


    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PERMISSION_ALL){
//            permission_flag = true;
            //************************************************
            //        ask for permissions in newer version of android.
            permission_flag = hasPermissions(this, PERMISSIONS);

            if(permission_flag){
                loadQueImage();
            }
            else{Toast.makeText(getApplicationContext(),"You need to allow all the required permissions to use this app!", Toast.LENGTH_LONG)
                    .show();
                ActivityCompat.finishAffinity(this);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //add geo fences
        addGeoFences();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Toast.makeText(
                    this,
                    "Geofences Added",
                    Toast.LENGTH_LONG
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
//            String errorMessage = GeofenceErrorMessages.getErrorString(this,
//                    status.getStatusCode());
            Toast.makeText(
                    this,
                    "Geofences Not",
                    Toast.LENGTH_LONG
            ).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void addGeoFences(){
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Google API Client not connected!", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void populateGeofenceList() {
        for (Map.Entry<String, Location> entry : Constants.LANDMARKS.entrySet()) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().getLatitude(),
                            entry.getValue().getLongitude(),
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
//        mGoogleApiClient.connect();
    }
}
