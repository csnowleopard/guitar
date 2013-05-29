/*  
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *  the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in all copies or substantial 
 *  portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *  LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *  EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.replayer.experiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.management.RuntimeErrorException;

import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.exception.ComponentDisabled;
import edu.umd.cs.guitar.exception.ComponentNotFound;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.data.EventMapType;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.model.data.WidgetMapElementType;
import edu.umd.cs.guitar.model.data.WidgetMapType;
import edu.umd.cs.guitar.model.wrapper.EventMapTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.WidgetMapTypeWrapper;
import edu.umd.cs.guitar.replayer.experiment.monitor.GTestMonitorExp;
import edu.umd.cs.guitar.replayer.monitor.GTestMonitor;
import edu.umd.cs.guitar.replayer.monitor.TestStepEndEventArgs;
import edu.umd.cs.guitar.replayer.monitor.TestStepStartEventArgs;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class GReplayerExp {

	WidgetMapType widgetMap;
	EventMapType eventMap;
	TestCase testcase;

	ObjectFactory factory = new ObjectFactory();

	public GReplayerMonitorExp getReplayerMonitor() {
		return replayerMonitor;
	}

	public void setReplayerMonitor(GReplayerMonitorExp replayerMonitor) {
		this.replayerMonitor = replayerMonitor;
	}

	GReplayerMonitorExp replayerMonitor;

	int intialWait = 0;

	private List<GTestMonitor> lTestMonitor = new ArrayList<GTestMonitor>();

	/**
	 * @param widgetMap
	 * @param eventMap
	 * @param testcase
	 */
	public GReplayerExp(WidgetMapType widgetMap, EventMapType eventMap,
			TestCase testcase) {
		super();
		this.widgetMap = widgetMap;
		this.eventMap = eventMap;
		this.testcase = testcase;
	}

	/**
	 * @throws IOException
	 * 
	 */
	public void execute() throws IOException {

		if (replayerMonitor == null)
			throw new RuntimeErrorException(null, "Replayer Monitor not found");

		replayerMonitor.setUp();

		GUITARLog.log.info("Connecting to application...");
		replayerMonitor.connectToApplication();

		// Monitor before the test case
		for (GTestMonitor monitors : lTestMonitor) {
			monitors.init();
			GUITARLog.log.info(monitors.getClass().getName()
					+ " is initialized");
		}

		GUITARLog.log.info("Executing test case.....");
		GUITARLog.log.info("Total steps: " + testcase.getStep().size());

		List<StepType> lSteps = testcase.getStep();
		int nStep = lSteps.size();

		for (int i = 0; i < nStep; i++) {
			GUITARLog.log.info("---------------------");
			StepType step = lSteps.get(i);
			executeStep(step);
		}

		// Monitor after the test case
		for (GTestMonitor testMonitor : lTestMonitor) {
			testMonitor.term();
		}

		replayerMonitor.cleanUp();

	}

	private void executeStep(StepType step) throws ComponentNotFound {
		TestStepStartEventArgs stepStartArgs = new TestStepStartEventArgs(step);

		// -----------------------
		// Monitor before step
		for (GTestMonitor aTestMonitor : lTestMonitor) {
			aTestMonitor.beforeStep(stepStartArgs);
		}

		// Events
		String eventID = step.getEventId();
		GUITARLog.log.info("EventID: " + eventID);

		// Looking up Event by ID
		EventMapTypeWrapper wEventMap = new EventMapTypeWrapper(eventMap
				.getEventMapElement());

		EventType event = wEventMap.getEvent(eventID);

		if (event == null) {
			GUITARLog.log.error("EventID not found");
			throw new ComponentNotFound();
		}

		// Find Widget ID
		String widgetID = event.getWidgetId();
		GUITARLog.log.info("WidgetID: " + widgetID);

		// Find widget GUI information
		WidgetMapTypeWrapper wWidgetMap = new WidgetMapTypeWrapper(widgetMap
				.getWidgetMapElement());

		WidgetMapElementType widgetMapElement = wWidgetMap
				.getElementByEventID(widgetID);

		if (widgetMapElement == null) {
			GUITARLog.log.error("Widget mapping element not found");
			throw new ComponentNotFound();
		}

		// -------------------
		// Find component

		GComponent gComponent = replayerMonitor
				.getComponentBySign(widgetMapElement);

		if (gComponent == null)
			throw new ComponentNotFound();

		if (!gComponent.isEnable())
			throw new ComponentDisabled();

		// Actions
		String action = event.getAction();
		GEvent gEvent = replayerMonitor.getAction(action);
		List<String> parameters = step.getParameter();

		GUITARLog.log.info("Action: *" + action);
		GUITARLog.log.info("");

		Hashtable<String, List<String>> optionalValues = new Hashtable<String, List<String>>();

		if (parameters == null)
			gEvent.perform(gComponent, optionalValues);
		else if (parameters.size() == 0) {
			gEvent.perform(gComponent, optionalValues);
		} else {
			gEvent.perform(gComponent, parameters, optionalValues);
		}

		// Work around
		GUIType dummyGUI = factory.createGUIType();

		TestStepEndEventArgs stepEndArgs = new TestStepEndEventArgs(step,
				widgetMapElement.getComponent(), dummyGUI);
		// -----------------------
		// Monitor after step
		for (GTestMonitor aTestMonitor : lTestMonitor) {
			aTestMonitor.afterStep(stepEndArgs);
		}

	}

	/**
	 * 
	 * Add a test monitor
	 * 
	 * <p>
	 * 
	 * @param aTestMonitor
	 */
	public void addTestMonitor(GTestMonitor aTestMonitor) {
		this.lTestMonitor.add(aTestMonitor);
		if (aTestMonitor instanceof GTestMonitorExp) {
			GTestMonitorExp sTestMonitor = (GTestMonitorExp) aTestMonitor;
			sTestMonitor.setsReplayer(this);
		}
	}

	/**
	 * Remove a test monitor
	 * 
	 * <p>
	 * 
	 * @param mTestMonitor
	 */
	public void removeTestMonitor(GTestMonitor mTestMonitor) {
		this.lTestMonitor.remove(mTestMonitor);
	}

}
