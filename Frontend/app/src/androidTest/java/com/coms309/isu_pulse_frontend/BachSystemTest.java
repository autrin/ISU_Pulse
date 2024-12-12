package com.coms309.isu_pulse_frontend;

import android.view.Gravity;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import com.coms309.isu_pulse_frontend.friend_functional.FriendSuggestion;
import com.coms309.isu_pulse_frontend.loginsignup.LoginActivity;
import com.coms309.isu_pulse_frontend.profile_activity.EditProfileActivity;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

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

    @Test
    public void testUpdateProfileWithValidData() {
        String netIdInput = "ntbachh";
        String passwordInput = "bachdbrr1234567890";
        String newDescription = "My name is Bach";
        String newLinkedInUrl = "My linkedin url is here";
        String newExternalUrl = "My external url is here";
        String newPassword = "bachdbrr1234567890@";
        String newConfirmPassword = "bachdbrr1234567890@";

        ActivityScenario.launch(EditProfileActivity.class);


        // Type NetID and old password to check credentials
        onView(withId(R.id.netIdEditText)).perform(typeText(netIdInput), closeSoftKeyboard());
        onView(withId(R.id.oldPasswordEditText)).perform(typeText(passwordInput), closeSoftKeyboard());
        onView(withId(R.id.checkCredentialsButton)).perform(click());

        // Wait for the "Correct Information" toast to appear
        try {
            Thread.sleep(1000); // Allow time for backend processing
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Enter new profile details
        onView(withId(R.id.descriptionEditText)).perform(typeText(newDescription), closeSoftKeyboard());
        onView(withId(R.id.linkedinUrlEditText)).perform(typeText(newLinkedInUrl), closeSoftKeyboard());
        onView(withId(R.id.externalUrlEditText)).perform(typeText(newExternalUrl), closeSoftKeyboard());
        onView(withId(R.id.newPasswordEditText)).perform(typeText(newPassword), closeSoftKeyboard());
        onView(withId(R.id.confirmNewPasswordEditText)).perform(typeText(newConfirmPassword), closeSoftKeyboard());

        // Click the update profile button
        onView(withId(R.id.updateProfileButton)).perform(click());

        // Wait for the update to process
        try {
            Thread.sleep(2000); // Allow time for backend processing and navigation
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the app navigates back to the ProfileActivity
        onView(withId(R.id.profileImage)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
    }

    @Test
    public void testSlideShow(){
        ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.drawer_layout)).perform(click())
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());

        onView(withId(R.id.nav_profile)).perform(click());

        onView(withId(R.id.profileImage)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
    }

    @Test
    public void testSearchFriend() {
        String netIdInput = "ntbachh";
        String passwordInput = "bachdbrr1234567890@";
        String searchQuery = "Hung";

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

        // Launch FriendSuggestion activity
        ActivityScenario.launch(FriendSuggestion.class);

        // Type in the search query
        onView(withId(R.id.search_bar)).perform(typeText(searchQuery), closeSoftKeyboard());

        // Click on the search button
        onView(withId(R.id.search_button)).perform(click());

        onView(withId(R.id.friends_list))
                .perform(actionOnItemAtPosition(0, click()));

        // Verify that the friend with the name "John" is displayed
        onView(allOf(withId(R.id.friend_name), withText(Matchers.containsString("Hung"))))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
    }
}
