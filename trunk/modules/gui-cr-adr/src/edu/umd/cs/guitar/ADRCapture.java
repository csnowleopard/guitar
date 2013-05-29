/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.umd.cs.guitar;

import com.android.chimpchat.ChimpChat;
import com.android.chimpchat.core.IChimpDevice;
import com.android.monkeyrunner.MonkeyDevice;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.WindowConstants;

import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.Configuration;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Helper entry point for MonkeyRecorder.
 */
public class ADRCapture {
    private static final Logger LOG = Logger.getLogger(ADRCapture.class.getName());
    // This lock is used to keep the python process blocked while the frame is runing.
    private static final Object LOCK = new Object();

    /**
     * Jython entry point for MonkeyRecorder.  Meant to be called like this:
     *
     * <code>
     * from com.android.monkeyrunner import MonkeyRunner as mr
     * from com.android.monkeyrunner import MonkeyRecorder
     * MonkeyRecorder.start(mr.waitForConnection())
     * </code>
     *
     * @param device
     */
    public static void start(final MonkeyDevice device) {
        start(device.getImpl());
    }
    
    /* package */static void start(final IChimpDevice device) {
    	//MAIN_CLASS currently hard-coded, we need to figure out a way to retrieve it through
    	// the adr-server
    	String MAIN_CLASS = "com.aut.HelloAUTActivity"; 
        ADRCaptureFrame frame = new ADRCaptureFrame(device);
        frame.setMainClass(MAIN_CLASS);
        frame.connect();
        // TODO: this is a hack until the window listener works.
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                device.dispose();
                synchronized (LOCK) {
                    LOCK.notifyAll();
                }
            }
        });

        frame.setVisible(true);
        synchronized (LOCK) {
            try {
                LOCK.wait();
            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE, "Unexpected Exception", e);
            }
        }
    }

    public static void main(String[] args) {
        ChimpChat chimp = ChimpChat.getInstance();
        ADRCapture.start(chimp.waitForConnection());
    }
}