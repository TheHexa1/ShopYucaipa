package com.yucaipa.shop.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.Result;
import com.yucaipa.shop.R;
import com.yucaipa.shop.utils.Constants;
import com.yucaipa.shop.utils.MySingleton;
import com.yucaipa.shop.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView mScannerView;

    String TAG = "QRCodeScanner:";
    String qrcode= "";
    Utils utils;
    SharedPreferences myPref;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view

        utils = Utils.getInstance(this);


        myPref = getSharedPreferences(Constants.myPrefKey,MODE_PRIVATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {

        // Do something with the result here
        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        qrcode = rawResult.getText();

        recordPurchase();
    }

    private void recordPurchase(){

        utils.showpDialog();

        JSONObject jsonParam = new JSONObject();

        try {
            jsonParam.put("m_id",qrcode);
            jsonParam.put("client_id","0"); //static for now
            jsonParam.put("user_id",myPref.getString("user_id",null));
            Log.i(TAG,jsonParam.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            // If you would like to resume scanning, call this method below:
            mScannerView.resumeCameraPreview(this);
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
                        Toast.makeText(getApplication(),"Your purchase is recorded successfully", Toast.LENGTH_LONG).show();
                        if(result_code.equals("111")){
                            showNotification();
                        }
                    }else if(result_code.equals("777")){
                        Toast.makeText(getApplication(),"Some fields are missing", Toast.LENGTH_LONG).show();
                    }
                    onBackPressed();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG, e.getMessage());
                }
                utils.hidepDialog();
                // If you would like to resume scanning, call this method below:
                mScannerView.resumeCameraPreview(QRCodeScanner.this);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if(error instanceof TimeoutError) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage() + "Timeout occured", Toast.LENGTH_LONG).show();
                    VolleyLog.d(TAG, "timeout Error: ");
                }
                else if(error instanceof ServerError) {
                    VolleyLog.d(TAG, "server Error: ");
                    Toast.makeText(getApplicationContext(),
                            error.getMessage() + "Server error occured", Toast.LENGTH_LONG).show();
                }else if (error instanceof NetworkError || error instanceof AuthFailureError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "No internet!", Toast.LENGTH_LONG).show();
                }
                // hide the progress dialog
                utils.hidepDialog();
                // If you would like to resume scanning, call this method below:
                mScannerView.resumeCameraPreview(QRCodeScanner.this);
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
                        .setContentText("You have made 5 purchases at "+utils.getShopNameFromMerchantID(qrcode))
                        .build();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mNotificationId is a unique integer your app uses to identify the
        // notification. For example, to cancel the notification, you can pass its ID
        // number to NotificationManager.cancel().
        mNotificationManager.notify((int)(c.getTimeInMillis()/1234), notification);
    }
}
