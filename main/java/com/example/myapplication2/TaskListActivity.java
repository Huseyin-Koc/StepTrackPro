package com.example.myapplication2;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        // Geri butonu işlemi
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish()); // Aktiviteyi kapatır ve önceki ekrana döner

        // Görev listesini alın veya oluşturun
        // Veritabanından görevleri alın
        TaskDatabaseManager dbManager = new TaskDatabaseManager(this);
        taskList = dbManager.getAllTasks(); // TaskData sınıfında tüm görevler saklanıyor

        recyclerView = findViewById(R.id.taskRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TaskAdapter(taskList, this);
        recyclerView.setAdapter(adapter);
    }


}
