package com.example.android.tasks.ui;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import com.example.android.tasks.R;
import com.example.android.tasks.data.SubTask;
import com.example.android.tasks.data.Task;
import com.example.android.tasks.data.TasksRepository;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CalendarConstraints.Builder;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import java.util.Collections;
import java.util.List;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

public class TaskActivity extends BaseActivity {

    public static final String EXTRA_TASK_ID = "task_id";
    private static final String KEY_CURRENT_DEADLINE = "current_deadline";

    private EditText titleEditText;
    private EditText descriptionEditText;
    private CheckBox completedCheckBox;
    private TextView deadlineTextView;

    private TasksRepository repository;

    private String taskId;
    private LocalDateTime currentDeadline;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        titleEditText = findViewById(R.id.task_title);
        descriptionEditText = findViewById(R.id.task_description);
        completedCheckBox = findViewById(R.id.task_completed_checkbox);
        deadlineTextView = findViewById(R.id.task_deadline);
        View dateButton = findViewById(R.id.date_button);

        completedCheckBox.setOnCheckedChangeListener((checkBox, isChecked) -> {
            if (isChecked) {
                deleteTask();
                finish();
            }
        });

        dateButton.setOnClickListener(v -> chooseDeadline());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        repository = new TasksRepository();

        Intent intent = getIntent();
        taskId = intent.getStringExtra(EXTRA_TASK_ID);

        boolean editingExistingTask = taskId != null;
        if (editingExistingTask && savedInstanceState == null) {
            fetchTask();
            fetchSubtasks();
        }
    }

    private void fetchTask() {
        LiveData<Task> taskLiveData = repository.getTask(taskId);
        taskLiveData.observe(this, new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                if (task != null) {
                    // Make sure we update UI only once,
                    // so that we don't erase what a user has done.
                    taskLiveData.removeObserver(this);

                    currentDeadline = task.getDeadline();
                    displayTask(task);
                }
            }
        });
    }

    private void fetchSubtasks() {
        LiveData<List<SubTask>> subtasksLiveData = repository.getSubTasksForTask(taskId);
        subtasksLiveData.observe(this, new Observer<List<SubTask>>() {
            @Override
            public void onChanged(List<SubTask> subtasks) {
                if (subtasks != null) {
                    // Make sure we update UI only once,
                    // so that we don't erase what a user has done.
                    subtasksLiveData.removeObserver(this);

                    displaySubtasks(subtasks);
                }
            }
        });
    }

    private void displayTask(@NonNull Task task) {
        titleEditText.setText(task.getTitle());
        descriptionEditText.setText(task.getDescription());
        completedCheckBox.setChecked(task.isCompleted());
        displayDeadline();
    }

    private void displayDeadline() {
        String formattedDeadline = currentDeadline != null ? formatDateTime(currentDeadline) : "";
        deadlineTextView.setText(formattedDeadline);
    }

    private void displaySubtasks(@NonNull List<SubTask> subtasks) {
        // TODO
    }

    private String formatDateTime(@NonNull LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT);
        return formatter.format(dateTime);
    }

    private void chooseDeadline() {
        CalendarConstraints constraints = new Builder()
            .setValidator(DateValidatorPointForward.now())
            .build();

        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraints)
            .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            ZoneId utcZone = ZoneId.of("UTC");
            Instant selectionInstant = Instant.ofEpochMilli(selection);
            currentDeadline = LocalDateTime.ofInstant(selectionInstant, utcZone);

            chooseDeadlineTime();
        });

        picker.show(getSupportFragmentManager(), MaterialDatePicker.class.getName());
    }

    private void chooseDeadlineTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        boolean isSystem24Hour = DateFormat.is24HourFormat(this);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            currentDeadline = currentDeadline.withHour(hourOfDay).withMinute(minute);
            displayDeadline();
        }, currentDateTime.getHour(), currentDateTime.getMinute(), isSystem24Hour);

        timePickerDialog.show();
    }

    private void saveTask() {
        String taskId = this.taskId;
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        boolean completed = completedCheckBox.isChecked();
        LocalDateTime deadline = currentDeadline;

        Task task = new Task(taskId, title, description, completed, deadline);
        List<SubTask> subtasks = /*TODO*/ Collections.emptyList();

        repository.insertOrUpdateTask(task, subtasks);
    }

    private void deleteTask() {
        if (taskId != null) {
            repository.deleteTask(taskId);
        }
    }

    @Override
    public void onBackPressed() {
        saveTask();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        saveTask();
        finish();
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(KEY_CURRENT_DEADLINE, currentDeadline);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        currentDeadline = (LocalDateTime) savedInstanceState.getSerializable(KEY_CURRENT_DEADLINE);
        displayDeadline();
    }
}
