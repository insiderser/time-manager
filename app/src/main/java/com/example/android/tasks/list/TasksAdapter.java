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

/**
 * {@link RecyclerView.Adapter} that displays a list of tasks, dividing them based on deadlines.
 * <p>
 * This adapter can show 2 separate view types: date & task.
 * To support that, we represent items as {@link ListItem} class that has 2 subclasses:
 * {@link ListItem.Date} and {@link ListItem.TaskItem}.
 * When a list of tasks is submitted, we process it (on a background thread)
 * to find where deadline date changes and insert {@link ListItem.Date} there.
 * <p>
 * Also it supports 2 separate states: editable & read-only.
 */
class TasksAdapter extends ListAdapter<ListItem, ViewHolder> {

    private static final int TYPE_DATE = 1;
    private static final int TYPE_TASK = 2;

    private final ListItemsProcessor listItemsProcessor = new ListItemsProcessor();
    private final OnTaskListener onTaskListener;
    private final boolean inEditMode;

    /**
     * @param inEditMode If {@code false}, tasks will be read-only, otherwise fully editable
     * (e.g. the user can toggle "completed" flag).
     */
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
