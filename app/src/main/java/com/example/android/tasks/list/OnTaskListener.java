package com.example.android.tasks.list;

import androidx.annotation.NonNull;
import com.example.android.tasks.data.Task;

/**
 * A listener to get notified when something in {@link TasksAdapter} happens.
 */
interface OnTaskListener {

    void onTaskClick(@NonNull Task task);

    /**
     * A "complete" flag of the task has been changed.
     */
    void onTaskChecked(@NonNull Task task, boolean isChecked);
}
