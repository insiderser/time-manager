package com.example.android.tasks.list;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import com.example.android.tasks.data.Task;
import com.example.android.tasks.utils.BackgroundExecutor;
import com.example.android.tasks.utils.MainThreadExecutor;
import java.util.ArrayList;
import java.util.List;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

/**
 * A class to process a list of tasks.
 */
class ListItemsProcessor {

    private final BackgroundExecutor backgroundExecutor = BackgroundExecutor.getInstance();
    private final MainThreadExecutor mainExecutor = new MainThreadExecutor();

    /**
     * Process given list of tasks on a background thread to find
     * where {@link ListItem.Date} items should be amid all tasks.
     *
     * @param consumer Will be called on a main thread.
     * @implNote It processes all tasks, finds where deadline date changes,
     * and puts {@link ListItem.Date} there.
     */
    void process(@NonNull List<Task> tasks, @NonNull Consumer<? super List<ListItem>> consumer) {
        backgroundExecutor.execute(() -> {
            List<ListItem> processed = process(tasks);

            mainExecutor.execute(() -> {
                consumer.accept(processed);
            });
        });
    }

    private List<ListItem> process(@NonNull List<Task> tasks) {
        List<ListItem> list = new ArrayList<>(tasks.size());

        LocalDate previousDate = LocalDate.MIN;
        for (Task task : tasks) {
            LocalDate newDate;
            LocalDateTime deadline = task.getDeadline();

            if (deadline != null) {
                newDate = deadline.toLocalDate();

                if (previousDate == null || newDate.isAfter(previousDate)) {
                    ListItem dateItem = new ListItem.Date(newDate);
                    list.add(dateItem);

                    previousDate = newDate;
                }
            } else if (previousDate != null) {
                ListItem dateItem = new ListItem.Date(null);
                list.add(dateItem);
                previousDate = null;
            }

            ListItem taskItem = new ListItem.TaskItem(task);
            list.add(taskItem);
        }

        return list;
    }
}
