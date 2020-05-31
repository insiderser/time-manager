package com.example.android.tasks.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.android.tasks.data.Task;
import com.example.android.tasks.data.TasksRepository;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private final TasksRepository repository = new TasksRepository();

    private final LiveData<List<Task>> tasks;

    public MainActivityViewModel(boolean inEditMode) {
        tasks = inEditMode
            ? repository.getAllTasksForCurrentUser()
            : repository.getAllTasksForAllUsers();
    }

    @NonNull
    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    public void deleteTask(@NonNull Task task) {
        String taskId = task.getId();
        repository.deleteTask(taskId);
    }

    public static class Factory implements ViewModelProvider.Factory {

        private final boolean inEditMode;

        public Factory(boolean inEditMode) {
            this.inEditMode = inEditMode;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new MainActivityViewModel(inEditMode);
        }
    }
}
