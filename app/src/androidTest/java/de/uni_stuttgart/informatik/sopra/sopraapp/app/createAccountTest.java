package de.uni_stuttgart.informatik.sopra.sopraapp.app;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class createAccountTest {

    @Rule
    public ActivityTestRule<StartSplashActivity> mActivityTestRule = new ActivityTestRule<>(StartSplashActivity.class);

    @Test
    public void createAccountTest() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.login_button), withText("Einloggen"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_layout),
                                        0),
                                2)));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.activity_authentication_create_new_account), withText("Noch keinen Account? Benutzerkonto erstellen"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_layout),
                                        0),
                                4)));
        appCompatTextView.perform(scrollTo(), click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.su_signup_button), withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.signup_layout),
                                        0),
                                6)));
        appCompatButton2.perform(scrollTo(), click());

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.activity_authentication_back_to_login), withText("Zurück zum Login"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.signup_layout),
                                        0),
                                8)));
        appCompatTextView2.perform(scrollTo(), click());

        ViewInteraction appCompatTextView3 = onView(
                allOf(withId(R.id.activity_authentication_create_new_account), withText("Noch keinen Account? Benutzerkonto erstellen"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_layout),
                                        0),
                                4)));
        appCompatTextView3.perform(scrollTo(), click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.su_name_first),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.signup_layout),
                                        0),
                                0)));
        appCompatEditText.perform(scrollTo(), replaceText("e"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.su_name_last),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.signup_layout),
                                        0),
                                1)));
        appCompatEditText2.perform(scrollTo(), replaceText("e"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.su_email),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.signup_layout),
                                        0),
                                2)));
        appCompatEditText3.perform(scrollTo(), replaceText("e"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.su_password),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.signup_layout),
                                        0),
                                3)));
        appCompatEditText4.perform(scrollTo(), replaceText("e"), closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.su_signup_button), withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.signup_layout),
                                        0),
                                6)));
        appCompatButton3.perform(scrollTo(), click());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.su_password_confirm),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.signup_layout),
                                        0),
                                4)));
        appCompatEditText5.perform(scrollTo(), click());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.su_password_confirm),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.signup_layout),
                                        0),
                                4)));
        appCompatEditText6.perform(scrollTo(), replaceText("e"), closeSoftKeyboard());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.su_signup_button), withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.signup_layout),
                                        0),
                                6)));
        appCompatButton4.perform(scrollTo(), click());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.su_email), withText("e"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.signup_layout),
                                        0),
                                2)));
        appCompatEditText7.perform(scrollTo(), click());

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.su_email), withText("e"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.signup_layout),
                                        0),
                                2)));
        appCompatEditText8.perform(scrollTo(), replaceText("e@e.e"));

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.su_email), withText("e@e.e"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.signup_layout),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText9.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.su_signup_button), withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.signup_layout),
                                        0),
                                6)));
        appCompatButton5.perform(scrollTo(), click());

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.login_email),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_layout),
                                        0),
                                0)));
        appCompatEditText10.perform(scrollTo(), replaceText("e"), closeSoftKeyboard());

        ViewInteraction appCompatEditText11 = onView(
                allOf(withId(R.id.login_password),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_layout),
                                        0),
                                1)));
        appCompatEditText11.perform(scrollTo(), replaceText("e"), closeSoftKeyboard());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.login_button), withText("Einloggen"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_layout),
                                        0),
                                2)));
        appCompatButton6.perform(scrollTo(), click());

        ViewInteraction appCompatEditText12 = onView(
                allOf(withId(R.id.login_email), withText("e"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_layout),
                                        0),
                                0)));
        appCompatEditText12.perform(scrollTo(), click());

        ViewInteraction appCompatEditText13 = onView(
                allOf(withId(R.id.login_email), withText("e"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_layout),
                                        0),
                                0)));
        appCompatEditText13.perform(scrollTo(), replaceText("e@e.e"));

        ViewInteraction appCompatEditText14 = onView(
                allOf(withId(R.id.login_email), withText("e@e.e"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_layout),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText14.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText15 = onView(
                allOf(withId(R.id.login_password), withText("e"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_layout),
                                        0),
                                1)));
        appCompatEditText15.perform(scrollTo(), replaceText(""));

        ViewInteraction appCompatEditText16 = onView(
                allOf(withId(R.id.login_password),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_layout),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText16.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.login_button), withText("Einloggen"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_layout),
                                        0),
                                2)));
        appCompatButton7.perform(scrollTo(), click());

        ViewInteraction appCompatEditText17 = onView(
                allOf(withId(R.id.login_password),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_layout),
                                        0),
                                1)));
        appCompatEditText17.perform(scrollTo(), click());

        ViewInteraction appCompatEditText18 = onView(
                allOf(withId(R.id.login_password),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_layout),
                                        0),
                                1)));
        appCompatEditText18.perform(scrollTo(), replaceText("e"), closeSoftKeyboard());

        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.login_button), withText("Einloggen"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_layout),
                                        0),
                                2)));
        appCompatButton8.perform(scrollTo(), click());

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
