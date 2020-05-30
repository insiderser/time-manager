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
        Query query = firestore.collection(TaskContract.COLLECTION_NAME)
            .whereEqualTo(TaskContract.USER_UID, userUid)
            .orderBy(TaskContract.DEADLINE);
        return getTasksInternal(query);
    }

    /**
     * Retrieves all tasks in the database.
     * This {@link LiveData} will be updated whenever data in the DB changes.
     */
    @NonNull
    public LiveData<List<Task>> getAllTasksForAllUsers() {
        Query query = firestore.collection(TaskContract.COLLECTION_NAME)
            .orderBy(TaskContract.DEADLINE);
        return getTasksInternal(query);
    }

    @NonNull
    private LiveData<List<Task>> getTasksInternal(@NonNull Query query) {
        MutableLiveData<List<Task>> tasksLiveData = new MutableLiveData<>();

        query.addSnapshotListener((snapshot, e) -> {
            if (snapshot != null) {
                List<Task> tasks = new ArrayList<>(snapshot.size());

                for (DocumentSnapshot document : snapshot) {
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
        DocumentReference documentReference = firestore.collection(TaskContract.COLLECTION_NAME)
            .document(taskId);

        return getTaskInternal(documentReference);
    }

    @NonNull
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
        String title = taskSnapshot.get(TaskContract.TITLE, String.class);
        String description = taskSnapshot.get(TaskContract.DESCRIPTION, String.class);
        boolean completed = taskSnapshot.get(TaskContract.COMPLETED, Boolean.TYPE);

        String deadlineTimestamp = taskSnapshot.get(TaskContract.DEADLINE, String.class);
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
        Query query = firestore.collection(TaskContract.COLLECTION_NAME)
            .document(taskId)
            .collection(SubtaskContract.COLLECTION_NAME);

        return getSubTasksInternal(query);
    }

    @NonNull
    private LiveData<List<SubTask>> getSubTasksInternal(Query query) {
        MutableLiveData<List<SubTask>> subTasksLiveData = new MutableLiveData<>();

        query.addSnapshotListener((snapshot, e) -> {
            if (snapshot != null) {
                List<SubTask> subTasks = new ArrayList<>(snapshot.size());

                for (DocumentSnapshot document : snapshot) {
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
        String title = document.get(SubtaskContract.TITLE, String.class);
        boolean completed = document.get(SubtaskContract.COMPLETED, Boolean.TYPE);

        return new SubTask(id, title, completed);
    }

    /**
     * Updates or (if not already) inserts given task and its subtasks into Firestore.
     * <p>
     * To let Firestore auto generate IDs for elements, set their IDs to {@code null}.
     * <p>
     * Why use this? Because if you try to insert a subtask for a task that doesn't exist yet,
     * you will get {@code PERMISSION_DENIED} error.
     *
     * @return ID of the task. If {@link Task#getId()} was null, new ID will be generated;
     * otherwise this will be same as {@link Task#getId()}.
     * @see #insertOrUpdateTask(Task)
     * @see #insertOrUpdateSubTask(SubTask, String)
     */
    @NonNull
    public String insertOrUpdateTask(@NonNull Task task, @NonNull Iterable<SubTask> subTasks) {
        String taskId = insertOrUpdateTask(task);

        for (SubTask subTask : subTasks) {
            insertOrUpdateSubTask(subTask, taskId);
        }

        return taskId;
    }

    /**
     * Updates or (if not already) inserts given task into Firestore.
     * <p>
     * To let Firestore auto generate ID for the task, set task's ID to {@code null}.
     *
     * @return ID of the task. If {@link Task#getId()} was null, new ID will be generated;
     * otherwise this will be same as {@link Task#getId()}.
     * @see #insertOrUpdateTask(Task, Iterable)
     * @see #insertOrUpdateSubTask(SubTask, String)
     */
    @NonNull
    public String insertOrUpdateTask(@NonNull Task task) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        int numberOfFields = 5;
        Map<String, Object> fields = new HashMap<>(numberOfFields);
        fields.put(TaskContract.TITLE, task.getTitle());
        fields.put(TaskContract.DESCRIPTION, task.getDescription());
        fields.put(TaskContract.COMPLETED, task.isCompleted());
        fields.put(TaskContract.DEADLINE, task.getDeadline().toString());
        fields.put(TaskContract.USER_UID, currentUser.getUid());

        CollectionReference tasksCollection = firestore.collection(TaskContract.COLLECTION_NAME);
        String taskId = task.getId();

        return insertOrUpdate(tasksCollection, fields, taskId);
    }

    /**
     * Updates or (if not already) inserts given subtask into Firestore.
     * <p>
     * To let Firestore auto generate ID for the task, set task's ID to {@code null}.
     * <p>
     * <b>Be careful!</b> If you try to insert a subtask for a task that doesn't exist yet,
     * you will get {@code PERMISSION_DENIED} error.
     *
     * @return ID of the subtask. If {@link SubTask#getId()} was null, new ID will be generated;
     * otherwise this will be same as {@link SubTask#getId()}.
     * @see #insertOrUpdateTask(Task)
     * @see #insertOrUpdateTask(Task, Iterable)
     */
    @NonNull
    public String insertOrUpdateSubTask(@NonNull SubTask subTask, @NonNull String parentTaskId) {
        int numberOfFields = 2;
        Map<String, Object> fields = new HashMap<>(numberOfFields);
        fields.put(SubtaskContract.TITLE, subTask.getTitle());
        fields.put(SubtaskContract.COMPLETED, subTask.isCompleted());

        CollectionReference subTasksCollection = firestore.collection(TaskContract.COLLECTION_NAME)
            .document(parentTaskId)
            .collection(SubtaskContract.COLLECTION_NAME);
        String subTaskId = subTask.getId();

        return insertOrUpdate(subTasksCollection, fields, subTaskId);
    }

    /**
     * Updates or (if not already) inserts a document into Firestore.
     *
     * @return ID of the item. If parameter documentId was null, it will be autogenerated; otherwise, same as
     * documentId.
     */
    private String insertOrUpdate(
        CollectionReference parentCollection,
        Map<String, Object> fields,
        @Nullable String documentId
    ) {
        DocumentReference documentReference;
        if (documentId != null) {
            // This document is already in the database. Update it.
            documentReference = parentCollection.document(documentId);
        } else {
            // This will create a new document, automatically generating ID.
            documentReference = parentCollection.document();
        }

        String newDocumentId = documentReference.getId();

        documentReference.set(fields)
            .addOnCompleteListener(result -> {
                String action = documentId != null ? "update" : "insert";
                String documentType = parentCollection.getId();

                if (result.isSuccessful()) {
                    Log.d(TAG, String.format("%s %s successful ", action, documentType));
                } else {
                    Exception e = result.getException();
                    Log.w(TAG, String.format("Failed to %s %s", action, documentType), e);
                }
            });

        return newDocumentId;
    }

    /**
     * Deletes task with given ID.
     */
    public void deleteTask(String taskId) {
        firestore.collection(TaskContract.COLLECTION_NAME)
            .document(taskId)
            .delete()
            .addOnCompleteListener(result -> {
                if (result.isSuccessful()) {
                    Log.d(TAG, "Task successfully deleted.");
                } else {
                    Exception e = result.getException();
                    Log.w(TAG, "Failed to delete task", e);
                }
            });
    }

    private static final class TaskContract {

        static final String COLLECTION_NAME = "tasks";

        static final String TITLE = "title";
        static final String DESCRIPTION = "description";
        static final String COMPLETED = "completed";
        static final String DEADLINE = "deadline";
        static final String USER_UID = "user_uid";
    }

    private static final class SubtaskContract {

        static final String COLLECTION_NAME = "subtasks";

        static final String TITLE = "title";
        static final String COMPLETED = "completed";
    }
}
