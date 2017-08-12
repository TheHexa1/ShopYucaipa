package com.yucaipa.shop.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.yucaipa.shop.R;

import java.io.Serializable;

/**
 * Created by Vivek_Hexa on 16-Jul-17.
 */

public class Question implements Parcelable {

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
    private String latitude;
    private String longitude;

    protected Question(Parcel in) {
        queNo = in.readInt();
        queName = in.readString();
        queText = in.readString();
        hint = in.readString();
        choice1 = in.readString();
        choice2 = in.readString();
        choice3 = in.readString();
        ansChoice = in.readInt();
        queImgDrawable = in.readString();
        ansImgDrawable = in.readString();
        latitude = in.readString();
        longitude = in.readString();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(queNo);
        dest.writeString(queName);
        dest.writeString(queText);
        dest.writeString(hint);
        dest.writeString(choice1);
        dest.writeString(choice2);
        dest.writeString(choice3);
        dest.writeInt(ansChoice);
        dest.writeString(queImgDrawable);
        dest.writeString(ansImgDrawable);
        dest.writeString(latitude);
        dest.writeString(longitude);
    }
}
