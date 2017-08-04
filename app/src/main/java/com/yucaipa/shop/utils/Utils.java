package com.yucaipa.shop.utils;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.yucaipa.shop.R;
import com.yucaipa.shop.activities.FeaturedPhotoQuiz;
import com.yucaipa.shop.activities.SignupActivity;

/**
 * Created by Vivek_Hexa on 12-July-17.
 */

public class Utils extends Application {

    Context context;
    public static Utils utils;
    ProgressDialog pDialog;

    public Utils(Context context){

        this.context = context;
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
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
            default:
                return "http://yucaipachamber.org/";
        }
    }
}
