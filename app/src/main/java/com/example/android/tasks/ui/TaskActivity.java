package com.example.android.tasks.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.android.tasks.R;
import com.example.android.tasks.data.Task;

public class TaskActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        if(getIntent().hasExtra("selected_task")) {
            Task task = getIntent().getParcelableExtra("selected_task");
        }

    }
}
