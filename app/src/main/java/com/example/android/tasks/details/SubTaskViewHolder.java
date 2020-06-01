package com.example.android.tasks.details;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.tasks.R;
import com.example.android.tasks.data.SubTask;

class SubTaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
    CheckBox.OnCheckedChangeListener {

    private final EditText titleView;
    private final CheckBox completedCheckBox;

    private final OnSubTaskListener onSubTaskListener;

    private SubTask currentSubTask;

    SubTaskViewHolder(@NonNull View itemView, @NonNull OnSubTaskListener onSubTaskListener, boolean inEditMode) {
        super(itemView);

        titleView = itemView.findViewById(R.id.subtask_title);
        completedCheckBox = itemView.findViewById(R.id.subtask_completed);
        View deleteButton = itemView.findViewById(R.id.subtask_delete);
        this.onSubTaskListener = onSubTaskListener;

        deleteButton.setOnClickListener(this);
        completedCheckBox.setOnCheckedChangeListener(this);

        if (!inEditMode) {
            titleView.setFocusable(false);
            deleteButton.setEnabled(false);
            completedCheckBox.setEnabled(false);
        }
    }

    void bind(SubTask subTask) {
        currentSubTask = subTask;
        titleView.setText(subTask.getTitle());
        completedCheckBox.setChecked(subTask.isCompleted());
    }

    @Override
    public void onClick(View v) {
        onSubTaskListener.onSubTaskDeleteButtonClicked(currentSubTask);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        onSubTaskListener.onSubTaskChecked(currentSubTask, isChecked);
    }
}
