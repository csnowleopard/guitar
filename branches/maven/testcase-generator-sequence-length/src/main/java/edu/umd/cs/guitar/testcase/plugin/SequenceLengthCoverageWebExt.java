/*   
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of
 *  Maryland. Names of owners of this group may be obtained by sending
 *  an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files
 *  (the "Software"), to deal in the Software without restriction, including
 *  without limitation the rights to use, copy, modify, merge, publish,
 *  distribute, sublicense, and/or sell copies of the Software, and to
 *  permit persons to whom the Software is furnished to do so, subject to
 *  the following conditions:
 * 
 *   The above copyright notice and this permission notice shall be included
 *   in all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *   OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *   IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *   CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *   TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 *   THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.testcase.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventGraphType;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.ParameterListType;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.testcase.TestCaseGeneratorConfiguration;
import edu.umd.cs.guitar.util.GraphUtil;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * An improved version of {@link SequenceLengthCoverage}
 * <ul>
 * <li>Accept event parameter from the EFG</li>
 * <li>Add terminal events as postfix</li>
 * </ul>
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 */
public class SequenceLengthCoverageWebExt extends GTestCaseGeneratorPlugin {
	/**
	 * 
	 */
	private static final String TERMINATION_ACTION_FOR_WEB = "edu.umd.cs.guitar.event.WebSubmit";

	// Requested # testcases (0=all)
	int nReqTestcases = 0;

	// Testcase being generated
	int nGeneratedTestcases = 0;

	// Testcases skipped during generation
	int nSkippedDup = 0;
	int nSkippedPath = 0;

	// Max possible testcases
	int nAllTestcases = 0;

	// Graph utility functions for computing path to root
	GraphUtil graphUtil;

	/**
     *
     */
	public SequenceLengthCoverageWebExt() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.testcase.plugin.TCPlugin#isValidArgs()
	 */
	@Override
	public boolean isValidArgs() {
		// Check instance
		if (TestCaseGeneratorConfiguration.LENGTH == null) {
			GUITARLog.log.error("Missing testcase generator configuration");
			return false;
		}
		if (TestCaseGeneratorConfiguration.EFG_FILE == null) {
			GUITARLog.log.error("Missing input graph file");
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.testcase.plugin.TCPlugin#generate()
	 */
	@Override
	public void generate(EFG efg, String outputDir, int nReqTestcases,
			boolean noDuplicateEvent, boolean treatTerminalEventSpecially) {

		new File(outputDir).mkdir();

		this.efg = efg;
		this.nReqTestcases = nReqTestcases;
		this.noDuplicateEvent = noDuplicateEvent;
		this.terminalEvents = new LinkedList<EventType>();
		this.treatTerminalEventSpecially = treatTerminalEventSpecially;

		if (efgx == null) {
			/*
			 * Use EFG as path-to-root lookup if additional graph is not
			 * specified.
			 */
			graphUtil = new GraphUtil(efg);
		} else {
			/*
			 * Use additional EFG as path-to-root lookup if given.
			 */
			graphUtil = new GraphUtil(efgx);
		}

		initialize();

		List<EventType> eventList = efg.getEvents().getEvent();
		List<EventType> interactionEventList = new ArrayList<EventType>();

		nGeneratedTestcases = 0;
		nSkippedDup = 0;
		nSkippedPath = 0;

		for (EventType event : eventList) {
			if (isSelectedEvent(event)) {
				interactionEventList.add(event);
			}
		}

		GUITARLog.log.info("InteractionEventList size: "
				+ interactionEventList.size());

		for (EventType e : interactionEventList) {
			LinkedList<EventType> initialList = new LinkedList<EventType>();

			initialList.add(e);
			nAllTestcases += countTestcases(
					TestCaseGeneratorConfiguration.LENGTH, initialList);
		}
		System.out.println("Maximum length "
				+ TestCaseGeneratorConfiguration.LENGTH + " testcases: "
				+ nAllTestcases);

		for (EventType aEvent : eventList) {
			LinkedList<EventType> initialList = new LinkedList<EventType>();
			initialList.add(aEvent);
			generateWithLength(TestCaseGeneratorConfiguration.LENGTH,
					initialList);
		}

		GUITARLog.log.info("Generated " + nGeneratedTestcases + " SkippedDup "
				+ nSkippedDup + " SkippedPath " + nSkippedPath);
	}

	/**
	 * Count the total number of potential test cases. Counting the maximum
	 * possible testcases is based off the input graph. (not the additional
	 * graph). This is because the main input graph determines the set of
	 * testcases which can potentially be generated from it.
	 * 
	 * <p>
	 * 
	 * @param length
	 * @param root
	 * @return
	 */
	private int countTestcases(int length, LinkedList<EventType> prefix) {
		if (length <= 1) {
			return 1;
		} else {
			int count = 0;

			for (EventType succEvent : succs.get(prefix.getLast())) {
				LinkedList<EventType> extendedPrefix = new LinkedList<EventType>(
						prefix);
				// Ignore non-interaction events
				if (!isSelectedEvent(succEvent)) {
					continue;
				}
				if (noDuplicateEvent && isDuplicateEvent(succEvent, prefix)) {
					continue;
				}
				extendedPrefix.add(succEvent);
				count += countTestcases(length - 1, extendedPrefix);
			}

			return count;
		}
	}

	private boolean isSelectedEvent(EventType event) {
		// Ignore non-interaction events and termination events
		if (treatTerminalEventSpecially) {
			String type = event.getType();
			if (type.equals(GUITARConstants.TERMINAL)
					|| !type.equals(GUITARConstants.SYSTEM_INTERACTION)) {
				return false;
			}
		}
		return true;
	}

	private void generateWithLength(int length, LinkedList<EventType> postfix) {
		if (nGeneratedTestcases >= nReqTestcases && nReqTestcases != 0) {
			return;
		}

		if (length <= 1) {
	

			LinkedList<EventType> path = graphUtil.pathToRoot(postfix
					.getFirst());

			if (path != null) {
				// Remove the root event itself to avoid duplication
				if (path.size() > 0) {
					path.removeLast();
				}

				LinkedList<EventType> tTestCase = new LinkedList<EventType>();

				tTestCase.addAll(path);
				Map<String, TestCase> allTestCases = new HashMap<String, TestCase>();
				tTestCase.addAll(postfix);

				populateTestCaseParameter(tTestCase, "",
						factory.createTestCase(), allTestCases);

				if (treatTerminalEventSpecially) {
					// Add termination event
					EventType lastEvent = tTestCase.getLast();
					allTestCases = appendTerminationEvents(
							allTestCases, lastEvent);
				}
				

				for (String shortName : allTestCases.keySet()) {

					TestCase testcase = allTestCases.get(shortName);

					String sTestName = TEST_NAME_PREFIX;

					if (TestCaseGeneratorConfiguration.LENGTH < 30) {
						sTestName += shortName;

					} else {
						sTestName += ("_" + nGeneratedTestcases);
					}

					sTestName += TEST_NAME_SUFIX;

					String sPath = TestCaseGeneratorConfiguration.OUTPUT_DIR
							+ File.separator + sTestName;
					// write to file
					File file = new File(sPath);

					if (!file.exists()) {
						GUITARLog.log.info("Writing " + nGeneratedTestcases
								+ "/" + nAllTestcases + " " + sPath);

						IO.writeObjToFile(testcase, sPath);

					} else {
						GUITARLog.log.info("Skipped, duplicate " + sPath);
						nSkippedDup++;
					}
				}
				nGeneratedTestcases++;
			} else {
				GUITARLog.log.info("Skipped, no path to root");
				nSkippedPath++;
			} // (path != null)
		} else {
			EventType lastEvent = postfix.getLast();

			for (EventType succEvent : succs.get(lastEvent)) {
				LinkedList<EventType> extendedPostfix = new LinkedList<EventType>(
						postfix);
				if (!isSelectedEvent(succEvent)) {
					continue;
				}
				if (noDuplicateEvent && isDuplicateEvent(succEvent, postfix)) {
					continue;
				}
				extendedPostfix.addLast(succEvent);
				generateWithLength(length - 1, extendedPostfix);
			}
		}
	}

	/**
	 * @param testcases
	 * @param lastEvent
	 * @return
	 */
	private Map<String, TestCase> appendTerminationEvents(
			Map<String, TestCase> testcases, EventType lastEvent) {

		Map<String, TestCase> result = new HashMap<String, TestCase>();

		List<EventType> followsTerminationEvents = getFollowsTerm(lastEvent);
		if (followsTerminationEvents == null
				|| followsTerminationEvents.size() < 1) {
			return testcases;
		}

		for (String testcaseLabel : testcases.keySet()) {

			TestCase nonTerminalizedTestCase = testcases.get(testcaseLabel);

			int count = 0;
			for (EventType terminalEvent : followsTerminationEvents) {

				String terminalizedLabel = testcaseLabel + ".term." + count;
				count++;

				TestCase terminalizedTestCase = factory.createTestCase();

				terminalizedTestCase.getStep().addAll(
						nonTerminalizedTestCase.getStep());

				StepType terminalStep = factory.createStepType();
				terminalStep.setEventId(terminalEvent.getEventId());
				terminalizedTestCase.getStep().add(terminalStep);

				result.put(terminalizedLabel, terminalizedTestCase);
			}

		}

		return result;

	}

	/**
	 * Get all termination event following an event
	 * 
	 * @param event
	 * @return
	 */
	private List<EventType> getFollowsTerm(EventType event) {
		Vector<EventType> succEvents = succs.get(event);
		List<EventType> result = new ArrayList<EventType>();
		for (EventType followsEvent : succEvents) {
			if (TERMINATION_ACTION_FOR_WEB.equals(followsEvent.getAction()))
				result.add(followsEvent);
		}

		return result;
	}

	/**
	 * Generate concrete test cases from an abstract event sequence
	 * 
	 * @param remainingEventList
	 * @param partialLabel
	 * @param partialTestCase
	 * @param result
	 */
	private void populateTestCaseParameter(List<EventType> remainingEventList,
			String partialLabel, TestCase partialTestCase,
			Map<String, TestCase> result) {
		if (remainingEventList.size() == 0) {

			result.put(partialLabel, partialTestCase);
		} else {
			EventType firstRemainingEvent = remainingEventList.get(0);
			Map<String, StepType> firstStepTypeMap = populateEventParameter(firstRemainingEvent);

			for (String firstRemainingStepLabel : firstStepTypeMap.keySet()) {

				String extendedLabel = partialLabel + "_"
						+ firstRemainingStepLabel;
				StepType firstStep = firstStepTypeMap
						.get(firstRemainingStepLabel);

				TestCase extendedTestCase = factory.createTestCase();
				extendedTestCase.getStep().addAll(partialTestCase.getStep());
				extendedTestCase.getStep().add(firstStep);

				List<EventType> reducedEventList = new ArrayList<EventType>();
				reducedEventList.addAll(remainingEventList);
				reducedEventList.remove(0);

				populateTestCaseParameter(reducedEventList, extendedLabel,
						extendedTestCase, result);
			}
		}

	}

	/**
	 * Populate parameter of an event
	 * <p>
	 * 
	 * @param firstEvent
	 * @return
	 */
	private Map<String, StepType> populateEventParameter(EventType event) {
		Map<String, StepType> stepSet = new HashMap<String, StepType>();

		List<ParameterListType> parameterList = event.getParameterList();
		String eid = event.getEventId();
		String label;
		// No parameter
		if (parameterList == null || parameterList.size() < 1) {
			StepType step = factory.createStepType();
			step.setEventId(eid);
			label = eid;
			stepSet.put(eid, step);
			// Has parameter
		} else {
			int count = 0;
			for (ParameterListType parameter : parameterList) {
				StepType step = factory.createStepType();
				step.setEventId(eid);
				step.setParameter(parameter.getParameter());
				label = eid + "." + (count);
				stepSet.put(label, step);
				count++;
			}
		}

		return stepSet;
	}

	/**
     * 
     */
	private void printGraph() {
		List<EventType> eventList = efg.getEvents().getEvent();
		int eventGraphSize = eventList.size();
		EventGraphType eventGraph = efg.getEventGraph();

		for (int row = 0; row < eventGraphSize; row++) {

			EventType currentEvent = eventList.get(row);
			Vector<EventType> s = new Vector<EventType>();

			for (int col = 0; col < eventGraphSize; col++) {

				EventType event = eventList.get(col);

				int relation = eventGraph.getRow().get(row).getE().get(col);

				if (relation == GUITARConstants.FOLLOW_EDGE) {
					GUITARLog.log.info(currentEvent.getEventId() + ","
							+ currentEvent.getWidgetId() + "->"
							+ event.getEventId() + "," + event.getWidgetId());
				}

			}
		}
	}

} // class SequenceLengthCoverage
