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
import static androidx.test.espresso.action.ViewActions.click;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.junit.Assert.assertNotNull;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class FriendsRecommendationTest {

    private String Description;

    @Rule
    public ActivityScenarioRule<LoginActivity> rule = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void release() {
        Intents.release();
    }

    @Test
    public void ViewFriendsRecommendation() throws InterruptedException {

       Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(FriendsRecommendation.class.getName(), null, false);

        onView(withId(R.id.search)).perform(click());
        Thread.sleep(2000);
        FriendsRecommendation nextActivity = (FriendsRecommendation) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        assertNotNull(nextActivity);
        nextActivity.finish();

    }
}