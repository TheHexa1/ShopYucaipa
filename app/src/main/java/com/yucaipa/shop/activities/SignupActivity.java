package com.yucaipa.shop.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.yucaipa.shop.R;
import com.yucaipa.shop.utils.Constants;
import com.yucaipa.shop.utils.MySingleton;
import com.yucaipa.shop.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    Utils utils;
    TextInputEditText tiet_user_email, tiet_user_phone_no;
    SharedPreferences my_pref;
    RelativeLayout mRootContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        my_pref = getSharedPreferences("com.yucaipa.shop",MODE_PRIVATE);

        utils = Utils.getInstance(this);

        if(my_pref.getBoolean("isRegistered",false)){
            startActivity(new Intent(SignupActivity.this, FeaturedPhotoQuiz.class));
        }else {
            setContentView(R.layout.activity_signup);

            mRootContentView = (RelativeLayout) findViewById(R.id.rl_root);
            tiet_user_email = (TextInputEditText) findViewById(R.id.tiet_user_email);
            tiet_user_phone_no = (TextInputEditText) findViewById(R.id.tiet_user_phone_no);

            Glide.with(this).load(R.drawable.yucaipa_logo_trans).into((ImageView) findViewById(R.id.iv_header_logo));

            findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    utils.showpDialog();
                    validateFields();
                }
            });

            checkEULA();
        }


    }

    public void validateFields(){
        if(tiet_user_email.getText().toString().equals("")){
            utils.hidepDialog();
            tiet_user_email.setError("Please enter email id!");
        }else if(!utils.isValidEmail(tiet_user_email.getText().toString())){
            utils.hidepDialog();
            tiet_user_email.setError("Enter valid email id!");
        }
        /*else if(tiet_user_phone_no.getText().toString().equals("")){
            utils.hidepDialog();
            tiet_user_phone_no.setError("Please enter phone number!");
        }*/
        else{
            makeSignUpReq(FirebaseInstanceId.getInstance().getToken());
        }

    }
    
    private void makeSignUpReq(final String fcm_id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SIGNUP_URL,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJson = new JSONObject(response);

//                            if(responseJson.getString("result_code").equals("0")){
                                my_pref.edit().putBoolean("isRegistered",true).apply();
                                utils.showAlertDialog("You are successfully registered");
//                            }else{
//                                my_pref.edit().putBoolean("isRegistered",false).apply();
//                                utils.showAlertDialog(responseJson.getString("message"));
//                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        utils.hidepDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    utils.showAlertDialog("TimeoutError!");
                } else if (error instanceof NoConnectionError){
                    utils.showAlertDialog("Please check your internet connection and try again");
                } else if (error instanceof AuthFailureError){
                    utils.showAlertDialog("Authorization Error!");
                } else if (error instanceof NetworkError){
                    utils.showAlertDialog("Network Error!");
                } else if (error instanceof ServerError){
                    utils.showAlertDialog("Server Error!");
                } else if (error instanceof ParseError){
                    utils.showAlertDialog("JSON Parse Error!");
                }

                utils.hidepDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams (){
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constants.USER_EMAIL, tiet_user_email.getText().toString());
                params.put(Constants.USER_PHONE_NO, tiet_user_phone_no.getText().toString());
                params.put("fcm_id", fcm_id);
                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void checkEULA(){
        if (!my_pref.contains(Constants.PREFKEY_EULA)) {

            findViewById(R.id.btn_signup).setVisibility(View.GONE);

//            enableMenu(false);
            final View eulaView = getLayoutInflater().inflate(R.layout.eula, mRootContentView, false);
            TextView tv_eula = (TextView) eulaView.findViewById(R.id.tv_eula);
            tv_eula.setMovementMethod(LinkMovementMethod.getInstance());
            View cancelView = eulaView.findViewById(R.id.cancel);
            cancelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            View okView = eulaView.findViewById(R.id.ok);
            okView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRootContentView.removeView(eulaView);
                    SharedPreferences.Editor editor = my_pref.edit();
                    editor.putBoolean(Constants.PREFKEY_EULA, true);
                    editor.apply();

                    findViewById(R.id.btn_signup).setVisibility(View.VISIBLE);
                }
            });

            RelativeLayout.LayoutParams eulaParams =
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mRootContentView.addView(eulaView, eulaParams);
        }
    }
}
