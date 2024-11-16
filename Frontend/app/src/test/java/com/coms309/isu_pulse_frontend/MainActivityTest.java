package com.coms309.isu_pulse_frontend;

import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.view.Menu;

import androidx.test.core.app.ApplicationProvider;

import com.coms309.isu_pulse_frontend.loginsignup.UserSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, manifest = Config.NONE) // Specify the SDK version
public class MainActivityTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testTeacherMenuVisibility() {
        // Simulate teacher login
        UserSession.getInstance(context).setUserType("FACULTY", context);

        // Initialize MainActivity using Robolectric
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();

        // Use the public getter method to access navigationView
        Menu menu = activity.getNavigationView().getMenu();

        // Ensure menu items are present
        assertNotNull("Dashboard menu item should be present", menu.findItem(R.id.nav_home));
        assertNotNull("Profile menu item should be present", menu.findItem(R.id.nav_profile));
    }
}
