package com.example.android.tasks.list;

import androidx.annotation.NonNull;
import com.example.android.tasks.data.Task;

interface OnTaskListener {

    void onTaskClick(@NonNull Task task);

    void onTaskChecked(@NonNull Task task, boolean isChecked);
}
