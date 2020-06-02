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
import java.util.List;

class TasksAdapter extends ListAdapter<ListItem, ViewHolder> {

    private static final int TYPE_DATE = 1;
    private static final int TYPE_TASK = 2;

    private final ListItemsProcessor listItemsProcessor = new ListItemsProcessor();
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
        if (tasks == null) {
            // Fast path.
            submitList(null);
            return;
        }

        listItemsProcessor.process(tasks, this::submitList);
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
