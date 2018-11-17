package com.example.finzik.listviewautomation;

import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.MotionEvents;
import android.view.View;

public enum CustomLocation implements CoordinatesProvider {
    CENTER {
        @Override
        public float[] calculateCoordinates(View view) {
            return getCoordinates(view, Position.MIDDLE, Position.MIDDLE);
        }
    },
    ONE_ROW_UP {
        @Override
        public float[] calculateCoordinates(View view) {
            return CustomLocation.getCoordinates(view, Position.MIDDLE_MINUS_ONE_SIXTH, Position.MIDDLE);
        }
    },
    END {
        @Override
        public float[] calculateCoordinates(View view) {
            return getCoordinates(view, Position.END, Position.MIDDLE);
        }
    },
    NEAR_TOP {
        public float[] calculateCoordinates(View view) {
            return getCoordinates(view, Position.BEGIN_PLUS_ONE_SIXTH, Position.MIDDLE);
        }
    },
    NEAR_BOTTOM {
        public float[] calculateCoordinates(View view) {
            return getCoordinates(view, Position.END_MINUS_ONE_SIXTH, Position.MIDDLE);
        }
    };

    private static float[] getCoordinates(View view, CustomLocation.Position vertical, CustomLocation.Position horizontal) {
        final int[] xy = new int[2];
        view.getLocationOnScreen(xy); // xy[0] = x; xy[1] = y.
        final float x = horizontal.getPosition(xy[0], view.getWidth());
        final float y = vertical.getPosition(xy[1], view.getHeight());
        float[] coordinates = {x, y};
        System.out.println("REPEATEDLY-UNTIL - View = " + view.toString() + " - getCoordinates - x = " + x + "; y = " + y);
        return coordinates;
    }

    private static enum Position {
        BEGIN {
            @Override
            public float getPosition(int viewPos, int viewLength) {
                return viewPos;
            }
        },
        BEGIN_PLUS_ONE_SIXTH {
            @Override
            public float getPosition(int viewPos, int viewLength) {
                return viewPos +  (viewLength - 1) / 6.0f;
            }
        },
        MIDDLE {
            @Override
            public float getPosition(int viewPos, int viewLength) {
                // Midpoint between the leftmost and rightmost pixel (position viewLength - 1).
                return viewPos + (viewLength - 1) / 2.0f;
            }
        },
        END {
            @Override
            public float getPosition(int viewPos, int viewLength) {
                return viewPos + viewLength - 1;
            }
        },
        END_MINUS_ONE_SIXTH {
            @Override
            public float getPosition(int viewPos, int viewLength) {
                return viewPos + viewLength - 1 - (viewLength - 1) / 6.0f;
            }
        },
        MIDDLE_MINUS_ONE_SIXTH {
            @Override
            public float getPosition(int viewPos, int viewLength) {
                return viewPos + (viewLength -1) / 2.0f - (viewLength - 1) / 6.0f;
            }
        };

        abstract float getPosition(int widgetPos, int widgetLength);
    }
}

