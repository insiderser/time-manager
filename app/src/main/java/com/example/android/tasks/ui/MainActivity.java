package com.example.android.tasks.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.tasks.R;
import com.example.android.tasks.adapter.TasksAdapter;
import com.example.android.tasks.data.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends BaseActivity implements TasksAdapter.OnTaskListener {

    private RecyclerView tasksRecyclerView;
    private TasksAdapter tasksAdapter;

    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // This changes AppTheme.Launcher theme to AppTheme.
        // Must be called before super.onCreate(…).
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        initRecyclerView();
        loadTasks();

        FloatingActionButton addTaskButton = findViewById(R.id.add_task_btn);
        addTaskButton.setOnClickListener(v -> navigateCreateNewTask());
    }

    private void initRecyclerView() {
        tasksRecyclerView = findViewById(R.id.tasks_recycle_view);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        tasksAdapter = new TasksAdapter(this);
        tasksRecyclerView.setAdapter(tasksAdapter);
    }

    private void loadTasks() {
        LiveData<List<Task>> tasksLiveData = viewModel.getTasks();
        tasksLiveData.observe(this, tasksAdapter::setItems);
    }

    @Override
    public void onTaskClick(@NonNull Task task) {
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra(TaskActivity.EXTRA_TASK_ID, task.getId());
        startActivity(intent);
    }

    @Override
    public void onTaskChecked(@NonNull Task task, boolean isChecked) {
        if (isChecked) {
            viewModel.deleteTask(task);
        }
    }

    private void navigateCreateNewTask() {
        Intent intent = new Intent(this, TaskActivity.class);
        startActivity(intent);
    }
}
