package com.example.android.tasks.utils;

import android.text.TextWatcher;

/**
 * Listener for changes of text in {@link android.widget.EditText} with default no-op
 * implementation of methods that aren't usually needed.
 */
@FunctionalInterface
public interface SimpleTextWatcher extends TextWatcher {

    @Override
    default void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // no-op
    }

    @Override
    default void onTextChanged(CharSequence s, int start, int before, int count) {
        // no-op
    }
}
