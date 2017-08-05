package com.yucaipa.shop.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yucaipa.shop.R;
import com.yucaipa.shop.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created by Vivek Solanki on 19/07/17
 * service which handles firebase notifications
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    NotificationCompat.Builder notificationBuilder;

    Utils utils;

    String title = "Yucaipa Shop", msgBody = "", msg = "";
    int noti_id = 0;
    int shop_id;

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    // [END receive_message]

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Log.d(TAG, "FCM Data: " + remoteMessage.getData());
//        Log.d(TAG, "FCM Noti: "+remoteMessage.getNotification().);

        utils = Utils.getInstance(getApplicationContext());

//        mContext = getApplicationContext().getApplicationContext();

//        String tag = remoteMessage.getNotification().getTag();
//        msgBody = remoteMessage.getNotification().getBody();
//        msg = remoteMessage.getData().get("message");

//        title = remoteMessage.getNotification().getTitle();
//        String tag = remoteMessage.getData().get("tag");
        msgBody = remoteMessage.getData().get("message");
        title = remoteMessage.getData().get("title");
        shop_id = utils.getDrawableResId("ans"+remoteMessage.getData().get("shop_id"));
        String noti_id_str = String.valueOf(new Date().getTime());

        if(remoteMessage.getData().containsKey("notification_id")) {
            Object obj = remoteMessage.getData().get("notification_id");
            if(obj instanceof Integer) {
                noti_id_str = (new Date().getTime()) + remoteMessage.getData().get("notification_id");
            }
        }

        noti_id = (int) (Long.parseLong(noti_id_str) % Integer.MAX_VALUE);

//        db = new DatabaseHelper(getApplicationContext());
//        if (img.contentEquals("NA")) {
        sendTextNotification("", msgBody);
//        } else {
//            image = getBitmapFromURL(img);
//            sendNotification(tag, msg, image);
//        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */

    private void sendTextNotification(String tag, String messageBody) {
        PendingIntent pendingIntent = null;
//        Intent intent = null;
        /*Intent intent = new Intent(this, Notifications.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
                PendingIntent.FLAG_ONE_SHOT);*/
        /*if (tag.equalsIgnoreCase("Login")) {

            intent = new Intent(this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
            Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(mContext, 0 *//* Request code *//*, intent,
                    PendingIntent.FLAG_ONE_SHOT);

        } else if (tag.equalsIgnoreCase("about")) {

            intent = new Intent(this, About_us.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(mContext, 0 *//* Request code *//*, intent,
                    PendingIntent.FLAG_ONE_SHOT);

        } else {
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(mContext, 0 *//* Request code *//*, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }*/

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
/*
        RemoteViews notificationView = new RemoteViews(getPackageName(),
                R.layout.custom_notification_layout);



        Intent intent = new Intent(getApplicationContext(), NotificationActionBroadcastReceiver.class);

        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0,
                intent, 0);

        notificationView.setOnClickPendingIntent(R.id.closeOnFlash,
                pendingSwitchIntent);*/


        Intent accessIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(utils.getWebsiteUrl(shop_id)));
        accessIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //define text to be shown in expanded mode
        NotificationCompat.BigTextStyle bigxtstyle =
                new NotificationCompat.BigTextStyle();
        bigxtstyle.bigText(messageBody);
        bigxtstyle.setBigContentTitle(title);

        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_ans1)
                .setContentTitle(title)
                .setStyle(bigxtstyle)
                .setAutoCancel(false)
                .setSound(defaultSoundUri)
                .setContentIntent(PendingIntent.getActivity(this, 0, accessIntent, 0))
                .setPriority(Notification.PRIORITY_MAX)
                .setContentText(msgBody);

        /*action buttons */

        Intent guidanceIntent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/dir/?api=1&origin=my location&destination="+utils.getLocation(shop_id)+"&travelmode=driving"));
        /*https://www.google.com/maps/dir/?api=1&origin=Uptown Pets, 35039 Yucaipa Blvd, Yucaipa, CA 92399&destination=Frisch's Clock Chalet & Gift Shop&travelmode=driving*/
        guidanceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(utils.getTelephoneNumber(shop_id)));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        /*notificationBuilder.addAction(R.drawable.ic_open_in_browser_black_24px, "ACCESS",
                PendingIntent.getActivity(this, 0, accessIntent, 0));*/

        notificationBuilder.addAction(R.drawable.ic_directions_black_24px, "GUIDANCE",
                PendingIntent.getActivity(this, 1, guidanceIntent, 0));

        notificationBuilder.addAction(R.drawable.ic_call_black_24px, "CALL",
                PendingIntent.getActivity(this, 2, callIntent, 0));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//        int noti_id = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        notificationManager.notify(noti_id/* ID of notification */
                , notificationBuilder.build());


    }

}
