package com.example.project_layouts;

import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Intent;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.EditText;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.project_layouts.MainActivity;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;
import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Checks.checkNotNull;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static java.util.EnumSet.allOf;
import static java.util.regex.Pattern.matches;
import static org.hamcrest.EasyMock2Matchers.equalTo;
import static org.junit.Assert.assertNotNull;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class DeletePlaylistTest {

    private String SongName,PlaylistName;

    @Rule
    public ActivityScenarioRule<MyPlaylistActivity> rule = new ActivityScenarioRule<>(MyPlaylistActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void release() {
        Intents.release();
    }

    @Test
    public void viewPlaylist() {

        onView(withRecyclerView(R.id.recycleview_requests).atPosition(0)).perform(longClick());
        onView(withId(android.R.id.button1)).perform(click());

    }

    public static RecyclerViewMatcher withRecyclerView( int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }


}