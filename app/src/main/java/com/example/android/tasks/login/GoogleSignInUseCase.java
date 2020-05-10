package com.example.android.tasks.login;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.android.tasks.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Use case that manages Google Sign-in process.
 */
@SuppressWarnings("JavadocReference")
final class GoogleSignInUseCase {

    private static final String TAG = GoogleSignInUseCase.class.getSimpleName();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final GoogleSignInClient googleSignInClient;

    GoogleSignInUseCase(Context context) {
        String clientId = context.getString(R.string.default_web_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder()
            .requestIdToken(clientId)
            .build();

        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    /**
     * Returns {@link Intent} that you can use to launch Google Sign-in flow.
     * The result will be returned to {@link android.app.Activity#onActivityResult(int, int, Intent)}.
     * In onActivityResult, you must call {@link #handleResult(Intent)}.
     *
     * @see #handleResult(Intent)
     * @see android.app.Activity#startActivityForResult(Intent, int)
     */
    Intent getSignInIntent() {
        return googleSignInClient.getSignInIntent();
    }

    /**
     * Must be called from {@link android.app.Activity#onActivityResult(int, int, Intent)}.
     * It tries to sign in to Firebase with Google credentials from given {@link Intent}.
     *
     * @see #getSignInIntent()
     */
    LiveData<SignInStatus> handleResult(@Nullable Intent data) {
        Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = accountTask.getResult(ApiException.class);
            return firebaseSignInWithGoogleAccount(account);
        } catch (ApiException e) {
            int statusCode = e.getStatusCode();
            String statusCodeString = GoogleSignInStatusCodes.getStatusCodeString(statusCode);

            Log.w(TAG, "Google sign in failed: " + statusCodeString, e);

            SignInStatus signInStatus;
            if (statusCode == GoogleSignInStatusCodes.CANCELED) {
                // User didn't choose any accounts.
                signInStatus = SignInStatus.USER_CANCELLED;
            } else {
                signInStatus = SignInStatus.UNKNOWN_ERROR;
            }

            return new MutableLiveData<>(signInStatus);
        }
    }

    private LiveData<SignInStatus> firebaseSignInWithGoogleAccount(GoogleSignInAccount account) {
        MutableLiveData<SignInStatus> signInStatusLiveData = new MutableLiveData<>();

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(result -> {
                if (result.isSuccessful()) {
                    assert firebaseAuth.getCurrentUser() != null;
                    signInStatusLiveData.setValue(SignInStatus.SUCCESS);
                } else {
                    // If sign in fails, display a message to the user.
                    Exception exception = result.getException();
                    Log.w(TAG, "Sign in with credentials failed", exception);

                    signInStatusLiveData.setValue(SignInStatus.UNKNOWN_ERROR);
                }
            });

        return signInStatusLiveData;
    }
}
