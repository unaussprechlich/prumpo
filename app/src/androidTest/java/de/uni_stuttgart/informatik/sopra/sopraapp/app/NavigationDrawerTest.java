package de.uni_stuttgart.informatik.sopra.sopraapp.app;

import android.support.test.rule.ActivityTestRule;
import android.view.Gravity;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import de.uni_stuttgart.informatik.sopra.sopraapp.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * sudo sh gradlew createDebugCoverageReport
 */
public class NavigationDrawerTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    @Ignore
    public void testNavigationDrawer() throws InterruptedException {
        Thread.sleep(2000);

        open();
       onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(open());
//                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
//                .perform(open());
    }

}
