package com.example.finzik.listviewautomation;

import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;

import org.hamcrest.Matcher;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.matcher.ViewMatchers;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;
import android.widget.SeekBar;


import org.hamcrest.Matcher;

import static android.support.test.espresso.action.ViewActions.actionWithAssertions;

import static android.support.test.espresso.action.ViewActions.actionWithAssertions;

public class ListViewActions {

    private static final float EDGE_FUZZ_FACTOR = 0.083f;

    public static ViewAction setProgress(final int progress) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                seekBar.setProgress(progress);
            }
            @Override
            public String getDescription() {
                return "Set a progress on a SeekBar";
            }
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }
        };
    }
/*
ViewAction repeatedlyUntil (ViewAction action,
                Matcher<View> desiredStateMatcher,
                int maxAttempts)

Returns an action that performs given ViewAction on the view until view matches the desired Matcher<View>.
It will repeat the given action until view matches the desired Matcher<View> or PerformException
will be thrown if given number of unsuccessful attempts are made.
 */

    public static ViewAction repeatedlyUntil (final ViewAction action, final Matcher<View> desiredStateMatcher, final int maxAttempts) {
        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(View.class);
            }

            @Override
            public String getDescription() {
                return "Repeated for " + maxAttempts + "times performing " + action.getDescription() + " until " + desiredStateMatcher.toString()  + " without match.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ListView l = (ListView)view;
                int counter = 0;
                boolean morePagesAvailable = true;
                boolean matchFound = false;
                boolean repeatInLastPage = true;
                // when bottom is reached,let's operate 10 more times
                int lastRepeats = 10;
                ArrayAdapter<String> la =
                        (ArrayAdapter<String>) ((ListView)view).getAdapter();
                /*
                Loop if:
                    (!matchFound) & (counter < maxAttempts) & (morePagesAvailable | repeatInLastPage)
                 */
                // let it loose for a little bit before looping.
                uiController.loopMainThreadUntilIdle();
                //uiController.loopMainThreadForAtLeast(1000);

                while ((!matchFound) & (counter < maxAttempts) & (morePagesAvailable | repeatInLastPage) ){
                    // check if there is a match
                    uiController.loopMainThreadUntilIdle();
                    //uiController.loopMainThreadForAtLeast(1000);

                    if ( desiredStateMatcher.matches(view)) {
                        System.out.println("REPEATEDLY-UNTIL - Match Found!");
                        matchFound = true;
                    } else {
                        counter ++;
                        System.out.println("REPEATEDLY-UNTIL - No match - Performing attempt #" + counter + " of " + maxAttempts + "; morePagesAvailable = " + morePagesAvailable
                                + "; lastRepeats = " + lastRepeats);
                        action.perform(uiController, view);
                        System.out.println("REPEATEDLY-UNTIL - Action performed.");

                        uiController.loopMainThreadUntilIdle();

                        morePagesAvailable = la.areAllItemsEnabled();
                        if (!morePagesAvailable) {
                            System.out.println("REPEATEDLY-UNTIL - reached last page of the list - decrementing lastRepeats.");
                            lastRepeats--;
                            if(lastRepeats == 0) repeatInLastPage = false;
                        }
                    }
                } // while
                System.out.println("REPEATEDLY-UNTIL: count = " + counter + ";  morePagesAvailable = " + morePagesAvailable + "; lastRepeats = " + lastRepeats);
                if(!matchFound) {
                    if (counter == maxAttempts)
                        throw new PerformException.Builder()
                                .withActionDescription(this.getDescription())
                                .withViewDescription(l.toString())
                                .build();
                    if (!morePagesAvailable)
                        throw new PerformException.Builder()
                                .withActionDescription("Reached end of scroll view without match")
                                .withViewDescription(l.toString())
                                .build();
                }
            }
        };
    }

    public static ViewAction mySwipeUp() {
        System.out.println("REPEATED-UNTIL - MY SWIPE UP is being called.");
        return actionWithAssertions(new GeneralSwipeAction(CustomSwipe.FAST_BUT_STEADY,
                //GeneralLocation.translate(GeneralLocation.BOTTOM_CENTER, 0, -EDGE_FUZZ_FACTOR),
                CustomLocation.NEAR_BOTTOM,
                CustomLocation.NEAR_TOP, Press.FINGER));
    }

    public static ViewAction swipeUpSlowly() {
        return actionWithAssertions(new GeneralSwipeAction(Swipe.SLOW,
                GeneralLocation.CENTER,
                GeneralLocation.TOP_CENTER,
                Press.FINGER));
    }
}

