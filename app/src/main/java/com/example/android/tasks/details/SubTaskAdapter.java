package com.example.android.tasks.details;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.tasks.R;
import com.example.android.tasks.data.SubTask;
import com.example.android.tasks.details.SubTaskAdapter.SubTaskViewHolder;
import com.example.android.tasks.utils.SimpleTextWatcher;
import java.util.List;

public class SubTaskAdapter extends RecyclerView.Adapter<SubTaskViewHolder> {

    private final SubtasksListDelegate listDelegate = new SubtasksListDelegate(this);
    private final SubTasksListener subTasksListener;
    private final boolean inEditMode;

    SubTaskAdapter(@NonNull SubTasksListener subTasksListener, boolean inEditMode) {
        this.subTasksListener = subTasksListener;
        this.inEditMode = inEditMode;
    }

    @NonNull
    @Override
    public SubTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.list_item_subtask;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layoutId, parent, false);
        return new SubTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubTaskViewHolder holder, int position) {
        SubTask subTask = listDelegate.get(position);
        holder.bind(subTask);
    }

    @Override
    public int getItemCount() {
        return listDelegate.size();
    }

    void setItems(@NonNull List<SubTask> subTasks) {
        listDelegate.setSubtasks(subTasks);
    }

    void createNewSubtask() {
        listDelegate.createNewSubtask();
    }

    List<SubTask> getItemsForSerialization() {
        return listDelegate.getSubtasks();
    }

    class SubTaskViewHolder extends RecyclerView.ViewHolder {

        private final EditText titleView;
        private final CheckBox completedCheckBox;

        SubTaskViewHolder(@NonNull View itemView) {
            super(itemView);

            titleView = itemView.findViewById(R.id.subtask_title);
            completedCheckBox = itemView.findViewById(R.id.subtask_completed);
            View deleteButton = itemView.findViewById(R.id.subtask_delete);

            titleView.addTextChangedListener((SimpleTextWatcher) newTitle -> {
                int position = getAdapterPosition();
                listDelegate.setTitle(position, newTitle.toString());
            });

            completedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                listDelegate.setCompleted(position, isChecked);
            });

            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                SubTask subTask = listDelegate.get(position);

                listDelegate.deleteSubtask(position);
                subTasksListener.onSubTaskDeleteButtonClicked(subTask);
            });

            titleView.setFocusable(inEditMode);
            deleteButton.setEnabled(inEditMode);
            completedCheckBox.setEnabled(inEditMode);
        }

        void bind(SubTask subTask) {
            titleView.setText(subTask.getTitle());
            completedCheckBox.setChecked(subTask.isCompleted());
        }
    }
}
