package com.example.android.tasks.details;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.example.android.tasks.data.SubTask;
import java.util.ArrayList;
import java.util.List;

class SubtasksListDelegate {

    private static final String DEFAULT_TITLE = "";
    private static final boolean DEFAULT_COMPLETED = false;

    private final List<SubTask> subtasks = new ArrayList<>();
    private final RecyclerView.Adapter<?> adapter;

    SubtasksListDelegate(@NonNull Adapter<?> adapter) {
        this.adapter = adapter;
    }

    @NonNull
    SubTask get(int position) {
        return subtasks.get(position);
    }

    List<SubTask> getSubtasks() {
        return subtasks;
    }

    void setSubtasks(@NonNull List<SubTask> newSubtasks) {
        boolean wasEmpty = subtasks.isEmpty();

        subtasks.clear();
        subtasks.addAll(newSubtasks);

        if (wasEmpty) {
            adapter.notifyItemRangeInserted(0, newSubtasks.size());
        } else {
            // TODO: replace with DiffUtils if needed.
            adapter.notifyDataSetChanged();
        }
    }

    int size() {
        return subtasks.size();
    }

    void deleteSubtask(int position) {
        subtasks.remove(position);
        adapter.notifyItemRemoved(position);
    }

    void createNewSubtask() {
        SubTask newSubtask = new SubTask(DEFAULT_TITLE, DEFAULT_COMPLETED);
        subtasks.add(newSubtask);
        int lastIndex = subtasks.size() - 1;
        adapter.notifyItemInserted(lastIndex);
    }

    void setCompleted(int position, boolean isCompleted) {
        SubTask subTask = subtasks.get(position);
        SubTask newSubTask = new SubTask(subTask.getId(), subTask.getTitle(), isCompleted);
        subtasks.set(position, newSubTask);
    }

    void setTitle(int position, String newTitle) {
        SubTask subTask = subtasks.get(position);
        SubTask newSubTask = new SubTask(subTask.getId(), newTitle, subTask.isCompleted());
        subtasks.set(position, newSubTask);
    }
}
