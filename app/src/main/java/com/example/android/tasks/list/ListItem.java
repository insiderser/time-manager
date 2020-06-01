package com.example.android.tasks.list;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.android.tasks.data.Task;
import java.util.Objects;
import org.threeten.bp.LocalDate;

abstract class ListItem {

    private ListItem() {
        // Sealed class.
    }

    @Override
    public abstract boolean equals(@Nullable Object obj);

    static class Date extends ListItem {

        private final LocalDate date;

        Date(@Nullable LocalDate date) {
            this.date = date;
        }

        @Nullable
        LocalDate getDate() {
            return date;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Date date1 = (Date) o;
            return Objects.equals(date, date1.date);
        }

        @Override
        public int hashCode() {
            return date.hashCode();
        }
    }

    static class TaskItem extends ListItem {

        private final Task task;

        TaskItem(@NonNull Task task) {
            this.task = task;
        }

        @NonNull
        Task getTask() {
            return task;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TaskItem taskItem = (TaskItem) o;
            return task.equals(taskItem.task);
        }

        @Override
        public int hashCode() {
            return task.hashCode();
        }
    }
}
