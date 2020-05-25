/*
 * Copyright 2020 Oleksandr Bezushko and Kratiuk Mykhailo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.tasks.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.tasks.R;
import com.example.android.tasks.adapter.TasksAdapter;
import com.example.android.tasks.data.Task;
import com.example.android.tasks.data.TasksRepository;
import java.util.List;

public class MainActivity extends BaseActivity implements TasksAdapter.OnTaskListener {

    private RecyclerView tasksRecyclerView;
    private TasksAdapter tasksAdapter;

    private TasksRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // This changes AppTheme.Launcher theme to AppTheme.
        // Must be called before super.onCreate(…).
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new TasksRepository();

        initRecyclerView();
        loadTasks();
    }

    private void initRecyclerView() {
        tasksRecyclerView = findViewById(R.id.tasks_recycle_view);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        tasksAdapter = new TasksAdapter(this);
        tasksRecyclerView.setAdapter(tasksAdapter);
    }

    private void loadTasks() {
        LiveData<List<Task>> tasksLiveData = repository.getAllTasksForCurrentUser();
        tasksLiveData.observe(this, tasksAdapter::setItems);
    }

    @Override
    public void onTaskClick(@NonNull Task task) {
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra(TaskActivity.EXTRA_TASK_ID, task.getId());
        startActivity(intent);
    }
}
