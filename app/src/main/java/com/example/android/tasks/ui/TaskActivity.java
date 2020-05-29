package com.example.android.tasks.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import com.example.android.tasks.R;
import com.example.android.tasks.data.SubTask;
import com.example.android.tasks.data.Task;
import com.example.android.tasks.data.TasksRepository;
import java.util.Collections;
import java.util.List;

public class TaskActivity extends BaseActivity {

    public static final String EXTRA_TASK_ID = "task_id";

    private EditText titleEditText;
    private EditText descriptionEditText;

    private TasksRepository repository;
    private String taskId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        titleEditText = findViewById(R.id.task_title);
        descriptionEditText = findViewById(R.id.task_description);

        repository = new TasksRepository();

        Intent intent = getIntent();
        taskId = intent.getStringExtra(EXTRA_TASK_ID);

        boolean editingExistingTask = taskId != null;
        if (editingExistingTask && savedInstanceState == null) {
            fetchTask();
            fetchSubtasks();
        }
    }

    private void fetchTask() {
        LiveData<Task> taskLiveData = repository.getTask(taskId);
        taskLiveData.observe(this, new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                if (task != null) {
                    // Make sure we update UI only once,
                    // so that we don't erase what a user has done.
                    taskLiveData.removeObserver(this);

                    displayTask(task);
                }
            }
        });
    }

    private void fetchSubtasks() {
        LiveData<List<SubTask>> subtasksLiveData = repository.getSubTasksForTask(taskId);
        subtasksLiveData.observe(this, new Observer<List<SubTask>>() {
            @Override
            public void onChanged(List<SubTask> subtasks) {
                if (subtasks != null) {
                    // Make sure we update UI only once,
                    // so that we don't erase what a user has done.
                    subtasksLiveData.removeObserver(this);

                    displaySubtasks(subtasks);
                }
            }
        });
    }

    private void displayTask(@NonNull Task task) {
        titleEditText.setText(task.getTitle());
        descriptionEditText.setText(task.getDescription());
    }

    private void displaySubtasks(@NonNull List<SubTask> subtasks) {
        // TODO
    }

    private void saveTask() {
        String taskId = this.taskId;
        Task task = /*TODO*/ null;
        List<SubTask> subtasks = /*TODO*/ Collections.emptyList();

        repository.insertOrUpdateTask(task, subtasks);
    }
}
