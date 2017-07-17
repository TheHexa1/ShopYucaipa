package com.yucaipa.shop.activities;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        utils = Utils.getInstance(this);
        tiet_user_email = (TextInputEditText) findViewById(R.id.tiet_user_email);
        tiet_user_phone_no = (TextInputEditText) findViewById(R.id.tiet_user_phone_no);

        findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*utils.showpDialog();
                validateFields();*/

                startActivity(new Intent(SignupActivity.this, FeaturedPhotoQuiz.class));
            }
        });
    }

    public void validateFields(){
        if(tiet_user_email.getText().toString().equals("")){
            utils.hidepDialog();
            tiet_user_email.setError("Please enter email id!");
        }else if(!utils.isValidEmail(tiet_user_email.getText().toString())){
            utils.hidepDialog();
            tiet_user_email.setError("Enter valid email id!");
        }else if(tiet_user_phone_no.getText().toString().equals("")){
            utils.hidepDialog();
            tiet_user_phone_no.setError("Please enter phone number!");
        }else{
            
        }

    }
    
    private void makeSignUpReq(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SIGNUP_URL,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJson = new JSONObject(response);

                            if(responseJson.getString("result_code").equals("0")){
                                utils.showAlertDialog("You are successfully registered");
                            }else{
                                utils.showAlertDialog(responseJson.getString("message"));
                            }
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
                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
