package com.example.android.tasks;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;

/**
 * An object that represents this application.
 * By default, Android uses {@link android.app.Application},
 * but we can change it to any object that extends Application class.
 * This class will be instantiated only once (until the app gets killed).
 */
public class TasksApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }
}
