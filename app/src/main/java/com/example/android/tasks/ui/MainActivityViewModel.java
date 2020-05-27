package com.example.android.tasks.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.android.tasks.data.Task;
import com.example.android.tasks.data.TasksRepository;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private final TasksRepository repository = new TasksRepository();

    private final LiveData<List<Task>> tasks = repository.getAllTasksForCurrentUser();

    @NonNull
    public LiveData<List<Task>> getTasks() {
        return tasks;
    }
}
