package com.example.android.contactmanager;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.example.android.contactmanager.ContactManagerTest \
 * com.example.android.contactmanager.tests/android.test.InstrumentationTestRunner
 */
public class ContactManagerTest extends ActivityInstrumentationTestCase2<ContactManager> {

    public ContactManagerTest() {
        super("com.example.android.contactmanager", ContactManager.class);
    }

}
