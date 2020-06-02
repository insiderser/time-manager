package com.example.android.tasks.details;

import androidx.annotation.NonNull;
import com.example.android.tasks.data.SubTask;

@FunctionalInterface
public interface SubTasksListener {

    void onSubTaskDeleteButtonClicked(@NonNull SubTask subTask);
}
