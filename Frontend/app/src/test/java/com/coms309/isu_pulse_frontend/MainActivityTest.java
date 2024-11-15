package com.coms309.isu_pulse_frontend;

import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.view.Menu;

import com.coms309.isu_pulse_frontend.loginsignup.UserSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28) // Specify an SDK version for Robolectric
public class MainActivityTest {
//
//    private Context context;
//
//    @Before
//    public void setUp() {
//        context = RuntimeEnvironment.getApplication();
//    }
//
//    @Test
//    public void testTeacherMenuVisibility() {
//        // Simulate teacher login
//        UserSession.getInstance(context).setUserType("FACULTY", context);
//        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
//
//        // Use the public getter method to access navigationView
//        Menu menu = activity.getNavigationView().getMenu();
//        assertNotNull(menu.findItem(R.id.nav_home));  // Ensure 'Dashboard' is present
//        assertNotNull(menu.findItem(R.id.nav_profile));  // Ensure 'Profile' is present
//    }
}
