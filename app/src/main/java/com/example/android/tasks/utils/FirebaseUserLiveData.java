package com.example.android.tasks.utils;

import androidx.lifecycle.LiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;

/**
 * Observes the current {@link FirebaseUser}.
 * It will be updated whenever the user signs in or signs out.
 * If there is no user logged in, value will be {@code null}.
 * <p>
 * Sample:
 * <pre>
 * FirebaseUserLiveData userLiveData = new FirebaseUserLiveData();
 * userLiveData.observe(this, firebaseUser -> {
 *     doStuff(firebaseUser);
 * });
 * </pre>
 */
public class FirebaseUserLiveData extends LiveData<FirebaseUser> {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final AuthStateListener stateListener = firebaseAuth -> {
        setValue(firebaseAuth.getCurrentUser());
    };

    @Override
    protected void onActive() {
        super.onActive();
        firebaseAuth.addAuthStateListener(stateListener);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        firebaseAuth.removeAuthStateListener(stateListener);
    }
}
