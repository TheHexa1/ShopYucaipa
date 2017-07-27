package com.yucaipa.shop.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yucaipa.shop.R;
import com.yucaipa.shop.model.Question;
import com.yucaipa.shop.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class FeaturedPhotoQuiz extends AppCompatActivity {

    TextView tv_show_hint,tv_ans_box,tv_hint;
    RadioGroup rg_ans;
    RadioButton rd_choice_1,rd_choice_2,rd_choice_3;
    LinearLayout ll_answers,ll_hint;
    RelativeLayout rl_hint_ans;
    ImageView iv_que_img,iv_yucaipa_logo;
    Button btn_prev, btn_next;
    Question question;
    List<Question> questions;
    int current_que_no = 0;
    Utils utils;
    RadioGroup.OnCheckedChangeListener checkedChangeListener;
    boolean exit = false;
    AlertDialog dialogInterface;
    String message = "", btn_label = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_photo_quiz);

        utils = Utils.getInstance(this);

//        initViews(getLayoutInflater().inflate(R.layout.custom_quiz_popup_layout,null));

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Question>>(){}.getType();
        questions = gson.fromJson(loadJSONFromAsset("questions.json"), listType);

        //load first que
        question = questions.get(current_que_no);

        tv_ans_box = (TextView) findViewById(R.id.tv_ans_box);
        tv_ans_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHintAnsChoicePopUp(1);
            }
        });

        tv_show_hint = (TextView) findViewById(R.id.tv_show_hint);

        tv_show_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHintAnsChoicePopUp(0);
            }
        });

        iv_que_img = (ImageView) findViewById(R.id.iv_que_img);
        iv_yucaipa_logo = (ImageView) findViewById(R.id.iv_yucaipa_logo);

        Glide.with(this).load(R.drawable.yucaipa_logo_quiz).into(iv_yucaipa_logo);
/*
        Glide.with(this)
                .load(utils.getDrawableResId(question.getQueImgDrawable()))
                .into(iv_que_img);*/

        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_que_no++;// increment index to next que
                if(current_que_no < questions.size()){
                    question = questions.get(current_que_no);
                    loadQueImage();
                }else{
                    current_que_no = questions.size()-1;
                    Toast.makeText(getApplicationContext(),"Done!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_prev = (Button) findViewById(R.id.btn_prev);
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_que_no--; //decrement index to previous question
                if(current_que_no >= 0){
                    question = questions.get(current_que_no);
                    loadQueImage();
                }else{
                    current_que_no = 0;
                    Toast.makeText(getApplicationContext(),"You are at 1st question!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadQueImage();
    }

    private void initViews(View view){

        tv_hint = (TextView) view.findViewById(R.id.tv_hint);
//        tv_que_text = (TextView) findViewById(R.id.tv_que_text);

        rg_ans = (RadioGroup) view.findViewById(R.id.rg_ans);
        checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if (checkedId == R.id.rd_choice_1) {
                    showAnsPopUp(1);
                    rd_choice_1.setChecked(true);
                } else if (checkedId == R.id.rd_choice_2) {
                    showAnsPopUp(2);
                    rd_choice_2.setChecked(true);
                } else if (checkedId == R.id.rd_choice_3) {
                    showAnsPopUp(3);
                    rd_choice_3.setChecked(true);
                }
            }
        };

        rg_ans.setOnCheckedChangeListener(checkedChangeListener);

        rd_choice_1 = (RadioButton) view.findViewById(R.id.rd_choice_1);
        rd_choice_2 = (RadioButton) view.findViewById(R.id.rd_choice_2);
        rd_choice_3 = (RadioButton) view.findViewById(R.id.rd_choice_3);

        ll_answers = (LinearLayout) view.findViewById(R.id.ll_answers);
        ll_hint = (LinearLayout) view.findViewById(R.id.ll_hint);

        rl_hint_ans = (RelativeLayout) view.findViewById(R.id.rl_hint_ans);

    }

    private void loadQueImage(){

        Glide.with(this)
                .load(utils.getDrawableResId(question.getQueImgDrawable()))
                .into(iv_que_img);
//        rg_ans.setOnCheckedChangeListener(null);
/*
        rd_choice_1.setChecked(false);
        rd_choice_2.setChecked(false);
        rd_choice_3.setChecked(false);

        rg_ans.setOnCheckedChangeListener(checkedChangeListener);*/

        tv_show_hint.setTextColor(getResources().getColor(R.color.accent));
        tv_ans_box.setTextColor(getResources().getColor(R.color.accent));
        /*ll_answers.setVisibility(View.GONE);
        ll_hint.setVisibility(View.GONE);
        rl_hint_ans.setVisibility(View.GONE);*/

//        tv_que_text.setText(question.getQueText());
    }

    private void showHint(){
        tv_show_hint.setTextColor(getResources().getColor(R.color.primary));
        tv_ans_box.setTextColor(getResources().getColor(R.color.accent));
        ll_answers.setVisibility(View.GONE);
        ll_hint.setVisibility(View.VISIBLE);
        rl_hint_ans.setVisibility(View.VISIBLE);

        tv_hint.setText(question.getHint());
    }

    private void showAnswerChoices(){
        tv_show_hint.setTextColor(getResources().getColor(R.color.accent));
        tv_ans_box.setTextColor(getResources().getColor(R.color.primary));
        ll_answers.setVisibility(View.VISIBLE);
        ll_hint.setVisibility(View.GONE);
        rl_hint_ans.setVisibility(View.VISIBLE);

        rd_choice_1.setText(question.getChoice1());
        rd_choice_2.setText(question.getChoice2());
        rd_choice_3.setText(question.getChoice3());
    }

    private boolean isAnswerCorrect(int ansChoice, int selectedChoice){
        return ansChoice == selectedChoice;
    }

    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {

            InputStream is = getAssets().open(filename);

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

    public void showAnsPopUp(int selectedChoice){

        View view = getLayoutInflater().inflate(R.layout.image_layout,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(isAnswerCorrect(question.getAnsChoice(), selectedChoice)){

            dialogInterface.dismiss();

            message = "Correct!";
            btn_label = "Navigate";
            builder.setView(view);

            if(question.getQueNo() == 2){
                view.findViewById(R.id.iv_ans_logo).setBackgroundColor(Color.BLACK);
            }else{
                view.findViewById(R.id.iv_ans_logo).setBackgroundColor(Color.WHITE);
            }

            Glide.with(this)
                    .load(utils.getDrawableResId(question.getAnsImgDrawable()))
                    .into((ImageView) view.findViewById(R.id.iv_ans_logo));
        }else{
            message = "Wrong!";
            btn_label = "Try Again!";
        }
        builder.setMessage(message);

        builder.setPositiveButton(btn_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(message.equals("Correct!")){
                    Intent guidanceIntent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/maps/dir/?api=1&origin=my location&destination=Frisch's Clock Chalet & Gift Shop&travelmode=driving"));
        /*https://www.google.com/maps/dir/?api=1&origin=Uptown Pets, 35039 Yucaipa Blvd, Yucaipa, CA 92399&destination=Frisch's Clock Chalet & Gift Shop&travelmode=driving*/
                    guidanceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(guidanceIntent);
                }else {
                    dialog.dismiss();
                }
            }
        });

        final AlertDialog alert =  builder.create();

        alert.show();
    }

    public void showHintAnsChoicePopUp(int flag){

        View view = getLayoutInflater().inflate(R.layout.custom_quiz_popup_layout,null);

        initViews(view);

        if(flag == 0){
            showHint();
        }else if(flag == 1){
            showAnswerChoices();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String message = "", btn_label = "";

        builder.setView(view);

        /*if(isAnswerCorrect(question.getAnsChoice(), selectedChoice)){
            message = "Correct!";
            btn_label = "Navigate";
            builder.setView(view);

            if(question.getQueNo() == 2){
                view.findViewById(R.id.iv_ans_logo).setBackgroundColor(Color.BLACK);
            }else{
                view.findViewById(R.id.iv_ans_logo).setBackgroundColor(Color.WHITE);
            }

            Glide.with(this)
                    .load(utils.getDrawableResId(question.getAnsImgDrawable()))
                    .into((ImageView) view.findViewById(R.id.iv_ans_logo));
        }else{
            message = "Wrong!";
            btn_label = "Try Again!";
        }
        builder.setMessage(message);*/

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alert =  builder.create();

        dialogInterface = alert;

        alert.show();
    }

    @Override
    public void onBackPressed() {


        if (exit) {
            ActivityCompat.finishAffinity(this); // finish activity and exit from the app
        } else {
            Toast.makeText(this, "Press Back again to exit",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }
}
