package com.example.android.tasks.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Objects;

/**
 * Model class that represents a single subtask of a task.
 * <p>
 * {@code null} {@link #id} indicates that this Task doesn't have any ID.
 */
public class SubTask {

    private final String id;
    private final String title;
    private final boolean completed;

    public SubTask(@NonNull String title, boolean completed) {
        this(null, title, completed);
    }

    public SubTask(@Nullable String id, @NonNull String title, boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    /**
     * {@code null} if this sub task has not yet been inserted into the database.
     */
    @Nullable
    public String getId() {
        return id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubTask subTask = (SubTask) o;
        return completed == subTask.completed &&
            Objects.equals(id, subTask.id) &&
            title.equals(subTask.title);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + title.hashCode();
        result = 31 * result + (completed ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SubTask{" +
            "id='" + id + '\'' +
            ", title='" + title + '\'' +
            ", completed=" + completed +
            '}';
    }
}
