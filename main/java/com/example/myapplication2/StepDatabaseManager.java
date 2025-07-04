package com.example.myapplication2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class StepDatabaseManager {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public StepDatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public long addStep(Step step) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_STEP_COUNTER, step.getStepCount());
        values.put(DatabaseHelper.COLUMN_DURATION, step.getDuration());
        values.put(DatabaseHelper.COLUMN_DATE, step.getDate());
        return database.insert(DatabaseHelper.TABLE_STEPS, null, values);
    }

    public List<Step> getAllSteps() {
        List<Step> stepList = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_STEPS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String stepCount = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STEP_COUNTER));
                String duration  =       cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DURATION));
                String date =        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));

                Step step = new Step(stepCount, duration, date);
                stepList.add(step);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return stepList;
    }

    public void deleteStep(String stepCounter) {
        // Silme işlemi için "name" sütununu kullanarak veritabanından sil
        String whereClause = DatabaseHelper.COLUMN_STEP_COUNTER + "=?";
        String[] whereArgs = new String[]{stepCounter};

        database.delete(DatabaseHelper.TABLE_STEPS, whereClause, whereArgs);
    }

    public void updateStep(String oldStepCount, Step newStep) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_STEP_COUNTER, newStep.getStepCount());
        values.put(DatabaseHelper.COLUMN_DURATION, newStep.getDuration());
        values.put(DatabaseHelper.COLUMN_DATE, newStep.getDate());

        String whereClause = DatabaseHelper.COLUMN_STEP_COUNTER + "=?";
        String[] whereArgs = {oldStepCount};

        database.update(DatabaseHelper.TABLE_STEPS, values, whereClause, whereArgs);
    }


}
