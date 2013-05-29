/*
 *  Copyright (c) 2009-@year@. The  GUITAR group  at the University of
 *  Maryland. Names of owners of this group may be obtained by sending
 *  an e-mail to atif@cs.umd.edu
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files
 *  (the "Software"), to deal in the Software without restriction,
 *  including without limitation  the rights to use, copy, modify, merge,
 *  publish,  distribute, sublicense, and/or sell copies of the Software,
 *  and to  permit persons  to whom  the Software  is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY,  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO  EVENT SHALL THE  AUTHORS OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR  OTHER LIABILITY,  WHETHER IN AN  ACTION OF CONTRACT,
 *  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.umd.cs.guitar.replayer;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.umd.cs.guitar.event.GEvent;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.ADRApplication;
import edu.umd.cs.guitar.model.ADRConstants;
import edu.umd.cs.guitar.model.ADRWindow;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.PropertyType;

import edu.umd.cs.guitar.util.GUITARLog;

public class ADRReplayerMonitor extends GReplayerMonitor {

	String MAIN_CLASS;
	int port;
	//Set<String> visitedWindowSet;

	public ADRReplayerMonitor(String main_class, int port) {
		super();
		MAIN_CLASS = main_class;
		this.port = port;
	}

	@Override
	public void connectToApplication() {
		application = new ADRApplication(MAIN_CLASS, port);
		application.connect();
	}

	@Override
	public void setUp() {
		// TODO Auto-generated method stub		
	}

	@Override
	public void cleanUp() {
		((ADRApplication)application).disconnect();
	}

	@Override
	public GWindow getWindow(String sWindowTitle) {
		GWindow retGXWindow = null;
		Set<GWindow> windows = null;
		String list[] = sWindowTitle.split("\\.");

	//	if (visitedWindowSet.contains(sWindowTitle)) {
			windows = ((ADRApplication)application).gotoWindow(sWindowTitle, list[list.length-1]);
		//}

		if (windows == null)
			windows = application.getAllWindow();

		for (GWindow aWindow : windows) {
			if (aWindow.getTitle().equals(sWindowTitle)) {
				retGXWindow = aWindow;
				break;
			}
		}
		delay(ADRReplayerConfiguration.DELAY);

//		visitedWindowSet.add(sWindowTitle);
		return retGXWindow;
	}

	@Override
	public GEvent getAction(String actionName) {
		GEvent retAction = null;
		try {
			Class<?> c = Class.forName(actionName);
			Object action = c.newInstance();

			retAction = (GEvent) action;

		} catch (Exception e) {
			GUITARLog.log.error("Error in getting action", e);
		}

		return retAction;
	}

	@Override
	public Object getArguments(String action) {
		return null;
	}

	@Override
	public List<PropertyType> selectIDProperties(ComponentType comp) {
		if (comp == null)
			return new ArrayList<PropertyType>();

		List<PropertyType> retIDProperties = new ArrayList<PropertyType>();

		AttributesType attributes = comp.getAttributes();
		List<PropertyType> lProperties = attributes.getProperty();
		for (PropertyType p : lProperties) {
			if (ADRConstants.ID_PROPERTIES.contains(p.getName()))
				retIDProperties.add(p);
		}
		return retIDProperties;
	}

	@Override
	public void delay(int delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

   @Override
   public GWindow
   waitForWindow(String sWindowTitle,
                 String sImageFilepath)
   throws IOException
	{
		return null;
	}

   /**
    * Placeholder function. See GReplayerMonitor for details.
    *
    * Write the images for a component. Two images are written:
    *
    *  * the component's ripped image
    *  * the component as identified during replay (testcase execution)
    *
    * @param replayComponent   GComponent of widget identified during replay
    * @param sRipImagefilePath File path of component's image as ripped
    */
   @Override
   public void
   writeMatchedComponents(GComponent replayComponent,
                          String sRipImageFilePath)
   throws IOException
   {
   }
}
