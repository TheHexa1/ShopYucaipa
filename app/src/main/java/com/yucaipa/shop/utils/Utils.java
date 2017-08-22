package com.yucaipa.shop.utils;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.yucaipa.shop.R;
import com.yucaipa.shop.activities.FeaturedPhotoQuiz;
import com.yucaipa.shop.activities.SignupActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Created by Vivek_Hexa on 12-July-17.
 */

public class Utils extends Application {

    Context context;
    public static Utils utils;
    ProgressDialog pDialog;
    Handler mHandler;

    public Utils(final Context context){
        this.context = context;
    }

    public static Utils getInstance(Context context){
//        if(utils == null){
            utils = new Utils(context);
//        }
        return utils;
    }

    public void showToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void showpDialog() {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
        if (!pDialog.isShowing())
            pDialog.show();
    }

    public void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public String getDeviceID(){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void showAlertDialog(final String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert);

        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(message.contains("registered")){
                    context.startActivity(new Intent(context, FeaturedPhotoQuiz.class));
                }
                dialog.dismiss();
            }
        });

        final AlertDialog alert =  builder.create();

//        alert.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0x000000);
//            }
//        });

        alert.show();
    }

    public int getDrawableResId(String drawableName){
        // get resource id by image name
        return context.getResources().getIdentifier(drawableName, "drawable",context.getPackageName());
    }

    public String getShopName(int flag){
        switch (flag){
            case R.drawable.ans1:
                return "Friches";
            case R.drawable.ans2:
                return "Hickory Ranch";
            case R.drawable.ans3:
                return "Lazer Legacy";
            case R.drawable.ans4:
                return "Sunshine Growers";
            case R.drawable.ans5:
                return "Uptown Pets";
            default:
                return "";
        }
    }

    public String getNotificationText(int flag){
        switch (flag){
            case R.drawable.ans1:
                return "Clock Chalet Cards and Gifts";
            case R.drawable.ans2:
                return "Steakhouse and Sports Bar";
            case R.drawable.ans3:
                return "The Inland Empires Premier Lazer Tag";
            case R.drawable.ans4:
                return "Yucaipa's Landscape Headquarters";
            case R.drawable.ans5:
                return "The Best Prices Online-The Best Service On-site";
            default:
                return "You are nearby to";
        }
    }

    public String getTelephoneNumber(int flag){
        switch (flag){
            case R.drawable.ans1:
                return "9097973249";
            case R.drawable.ans2:
                return "9097901953";
            case R.drawable.ans3:
                return "9097907011";
            case R.drawable.ans4:
                return "9099237277";
            case R.drawable.ans5:
                return "9097975566";
            default:
                return "";
        }
    }

    public String getWebsiteUrl(int flag){
        switch (flag){
            case R.drawable.ans1:
                return "https://www.facebook.com/Frischs-Clock-Chalet-Gifts-508757595820237/";
            case R.drawable.ans2:
                return "http://hickoryranch.com/";
            case R.drawable.ans3:
                return "https://lazerlegacy.net/";
            case R.drawable.ans4:
                return "http://sunshinegrowersnursery.com/";
            case R.drawable.ans5:
                return "https://uptown-pet.com/";
            case 111:
                return "http://yucaipachamber.org/Yucaipa-First";
            case 222:
                return "http://yucaipachamber.org/Explore-Yucaipa";
            default:
                return "http://yucaipachamber.org/";
        }
    }

    public String getLocation(int flag){
        switch (flag){
            case R.drawable.ans1:
                return "Frisch's Clock Chalet & Gift Shop, 35145 Yucaipa Blvd, Yucaipa, CA 92399";
            case R.drawable.ans2:
                return "Hickory Ranch Steakhouse, 32971 Yucaipa Blvd, Yucaipa, CA 92399";
            case R.drawable.ans3:
                return "Lazer Legacy, 32355 Yucaipa Blvd Suite 11, Yucaipa, CA 92399";
            case R.drawable.ans4:
                return "Sunshine Nurseries, 34017 Yucaipa Blvd, Yucaipa, CA 92399";
            case R.drawable.ans5:
                return "Uptown Pets, 35039 Yucaipa Blvd, Yucaipa, CA 92399";
            default:
                return "my location";
        }
    }

    public String getCurrentTime() {
        String delegate = "hh:mm aaa";
        return (String) DateFormat.format(delegate, Calendar.getInstance().getTime());
    }

    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {

            InputStream is = context.getAssets().open(filename);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;

    }
}
