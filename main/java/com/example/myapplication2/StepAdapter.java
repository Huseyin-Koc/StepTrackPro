package com.example.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {

    private final List<Step> stepList;
    private final Context context;
    private final StepDatabaseManager dbManager;

    public StepAdapter(List<Step> stepList, Context context) {
        this.stepList = stepList;
        this.context = context;
        this.dbManager = new StepDatabaseManager(context);
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_step, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        Step step = stepList.get(position);

        // "Adım Sayısı: " -> R.string.step_count_label
        // "Harcanan Süre: " -> R.string.duration_label
        // "Tarih: " -> R.string.date_label
        // Ek olarak "... dakika" -> R.string.minute_label

        String stepCountText = context.getString(R.string.step_count_label) + step.getStepCount();
        String durationText = context.getString(R.string.duration_label) + step.getDuration()
                + " " + context.getString(R.string.minute_label);
        String dateText = context.getString(R.string.date_label) + step.getDate();

        holder.stepCountTextView.setText(stepCountText);
        holder.durationTextView.setText(durationText);
        holder.dateTextView.setText(dateText);

        // Sil
        holder.deleteButton.setOnClickListener(v -> {
            dbManager.deleteStep(step.getStepCount());
            stepList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, stepList.size());
        });

        // Düzenle
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditStepActivity.class);
            intent.putExtra("stepCount", step.getStepCount());
            intent.putExtra("duration", step.getDuration());
            intent.putExtra("date", step.getDate());
            intent.putExtra("position", position);
            ((MainActivity) context).startActivityForResult(intent, MainActivity.REQUEST_CODE_EDIT_STEP);
        });
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }

    static class StepViewHolder extends RecyclerView.ViewHolder {
        TextView stepCountTextView, durationTextView, dateTextView;
        Button deleteButton, editButton, shareButton;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            stepCountTextView = itemView.findViewById(R.id.nameTextView);
            durationTextView = itemView.findViewById(R.id.durationTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
            shareButton = itemView.findViewById(R.id.shareButton);
        }
    }
}
