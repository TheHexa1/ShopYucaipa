package com.yucaipa.shop.utils;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.yucaipa.shop.R;

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

    public void showAlertDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert);

        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
}
