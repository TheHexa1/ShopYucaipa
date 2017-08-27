package com.yucaipa.shop.activities;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

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
import com.yucaipa.shop.R;
import com.yucaipa.shop.utils.Constants;
import com.yucaipa.shop.utils.MySingleton;
import com.yucaipa.shop.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RateYourVisitActivity extends AppCompatActivity {

    Toolbar toolbar;
    String rat_1="",rat_2="",rat_3="",rat_4="";
    RadioGroup rg_knowledgeable, rg_efficiency, rg_helpful, rg_overall;
    Utils utils;
    ImageView iv_shop_logo;
    int shop_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_your_visit);

        utils = Utils.getInstance(this);

        Intent i = getIntent();
        shop_id = i.getIntExtra("shop_id",R.drawable.yucaipa_logo_trans);

        toolbar = (Toolbar) findViewById(R.id.rate_your_visit_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rg_knowledgeable = (RadioGroup) findViewById(R.id.rg_knowledgeable);
        rg_efficiency = (RadioGroup) findViewById(R.id.rg_efficiency);
        rg_helpful = (RadioGroup) findViewById(R.id.rg_helpful);
        rg_overall = (RadioGroup) findViewById(R.id.rg_overall_exp);

        iv_shop_logo = (ImageView) findViewById(R.id.iv_shop_logo);

        Glide.with(this).load(shop_id).into(iv_shop_logo);

        rg_knowledgeable.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                switch (checkedId){
                    case R.id.rb_kn_excellent:
                        rat_1 = "4";
                        break;
                    case R.id.rb_kn_good:
                        rat_1 = "3";
                        break;
                    case R.id.rb_kn_fair:
                        rat_1 = "2";
                        break;
                    case R.id.rb_kn_poor:
                        rat_1 = "1";
                        break;
                }

            }
        });

        rg_efficiency.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.rb_ef_excellent:
                        rat_2 = "4";
                        break;
                    case R.id.rb_ef_good:
                        rat_2 = "3";
                        break;
                    case R.id.rb_ef_fair:
                        rat_2 = "2";
                        break;
                    case R.id.rb_ef_poor:
                        rat_2 = "1";
                        break;
                }
            }
        });

        rg_helpful.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                switch (checkedId){
                    case R.id.rb_hl_excellent:
                        rat_3 = "4";
                        break;
                    case R.id.rb_hl_good:
                        rat_3 = "3";
                        break;
                    case R.id.rb_hl_fair:
                        rat_3 = "2";
                        break;
                    case R.id.rb_hl_poor:
                        rat_3 = "1";
                        break;
                }
            }
        });

        rg_overall.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                switch (checkedId){
                    case R.id.rb_ov_excellent:
                        rat_4 = "4";
                        break;
                    case R.id.rb_ov_good:
                        rat_4 = "3";
                        break;
                    case R.id.rb_ov_fair:
                        rat_4 = "2";
                        break;
                    case R.id.rb_ov_poor:
                        rat_4 = "1";
                        break;
                }

            }
        });

        findViewById(R.id.btn_submit_ratings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validateRatings()) {
                    utils.showpDialog();
                    sendRatings();
                }else{
                    Toast.makeText(RateYourVisitActivity.this,"Please give all 4 ratings!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validateRatings(){

        if(rat_1.equals("") || rat_2.equals("") || rat_3.equals("") || rat_4.equals("")){
            return false;
        }else{
            return true;
        }
    }

    private void sendRatings(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.RATINGS_URL,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        printLog(response);

                        if(response.contains("suc")){
                            Toast.makeText(RateYourVisitActivity.this,"Thank you for your valuable time.",Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }else{
                            Toast.makeText(RateYourVisitActivity.this,"Something went wrong. Try again.",Toast.LENGTH_LONG).show();
                        }
                        utils.hidepDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    printLog("TimeoutError!");
                } else if (error instanceof NoConnectionError){
                    printLog("Please check your internet connection and try again");
                } else if (error instanceof AuthFailureError){
                    printLog("Authorization Error!");
                } else if (error instanceof NetworkError){
                    printLog("Network Error!");
                } else if (error instanceof ServerError){
                    printLog("Server Error!");
                } else if (error instanceof ParseError){
                    printLog("JSON Parse Error!");
                }

                utils.hidepDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams (){
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constants.RAT_1, rat_1);
                params.put(Constants.RAT_2, rat_2);
                params.put(Constants.RAT_3, rat_3);
                params.put(Constants.RAT_4, rat_4);
                params.put(Constants.SHOP_NAME, utils.getShopName(shop_id));
                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void printLog(String text){
        Log.i("Rating Response", text);
    }
}
