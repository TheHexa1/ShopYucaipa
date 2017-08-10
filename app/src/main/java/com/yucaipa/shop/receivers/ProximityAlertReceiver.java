package com.yucaipa.shop.receivers;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.yucaipa.shop.R;
import com.yucaipa.shop.utils.Utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Vivek_Hexa on 10-Aug-17.
 */

public class ProximityAlertReceiver extends BroadcastReceiver {
    
    Utils utils;
    Context mContext;
    int shop_id;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        final String key = LocationManager.KEY_PROXIMITY_ENTERING;
        final Boolean entering = intent.getBooleanExtra(key, false);
        
        utils = Utils.getInstance(context);
        mContext = context;
        shop_id = utils.getDrawableResId("ans"+intent.getIntExtra("shop_id",1));

        if (entering) {
            createNotification(shop_id);
            Toast.makeText(context, "entering location...", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "exiting location...", Toast.LENGTH_LONG).show();
        }

    }

    private void createNotification(int shop_id){
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        Intent accessIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(utils.getWebsiteUrl(shop_id)));
        accessIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        RemoteViews bigView = new RemoteViews(mContext.getPackageName(),
                R.layout.custom_notification_layout);

        // notification's icon
        bigView.setImageViewResource(R.id.notifiation_image, shop_id);
        // notification's title
        bigView.setTextViewText(R.id.title, "You are near to");
        // shop's title
        bigView.setTextViewText(R.id.shop_name, utils.getShopName(shop_id));
        // notification's content
//        bigView.setTextViewText(R.id.tv_body, messageBody);
        // notification's time
        bigView.setTextViewText(R.id.tv_time, utils.getCurrentTime());

        Intent guidanceIntent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/dir/?api=1&origin=my location&destination="+utils.getLocation(shop_id)+"&travelmode=driving"));
        /*https://www.google.com/maps/dir/?api=1&origin=Uptown Pets, 35039 Yucaipa Blvd, Yucaipa, CA 92399&destination=Frisch's Clock Chalet & Gift Shop&travelmode=driving*/
        guidanceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+utils.getTelephoneNumber(shop_id)));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        bigView.setOnClickPendingIntent(R.id.tv_guidance, PendingIntent.getActivity(mContext,0,guidanceIntent,0));
        bigView.setOnClickPendingIntent(R.id.tv_call, PendingIntent.getActivity(mContext,1,callIntent,0));

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                .setSound(defaultSoundUri)
                .setContentIntent(PendingIntent.getActivity(mContext, 2, accessIntent, 0))
                .setPriority(Notification.PRIORITY_MAX)
                .setCustomContentView(bigView)
                .setCustomBigContentView(bigView);

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        int noti_id = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        notificationManager.notify(noti_id/* ID of notification */
                , notificationBuilder.build());
    }
}
