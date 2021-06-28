package com.example.project_layouts;

import android.app.Instrumentation;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;



@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    private static final String PACKAGE_NAME = "ccom.example.project_layouts";
    private String passwordUser,mail;

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void release() {
        Intents.release();
    }

    @Test
    public void login_succeed() {

        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(LoginActivity.class.getName(), null, false);
        mail = "ben@gmail.com";
        passwordUser = "11221122";
        //clear text
        onView(withId(R.id.email_login))
                .perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.password_login))
                .perform(clearText(), closeSoftKeyboard());

        //set data
        onView(withId(R.id.email_login))
                .perform(typeText(mail), closeSoftKeyboard());
        onView(withId(R.id.password_login))
                .perform(typeText(passwordUser), closeSoftKeyboard());

        onView(withId(R.id.login)).perform(click());

        LoginActivity nextActivity = (LoginActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 10000000);
        // next activity is opened and captured.
        assertNotNull(nextActivity);
        nextActivity.finish();

    }

    @Test
    public void wrong_mail() {

        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(LoginActivity.class.getName(), null, false);
        mail = "benrr@gmail.com";
        passwordUser = "11221122";
        //clear text
        onView(withId(R.id.email_login))
                .perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.password_login))
                .perform(clearText(), closeSoftKeyboard());

        //set data
        onView(withId(R.id.email_login))
                .perform(typeText(mail), closeSoftKeyboard());
        onView(withId(R.id.password_login))
                .perform(typeText(passwordUser), closeSoftKeyboard());

        onView(withId(R.id.login)).perform(click());

        Object nextActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        assertNull(nextActivity);
    }

    @Test
    public void wrong_password() {

        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(LoginActivity.class.getName(), null, false);
        mail = "ben@gmail.com";
        passwordUser = "111122";
        //clear text
        onView(withId(R.id.email_login))
                .perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.password_login))
                .perform(clearText(), closeSoftKeyboard());

        //set data
        onView(withId(R.id.email_login))
                .perform(typeText(mail), closeSoftKeyboard());
        onView(withId(R.id.password_login))
                .perform(typeText(passwordUser), closeSoftKeyboard());

        onView(withId(R.id.login)).perform(click());

        Object nextActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        assertNull(nextActivity);
    }
}