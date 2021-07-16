package com.amdocs.aia.il.utils;

import java.util.concurrent.TimeUnit;

import static com.amdocs.aia.common.core.test.utils.OpenShiftUtils.sleep;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public final class AssertUtils {
    private AssertUtils() {
        // singleton
    }

    public static void repetitive(Runnable repeatedAssert, long timeout) {
        for (int delay = 0; delay < timeout; delay += 5) {
            try {
                repeatedAssert.run();
                return;
            } catch (AssertionError e) {
                sleep(5000);
            }
        }
        repeatedAssert.run();
    }

    public static void repetitiveAssertEquals(int expected, int actual, String msg, long timeout, TimeUnit timeUnit) {
        repetitive(() -> assertEquals(actual, expected), timeUnit.toMillis(timeout));
        assertEquals(actual, expected, msg);
    }

    public static void repetitiveAssertTrue(boolean expression, String msg, long timeout, TimeUnit timeUnit) {
        repetitive(() -> assertTrue(expression), timeUnit.toMillis(timeout));
        assertTrue(expression, msg);
    }
}