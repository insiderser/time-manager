/*
 * Copyright 2020 Oleksandr Bezushko and Kratiuk Mykhailo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.tasks.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * An {@link AppCompatActivity activity} that either redirects user to {@link MainActivity} if the
 * user is signed in, or to {@code SignInActivity} otherwise.
 */
public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO if user isn't signed in, navigate to sign in screen.
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        // Remove LauncherActivity from the back stack.
        // We don't want users to navigate to this screen.
        finish();
    }
}
