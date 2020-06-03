package com.example.android.tasks.details;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.android.tasks.data.SubTask;
import com.example.android.tasks.data.Task;
import com.example.android.tasks.data.TasksRepository;
import com.google.firebase.firestore.util.Util;
import java.util.Collection;
import java.util.List;

/**
 * A container class for {@link TaskActivity} that holds stuff
 * that we want them to live beyond a lifetime of a single activity.
 *
 * @see ViewModel
 */
class TaskActivityViewModel extends ViewModel {

    private final String taskId;

    private final TasksRepository repository = new TasksRepository();

    private final LiveData<Task> task;
    private final LiveData<List<SubTask>> subtasks;

    @SuppressLint("RestrictedApi")
    TaskActivityViewModel(@Nullable String taskId) {
        this.taskId = taskId != null ? taskId : Util.autoId();
        task = repository.getTask(this.taskId);
        subtasks = repository.getSubTasksForTask(this.taskId);
    }

    @NonNull
    LiveData<Task> getTask() {
        return task;
    }

    @NonNull
    LiveData<List<SubTask>> getSubtasks() {
        return subtasks;
    }

    void save(@NonNull Task task, @NonNull Collection<SubTask> subTasks) {
        repository.insertOrUpdateTask(task, subTasks);
    }

    void deleteSubtask(@NonNull SubTask subTask) {
        String subTaskId = subTask.getId();
        if (subTaskId != null) {
            repository.deleteSubtask(subTaskId, taskId);
        }
    }

    void deleteTask() {
        repository.deleteTask(taskId);
    }

    @NonNull
    String getTaskId() {
        return taskId;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.unregisterAllListeners();
    }

    static class Factory implements ViewModelProvider.Factory {

        private final String taskId;

        Factory(@Nullable String taskId) {
            this.taskId = taskId;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new TaskActivityViewModel(taskId);
        }
    }
}
