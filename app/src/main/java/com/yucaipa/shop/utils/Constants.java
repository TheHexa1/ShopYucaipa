package com.yucaipa.shop.utils;

import com.google.android.gms.location.Geofence;
import com.yucaipa.shop.model.Location;

import java.util.HashMap;

/**
 * Created by Vivek_Hexa on 28-May-17.
 */

public class Constants {

    public static String BASE_URL = "http://solankivivek.com/api";
    public static String SIGNUP_URL = BASE_URL + "/register/register.php";
    public static String RATINGS_URL = BASE_URL + "/send_user_ratings.php";
    public static String RECORD_OF_PURCHASE_URL = BASE_URL + "/record_user_purchase_new.php";

    public static String USER_DEVICE_ID = "device_id";
    public static String USER_EMAIL = "user_email";
    public static String USER_PHONE_NO = "user_phone_no";

    public static String ACTION_PROXIMITY_ALERT = "com.yucaipa.shop";

    public static String myPrefKey = "com.yucaipa.shop";


    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = Geofence.NEVER_EXPIRE; //never expires
    public static final float GEOFENCE_RADIUS_IN_METERS = 100; //50;
    public static final float GEOFENCE_DWELL_RADIUS_IN_METERS = 50;//10;

    public static String RAT_1 = "rat_1";
    public static String RAT_2 = "rat_2";
    public static String RAT_3 = "rat_3";
    public static String RAT_4 = "rat_4";

    public static String SHOP_NAME = "shop_name";

    public static String PREFKEY_EULA = "eulaAccepted";

    public static int NORMAL_VOLLEY_TIMEOUT = 10000; //10 secs
    public static int FULL_SYNC_VOLLEY_TIMEOUT = 15000;// 15 secs
    public static int VOLLEY_RETRY_COUNTS = 0;

    /*public static final HashMap<String, Location> LANDMARKS = new HashMap<String, Location>();
    static {
        // San Francisco International Airport.
        LANDMARKS.put("Moscone South", new Location(1,23.228876,72.632656,100));

        // Googleplex.
        LANDMARKS.put("Japantown", new Location(1,13.13,13.31,100));

        // Test
        LANDMARKS.put("SFO", new Location(1,13.13,13.31,100));
    }*/
}
