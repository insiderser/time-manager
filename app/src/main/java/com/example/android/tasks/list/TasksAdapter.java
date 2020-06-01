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

class TasksAdapter extends RecyclerView.Adapter<TaskViewHolder> {

    private final List<Task> tasksList = new ArrayList<>();
    private final OnTaskListener onTaskListener;
    private final boolean inEditMode;

    TasksAdapter(@NonNull OnTaskListener onTaskListener, boolean inEditMode) {
        this.onTaskListener = onTaskListener;
        this.inEditMode = inEditMode;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.list_item_task;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layoutId, parent, false);
        return new TaskViewHolder(view, onTaskListener, inEditMode);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(tasksList.get(position));
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    void setItems(@Nullable Collection<Task> tasks) {
        tasksList.clear();
        if (tasks != null) {
            tasksList.addAll(tasks);
        }
        notifyDataSetChanged();
    }
}
