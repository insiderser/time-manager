package com.example.android.tasks.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.tasks.R;
import com.example.android.tasks.data.SubTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SubTaskAdapter extends RecyclerView.Adapter<SubTaskViewHolder> {

    private final List<SubTask> subTasksList = new ArrayList<>();
    private final OnSubTaskListener onSubTaskListener;
    private final boolean inEditMode;

    public SubTaskAdapter(@NonNull OnSubTaskListener onSubTaskListener, boolean inEditMode) {
        this.onSubTaskListener = onSubTaskListener;
        this.inEditMode = inEditMode;
    }

    @NonNull
    @Override
    public SubTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.list_item_subtask;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layoutId, parent, false);
        return new SubTaskViewHolder(view, onSubTaskListener, inEditMode);
    }

    @Override
    public void onBindViewHolder(@NonNull SubTaskViewHolder holder, int position) {
        holder.bind(subTasksList.get(position));
    }

    @Override
    public int getItemCount() {
        return subTasksList.size();
    }

    void setItems(@Nullable Collection<SubTask> subTasks) {
        subTasksList.clear();
        if (subTasks != null) {
            subTasksList.addAll(subTasks);
        }
        notifyDataSetChanged();
    }
}
