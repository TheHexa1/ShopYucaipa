package com.yucaipa.shop.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.facebook.share.widget.LikeView;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.yucaipa.shop.R;
import com.yucaipa.shop.model.Question;
import com.yucaipa.shop.services.GeofenceMonitorService;
import com.yucaipa.shop.utils.Constants;
import com.yucaipa.shop.utils.MySingleton;
import com.yucaipa.shop.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FeaturedPhotoQuiz extends AppCompatActivity {

    TextView tv_show_hint,tv_ans_box,tv_hint;
    RadioGroup rg_ans;
    RadioButton rd_choice_1,rd_choice_2,rd_choice_3;
    LinearLayout ll_answers,ll_hint;
    RelativeLayout rl_hint_ans;
    ImageView iv_que_img,iv_yucaipa_logo;
    Button btn_prev, btn_next;
    Question question;
    ArrayList<Question> questions;
    int current_que_no = 0;
    Utils utils;
    RadioGroup.OnCheckedChangeListener checkedChangeListener;
    boolean exit = false;
    AlertDialog dialogInterface;
    String message = "", btn_label = "";
    int shop_id;

    Toolbar toolbar;

    LocationManager locationManager;

    boolean permission_flag = false;

    int PERMISSION_ALL = 321;

    String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CAMERA};

    SharedPreferences myPref;

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_photo_quiz);

        myPref = getSharedPreferences("com.yucaipa.shop",MODE_PRIVATE);

        utils = Utils.getInstance(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Question>>(){}.getType();
        questions = gson.fromJson(utils.loadJSONFromAsset("questions.json"), listType);

        //load first que
        question = questions.get(current_que_no);

        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_attach_money_white);
        setSupportActionBar(toolbar);

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

        /*temp*/
        /*iv_que_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FeaturedPhotoQuiz.this,RateYourVisitActivity.class);
                i.putExtra("shop_id",R.drawable.ans5);
                startActivity(i);
            }
        });*/
        iv_yucaipa_logo = (ImageView) findViewById(R.id.iv_yucaipa_logo);

        iv_yucaipa_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url;
                if(question.getQueNo() % 2 != 0){
                    url = utils.getWebsiteUrl(111);
                }else{
                    url = utils.getWebsiteUrl(222);
                }
                Intent accessIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(accessIntent);
            }
        });

        Glide.with(this).load(R.drawable.yucaipa_logo_quiz_1).into(iv_yucaipa_logo);
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

            /*SharedPreferences myPref = getSharedPreferences("com.yucaipa.shop",MODE_PRIVATE);

            if(myPref.getBoolean("isFirstRun",true)){

                Intent intent = new Intent(this, GeofenceMonitorService.class);
                intent.putParcelableArrayListExtra("questions_obj",questions);
                startService(intent);

                myPref.edit().putBoolean("isNotificationTurnedOn", true).apply();
                myPref.edit().putBoolean("isFirstRun", false).apply();
            }*/

            enableProximityAlerts();
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

        shop_id = utils.getDrawableResId("ans"+question.getQueNo());

        if(question.getQueNo() % 2 != 0){
            Glide.with(this).load(R.drawable.yucaipa_logo_quiz_1).into(iv_yucaipa_logo);
        }else{
            Glide.with(this).load(R.drawable.yucaipa_logo_quiz_2).into(iv_yucaipa_logo);
        }

        Glide.with(this)
                .load(utils.getDrawableResId(question.getQueImgDrawable()))
                .into(iv_que_img);

        //temporary
        /*findViewById(R.id.iv_que_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FeaturedPhotoQuiz.this, RateYourVisitActivity.class);
                i.putExtra("shop_id",shop_id);
                startActivity(i);
            }
        });*/
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

    public void showAnsPopUp(int selectedChoice){

        View view = getLayoutInflater().inflate(R.layout.image_layout,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(isAnswerCorrect(question.getAnsChoice(), selectedChoice)){

            dialogInterface.dismiss();

            message = "Correct!";
            btn_label = "Take me to it";
            builder.setView(view);

            /*if(question.getQueNo() == 2){
                view.findViewById(R.id.iv_ans_logo).setBackgroundColor(Color.BLACK);
            }else{
                view.findViewById(R.id.iv_ans_logo).setBackgroundColor(Color.WHITE);
            }*/

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
                            Uri.parse("https://www.google.com/maps/dir/?api=1&origin=my location&destination="+utils.getLocation(shop_id)+"&travelmode=driving"));
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
/*
        //setButton sizes
        if(message.equals("Correct!")) {
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(R.dimen.alert_dialog_btn_size);
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(R.dimen.alert_dialog_btn_size);
        }
        alert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextSize(R.dimen.alert_dialog_btn_size);*/

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

        if(flag == 0){
            builder.setNeutralButton("Take me to it", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent guidanceIntent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/maps/dir/?api=1&origin=my location&destination="+utils.getLocation(shop_id)+"&travelmode=driving"));
        /*https://www.google.com/maps/dir/?api=1&origin=Uptown Pets, 35039 Yucaipa Blvd, Yucaipa, CA 92399&destination=Frisch's Clock Chalet & Gift Shop&travelmode=driving*/
                    guidanceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(guidanceIntent);
                }
            });
        }

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

        alert.show();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                btnPositive.setTextSize(50);

//                Button btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE);
//                btnNegative.setTextSize(TEXT_SIZE);
            }
        });

//        alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextSize(R.dimen.alert_dialog_btn_size);

        dialogInterface = alert;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quiz_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                i.putParcelableArrayListExtra("questions_obj",questions);
                startActivity(i);
                return true;
            case android.R.id.home:
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.setCaptureActivity(QRCodeScanner.class);
                integrator.initiateScan();
//                startActivity(new Intent(this, QRCodeScanner.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_ALL){
//            permission_flag = true;
            //************************************************
            //        ask for permissions in newer version of android.
            permission_flag = hasPermissions(this, PERMISSIONS);

            if(!permission_flag){
                Toast.makeText(getApplicationContext(),"You need to allow all the required permissions to use this app!", Toast.LENGTH_LONG)
                        .show();
                ActivityCompat.finishAffinity(this);
            }else{
                enableProximityAlerts();
                loadQueImage();
            }
        }

    }


    private void enableProximityAlerts(){
        initGoogleAPIClient();
        showSettingDialog();

        myPref.edit().putBoolean("isNotificationTurnedOn", true).apply();
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
                        Intent i = new Intent(FeaturedPhotoQuiz.this, GeofenceMonitorService.class);
                        i.putParcelableArrayListExtra("questions_obj",questions);
                        startService(i);

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(FeaturedPhotoQuiz.this, REQUEST_CHECK_SETTINGS);
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
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            // Check for the integer request code originally supplied to startResolutionForResult().
//            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");
                        //startLocationUpdates();
                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");
                        Intent i = new Intent(FeaturedPhotoQuiz.this, GeofenceMonitorService.class);
                        i.putParcelableArrayListExtra("questions_obj",questions);
                        startService(i);
                        break;
                }
//                break;
        }else{
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null) {
                if(result.getContents() == null) {
                    Log.d("FeaturedPhotoQuiz", "Cancelled scan");
                    Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("FeaturedPhotoQuiz", "Scanned");
                    qrcode = result.getContents();
                    recordPurchase();
                }
            } else {
                // This is important, otherwise the result will not be passed to the fragment
                super.onActivityResult(requestCode, resultCode, data);
            }

        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

    }*/

    String TAG = "QRCodeScanner:";
    String qrcode= "";

    private void recordPurchase(){

        utils.showpDialog();

        JSONObject jsonParam = new JSONObject();

        try {
            jsonParam.put("m_id",qrcode);
            jsonParam.put("client_id","1"); //static for now
            jsonParam.put("user_id",myPref.getString("user_id",null));
            Log.i(TAG,jsonParam.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            // If you would like to resume scanning, call this method below:
//            mScannerView.resumeCameraPreview(this);
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Constants.RECORD_OF_PURCHASE_URL, jsonParam, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response
                    // response will be a json object
                    String result_code = response.getString("result_code");

                    if(result_code.equals("111") || result_code.equals("222")){
//                        Toast.makeText(getApplication(),"Your purchase is recorded successfully", Toast.LENGTH_LONG).show();
                        editPurchaseCounter();
                        showPurchaseRecordPopUp("Your purchase is recorded successfully");
                        if(result_code.equals("111")){
                            showNotification();
                        }
                    }else if(result_code.equals("555")){
                        showPurchaseRecordPopUp("QRcode is invalid!");
                    }else if(result_code.equals("777")){
                        showPurchaseRecordPopUp("Some fields are missing");
//                        Toast.makeText(getApplication(),"Some fields are missing", Toast.LENGTH_LONG).show();
                    }
                    onBackPressed();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG, e.getMessage());
                }
                utils.hidepDialog();
                // If you would like to resume scanning, call this method below:
//                mScannerView.resumeCameraPreview(QRCodeScanner.this);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if(error instanceof TimeoutError) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
                    showPurchaseRecordPopUp("Request timed out due to slow internet! Please try again");
                    /*Toast.makeText(getApplicationContext(),
                            error.getMessage() + "Timeout occured", Toast.LENGTH_LONG).show();*/
                    VolleyLog.d(TAG, "timeout Error: ");
                }
                else if(error instanceof ServerError) {
                    VolleyLog.d(TAG, "server Error: ");
                    showPurchaseRecordPopUp("Something went wrong on server! Contact admin");
                    /*Toast.makeText(getApplicationContext(),
                            error.getMessage() + "Server error occured", Toast.LENGTH_LONG).show();*/
                }else if (error instanceof NetworkError || error instanceof AuthFailureError || error instanceof NoConnectionError) {
//                    Toast.makeText(getApplicationContext(), "No internet!", Toast.LENGTH_LONG).show();
                    showPurchaseRecordPopUp("Make sure that your device is connected to internet");
                }
                // hide the progress dialog
                utils.hidepDialog();
                // If you would like to resume scanning, call this method below:
//                mScannerView.resumeCameraPreview(QRCodeScanner.this);
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                Constants.NORMAL_VOLLEY_TIMEOUT,
                Constants.VOLLEY_RETRY_COUNTS,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        MySingleton.getInstance(this).addToRequestQueue(jsonObjReq);
    }

    private void showNotification(){


        Calendar c = Calendar.getInstance();

        // The id of the channel.
        String CHANNEL_ID = "my_channel_01";
        Notification notification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_yf)
                        .setContentTitle("Congratulations!")
                        .setContentText("You have made 5 purchases")
                        .build();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mNotificationId is a unique integer your app uses to identify the
        // notification. For example, to cancel the notification, you can pass its ID
        // number to NotificationManager.cancel().
        mNotificationManager.notify((int)(c.getTimeInMillis()/1234), notification);
    }

    private void showPurchaseRecordPopUp(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void editPurchaseCounter(){
        int counter = myPref.getInt("purchase_counter",0);
        counter++;
        if(counter == 5) {
            myPref.edit().putInt("purchase_counter", 0).apply();
        }else{
            myPref.edit().putInt("purchase_counter", counter).apply();
        }
    }
}
