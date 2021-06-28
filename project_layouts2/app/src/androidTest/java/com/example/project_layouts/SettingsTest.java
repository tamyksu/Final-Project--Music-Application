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


@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsTest {

    private String Description;

    @Rule
    public ActivityScenarioRule<SettingsActivity> rule = new ActivityScenarioRule<>(SettingsActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void release() {
        Intents.release();
    }

    @Test
    public void changeDescription() {

        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(MyPlace.class.getName(), null, false);
        Description = " Rock are the best!!";

        onView(withId(R.id.update_text))
                .perform(clearText(), closeSoftKeyboard());

        onView(withId(R.id.update_text))
                .perform(typeText(Description), closeSoftKeyboard());

        onView(withId(R.id.save_des)).perform(click());
        onView(withId(R.id.my_place)).perform(click());
        MyPlace nextActivity = (MyPlace) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        assertNotNull(nextActivity);
        nextActivity.finish();

    }
}