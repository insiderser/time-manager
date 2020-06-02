package com.example.android.tasks.utils;

import androidx.annotation.NonNull;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class BackgroundExecutor implements Executor {

    private static final BackgroundExecutor INSTANCE = new BackgroundExecutor();

    @NonNull
    public static BackgroundExecutor getInstance() {
        return INSTANCE;
    }

    private final ExecutorService executors = Executors.newCachedThreadPool();

    private BackgroundExecutor() {
    }

    @Override
    public void execute(Runnable command) {
        executors.execute(command);
    }
}
