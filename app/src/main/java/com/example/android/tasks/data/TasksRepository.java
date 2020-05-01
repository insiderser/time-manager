package com.example.android.tasks.data;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.android.tasks.utils.FirebaseUserLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.threeten.bp.LocalDateTime;

/**
 * Repository that manages tasks. Here, you can retrieve, add or update tasks.
 */
public class TasksRepository {

    private static final String TAG = TasksRepository.class.getSimpleName();

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final FirebaseUserLiveData userLiveData = new FirebaseUserLiveData();
    private LiveData<List<Task>> currentUserTasks = null;

    /**
     * Returns {@link LiveData} of all tasks for the current user.
     * This will be updated automatically when user switches account or when database is updated
     * (either locally or remotely).
     * <p>
     * Firestore automatically caches all data for offline use
     * (though we can't be sure this cached data will be up-to-date).
     * <p>
     * <b>Be careful!</b> LiveData value might be {@code null}.
     * This will probably happen when the user signs out.
     */
    @NonNull
    public LiveData<List<Task>> getAllTasksForCurrentUser() {
        MediatorLiveData<List<Task>> tasks = new MediatorLiveData<>();

        tasks.addSource(userLiveData, firebaseUser -> {
            if (currentUserTasks != null) {
                tasks.removeSource(currentUserTasks);
            }

            if (firebaseUser == null) {
                currentUserTasks = null;
                // Make sure we don't leak tasks of a previous user.
                tasks.setValue(null);
            } else {
                currentUserTasks = getAllTasksForUser(firebaseUser.getUid());
                tasks.addSource(currentUserTasks, tasks::setValue);
            }
        });

        return tasks;
    }

    /**
     * Same as {@link #getAllTasksForCurrentUser()}, but you can explicitly specify the user.
     *
     * @see FirebaseAuth#getCurrentUser()
     * @see FirebaseUser#getUid()
     */
    @NonNull
    public LiveData<List<Task>> getAllTasksForUser(@NonNull String userUid) {
        Query query = firestore.collection("tasks")
            .whereEqualTo("user_uid", userUid)
            .orderBy("deadline");
        return getTasksInternal(query);
    }

    /**
     * Retrieves all tasks in the database.
     * This {@link LiveData} will be updated whenever data in the DB changes.
     */
    @NonNull
    public LiveData<List<Task>> getAllTasksForAllUsers() {
        Query query = firestore.collection("tasks")
            .orderBy("deadline");
        return getTasksInternal(query);
    }

    @NonNull
    private LiveData<List<Task>> getTasksInternal(@NonNull Query query) {
        MutableLiveData<List<Task>> tasksLiveData = new MutableLiveData<>();

        query.addSnapshotListener((snapshot, e) -> {
            if (snapshot != null) {
                List<DocumentSnapshot> documents = snapshot.getDocuments();
                List<Task> tasks = new ArrayList<>(documents.size());

                for (DocumentSnapshot document : documents) {
                    Task task = getTask(document);
                    tasks.add(task);
                }

                tasksLiveData.setValue(tasks);
            } else {
                Log.w(TAG, "Error getting list of tasks", e);
                tasksLiveData.setValue(null);
            }
        });

        return tasksLiveData;
    }

    /**
     * Returns data about a single task if it exists.
     *
     * @see Task#getId()
     */
    @NonNull
    public LiveData<Task> getTask(@NonNull String taskId) {
        DocumentReference documentReference = firestore.collection("tasks")
            .document(taskId);

        return getTaskInternal(documentReference);
    }

    @NotNull
    private LiveData<Task> getTaskInternal(DocumentReference documentReference) {
        MutableLiveData<Task> taskLiveData = new MutableLiveData<>();

        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null) {
                Task task = getTask(documentSnapshot);
                taskLiveData.setValue(task);
            } else {
                Log.w(TAG, "Error getting task " + documentReference.getId(), e);
                taskLiveData.setValue(null);
            }
        });

        return taskLiveData;
    }

    @NonNull
    @SuppressWarnings("ConstantConditions")
    private static Task getTask(@NonNull DocumentSnapshot taskSnapshot) {
        String id = taskSnapshot.getId();
        String title = taskSnapshot.get("title", String.class);
        String description = taskSnapshot.get("description", String.class);
        boolean completed = taskSnapshot.get("completed", Boolean.TYPE);

        String deadlineTimestamp = taskSnapshot.get("deadline", String.class);
        LocalDateTime deadline = LocalDateTime.parse(deadlineTimestamp);

        return new Task(id, title, description, completed, deadline);
    }

    /**
     * Returns all subtasks of the task with a given ID.
     *
     * @param taskId ID of the parent task.
     * @see Task#getId()
     */
    @NonNull
    public LiveData<List<SubTask>> getSubTasksForTask(@NonNull String taskId) {
        Query query = firestore.collection("tasks")
            .document(taskId)
            .collection("subtasks");

        return getSubTasksInternal(query);
    }

    @NotNull
    private LiveData<List<SubTask>> getSubTasksInternal(Query query) {
        MutableLiveData<List<SubTask>> subTasksLiveData = new MutableLiveData<>();

        query.addSnapshotListener((snapshot, e) -> {
            if (snapshot != null) {
                List<DocumentSnapshot> documents = snapshot.getDocuments();
                List<SubTask> subTasks = new ArrayList<>(documents.size());

                for (DocumentSnapshot document : documents) {
                    SubTask subTask = getSubTask(document);
                    subTasks.add(subTask);
                }

                subTasksLiveData.setValue(subTasks);
            } else {
                Log.w(TAG, "Error getting subtasks", e);
                subTasksLiveData.setValue(null);
            }
        });

        return subTasksLiveData;
    }

    @NonNull
    @SuppressWarnings("ConstantConditions")
    private static SubTask getSubTask(@NonNull DocumentSnapshot document) {
        String id = document.getId();
        String title = document.get("title", String.class);
        boolean completed = document.get("completed", Boolean.TYPE);

        return new SubTask(id, title, completed);
    }

    /**
     * Updates or (if not already) inserts given task into Firestore.
     * <p>
     * To let Firestore auto generate ID for the task, set task's ID to {@code null}.
     */
    public void addOrUpdateTask(@NonNull Task task) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;

        int numberOfFields = 4;
        Map<String, Object> fields = new HashMap<>(numberOfFields);
        fields.put("title", task.getTitle());
        fields.put("description", task.getDescription());
        fields.put("completed", task.isCompleted());
        fields.put("deadline", task.getDeadline().toString());
        fields.put("user_uid", currentUser.getUid());

        CollectionReference tasksCollection = firestore.collection("tasks");
        String taskId = task.getId();

        addOrUpdateInternal(tasksCollection, fields, taskId);
    }

    /**
     * Updates or (if not already) inserts given subtask into Firestore.
     * <p>
     * To let Firestore auto generate ID for the task, set task's ID to {@code null}.
     */
    public void addOrUpdateSubTask(@NonNull SubTask subTask, @NonNull String parentTaskId) {
        int numberOfFields = 2;
        Map<String, Object> fields = new HashMap<>(numberOfFields);
        fields.put("title", subTask.getTitle());
        fields.put("completed", subTask.isCompleted());

        CollectionReference subTasksCollection = firestore.collection("tasks")
            .document(parentTaskId)
            .collection("subtasks");
        String subTaskId = subTask.getId();

        addOrUpdateInternal(subTasksCollection, fields, subTaskId);
    }

    private void addOrUpdateInternal(
        CollectionReference parentCollection,
        Map<String, Object> fields,
        @Nullable String documentId
    ) {
        DocumentReference newSubTaskReference;
        if (documentId != null) {
            // This subTask is already in the database. Update it.
            newSubTaskReference = parentCollection.document(documentId);
        } else {
            // This will create a new document for the subTask, automatically generating ID.
            newSubTaskReference = parentCollection.document();
        }

        newSubTaskReference.set(fields)
            .addOnCompleteListener(result -> {
                String action = documentId != null ? "update" : "insert";

                if (result.isSuccessful()) {
                    Log.d(TAG, action + " task successful");
                } else {
                    Exception e = result.getException();
                    Log.w(TAG, "Failed to " + action + " task", e);
                }
            });
    }
}