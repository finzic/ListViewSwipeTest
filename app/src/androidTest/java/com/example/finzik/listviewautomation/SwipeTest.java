package com.example.finzik.listviewautomation;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.example.finzik.listviewautomation.ListViewActions.mySwipeUp;
import static com.example.finzik.listviewautomation.ListViewActions.repeatedlyUntil;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasToString;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SwipeTest {
    public static final String ROW = "This is Row 254";
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void swipeTest(){
        onView(withId(R.id.MyListView)).check(matches(isDisplayed()));
        onData(anything()) // We are using the position so don't need to specify a data matcher
                .inAdapterView(withId(R.id.MyListView)) // Specify the explicit id of the ListView
                .atPosition(0) // Start from the top
                .check(matches(isCompletelyDisplayed()));

        onView(withId(R.id.MyListView))
                .perform(repeatedlyUntil(mySwipeUp(), hasDescendant(withText(containsString(ROW))), 1000))
                .check(matches(isDisplayed()));
        onData(hasToString(ROW))
                .inAdapterView(withId(R.id.MyListView))
                .perform(click());
    }

}
