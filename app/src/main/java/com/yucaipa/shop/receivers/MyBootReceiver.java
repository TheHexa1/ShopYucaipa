package com.yucaipa.shop.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yucaipa.shop.model.Question;
import com.yucaipa.shop.services.GeofenceMonitorService;
import com.yucaipa.shop.services.MyFirebaseMessagingService;
import com.yucaipa.shop.utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Vivek_Hexa on 25-Jan-17.
 * used for starting firebase messaging service
 */

public class MyBootReceiver extends BroadcastReceiver {

    ArrayList<Question> questions;
    Utils utils;

    @Override
    public void onReceive(Context context, Intent intent) {

        utils = Utils.getInstance(context);
        questions = new ArrayList<>();
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Question>>(){}.getType();
        questions = gson.fromJson(utils.loadJSONFromAsset("questions.json"), listType);

        if(intent.getAction().equals("android.location.PROVIDERS_CHANGED") ||
                intent.getAction().equals("android.location.MODE_CHANGED")){
            //stop and the restart geo fencing service
            Intent geofencing = new Intent(context, GeofenceMonitorService.class);
            context.stopService(geofencing);
        }else{
            //firebase messaging service
            Intent fcm_noti = new Intent(context, MyFirebaseMessagingService.class);
            context.startService(fcm_noti);
        }
        //geo fencing service
        Intent geofencing = new Intent(context, GeofenceMonitorService.class);
        geofencing.putParcelableArrayListExtra("questions_obj",questions);
        context.startService(geofencing);
    }
}
