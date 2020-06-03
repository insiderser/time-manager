package com.example.android.tasks.details;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.example.android.tasks.data.SubTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class that manages everything regarding the current state of the list
 * (get, create, update, delete).
 * <p>
 * Why can't we just let an Activity to manage list?
 * Because the list is mutable by the user:
 * <p>
 * Suppose the user types something in an {@link android.widget.EditText}.
 * We would save that and full list would get fully reloaded, replacing everything that
 * the user might've typed while reloading was in progress.
 * Also, the currently focused item would probably loose focus.
 * How's the experience? That's why we manage the list ourselves.
 */
class SubtasksListDelegate {

    private static final String TAG = SubtasksListDelegate.class.getSimpleName();

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

    /**
     * @return Read-only current list.
     */
    List<SubTask> getSubtasks() {
        return Collections.unmodifiableList(subtasks);
    }

    void setSubtasks(@NonNull List<SubTask> newSubtasks) {
        boolean wasEmpty = subtasks.isEmpty();

        subtasks.clear();
        subtasks.addAll(newSubtasks);

        if (wasEmpty) {
            adapter.notifyItemRangeInserted(0, newSubtasks.size());
        } else {
            Log.w(TAG, "Replacing a list of subtasks. Do you really want to do this?");
            // Replace with DiffUtils if needed.
            adapter.notifyDataSetChanged();
        }
    }

    int size() {
        return subtasks.size();
    }

    /**
     * Adds new empty subtask to the end of the list.
     */
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

    void deleteSubtask(int position) {
        subtasks.remove(position);
        adapter.notifyItemRemoved(position);
    }
}
