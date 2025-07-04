package com.example.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> taskList;
    private final Context context;
    private final TaskDatabaseManager dbManager;

    public TaskAdapter(List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
        this.dbManager = new TaskDatabaseManager(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // DİKKAT: "Hedef XX adım" -> getString(R.string.target_step_display, task.getTargetStep())
        // Örnek: <string name="target_step_display">Hedef %1$d adım</string>
        String stepStr = context.getString(R.string.target_step_display, task.getTargetStep());

        // "Hedef XX kcal" -> getString(R.string.target_calorie_display, task.getTargetCalorie())
        String calStr = context.getString(R.string.target_calorie_display, task.getTargetCalorie());

        // "Öncelik: " -> getString(R.string.priority_label) + priority
        // <string name="priority_label">Öncelik: </string>
        String priorityStr = context.getString(R.string.priority_label) + task.getPriority();

        holder.targetStepTextView.setText(stepStr);
        holder.targetCalorieTextView.setText(calStr);
        holder.priorityTextView.setText(priorityStr);
        holder.completedCheckBox.setChecked(task.isCompleted());

        // Takvime Ekle
        holder.addToCalendarButton.setOnClickListener(v -> {
            Intent calendarIntent = new Intent(Intent.ACTION_INSERT);
            calendarIntent.setType("vnd.android.cursor.item/event");
            calendarIntent.putExtra(CalendarContract.Events.TITLE,
                    context.getString(R.string.calendar_event_title, task.getTargetStep()));
            calendarIntent.putExtra(CalendarContract.Events.DESCRIPTION,
                    context.getString(R.string.calendar_event_desc, task.getTargetCalorie()));
            calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis());
            calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                    System.currentTimeMillis() + 60 * 60 * 1000);

            context.startActivity(calendarIntent);
        });

        // Tamamlandı
        holder.completedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            dbManager.updateTask(task.getTargetStep(), task);
        });

        // Sil
        holder.deleteButton.setOnClickListener(v -> {
            dbManager.deleteTask(task.getTargetStep());
            taskList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, taskList.size());
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView targetStepTextView, targetCalorieTextView, priorityTextView;
        CheckBox completedCheckBox;
        Button deleteButton, addToCalendarButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            targetStepTextView = itemView.findViewById(R.id.targetStepTextView);
            targetCalorieTextView = itemView.findViewById(R.id.targetCalorieTextView);
            priorityTextView = itemView.findViewById(R.id.priorityTextView);
            completedCheckBox = itemView.findViewById(R.id.completedCheckBox);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            addToCalendarButton = itemView.findViewById(R.id.addToCalendarButton);
        }
    }
}
