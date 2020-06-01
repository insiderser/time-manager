package com.example.android.tasks.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.tasks.R;
import com.example.android.tasks.data.Task;
import com.example.android.tasks.details.TaskActivity;
import com.example.android.tasks.ui.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class MainActivity extends BaseActivity implements OnTaskListener {

    public static final String EXTRA_IN_EDIT_MODE = "view_tasks_for_all_users";
    private static final boolean DEFAULT_EXTRA_IN_EDIT_MODE = true;

    private RecyclerView tasksRecyclerView;
    private TasksAdapter tasksAdapter;

    private MainActivityViewModel viewModel;

    private boolean inEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // This changes AppTheme.Launcher theme to AppTheme.
        // Must be called before super.onCreate(…).
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        inEditMode = intent.getBooleanExtra(EXTRA_IN_EDIT_MODE, DEFAULT_EXTRA_IN_EDIT_MODE);

        ViewModelProvider.Factory factory = new MainActivityViewModel.Factory(inEditMode);
        viewModel = new ViewModelProvider(this, factory).get(MainActivityViewModel.class);

        initRecyclerView();
        loadTasks();

        FloatingActionButton addTaskButton = findViewById(R.id.add_task_btn);
        addTaskButton.setOnClickListener(v -> navigateCreateNewTask());
    }

    private void initRecyclerView() {
        tasksRecyclerView = findViewById(R.id.tasks_recycle_view);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        tasksAdapter = new TasksAdapter(this, inEditMode);
        tasksRecyclerView.setAdapter(tasksAdapter);
    }

    private void loadTasks() {
        LiveData<List<Task>> tasksLiveData = viewModel.getTasks();
        tasksLiveData.observe(this, tasksAdapter::setItems);
    }

    @Override
    public void onTaskClick(@NonNull Task task) {
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra(TaskActivity.EXTRA_TASK_ID, task.getId());
        intent.putExtra(TaskActivity.EXTRA_IN_EDIT_MODE, inEditMode);
        startActivity(intent);
    }

    @Override
    public void onTaskChecked(@NonNull Task task, boolean isChecked) {
        if (inEditMode && isChecked) {
            viewModel.deleteTask(task);
        }
    }

    private void navigateCreateNewTask() {
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra(TaskActivity.EXTRA_IN_EDIT_MODE, true);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem toggleViewScopeItem = menu.findItem(R.id.toggle_view_scope);
        int titleRes = inEditMode ? R.string.view_scope_for_all_users : R.string.view_scope_for_current_user;
        toggleViewScopeItem.setTitle(titleRes);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toggle_view_scope:
                toggleViewScope();
                return true;

            case R.id.sign_out:
                signOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toggleViewScope() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_IN_EDIT_MODE, !inEditMode);
        startActivity(intent);

        // Disable animation.
        overridePendingTransition(0, 0);

        finish();
    }

    private void signOut() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
    }
}