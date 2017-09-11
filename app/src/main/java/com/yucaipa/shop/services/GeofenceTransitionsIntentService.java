package com.yucaipa.shop.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.yucaipa.shop.R;
import com.yucaipa.shop.activities.FeaturedPhotoQuiz;
import com.yucaipa.shop.activities.RateYourVisitActivity;
import com.yucaipa.shop.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vivek_Hexa on 11-Aug-17.
 */

public class GeofenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "GeofenceTransitionsIS";
    Utils utils;

    int shop_id;

    public GeofenceTransitionsIntentService() {
        super(TAG);  // use TAG to name the IntentService worker thread
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        utils = Utils.getInstance(this);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "GeofencingEvent Error: " + geofencingEvent.getErrorCode());
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        List <Geofence> triggerList = geofencingEvent.getTriggeringGeofences();


        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            for (int i = 0; i < triggerList.size(); i++) {
                showProximityNotification(triggerList.get(i).getRequestId());
            }
            Log.i("ENTER CALLEd?","Of course");
        }else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){

        }else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){
            //show rate your visit notification
            for (int i = 0; i < triggerList.size(); i++) {
                showRateYourVisitNotification(triggerList.get(i).getRequestId().split("_")[0]);
            }
            Log.i("DWELL called?","Of course");
        }


    }

    private void showProximityNotification(String str_shop_id) {

        shop_id = utils.getDrawableResId("ans"+str_shop_id);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent accessIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(utils.getWebsiteUrl(shop_id)));
        accessIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        RemoteViews bigView = new RemoteViews(getPackageName(),
                R.layout.custom_notification_layout);

        // notification's icon
        bigView.setImageViewResource(R.id.notifiation_image, shop_id);
        // notification's title
        bigView.setTextViewText(R.id.title, utils.getNotificationText(shop_id));
        // shop's title
        bigView.setTextViewText(R.id.shop_name, utils.getShopName(shop_id));
        // notification's content
        bigView.setTextViewText(R.id.tv_body, "tap here to visit their website");
        // notification's time
        bigView.setTextViewText(R.id.tv_time, utils.getCurrentTime());

        Intent guidanceIntent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/dir/?api=1&origin=my location&destination="+utils.getLocation(shop_id)+"&travelmode=driving"));
        guidanceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+utils.getTelephoneNumber(shop_id)));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        bigView.setOnClickPendingIntent(R.id.tv_guidance, PendingIntent.getActivity(this,0,guidanceIntent,0));
        bigView.setOnClickPendingIntent(R.id.tv_call, PendingIntent.getActivity(this,1,callIntent,0));

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_yf)
                .setAutoCancel(false)
                .setSound(defaultSoundUri)
                .setContentIntent(PendingIntent.getActivity(this, 2, accessIntent, 0))
                .setPriority(Notification.PRIORITY_MAX)
                .setCustomContentView(bigView)
                .setCustomBigContentView(bigView);

        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(shop_id, notificationBuilder.build());
    }

    private void showRateYourVisitNotification(String str_shop_id){

        shop_id = utils.getDrawableResId("ans"+str_shop_id);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent rateYourVisitIntent = new Intent(this, RateYourVisitActivity.class);
        rateYourVisitIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        rateYourVisitIntent.putExtra("shop_id",shop_id);

        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getActivity(this, shop_id /* Request code */, rateYourVisitIntent,0);

        RemoteViews bigView = new RemoteViews(getPackageName(),
                R.layout.custom_rate_notification_layout);

        // notification's icon
        bigView.setImageViewResource(R.id.notifiation_image, shop_id);
        // shop's title
        bigView.setTextViewText(R.id.shop_name, utils.getShopName(shop_id));
        // notification's content
        bigView.setTextViewText(R.id.tv_body, "Rate your visit to this shop");
        // notification's time
        bigView.setTextViewText(R.id.tv_time, utils.getCurrentTime());

        /*Intent guidanceIntent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/dir/?api=1&origin=my location&destination="+utils.getLocation(shop_id)+"&travelmode=driving"));
        guidanceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/

        /*Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+utils.getTelephoneNumber(shop_id)));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        bigView.setOnClickPendingIntent(R.id.tv_guidance, PendingIntent.getActivity(this,0,guidanceIntent,0));
        bigView.setOnClickPendingIntent(R.id.tv_call, PendingIntent.getActivity(this,1,callIntent,0));*/

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setCustomContentView(bigView)
                .setCustomBigContentView(bigView);

        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(shop_id, notificationBuilder.build());
    }
}
