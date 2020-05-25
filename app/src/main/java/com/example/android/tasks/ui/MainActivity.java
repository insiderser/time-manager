package com.example.android.tasks.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.tasks.R;
import com.example.android.tasks.adapter.TasksAdapter;
import com.example.android.tasks.data.Task;

import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends BaseActivity implements TasksAdapter.OnTaskListener {
    private RecyclerView tasksRecyclerView;
    private TasksAdapter tasksAdapter;

    private ArrayList<Task> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // This changes AppTheme.Launcher theme to AppTheme.
        // Must be called before super.onCreate(…).
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerView();
        loadTasks();
    }

    private void initRecyclerView(){
        tasksRecyclerView = findViewById(R.id.tasks_recycle_view);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        tasksAdapter = new TasksAdapter(tasks, this);
        tasksRecyclerView.setAdapter(tasksAdapter);
    }

    private Collection<Task> addTasks(){
        tasks.add(new Task("Task 1", "fsa", true, LocalDateTime.now()));
        tasks.add(new Task("Task 2", "f31fasda", true, LocalDateTime.now()));
        return tasks;
    }

    private void loadTasks(){
        Collection<Task> tasks = addTasks();
        tasksAdapter.setItems(tasks);
    }

    @Override
    public void onTaskClick(int position) {
        Intent intent = new Intent(this, TaskActivity.class);

        intent.putExtra("selected_task", tasks.get(position));
        startActivity(intent);
    }
}
