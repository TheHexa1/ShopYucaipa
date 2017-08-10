package com.yucaipa.shop.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yucaipa.shop.services.MyFirebaseMessagingService;


/**
 * Created by Vivek_Hexa on 25-Jan-17.
 * used for starting firebase messaging service
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //firebase messaging service
        Intent fcm_noti = new Intent(context, MyFirebaseMessagingService.class);
        context.startService(fcm_noti);
    }
}
