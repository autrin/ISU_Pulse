package com.coms309.isu_pulse_frontend;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.coms309.isu_pulse_frontend.R;
import com.coms309.isu_pulse_frontend.adapters.TaskListAdapter;
import com.coms309.isu_pulse_frontend.adapters.WeeklyCalendarAdapter;
import com.coms309.isu_pulse_frontend.api.TaskApiService;
import com.coms309.isu_pulse_frontend.model.Announcement;
import com.coms309.isu_pulse_frontend.model.PersonalTask;
import com.coms309.isu_pulse_frontend.ui.home.HomeFragment;
import com.coms309.isu_pulse_frontend.loginsignup.UserSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;

import static com.coms309.isu_pulse_frontend.ViewActions.clickChildViewWithId;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


public class AutrinSystemTest {
    // I need at least 4 non- trivial tests
    // use assert

    @Before
    public void setup() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString("netId", "tanner01")
                .apply();
    }


    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

//    @Test
//    public void testNavigateToHomeFragment() {
//        // Open navigation drawer
//        onView(withId(R.id.drawer_layout))
//                .check(matches(isClosed(Gravity.LEFT)))
//                .perform(DrawerActions.open());
//
//        // Click on the Home navigation item
//        onView(withId(R.id.nav_home)).perform(click());
//
//        // Verify the title displayed matches the user role
//        String expectedTitle = UserSession.getInstance(getContext()).getUserType().equals("FACULTY") ? "Teacher Dashboard" : "Student Dashboard";
//        if (expectedTitle != null) {
//            onView(withId(R.id.dashboardTitle))
//                    .check(matches(withText(expectedTitle)));
//        }
//    }

//    @Test
//    public void testAddTaskDialog() {
//        onView(withId(R.id.buttonAddTask)).check(matches(isDisplayed()));
//        // Click on the Add Task button
//        onView(withId(R.id.buttonAddTask)).perform(click());
//
//        // Fill out the task details
//        onView(withId(R.id.editTextTitle)).perform(typeText("Test Task"), closeSoftKeyboard());
//        onView(withId(R.id.editTextDescription)).perform(typeText("This is a test task."), closeSoftKeyboard());
//        onView(withId(R.id.editTextDueDate)).perform(typeText("2024-12-10"), closeSoftKeyboard());
//
//        // Submit the task
//        onView(withId(R.id.buttonSubmit)).perform(click());
//
//        // Verify that the task appears in the "Tasks Due Today" RecyclerView
//        onView(withId(R.id.recyclerViewTasksDueToday))
//                .check(matches(hasDescendant(withText("Test Task"))));
//    }

    @Test
    public void testEditTaskDialog() {
        // Open the edit dialog for the first task
        onView(withId(R.id.recyclerViewTasksDueToday))
                .perform(actionOnItemAtPosition(0, clickChildViewWithId(R.id.buttonEditTask)));


        // Update the task details
        onView(withId(R.id.editTextTitle)).perform(replaceText("Updated Task"), closeSoftKeyboard());
        onView(withId(R.id.editTextDescription)).perform(replaceText("Updated description."), closeSoftKeyboard());
        onView(withId(R.id.editTextDueDate)).perform(replaceText("2024-12-15"), closeSoftKeyboard());

        // Submit the changes
        onView(withId(R.id.buttonSubmit)).perform(click());

        // Verify that the updated task appears in the RecyclerView
        onView(withId(R.id.recyclerViewTasksDueToday))
                .check(matches(hasDescendant(withText("Updated Task"))));
    }



}
