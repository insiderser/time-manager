package com.example.android.tasks.details;

import androidx.annotation.NonNull;
import com.example.android.tasks.data.SubTask;

/**
 * A listener for events in subtask list.
 */
@FunctionalInterface
public interface SubTasksListener {

    /**
     * User wants to remove this subtask. Please, delete it.
     */
    void onSubTaskDeleteButtonClicked(@NonNull SubTask subTask);
}
