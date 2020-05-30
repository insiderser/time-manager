package com.example.android.tasks.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Objects;
import org.threeten.bp.LocalDateTime;

/**
 * Model class that represents a single task.
 * <p>
 * <b>Note</b>: Task doesn't come with all its subtasks — you can get them with a separate request.
 * <p>
 * Rationale behind this: Firebase counts every fetched field for billing purposes.
 * Fetching every task along with its subtasks would significantly grow our usage.
 * <p>
 * {@code null} {@link #id} indicates that this Task doesn't have any ID.
 */
public class Task {

    private final String id;
    private final String title;
    private final String description;
    private final boolean completed;
    private final LocalDateTime deadline;

    public Task(
        @Nullable String id,
        @NonNull String title,
        @NonNull String description,
        boolean completed,
        @NonNull LocalDateTime deadline
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.deadline = deadline;
    }

    /**
     * {@code null} if this Task has not yet been inserted into the database.
     */
    @Nullable
    public String getId() {
        return id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    @NonNull
    public LocalDateTime getDeadline() {
        return deadline;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Task task = (Task) o;

        if (completed != task.completed) {
            return false;
        }
        if (!Objects.equals(id, task.id)) {
            return false;
        }
        if (!Objects.equals(title, task.title)) {
            return false;
        }
        if (!Objects.equals(description, task.description)) {
            return false;
        }
        return Objects.equals(deadline, task.deadline);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + title.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + (completed ? 1 : 0);
        result = 31 * result + deadline.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Task{" +
            "id='" + id + '\'' +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", completed=" + completed +
            ", deadline=" + deadline +
            '}';
    }
}
