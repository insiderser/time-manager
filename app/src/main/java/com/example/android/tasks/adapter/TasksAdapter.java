package com.example.android.tasks.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.tasks.R;
import com.example.android.tasks.data.Task;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    private final List<Task> tasksList = new ArrayList<>();
    private final OnTaskListener onTaskListener;

    public TasksAdapter(OnTaskListener onTaskListener) {
        this.onTaskListener = onTaskListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.task_item_view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layoutId, parent, false);
        return new TaskViewHolder(view, onTaskListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(tasksList.get(position));
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titleView;
        CheckBox completedCheckBox;
        OnTaskListener onTaskListener;

        private Task currentTask = null;

        public TaskViewHolder(View itemView, OnTaskListener onTaskListener) {
            super(itemView);

            titleView = itemView.findViewById(R.id.task_title);
            completedCheckBox = itemView.findViewById(R.id.task_completed);
            this.onTaskListener = onTaskListener;

            itemView.setOnClickListener(this);
        }

        void bind(Task task) {
            currentTask = task;
            titleView.setText(task.getTitle());
            completedCheckBox.setActivated(task.isCompleted());
        }

        @Override
        public void onClick(View v) {
            onTaskListener.onTaskClick(currentTask);
        }
    }

    public interface OnTaskListener {

        void onTaskClick(@NonNull Task task);
    }

    public void setItems(@Nullable Collection<Task> tasks) {
        tasksList.clear();
        if (tasks != null) {
            tasksList.addAll(tasks);
        }
        notifyDataSetChanged();
    }
}
