package com.yucaipa.shop.model;

import com.yucaipa.shop.R;

/**
 * Created by Vivek_Hexa on 16-Jul-17.
 */

public class Question {

    private int queNo;
    private String queName;
    private String queText;
    private String hint;
    private String choice1;
    private String choice2;
    private String choice3;
    private int ansChoice;
    /*private String queImgUrl;
    private String ansImgUrl;*/
    private String queImgDrawable;
    private String ansImgDrawable;

    public int getQueNo() {
        return queNo;
    }

    public void setQueNo(int queNo) {
        this.queNo = queNo;
    }

    public String getQueName() {
        return queName;
    }

    public void setQueName(String queName) {
        this.queName = queName;
    }

    public String getQueText() {
        return queText;
    }

    public void setQueText(String queText) {
        this.queText = queText;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getChoice1() {
        return choice1;
    }

    public void setChoice1(String choice1) {
        this.choice1 = choice1;
    }

    public String getChoice2() {
        return choice2;
    }

    public void setChoice2(String choice2) {
        this.choice2 = choice2;
    }

    public String getChoice3() {
        return choice3;
    }

    public void setChoice3(String choice3) {
        this.choice3 = choice3;
    }

    public int getAnsChoice() {
        return ansChoice;
    }

    public void setAnsChoice(int ansChoice) {
        this.ansChoice = ansChoice;
    }

    public String getQueImgDrawable() {
        return queImgDrawable;
    }

    public void setQueImgDrawable(String queImgDrawable) {
        this.queImgDrawable = queImgDrawable;
    }

    public String getAnsImgDrawable() {
        return ansImgDrawable;
    }

    public void setAnsImgDrawable(String ansImgDrawable) {
        this.ansImgDrawable = ansImgDrawable;
    }
}
