package com.example.android.tasks.list;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.tasks.R;
import com.example.android.tasks.data.Task;

class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
    CheckBox.OnCheckedChangeListener {

    private final TextView titleView;
    private final CheckBox completedCheckBox;
    private final OnTaskListener onTaskListener;

    private Task currentTask = null;

    TaskViewHolder(View itemView, OnTaskListener onTaskListener, boolean inEditMode) {
        super(itemView);

        titleView = itemView.findViewById(R.id.task_title);
        completedCheckBox = itemView.findViewById(R.id.task_completed);
        this.onTaskListener = onTaskListener;

        itemView.setOnClickListener(this);
        completedCheckBox.setOnCheckedChangeListener(this);

        completedCheckBox.setEnabled(inEditMode);
    }

    void bind(Task task) {
        currentTask = task;
        titleView.setText(task.getTitle());
        completedCheckBox.setChecked(task.isCompleted());
    }

    @Override
    public void onClick(View v) {
        onTaskListener.onTaskClick(currentTask);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        onTaskListener.onTaskChecked(currentTask, isChecked);
    }
}
