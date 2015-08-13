package org.simple;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class org.simple.SimpleTest \
 * org.simple.tests/android.test.InstrumentationTestRunner
 */
public class SimpleTest extends ActivityInstrumentationTestCase2<Simple> {

    public SimpleTest() {
        super("org.simple", Simple.class);
    }

}
