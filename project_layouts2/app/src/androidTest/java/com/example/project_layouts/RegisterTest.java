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
public class RegisterTest {

    private String passwordUser,mail,name;

    @Rule
    public ActivityScenarioRule<RegisterActivity> rule = new ActivityScenarioRule<>(RegisterActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void release() {
        Intents.release();
    }

    @Test
    public void register_failed_user_already_registered() {

        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
        mail = "dor1@gmail.com";
        passwordUser = "112211";
        name = "Dor";
        //clear text
        onView(withId(R.id.email))
                .perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.name))
                .perform(clearText(), closeSoftKeyboard());

        //set data
        onView(withId(R.id.email))
                .perform(typeText(mail), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText(passwordUser), closeSoftKeyboard());
        onView(withId(R.id.name))
                .perform(typeText(name), closeSoftKeyboard());

        onView(withId(R.id.sign_up)).perform(click());

        MainActivity nextActivity = (MainActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        // next activity is opened and captured.
        assertNull(nextActivity);

    }
    @Test
    public void register_failed_name_empty() {

        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
        mail = "dordd1@gmail.com";
        passwordUser = "112211";
        name = "";
        //clear text
        onView(withId(R.id.email))
                .perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.name))
                .perform(clearText(), closeSoftKeyboard());

        //set data
        onView(withId(R.id.email))
                .perform(typeText(mail), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText(passwordUser), closeSoftKeyboard());


        onView(withId(R.id.sign_up)).perform(click());

        MainActivity nextActivity = (MainActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        // next activity is opened and captured.
        assertNull(nextActivity);

    }


    @Test
    public void register_failed_password_empty() {

        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
        mail = "dordd1@gmail.com";
        passwordUser = "";
        name = "or";
        //clear text
        onView(withId(R.id.email))
                .perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.name))
                .perform(clearText(), closeSoftKeyboard());

        //set data
        onView(withId(R.id.email))
                .perform(typeText(mail), closeSoftKeyboard());
        onView(withId(R.id.name))
                .perform(typeText(name), closeSoftKeyboard());


        onView(withId(R.id.sign_up)).perform(click());

        MainActivity nextActivity = (MainActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        // next activity is opened and captured.
        assertNull(nextActivity);

    }
    @Test
    public void register_succeed() {

        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
        mail = "dor51@gmail.com";
        passwordUser = "112211";
        name = "Dor";
        //clear text
        onView(withId(R.id.email))
                .perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.name))
                .perform(clearText(), closeSoftKeyboard());

        //set data
        onView(withId(R.id.email))
                .perform(typeText(mail), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText(passwordUser), closeSoftKeyboard());
        onView(withId(R.id.name))
                .perform(typeText(name), closeSoftKeyboard());

        onView(withId(R.id.sign_up)).perform(click());

        MainActivity nextActivity = (MainActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 10000);
        // next activity is opened and captured.
        assertNotNull(nextActivity);
        nextActivity.finish();
    }}