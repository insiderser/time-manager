package com.example.android.tasks.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.example.android.tasks.R;
import com.example.android.tasks.data.Task;
import java.util.ArrayList;
import java.util.List;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

class TasksAdapter extends ListAdapter<ListItem, ViewHolder> {

    private static final int TYPE_DATE = 1;
    private static final int TYPE_TASK = 2;

    private final OnTaskListener onTaskListener;
    private final boolean inEditMode;

    TasksAdapter(@NonNull OnTaskListener onTaskListener, boolean inEditMode) {
        super(new ListItemDiffCallback());

        this.onTaskListener = onTaskListener;
        this.inEditMode = inEditMode;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_TASK) {
            View view = inflater.inflate(R.layout.list_item_task, parent, false);
            return new TaskViewHolder(view, onTaskListener, inEditMode);
        } else if (viewType == TYPE_DATE) {
            View view = inflater.inflate(R.layout.list_item_date, parent, false);
            return new DateViewHolder(view);
        } else {
            throw new IllegalStateException("Unknown view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem item = getItem(position);

        if (holder instanceof TaskViewHolder) {
            TaskViewHolder taskHolder = (TaskViewHolder) holder;
            ListItem.TaskItem taskItem = (ListItem.TaskItem) item;

            taskHolder.bind(taskItem.getTask());
        } else if (holder instanceof DateViewHolder) {
            DateViewHolder dateHolder = (DateViewHolder) holder;
            ListItem.Date dateItem = (ListItem.Date) item;

            dateHolder.bind(dateItem.getDate());
        } else {
            throw new IllegalStateException("Unknown holder: " + holder.getClass().getName());
        }
    }

    void setItems(@Nullable List<Task> tasks) {
        List<ListItem> items = tasks != null ? process(tasks) : null;
        submitList(items);
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

    @Override
    public int getItemViewType(int position) {
        ListItem item = getItem(position);

        if (item instanceof ListItem.Date) {
            return TYPE_DATE;
        } else if (item instanceof ListItem.TaskItem) {
            return TYPE_TASK;
        } else {
            throw new IllegalStateException("Unknown item: " + item.getClass().getName());
        }
    }
}
