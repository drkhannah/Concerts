package com.drkhannah.concerts.data;

import junit.framework.Assert;

import java.util.concurrent.Callable;

/**
 * Created by dhannah on 2/2/17.
 */

//this was taken from the Android CTS
//you can find the source here:
//https://android.googlesource.com/platform/cts/+/11a8ac0beaac2db82ddab83c177c3c7c92cd23e8/libs/deviceutil/src/android/cts/util/PollingCheck.java
public abstract class PollingCheck {
    private static final long TIME_SLICE = 50;
    private long mTimeout = 3000;

    public PollingCheck() {
    }

    public PollingCheck(long timeout) {
        mTimeout = timeout;
    }

    protected abstract boolean check();

    public void run() {
        if (check()) {
            return;
        }

        long timeout = mTimeout;
        while (timeout > 0) {
            try {
                Thread.sleep(TIME_SLICE);
            } catch (InterruptedException e) {
                Assert.fail("unexpected InterruptedException");
            }

            if (check()) {
                return;
            }

            timeout -= TIME_SLICE;
        }

        Assert.fail("unexpected timeout");
    }

    public static void check(CharSequence message, long timeout, Callable<Boolean> condition)
            throws Exception {
        while (timeout > 0) {
            if (condition.call()) {
                return;
            }

            Thread.sleep(TIME_SLICE);
            timeout -= TIME_SLICE;
        }

        Assert.fail(message.toString());
    }
}
