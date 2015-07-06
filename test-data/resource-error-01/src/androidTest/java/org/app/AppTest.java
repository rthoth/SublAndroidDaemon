package org.app;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class org.app.AppTest \
 * org.app.tests/android.test.InstrumentationTestRunner
 */
public class AppTest extends ActivityInstrumentationTestCase2<App> {

    public AppTest() {
        super("org.app", App.class);
    }

}
