package com.example.finzik.listviewautomation;

import android.os.SystemClock;
import android.support.test.espresso.UiController;
import android.support.test.espresso.action.MotionEvents;
import android.support.test.espresso.action.Swiper;
import android.util.Log;
import android.view.MotionEvent;

import static android.support.test.espresso.core.internal.deps.guava.base.Preconditions.checkElementIndex;
import static android.support.test.espresso.core.internal.deps.guava.base.Preconditions.checkNotNull;

public enum CustomSwipe implements Swiper {
    /** Swipe fast and steady stopping at the end **/
    FAST_BUT_STEADY {
        @Override
        public Status sendSwipe(UiController uiController, float[] startCoordinates, float[] endCoordinates, float[] precision) {
            return sendCustomSwipe(uiController, startCoordinates, endCoordinates, precision,
                    SWIPE_FAST_DURATION_MS);
        }
    };
    private static final String TAG = CustomSwipe.class.getSimpleName();

    /** The number of motion events to send for each swipe. */
    private static final int SWIPE_EVENT_COUNT = 10;

    /** Length of time a "fast" swipe should last for, in milliseconds. */
    private static final int SWIPE_FAST_DURATION_MS = 100;

    /** Length of time a "slow" swipe should last for, in milliseconds. */
    private static final int SWIPE_SLOW_DURATION_MS = 1500;
    /** Ni - number of initial steps in the start location */
    /** Nf - number of final   steps in the end  location */
    private static final int Ni = 2;
    private static final int Nf = 20;

    private static float[][] interpolate(float[] start, float[] end, int steps) {
        checkElementIndex(1, start.length);
        checkElementIndex(1, end.length);

        final int  total_steps = steps + Ni + Nf;

        float[][] res = new float[total_steps][2];

        /**
         * Ns initial steps
         * N/2  swipe steps accelerating
         * N/2 swipe steps decelerating
         * Ns final steps
         *
         * Initial and final steps are sequence of close steps - meaning - slow slow motions.
         * Easy shortcut : first 2 steps and last 2 steps are 'identical' in the same position - start.
         */

        res[0][0] = start[0];
        res[0][1] = start[1];

        res[1][0] = start[0];
        res[1][1] = start[1];

        for (int i = Ni + 1; i < steps + Ni + 1; i++) {
            res[i - 1][0] = start[0] + (end[0] - start[0]) * i / (steps + 2f);
            res[i - 1][1] = start[1] + (end[1] - start[1]) * i / (steps + 2f);
        }


        for (int i = 0; i < Nf  ; i++) {
            res[Ni + steps + i ][0] = end[0];
            res[Ni + steps + i ][1] = end[1];
        }

        StringBuffer sbx = new StringBuffer ();
        StringBuffer sby = new StringBuffer ();

        for(int i = 0; i < total_steps; i++){
            sbx.append(res[i][0]).append(", ");
            sby.append(res[i][1]).append(", ");
        }
        System.out.println("SWIPE: x = "  + sbx.toString());
        System.out.println("SWIPE: y = "  + sby.toString());


        return res;
    }



    private static Swiper.Status sendCustomSwipe(UiController uiController, float[] startCoordinates,
                                                 float[] endCoordinates, float[] precision, int duration) {
        checkNotNull(uiController);
        checkNotNull(startCoordinates);
        checkNotNull(endCoordinates);
        checkNotNull(precision);

        float[][] steps = interpolate(startCoordinates, endCoordinates, SWIPE_EVENT_COUNT);
        final int delayBetweenMovements = duration / steps.length;

        MotionEvent downEvent = MotionEvents.sendDown(uiController, startCoordinates, precision).down;
        try {
            for (int i = 0; i < steps.length; i++) {
                if (!MotionEvents.sendMovement(uiController, downEvent, steps[i])) {
                    Log.e(TAG, "Injection of move event as part of the swipe failed. Sending cancel event.");
                    MotionEvents.sendCancel(uiController, downEvent);
                    return Swiper.Status.FAILURE;
                }

                long desiredTime = downEvent.getDownTime() + delayBetweenMovements * i;
                long timeUntilDesired = desiredTime - SystemClock.uptimeMillis();
                if (timeUntilDesired > 10) {
                    uiController.loopMainThreadForAtLeast(timeUntilDesired);
                }
            }

            if (!MotionEvents.sendUp(uiController, downEvent, endCoordinates)) {
                Log.e(TAG, "Injection of up event as part of the swipe failed. Sending cancel event.");
                MotionEvents.sendCancel(uiController, downEvent);
                return Swiper.Status.FAILURE;
            }
        } finally {
            downEvent.recycle();
        }
        return Swiper.Status.SUCCESS;
    }
}
