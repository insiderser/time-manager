package com.example.android.tasks.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.tasks.R;
import com.example.android.tasks.data.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {
    private List<Task> tasksList = new ArrayList<>();

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.task_item_view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layoutId, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(tasksList.get(position));
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        CheckBox checkBox;

        public TaskViewHolder (View itemView){
            super(itemView);

            titleView = itemView.findViewById(R.id.task_title);
            checkBox = itemView.findViewById(R.id.task_checkbox);
        }

        void bind(Task task){
            titleView.setText(task.getTitle());
            checkBox.setActivated(task.isCompleted());
        }

    }

    public void setItems(Collection<Task> tasks){
        tasksList.addAll(tasks);
        notifyDataSetChanged();
    }

}
