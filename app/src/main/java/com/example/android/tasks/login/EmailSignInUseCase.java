package com.example.android.tasks.login;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

/**
 * Use case that manages everything related to email/password sign in/up.
 */
final class EmailSignInUseCase {

    private static final String TAG = EmailSignInUseCase.class.getSimpleName();

    private static final EmailSignInUseCase INSTANCE = new EmailSignInUseCase();

    static EmailSignInUseCase getInstance() {
        return INSTANCE;
    }

    private EmailSignInUseCase() {
    }

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    LiveData<SignInStatus> trySignIn(String email, String password) {
        Task<AuthResult> authTask = firebaseAuth.signInWithEmailAndPassword(email, password);
        return getStatusOfAuthTask(authTask);
    }

    LiveData<SignInStatus> trySignUp(String email, String password) {
        Task<AuthResult> authTask = firebaseAuth.createUserWithEmailAndPassword(email, password);
        return getStatusOfAuthTask(authTask);
    }

    private LiveData<SignInStatus> getStatusOfAuthTask(Task<AuthResult> authTask) {
        MutableLiveData<SignInStatus> signInStatusLiveData = new MutableLiveData<>();

        authTask.addOnCompleteListener(resultTask -> {
            if (resultTask.isSuccessful()) {
                assert firebaseAuth.getCurrentUser() != null;
                Log.d(TAG, "Sign in successful");
                signInStatusLiveData.setValue(SignInStatus.SUCCESS);
            } else {
                Exception exception = resultTask.getException();
                Log.w(TAG, "Sign in failed.", exception);

                SignInStatus signInStatus;
                if (exception instanceof FirebaseAuthInvalidUserException) {
                    // User is disabled or doesn't exist.
                    signInStatus = SignInStatus.INVALID_USER;
                } else if (exception instanceof FirebaseAuthWeakPasswordException) {
                    // Password is too weak.
                    signInStatus = SignInStatus.TOO_WEAK_PASSWORD;
                } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                    // Wrong password or malformed email.
                    signInStatus = SignInStatus.INVALID_CREDENTIALS;
                } else if (exception instanceof FirebaseAuthUserCollisionException) {
                    // Trying to sign up, but a user with this email already exists.
                    signInStatus = SignInStatus.USER_COLLISION;
                } else {
                    // This isn't Firebase related exception. Possibly internet error.
                    signInStatus = SignInStatus.UNKNOWN_ERROR;
                }

                signInStatusLiveData.setValue(signInStatus);
            }
        });

        return signInStatusLiveData;
    }
}
