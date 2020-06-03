package com.example.android.tasks.list;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.android.tasks.data.Task;
import com.example.android.tasks.data.TasksRepository;
import java.util.List;

/**
 * A container class for {@link MainActivity} that holds stuff
 * that we want them to live beyond a lifetime of a single activity.
 *
 * @see ViewModel
 */
class MainActivityViewModel extends ViewModel {

    private final TasksRepository repository = new TasksRepository();

    private final LiveData<List<Task>> tasks;

    MainActivityViewModel(boolean inEditMode) {
        tasks = inEditMode
            ? repository.getAllTasksForCurrentUser()
            : repository.getAllTasksForAllUsers();
    }

    @NonNull
    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    void deleteTask(@NonNull Task task) {
        String taskId = task.getId();
        repository.deleteTask(taskId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.unregisterAllListeners();
    }

    static class Factory implements ViewModelProvider.Factory {

        private final boolean inEditMode;

        Factory(boolean inEditMode) {
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
