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

    public static String USER_DEVICE_ID = "device_id";
    public static String USER_EMAIL = "user_email";
    public static String USER_PHONE_NO = "user_phone_no";

    public static String ACTION_PROXIMITY_ALERT = "com.yucaipa.shop";


    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = Geofence.NEVER_EXPIRE; //never expires
    public static final float GEOFENCE_RADIUS_IN_METERS = 500;

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
