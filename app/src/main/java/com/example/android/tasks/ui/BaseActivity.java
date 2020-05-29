package com.example.android.tasks.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.android.tasks.login.LoginActivity;
import com.example.android.tasks.utils.FirebaseUserLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Base activity that shares common logic between different activities.
 * You can put here anything you want.
 * <p>
 * For now, it just observes current user and navigates to {@link LoginActivity} if the user isn't signed in.
 * <p>
 * To use this, in your activity:
 * <pre>
 * public class MyActivity extends BaseActivity {
 *     // Your stuff here.
 * }
 * </pre>
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isUserSignedIn()) {
            navigateToLoginScreen();
        }

        FirebaseUserLiveData userLiveData = new FirebaseUserLiveData();
        userLiveData.observe(this, firebaseUser -> {
            if (!isUserSignedIn()) {
                navigateToLoginScreen();
            }
        });
    }

    protected boolean isUserSignedIn() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        return currentUser != null;
    }

    protected void navigateToLoginScreen() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }
}
