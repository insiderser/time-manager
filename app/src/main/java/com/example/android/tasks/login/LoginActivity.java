package com.example.android.tasks.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import com.example.android.tasks.R;
import com.example.android.tasks.utils.FirebaseUserLiveData;
import com.google.android.gms.common.SignInButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 4994;

    private GoogleSignInUseCase googleSignInUseCase;
    private EmailSignInUseCase emailSignInUseCase;

    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;

    private View rootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        googleSignInUseCase = new GoogleSignInUseCase(this);
        emailSignInUseCase = new EmailSignInUseCase();

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        rootView = findViewById(R.id.root_layout);

        SignInButton googleSignInButton = findViewById(R.id.google_sign_in);
        Button emailSignInButton = findViewById(R.id.sign_in_btn);
        Button emailSignUpButton = findViewById(R.id.sign_up_btn);

        googleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = googleSignInUseCase.getSignInIntent();
            startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_SIGN_IN);
        });

        emailSignInButton.setOnClickListener(v -> {
            tryEmailSignIn(false);
        });

        emailSignUpButton.setOnClickListener(v -> {
            tryEmailSignIn(true);
        });

        FirebaseUserLiveData userLiveData = new FirebaseUserLiveData();
        userLiveData.observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                // The user is signed in. Return back to normal flow.
                finish();
            }
        });
    }

    private void tryEmailSignIn(boolean signUp) {
        resetEmailAndPasswordErrors();

        CharSequence emailSequence = emailEditText.getText();
        CharSequence passwordSequence = passwordEditText.getText();

        if (!TextUtils.isEmpty(emailSequence) && !TextUtils.isEmpty(passwordSequence)) {
            String email = emailSequence.toString();
            String password = passwordSequence.toString();

            LiveData<SignInStatus> authStatusLiveData;
            if (signUp) {
                authStatusLiveData = emailSignInUseCase.trySignUp(email, password);
            } else {
                authStatusLiveData = emailSignInUseCase.trySignIn(email, password);
            }
            handleAuthStatus(authStatusLiveData);
        }
    }

    private void resetEmailAndPasswordErrors() {
        emailEditText.setError(null);
        passwordEditText.setError(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN) {
            LiveData<SignInStatus> authStatusLiveData = googleSignInUseCase.handleResult(data);
            handleAuthStatus(authStatusLiveData);
        }
    }

    private void handleAuthStatus(LiveData<SignInStatus> statusLiveData) {
        statusLiveData.observe(this, signInStatus -> {
            switch (signInStatus) {
                case USER_CANCELLED:
                    // Ignore.
                    break;

                case INVALID_CREDENTIALS:
                    showInvalidCredentialsError();
                    break;

                case TOO_WEAK_PASSWORD:
                    showTooWeakPassword();
                    break;

                case USER_COLLISION:
                    showUserAlreadyExists();
                    break;

                case INVALID_USER:
                    showUserDoesNotExist();
                    break;

                case UNKNOWN_ERROR:
                    showUnknownFailureSnackbar();
            }
        });
    }

    private void showInvalidCredentialsError() {
        Snackbar snackbar = Snackbar.make(rootView, R.string.invalid_credentials, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void showTooWeakPassword() {
        CharSequence error = getText(R.string.too_weak_password);
        passwordEditText.setError(error);
    }

    private void showUserAlreadyExists() {
        Snackbar snackbar = Snackbar.make(rootView, R.string.user_already_exists, Snackbar.LENGTH_SHORT)
            .setAction(R.string.sign_in, v -> tryEmailSignIn(false));

        snackbar.show();
    }

    private void showUserDoesNotExist() {
        Snackbar snackbar = Snackbar.make(rootView, R.string.user_does_not_exist, Snackbar.LENGTH_SHORT)
            .setAction(R.string.sign_up, v -> tryEmailSignIn(true));

        snackbar.show();
    }

    private void showUnknownFailureSnackbar() {
        Snackbar snackbar = Snackbar.make(rootView, R.string.login_failed, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        // Put this app to the background.
        // We don't want not-signed-in users to navigate back to whatever screen they were on.
        moveTaskToBack(true);
    }
}
