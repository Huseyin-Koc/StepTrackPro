package com.example.myapplication2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "step_database.db";
    private static final int DATABASE_VERSION = 4;

    // Adım Tablosu
    public static final String TABLE_STEPS = "step";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_STEP_COUNTER = "step_counter";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_DATE = "date";

    // Görevler Tablosu
    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_TASK_ID = "_id";
    public static final String COLUMN_TARGET_STEP = "target_step";
    public static final String COLUMN_TARGET_CALORIE = "target_calorie";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_COMPLETED = "completed";
    public static final String COLUMN_URL = "url";

    // Adım Tablosu Oluşturma Sorgusu
    private static final String CREATE_TABLE_STEPS =
            "CREATE TABLE " + TABLE_STEPS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_STEP_COUNTER + " TEXT, " +
                    COLUMN_DURATION + " TEXT, " +
                    COLUMN_DATE + " TEXT);";

    // Görevler Tablosu Oluşturma Sorgusu
    private static final String CREATE_TABLE_TASKS =
            "CREATE TABLE " + TABLE_TASKS + " (" +
                    COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TARGET_STEP + " TEXT, " +
                    COLUMN_TARGET_CALORIE + " TEXT, " +
                    COLUMN_PRIORITY + " TEXT, " +
                    COLUMN_COMPLETED + " INTEGER, " +
                    COLUMN_URL + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STEPS);
        db.execSQL(CREATE_TABLE_TASKS); // Görevler tablosu oluşturuluyor
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) { // Veritabanı sürümüne göre güncelleme yapılır
            db.execSQL(CREATE_TABLE_TASKS);
        }
    }

    public void deleteStep(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STEPS, COLUMN_STEP_COUNTER + "=?", new String[]{name});
        db.close();
    }

    public void deleteTask(String taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COLUMN_TASK_ID + "=?", new String[]{taskId});
        db.close();
    }
}
