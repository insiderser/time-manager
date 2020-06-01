package com.example.android.tasks.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.tasks.R;
import com.example.android.tasks.data.Task;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

class TasksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_DATE = 1;
    private static final int TYPE_TASK = 2;

    private final List<Object> list = new ArrayList<>();
    private final OnTaskListener onTaskListener;
    private final boolean inEditMode;

    TasksAdapter(@NonNull OnTaskListener onTaskListener, boolean inEditMode) {
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
        Object item = list.get(position);

        if (holder instanceof TaskViewHolder) {
            TaskViewHolder taskHolder = (TaskViewHolder) holder;
            taskHolder.bind((Task) item);
        } else if (holder instanceof DateViewHolder) {
            DateViewHolder dateHolder = (DateViewHolder) holder;
            dateHolder.bind((LocalDate) item);
        } else {
            throw new IllegalStateException("Unknown holder: " + holder.getClass().getName());
        }
    }

    void setItems(@Nullable Collection<Task> tasks) {
        list.clear();
        if (tasks != null) {
            process(tasks);
        }
        notifyDataSetChanged();
    }

    private void process(@NonNull Collection<Task> tasks) {
        LocalDate previousDate = LocalDate.MIN;
        for (Task task : tasks) {
            LocalDate newDate;
            LocalDateTime deadline = task.getDeadline();

            if (deadline != null) {
                newDate = deadline.toLocalDate();

                if (previousDate == null || newDate.isAfter(previousDate)) {
                    list.add(newDate);
                    previousDate = newDate;
                }
            } else if (previousDate != null) {
                list.add(null);
                previousDate = null;
            }

            list.add(task);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = list.get(position);

        if (item == null || item instanceof LocalDate) {
            return TYPE_DATE;
        } else if (item instanceof Task) {
            return TYPE_TASK;
        } else {
            throw new IllegalStateException("Unknown item: " + item.getClass().getName());
        }
    }
}
