package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class EditStepActivity extends AppCompatActivity {

    private int position;
    private String oldStepCount; // Eski stepCount'u saklamak için değişken
    private StepDatabaseManager dbManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_step);

        EditText nameEditText = findViewById(R.id.editTextName);
        EditText durationEditText = findViewById(R.id.editTextDuration);
        EditText dateEditText = findViewById(R.id.editTextDate);
        Button saveButton = findViewById(R.id.buttonSave);

        // Geri düğmesini bul ve tıklama dinleyicisi ekle
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        // Veritabanı yöneticisini başlat
        dbManager = new StepDatabaseManager(this);

        // Gelen verileri al
        Intent intent = getIntent();
        oldStepCount = intent.getStringExtra("stepCount"); // Eski stepCount değerini sakla
        String duration = intent.getStringExtra("duration");
        String date = intent.getStringExtra("date");
        position = intent.getIntExtra("position", -1);

        nameEditText.setText(oldStepCount); // Eski adı nameEditText'e ata
        durationEditText.setText(duration);
        dateEditText.setText(date);

        // Kaydet butonuna tıklanınca
        saveButton.setOnClickListener(v -> {
            String newStepCount = nameEditText.getText().toString();
            String newDuration = durationEditText.getText().toString();
            String newDate = dateEditText.getText().toString();

            // Yeni adımı oluştur
            Step updatedStep = new Step(newStepCount, newDuration, newDate);

            // Veritabanında güncelle
            dbManager.updateStep(oldStepCount, updatedStep);

            // Değişiklikleri geri gönder
            Intent resultIntent = new Intent();
            resultIntent.putExtra("stepCount", newStepCount);
            resultIntent.putExtra("duration", newDuration);
            resultIntent.putExtra("date", newDate);
            resultIntent.putExtra("position", position);
            setResult(RESULT_OK, resultIntent);

            // Aktiviteyi kapat
            finish();
        });
        // Geri butonuna tıklanınca aktiviteyi kapat

    }

}

