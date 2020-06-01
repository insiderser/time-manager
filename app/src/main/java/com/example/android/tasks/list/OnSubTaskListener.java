package com.example.android.tasks.list;

import androidx.annotation.NonNull;

import com.example.android.tasks.data.SubTask;

public interface OnSubTaskListener {
    void onSubTaskClick(@NonNull SubTask subTask);

    void onSubTaskChecked(@NonNull SubTask task, boolean isChecked);
}
