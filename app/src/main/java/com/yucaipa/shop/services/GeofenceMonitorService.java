package com.yucaipa.shop.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.yucaipa.shop.model.Location;
import com.yucaipa.shop.model.Question;
import com.yucaipa.shop.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Vivek_Hexa on 11-Aug-17.
 */

public class GeofenceMonitorService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {


    protected ArrayList<Geofence> mGeofenceList;
    protected GoogleApiClient mGoogleApiClient;
    List<Question> questionList;

    LocationRequest mLocationRequest;

    Handler handler = new Handler();
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("Service Started","Of course");

        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();

        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //add geo fences
        addGeoFences();

        setLocationUpdates();
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
            /*Toast.makeText(
                    this,
                    "Geofences Added",
                    Toast.LENGTH_SHORT
            ).show();*/
            Log.i("Success:","Successfully added/removed geo fences");
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            Log.i("Error:","Error in adding/removing geo fences");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Log.i("onStartCommand","Of course");

        questionList = new ArrayList<>();
        questionList = intent.getParcelableArrayListExtra("questions_obj");

        // Get the geofences used. Geofence data is hard coded in this sample.
        mGeofenceList.clear();
        populateGeofenceList();

        if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, getGeofencePendingIntent())
                .setResultCallback(this);

        if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        stopSelf();
        Log.i("Service Stopped","Yeah");
    }

    public void setLocationUpdates(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000); //10 secs
        mLocationRequest.setFastestInterval(5000); //5secs
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, getGeofencePendingIntent());
        }catch (SecurityException se){
            se.printStackTrace();
        }
        Log.i("LocationReqs","Added");
    }

    public void addGeoFences(){
        if (!mGoogleApiClient.isConnected()) {
            Log.i("Error:", "Google API Client not connected!");
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
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER
                | GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void populateGeofenceList() {

        //offers/proximity geofences
        for (int i=0; i<questionList.size(); i++) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(questionList.get(i).getQueNo()+"")
                    .setCircularRegion(
                            Double.parseDouble(questionList.get(i).getLatitude()),
                            Double.parseDouble(questionList.get(i).getLongitude()),
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
        //rate your visit/dwell geofences
        for (int i=0; i<questionList.size(); i++) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(questionList.get(i).getQueNo()+"_"+i)
                    .setCircularRegion(
                            Double.parseDouble(questionList.get(i).getLatitude()),
                            Double.parseDouble(questionList.get(i).getLongitude()),
                            Constants.GEOFENCE_DWELL_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                    .setLoiteringDelay(300000) //if user spends 5 mins then rate your visit notification
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
