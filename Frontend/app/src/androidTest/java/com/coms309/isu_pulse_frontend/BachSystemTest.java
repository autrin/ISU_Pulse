package com.coms309.isu_pulse_frontend;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import com.coms309.isu_pulse_frontend.loginsignup.LoginActivity;
import com.coms309.isu_pulse_frontend.MainActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BachSystemTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLoginWithValidCredentials() {
        String netIdInput = "ntbachh";
        String passwordInput = "bachdbrr1234567890";

        // Type in NetID and Password
        onView(withId(R.id.netid_isu_pulse)).perform(typeText(netIdInput), closeSoftKeyboard());
        onView(withId(R.id.password_isu_pulse)).perform(typeText(passwordInput), closeSoftKeyboard());

        // Click on Enter button
        onView(withId(R.id.enter_isu_pulse)).perform(click());

        // Put thread to sleep to allow the login process to complete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify successful login by checking if MainActivity is displayed
        onView(withId(R.id.app_bar_main)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
    }
}
