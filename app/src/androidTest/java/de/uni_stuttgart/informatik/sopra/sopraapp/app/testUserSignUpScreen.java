package de.uni_stuttgart.informatik.sopra.sopraapp.app;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.SignUpActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class testUserSignUpScreen {

    @Rule
    public ActivityTestRule<SignUpActivity> mActivityTestRule = new ActivityTestRule<>(SignUpActivity.class);

    @Test
    public void testUserSignUpScreen() {

        onView(withId(R.id.su_name_first))
                .perform(replaceText("Test"));

        onView(withId(R.id.su_name_last))
                .perform(replaceText("UserInterface"));

        onView(withId(R.id.su_email))
                .perform(replaceText("elias.mueller@mailbox.org"));

        onView(withId(R.id.su_password))
                .perform(replaceText("abcd"));

        onView(withId(R.id.su_password_confirm))
                .perform(replaceText("abcd"), closeSoftKeyboard());

        onView(withId(R.id.su_signup_button)).perform(click());

        onView((withId(R.id.sign_up_button)))
                .check(matches(isDisplayed()));


    }
}
