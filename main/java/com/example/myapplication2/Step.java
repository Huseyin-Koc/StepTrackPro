package com.example.myapplication2;

import android.os.Parcel;
import android.os.Parcelable;

public class Step implements Parcelable {
    private String stepCount;   // Adım Sayısı
    private String duration;    // Harcanan Süre
    private String date;        // Tarih

    // Constructor
    public Step(String stepCount, String duration, String date) {
        this.stepCount = stepCount;
        this.duration = duration;
        this.date = date;
    }

    // Parcelable Constructor
    protected Step(Parcel in) {
        stepCount = in.readString();
        duration = in.readString();
        date = in.readString();
    }

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };

    // Getter'lar
    public String getStepCount() {
        return stepCount;
    }

    public void setStepCount(String stepCount) { // Setter eklendi
        this.stepCount = stepCount;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) { // Setter eklendi
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) { // Setter eklendi
        this.date = date;
    }

    public int getStepCountInt() {
        try {
            return Integer.parseInt(stepCount);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Parcelable Metodları
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stepCount);
        dest.writeString(duration);
        dest.writeString(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
