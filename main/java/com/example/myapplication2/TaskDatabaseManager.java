package com.example.myapplication2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TaskDatabaseManager {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public TaskDatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    // Yeni Görev Ekle
    public long addTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TARGET_STEP, task.getTargetStep());
        values.put(DatabaseHelper.COLUMN_TARGET_CALORIE, task.getTargetCalorie());
        values.put(DatabaseHelper.COLUMN_PRIORITY, task.getPriority());
        values.put(DatabaseHelper.COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);


        return database.insert(DatabaseHelper.TABLE_TASKS, null, values);
    }

    // Tüm Görevleri Al
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_TASKS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String targetStep = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TARGET_STEP));
                String targetCalorie = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TARGET_CALORIE));
                String priority = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRIORITY));
                boolean completed = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMPLETED)) == 1;

                Task task = new Task(targetStep, targetCalorie, priority, completed);
                taskList.add(task);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return taskList;
    }

    // Görev Sil
    public void deleteTask(String targetStep) {
        String whereClause = DatabaseHelper.COLUMN_TARGET_STEP  + "=?";
        String[] whereArgs = {targetStep};

        database.delete(DatabaseHelper.TABLE_TASKS, whereClause, whereArgs);
    }

    // Görev Güncelle
    public int updateTask(String oldTargetStep, Task task) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TARGET_STEP, task.getTargetStep());
        values.put(DatabaseHelper.COLUMN_TARGET_CALORIE, task.getTargetCalorie());
        values.put(DatabaseHelper.COLUMN_PRIORITY, task.getPriority());
        values.put(DatabaseHelper.COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);

        String whereClause = DatabaseHelper.COLUMN_TARGET_STEP + "=?";
        String[] whereArgs = {oldTargetStep};


        return database.update(DatabaseHelper.TABLE_TASKS, values, whereClause, whereArgs);
    }
}
