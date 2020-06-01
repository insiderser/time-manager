package com.example.android.tasks.details;

import androidx.annotation.NonNull;
import com.example.android.tasks.data.SubTask;

public interface OnSubTaskListener {

    void onSubTaskDeleteButtonClicked(@NonNull SubTask subTask);

    void onSubTaskChecked(@NonNull SubTask subTask, boolean isChecked);
}
