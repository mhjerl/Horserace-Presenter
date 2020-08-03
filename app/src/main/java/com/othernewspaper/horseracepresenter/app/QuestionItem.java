package com.othernewspaper.horseracepresenter.app;

import android.os.Parcel;
import android.os.Parcelable;

public class QuestionItem implements Parcelable {

    public static final Creator<QuestionItem> CREATOR = new Creator<QuestionItem>() {
        @Override
        public QuestionItem createFromParcel(Parcel in) {
            return new QuestionItem(in);
        }

        @Override
        public QuestionItem[] newArray(int size) {
            return new QuestionItem[size];
        }
    };

    int id;
    String question;
    String option_a;
    String option_b;
    String option_c;
    String option_d;
    int time;
    String ans;



    protected QuestionItem(Parcel in) {
        id = in.readInt();
        question = in.readString();
        option_a = in.readString();
        option_b = in.readString();
        option_c = in.readString();
        option_d = in.readString();
        time = in.readInt();
        ans = in.readString();
    }


    public QuestionItem(int id, String question, String option_a, String option_b, String option_c, String option_d, int time, String ans) {
        this.id = id;
        this.question = question;
        this.option_a = option_a;
        this.option_b = option_b;
        this.option_c = option_c;
        this.option_d = option_d;
        this.time = time;
        this.ans = ans;
    }

    public QuestionItem() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption_a() {
        return option_a;
    }

    public void setOption_a(String option_a) {
        this.option_a = option_a;
    }

    public String getOption_b() {
        return option_b;
    }

    public void setOption_b(String option_b) {
        this.option_b = option_b;
    }

    public String getOption_c() {
        return option_c;
    }

    public void setOption_c(String option_c) {
        this.option_c = option_c;
    }

    public String getOption_d() {
        return option_d;
    }

    public void setOption_d(String option_d) {
        this.option_d = option_d;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(question);
        dest.writeString(option_a);
        dest.writeString(option_b);
        dest.writeString(option_c);
        dest.writeString(option_d);
        dest.writeInt(time);
        dest.writeString(ans);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static Creator<QuestionItem> getCREATOR() {
        return CREATOR;
    }
}
