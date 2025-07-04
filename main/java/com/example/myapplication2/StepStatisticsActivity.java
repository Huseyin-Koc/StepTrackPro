package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class StepStatisticsActivity extends AppCompatActivity {

    private int totalSteps = 0;
    private int totalCalories = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_statistics);

        ImageView backButton = findViewById(R.id.backButton);
        TextView totalStepsTextView = findViewById(R.id.totalStepsTextView);
        TextView totalCaloriesTextView = findViewById(R.id.totalCaloriesTextView);
        BarChart stepsChart = findViewById(R.id.stepsChart);
        BarChart caloriesChart = findViewById(R.id.caloriesChart);
        Button shareButton = findViewById(R.id.shareButton);

        backButton.setOnClickListener(v -> finish());

        // Veritabanı
        StepDatabaseManager dbManager = new StepDatabaseManager(this);
        List<Step> stepList = dbManager.getAllSteps();

        ArrayList<BarEntry> stepsEntries = new ArrayList<>();
        ArrayList<BarEntry> caloriesEntries = new ArrayList<>();

        for (int i = 0; i < stepList.size(); i++) {
            Step step = stepList.get(i);

            int stepCount = step.getStepCountInt();
            int calories = calculateCalories(stepCount);

            totalSteps += stepCount;
            totalCalories += calories;

            stepsEntries.add(new BarEntry(i, stepCount));
            caloriesEntries.add(new BarEntry(i, calories));
        }

        // Toplam Adım / Kalori
        // DİKKAT: getString(R.string.total_steps_label) -> "Toplam Adım: "
        totalStepsTextView.setText(getString(R.string.total_steps_label) + totalSteps);
        totalCaloriesTextView.setText(getString(R.string.total_calories_label) + totalCalories + " kcal");

        // Adım Grafiği
        BarDataSet stepsDataSet = new BarDataSet(stepsEntries, getString(R.string.step_count_label));
        stepsDataSet.setColor(getResources().getColor(R.color.teal_200));
        stepsDataSet.setValueTextColor(getResources().getColor(R.color.black));
        stepsDataSet.setValueTextSize(12f);

        stepsChart.setData(new BarData(stepsDataSet));
        stepsChart.animateY(1000);
        stepsChart.invalidate();

        // Kalori Grafiği
        BarDataSet caloriesDataSet = new BarDataSet(caloriesEntries, getString(R.string.calorie_label));
        caloriesDataSet.setColor(getResources().getColor(R.color.purple_200));
        caloriesDataSet.setValueTextColor(getResources().getColor(R.color.black));
        caloriesDataSet.setValueTextSize(12f);

        caloriesChart.setData(new BarData(caloriesDataSet));
        caloriesChart.animateY(1000);
        caloriesChart.invalidate();

        // Paylaş
        shareButton.setOnClickListener(v -> {
            StringBuilder shareContent = new StringBuilder();
            // "Toplam Adım: 3000"
            shareContent.append(getString(R.string.total_steps_label)).append(totalSteps).append("\n");
            shareContent.append(getString(R.string.total_calories_label)).append(totalCalories).append(" kcal\n");

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            // "Adım İstatistiklerini Paylaş" -> strings.xml'e ekleyebilirsin ( share_step_stats_text vb.)
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent.toString());
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_step_stats_text)));
        });
    }

    private int calculateCalories(int steps) {
        return (steps / 1000) * 50;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
