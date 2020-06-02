package com.example.android.tasks.details;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.tasks.R;
import com.example.android.tasks.data.SubTask;
import com.example.android.tasks.data.Task;
import com.example.android.tasks.details.TaskActivityViewModel.Factory;
import com.example.android.tasks.ui.BaseActivity;
import java.util.List;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

public class TaskActivity extends BaseActivity implements SubTasksListener {

    public static final String EXTRA_TASK_ID = "task_id";
    public static final String EXTRA_IN_EDIT_MODE = "in_edit_mode";

    private EditText titleEditText;
    private EditText descriptionEditText;
    private CheckBox completedCheckBox;
    private TextView deadlineTextView;
    private View dateButton;
    private View addSubtaskButton;

    private SubTaskAdapter subTaskAdapter;

    private TaskActivityViewModel viewModel;
    private DeadlineDelegate deadlineDelegate;

    private boolean inEditMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Intent intent = getIntent();
        String taskId = intent.getStringExtra(EXTRA_TASK_ID);
        inEditMode = intent.getBooleanExtra(EXTRA_IN_EDIT_MODE, true);

        ViewModelProvider.Factory viewModelFactory = new Factory(taskId);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(TaskActivityViewModel.class);

        deadlineDelegate = new DeadlineDelegate(this, this::displayDeadline);

        initViews();
        initData();
    }

    private void initViews() {
        titleEditText = findViewById(R.id.task_title);
        descriptionEditText = findViewById(R.id.task_description);
        completedCheckBox = findViewById(R.id.task_completed_checkbox);
        deadlineTextView = findViewById(R.id.task_deadline);
        dateButton = findViewById(R.id.date_button);
        addSubtaskButton = findViewById(R.id.subtask_add_btn);

        initRecyclerView();
        updateEditability();

        completedCheckBox.setOnCheckedChangeListener((checkBox, isChecked) -> {
            if (inEditMode && isChecked) {
                deleteTask();
                finish();
            }
        });

        dateButton.setOnClickListener(v -> deadlineDelegate.chooseNewDeadline());
        addSubtaskButton.setOnClickListener(v -> createNewSubtask());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initRecyclerView() {
        RecyclerView subtaskRecyclerView = findViewById(R.id.subtasks_recycle_view);

        subTaskAdapter = new SubTaskAdapter(this, inEditMode);
        subtaskRecyclerView.setAdapter(subTaskAdapter);
    }

    private void updateEditability() {
        titleEditText.setFocusable(inEditMode);
        descriptionEditText.setFocusable(inEditMode);
        completedCheckBox.setEnabled(inEditMode);
        dateButton.setEnabled(inEditMode);
        addSubtaskButton.setEnabled(inEditMode);
    }

    private void initData() {
        LiveData<Task> taskLiveData = viewModel.getTask();
        LiveData<List<SubTask>> subtasksLiveData = viewModel.getSubtasks();

        taskLiveData.observe(this, task -> {
            if (task != null) {
                deadlineDelegate.setDeadline(task.getDeadline());
                displayTask(task);
            }
        });

        subtasksLiveData.observe(this, subtasks -> {
            if (subtasks != null) {
                displaySubtasks(subtasks);
            }
        });
    }

    private void displayTask(@NonNull Task task) {
        titleEditText.setText(task.getTitle());
        descriptionEditText.setText(task.getDescription());
        completedCheckBox.setChecked(task.isCompleted());
        displayDeadline(task.getDeadline());
    }

    private void displayDeadline(LocalDateTime deadline) {
        String formattedDeadline = deadline != null
            ? formatDateTime(deadline)
            : getString(R.string.choose_deadline);
        deadlineTextView.setText(formattedDeadline);
    }

    private String formatDateTime(@NonNull LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT);
        return formatter.format(dateTime);
    }

    private void displaySubtasks(@NonNull List<SubTask> subtasks) {
        subTaskAdapter.setItems(subtasks);
    }

    private void createNewSubtask() {
        subTaskAdapter.createNewSubtask();
    }

    private void saveTask() {
        if (!inEditMode) {
            return;
        }

        String taskId = viewModel.getTaskId();
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        boolean completed = completedCheckBox.isChecked();
        LocalDateTime deadline = deadlineDelegate.getDeadline();

        Task task = new Task(taskId, title, description, completed, deadline);
        List<SubTask> subtasks = subTaskAdapter.getItemsForSerialization();

        viewModel.save(task, subtasks);
    }

    private void deleteTask() {
        if (inEditMode) {
            viewModel.deleteTask();
        }
    }

    @Override
    public void onSubTaskDeleteButtonClicked(@NonNull SubTask subTask) {
        viewModel.deleteSubtask(subTask);
    }

    @Override
    protected void onStop() {
        saveTask();
        super.onStop();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
