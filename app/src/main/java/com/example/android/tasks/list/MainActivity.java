package com.example.android.tasks.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.tasks.R;
import com.example.android.tasks.data.Task;
import com.example.android.tasks.details.TaskActivity;
import com.example.android.tasks.ui.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

/**
 * Activity that displays a list of tasks. Can show either tasks of the current user
 * or all tasks (of all users). When showing tasks of all users, editing is prohibited.
 */
public class MainActivity extends BaseActivity implements OnTaskListener {

    public static final String EXTRA_IN_EDIT_MODE = "view_tasks_for_all_users";
    private static final boolean DEFAULT_EXTRA_IN_EDIT_MODE = true;

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
        RecyclerView tasksRecyclerView = findViewById(R.id.tasks_recycle_view);

        tasksAdapter = new TasksAdapter(this, inEditMode);
        tasksRecyclerView.setAdapter(tasksAdapter);
    }

    private void loadTasks() {
        View emptyView = findViewById(R.id.empty_layout);

        LiveData<List<Task>> tasksLiveData = viewModel.getTasks();
        tasksLiveData.observe(this, tasks -> {
            tasksAdapter.setItems(tasks);

            boolean showEmptyView = tasks != null && tasks.isEmpty();
            emptyView.setVisibility(showEmptyView ? View.VISIBLE : View.GONE);
        });
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

    /**
     * Toggles between viewing tasks of a current user & tasks of all users.
     */
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
