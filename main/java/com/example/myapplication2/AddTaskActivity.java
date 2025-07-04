package com.example.myapplication2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AddTaskActivity extends AppCompatActivity {

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_add_task);

        // Geri butonu işlemi
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Bileşenler
        EditText targetStepEditText = findViewById(R.id.targetStepEditText);
        EditText targetCalorieEditText = findViewById(R.id.targetCalorieEditText);
        EditText priorityEditText = findViewById(R.id.priorityEditText);
        Button saveButton = findViewById(R.id.saveButton);

        // Gelen verileri al
        Intent intent = getIntent();
        String targetStep = intent.getStringExtra("targetStep");
        String targetCalorie = intent.getStringExtra("targetCalorie");
        String priority = intent.getStringExtra("priority");
        position = intent.getIntExtra("position", -1);

        targetStepEditText.setText(targetStep);
        targetCalorieEditText.setText(targetCalorie);
        priorityEditText.setText(priority);

        // Kaydet butonu
        saveButton.setOnClickListener(v -> {
            String newTargetStep = targetStepEditText.getText().toString();
            String newTargetCalorie = targetCalorieEditText.getText().toString();
            String newPriority = priorityEditText.getText().toString();

            // Veritabanına ekle veya güncelle
            TaskDatabaseManager dbManager = new TaskDatabaseManager(this);
            Task updatedTask = new Task(newTargetStep, newTargetCalorie, newPriority, false);
            if (position == -1) {
                dbManager.addTask(updatedTask); // Yeni bir görev ekle
            } else {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("position", position);
                resultIntent.putExtra("targetStep", newTargetStep);
                resultIntent.putExtra("targetCalorie", newTargetCalorie);
                resultIntent.putExtra("priority", newPriority);
                setResult(RESULT_OK, resultIntent);
            }

            finish();
        });
    }
}
