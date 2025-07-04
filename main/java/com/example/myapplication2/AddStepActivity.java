package com.example.myapplication2;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddStepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_step);

        // Geri düğmesini bul ve tıklama dinleyicisi ekle
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        // Bileşenler
        EditText stepCountEditText = findViewById(R.id.editTextStepCount); // Adım Sayısı
        EditText durationEditText = findViewById(R.id.editTextDuration);  // Harcanan Süre
        EditText dateEditText = findViewById(R.id.editTextDate);          // Tarih
        Button saveButton = findViewById(R.id.buttonSave);

        // DatePickerDialog ile tarih seçimi
        dateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddStepActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        dateEditText.setText(selectedDate);
                    },
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        });

        // Kaydet Butonu
        saveButton.setOnClickListener(v -> {
            String stepCount = stepCountEditText.getText().toString(); // Adım Sayısı
            String duration = durationEditText.getText().toString();   // Harcanan Süre
            String date = dateEditText.getText().toString();           // Tarih

            Step step = new Step(stepCount, duration, date);
            StepDatabaseManager dbManager = new StepDatabaseManager(this);
            dbManager.addStep(step);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("stepCount", stepCount);
            resultIntent.putExtra("duration", duration);
            resultIntent.putExtra("date", date);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // Geri düğmesine basıldığında
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
