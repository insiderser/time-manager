package com.example.android.tasks.login;

enum SignInStatus {
    SUCCESS,
    /**
     * Wrong password or malformed email.
     */
    INVALID_CREDENTIALS,
    /**
     * The password is too weak (probably too short).
     */
    TOO_WEAK_PASSWORD,
    /**
     * User is disabled or doesn't exist.
     */
    INVALID_USER,
    /**
     * Trying to sign up, but a user with given email address already exists.
     */
    USER_COLLISION,
    /**
     * Cause is unknown. Probably no internet connection or GMS isn't installed.
     */
    UNKNOWN_ERROR,
    /**
     * Sign in was canceled by the user.
     */
    USER_CANCELLED
}
