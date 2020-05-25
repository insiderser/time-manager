package com.example.android.tasks.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import com.example.android.tasks.R;
import com.example.android.tasks.data.SubTask;
import com.example.android.tasks.data.Task;
import com.example.android.tasks.data.TasksRepository;
import java.util.Collections;
import java.util.List;

public class TaskActivity extends BaseActivity {

    public static final String EXTRA_TASK_ID = "task_id";

    private TasksRepository repository;
    private String taskId = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        repository = new TasksRepository();

        if (getIntent().hasExtra(EXTRA_TASK_ID)) {
            taskId = getIntent().getStringExtra(EXTRA_TASK_ID);

            LiveData<Task> taskLiveData = repository.getTask(taskId);
            taskLiveData.observe(this, task -> {
                if (task != null) {
                    displayTask(task);
                }
            });

            LiveData<List<SubTask>> subtasksLiveData = repository.getSubTasksForTask(taskId);
            subtasksLiveData.observe(this, subtasks -> {
                if (subtasks != null) {
                    displaySubtasks(subtasks);
                }
            });
        }
    }

    private void displayTask(Task task) {
        // TODO
    }

    private void displaySubtasks(List<SubTask> subtasks) {
        // TODO
    }

    private void saveTask() {
        String taskId = this.taskId;
        Task task = /*TODO*/ null;
        List<SubTask> subtasks = /*TODO*/ Collections.emptyList();

        repository.insertOrUpdateTask(task, subtasks);
    }
}
