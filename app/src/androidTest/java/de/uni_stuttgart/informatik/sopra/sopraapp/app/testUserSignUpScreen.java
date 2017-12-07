package de.uni_stuttgart.informatik.sopra.sopraapp.app;


import android.support.design.widget.TextInputLayout;
import android.support.test.espresso.DataInteraction;
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
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class testUserSignUpScreen {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

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

    @Test
    public void testUserSignUpScreen() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.signup_button), withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pressBack();

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.su_usergroup_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.su_form_layout),
                                        5),
                                1)));
        appCompatSpinner.perform(scrollTo(), click());

        ViewInteraction checkedTextView = onView(
                allOf(withId(android.R.id.text1),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.instanceOf(android.widget.FrameLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        checkedTextView.check(matches(isDisplayed()));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.su_signup_button), withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                allOf(withId(R.id.su_form_layout),
                                        childAtPosition(
                                                withId(R.id.su_form),
                                                0)),
                                6)));
        appCompatButton2.perform(scrollTo(), click());

        ViewInteraction textView = onView(
                allOf(withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Benutzerkonto anlegen")));

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.su_name_first),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText.perform(scrollTo(), click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.su_name_first),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText2.perform(scrollTo(), replaceText("Test "), closeSoftKeyboard());

        pressBack();

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.su_signup_button), withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                allOf(withId(R.id.su_form_layout),
                                        childAtPosition(
                                                withId(R.id.su_form),
                                                0)),
                                6)));
        appCompatButton3.perform(scrollTo(), click());

        ViewInteraction textView2 = onView(
                allOf(withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("Benutzerkonto anlegen")));

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.su_name_last),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText3.perform(scrollTo(), click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.su_name_last),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText4.perform(scrollTo(), replaceText("Userinterface"), closeSoftKeyboard());

        pressBack();

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.su_signup_button), withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                allOf(withId(R.id.su_form_layout),
                                        childAtPosition(
                                                withId(R.id.su_form),
                                                0)),
                                6)));
        appCompatButton4.perform(scrollTo(), click());

        ViewInteraction textView3 = onView(
                allOf(withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                0),
                        isDisplayed()));
        textView3.check(matches(withText("Benutzerkonto anlegen")));

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.su_email),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText5.perform(scrollTo(), click());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.su_email),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText6.perform(scrollTo(), replaceText("elias.muellermailbox.org"), closeSoftKeyboard());

        pressBack();

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.su_signup_button), withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                allOf(withId(R.id.su_form_layout),
                                        childAtPosition(
                                                withId(R.id.su_form),
                                                0)),
                                6)));
        appCompatButton5.perform(scrollTo(), click());

        ViewInteraction textView4 = onView(
                allOf(withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                0),
                        isDisplayed()));
        textView4.check(matches(withText("Benutzerkonto anlegen")));

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.su_password),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText7.perform(scrollTo(), click());

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.su_password),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText8.perform(scrollTo(), replaceText("gutenacht"), closeSoftKeyboard());

        pressBack();

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.su_signup_button), withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                allOf(withId(R.id.su_form_layout),
                                        childAtPosition(
                                                withId(R.id.su_form),
                                                0)),
                                6)));
        appCompatButton6.perform(scrollTo(), click());

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.su_password_confirm),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText9.perform(scrollTo(), click());

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.su_password_confirm),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText10.perform(scrollTo(), replaceText("gutenach"), closeSoftKeyboard());

        pressBack();

        ViewInteraction editText = onView(
                allOf(withId(R.id.su_password), withText("•••••••••"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.instanceOf(TextInputLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        editText.check(matches(withText("•••••••••")));

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.su_password_confirm), withText("••••••••"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.instanceOf(TextInputLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        editText2.check(matches(withText("••••••••")));

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.su_password_confirm), withText("••••••••"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.instanceOf(TextInputLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        editText3.check(matches(withText("••••••••")));

        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.su_usergroup_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.su_form_layout),
                                        5),
                                1)));
        appCompatSpinner2.perform(scrollTo(), click());

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(2);
        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.su_signup_button), withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                allOf(withId(R.id.su_form_layout),
                                        childAtPosition(
                                                withId(R.id.su_form),
                                                0)),
                                6)));
        appCompatButton7.perform(scrollTo(), click());

        ViewInteraction textView5 = onView(
                allOf(withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                0),
                        isDisplayed()));
        textView5.check(matches(withText("Benutzerkonto anlegen")));

        ViewInteraction appCompatEditText11 = onView(
                allOf(withId(R.id.su_password_confirm), withText("gutenach"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText11.perform(scrollTo(), click());

        ViewInteraction appCompatEditText12 = onView(
                allOf(withId(R.id.su_password_confirm), withText("gutenach"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText12.perform(scrollTo(), replaceText("gutenacht"));

        ViewInteraction appCompatEditText13 = onView(
                allOf(withId(R.id.su_password_confirm), withText("gutenacht"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText13.perform(closeSoftKeyboard());

        pressBack();

        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.su_signup_button), withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                allOf(withId(R.id.su_form_layout),
                                        childAtPosition(
                                                withId(R.id.su_form),
                                                0)),
                                6)));
        appCompatButton8.perform(scrollTo(), click());

        ViewInteraction textView6 = onView(
                allOf(withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                0),
                        isDisplayed()));
        textView6.check(matches(withText("Benutzerkonto anlegen")));

        ViewInteraction appCompatEditText14 = onView(
                allOf(withId(R.id.su_email), withText("elias.muellermailbox.org"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText14.perform(scrollTo(), click());

        ViewInteraction appCompatEditText15 = onView(
                allOf(withId(R.id.su_email), withText("elias.muellermailbox.org"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText15.perform(scrollTo(), replaceText("elias.mueller@mailbox.org"));

        ViewInteraction appCompatEditText16 = onView(
                allOf(withId(R.id.su_email), withText("elias.mueller@mailbox.org"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText16.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText17 = onView(
                allOf(withId(R.id.su_email), withText("elias.mueller@mailbox.org"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText17.perform(pressImeActionButton());

        ViewInteraction appCompatEditText18 = onView(
                allOf(withId(R.id.su_password), withText("gutenacht"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText18.perform(pressImeActionButton());

        ViewInteraction appCompatEditText19 = onView(
                allOf(withId(R.id.su_password_confirm), withText("gutenacht"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText19.perform(pressImeActionButton());

        ViewInteraction linearLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(android.R.id.content),
                                childAtPosition(
                                        withId(R.id.decor_content_parent),
                                        1)),
                        0),
                        isDisplayed()));
        linearLayout.check(matches(isDisplayed()));

        ViewInteraction appCompatButton9 = onView(
                allOf(withId(R.id.su_signup_button), withText("Benutzerkonto anlegen"),
                        childAtPosition(
                                allOf(withId(R.id.su_form_layout),
                                        childAtPosition(
                                                withId(R.id.su_form),
                                                0)),
                                6)));
        appCompatButton9.perform(scrollTo(), click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView7 = onView(
                allOf(withText("Login"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                0),
                        isDisplayed()));
        textView7.check(matches(withText("Login")));

        ViewInteraction appCompatButton10 = onView(
                allOf(withId(R.id.sign_up_button), withText("Einloggen"),
                        childAtPosition(
                                allOf(withId(R.id.email_login_form),
                                        childAtPosition(
                                                withId(R.id.login_form),
                                                0)),
                                2)));
        appCompatButton10.perform(scrollTo(), click());

        ViewInteraction editText4 = onView(
                allOf(withId(R.id.email),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.instanceOf(TextInputLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        editText4.check(matches(isDisplayed()));

        ViewInteraction editText5 = onView(
                allOf(withId(R.id.password),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.instanceOf(TextInputLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        editText5.check(matches(isDisplayed()));

        ViewInteraction editText6 = onView(
                allOf(withId(R.id.password),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.instanceOf(TextInputLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        editText6.check(matches(withText("")));

        ViewInteraction editText7 = onView(
                allOf(withId(R.id.email),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.instanceOf(TextInputLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        editText7.check(matches(withText("")));

        ViewInteraction button = onView(
                allOf(withId(R.id.sign_up_button),
                        childAtPosition(
                                allOf(withId(R.id.email_login_form),
                                        childAtPosition(
                                                withId(R.id.login_form),
                                                0)),
                                2),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

    }
}
