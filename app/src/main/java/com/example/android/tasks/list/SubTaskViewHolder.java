package com.example.android.tasks.list;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android.tasks.R;
import com.example.android.tasks.data.SubTask;

class SubTaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        CheckBox.OnCheckedChangeListener {

    private final TextView titleView;
    private final CheckBox completedCheckBox;
    private final OnSubTaskListener onSubTaskListener;

    private SubTask currentSubTask;

    public SubTaskViewHolder(View itemView, OnSubTaskListener onSubTaskListener, boolean inEditMode) {
        super(itemView);

        titleView = itemView.findViewById(R.id.subtask_title);
        completedCheckBox = itemView.findViewById(R.id.subtask_completed);
        this.onSubTaskListener = onSubTaskListener;

        itemView.setOnClickListener(this);
        completedCheckBox.setOnCheckedChangeListener(this);

        completedCheckBox.setEnabled(inEditMode);
    }

    void bind(SubTask subTask) {
        currentSubTask = subTask;
        titleView.setText(subTask.getTitle());
        completedCheckBox.setChecked(subTask.isCompleted());
    }

    @Override
    public void onClick(View v) {
        onSubTaskListener.onSubTaskClick(currentSubTask);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        onSubTaskListener.onSubTaskChecked(currentSubTask, isChecked);
    }
}