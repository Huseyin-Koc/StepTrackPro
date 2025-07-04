package com.example.myapplication2;

import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable {
    private String targetStep; // title -> targetStep
    private String targetCalorie; // description -> targetCalorie
    private String priority;
    private boolean completed;


    // Constructor
    public Task( String targetStep, String targetCalorie, String priority, boolean completed) {
        this.targetStep = targetStep;
        this.targetCalorie = targetCalorie;
        this.priority = priority;
        this.completed = completed;
    }

    // Parcelable Constructor
    protected Task(Parcel in) {
        targetStep = in.readString();
        targetCalorie = in.readString();
        priority = in.readString();
        completed = in.readByte() != 0; // true if byte != 0
    }

    // Parcelable CREATOR
    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };



    public String getTargetStep() {
        return targetStep;
    }

    public void setTargetStep(String targetStep) {
        this.targetStep = targetStep;
    }


    public String getTargetCalorie() {
        return targetCalorie;
    }

    public void setTargetCalorie(String targetCalorie) {
        this.targetCalorie = targetCalorie;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // Parcelable Implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(targetStep);
        dest.writeString(targetCalorie);
        dest.writeString(priority);
        dest.writeByte((byte) (completed ? 1 : 0)); // Write 1 if true, else 0
    }
}
