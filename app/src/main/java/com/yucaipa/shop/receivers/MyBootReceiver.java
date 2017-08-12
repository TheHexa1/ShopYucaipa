package com.yucaipa.shop.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yucaipa.shop.services.GeofenceMonitorService;
import com.yucaipa.shop.services.MyFirebaseMessagingService;


/**
 * Created by Vivek_Hexa on 25-Jan-17.
 * used for starting firebase messaging service
 */

public class MyBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

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
        context.startService(geofencing);
    }
}
