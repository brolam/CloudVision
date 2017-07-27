/*
 * Copyright (C) The Android Open Source Project
 * https://github.com/googlesamples/android-vision
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.brolam.cloudvision.ui;

import android.support.annotation.NonNull;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.brolam.cloudvision.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

/**
 * Atenção: Sempre remover o app executando o :app:uninstallDebug no Android instrumented Test
 * antes de executar o test.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class UserSignInTest  {
    private static final String TAG = "UserSignInTest";
    private static final String USER_EMAIL = "test@brolam.com.br";
    private static final String USER_PASSWORD = "123.test";
    private FirebaseAuth firebaseAuth;
    private boolean isSetupCompleted = false;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);


    @Before
    public void setup() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        Task<AuthResult> firebaseAuthTask = firebaseAuth.createUserWithEmailAndPassword(USER_EMAIL, USER_PASSWORD);
        firebaseAuthTask.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                UserSignInTest.this.isSetupCompleted = true;

            }
        });
    }

    @Test
    public void signInTest() {

        while (this.isSetupCompleted == false){
            Log.i(TAG, "Setup is not completed");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        ViewInteraction appCompatButton = onView(allOf(withId(R.id.email_provider),isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textInputEditText = onView(allOf(withId(R.id.email), isDisplayed()));
        textInputEditText.perform(replaceText(USER_EMAIL), closeSoftKeyboard());

        ViewInteraction nextButton = onView(allOf(withId(R.id.button_next), isDisplayed()));
        nextButton.perform(click());

        ViewInteraction textInputEditText2 = onView(allOf(withId(R.id.password), isDisplayed()));
        textInputEditText2.perform(replaceText(USER_PASSWORD), closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(allOf(withId(R.id.button_done), isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction floatingActionButton = onView(allOf(withId(R.id.fab_add), isDisplayed()));
        floatingActionButton.check(matches(withContentDescription("Add Note Vision")));

    }
}
