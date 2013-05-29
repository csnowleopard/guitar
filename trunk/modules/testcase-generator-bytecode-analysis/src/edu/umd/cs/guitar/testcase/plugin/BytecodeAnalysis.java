/*	
 *  Copyright (c) 2011. The GREYBOX group at the University of Freiburg, Chair of Software Engineering.
 *  Names of owners of this group may be obtained by sending an e-mail to arlt@informatik.uni-freiburg.de
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in all copies or substantial 
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */

package edu.umd.cs.guitar.testcase.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.testcase.BytecodeAnalysisConfiguration;
import edu.umd.cs.guitar.testcase.TestCaseGeneratorConfiguration;
import edu.umd.cs.guitar.testcase.plugin.edg.Class;
import edu.umd.cs.guitar.testcase.plugin.edg.ClassDB;
import edu.umd.cs.guitar.testcase.plugin.edg.EventNode;
import edu.umd.cs.guitar.testcase.plugin.edg.Field;
import edu.umd.cs.guitar.util.GraphUtil;

/**
 * BytecodeAnalysis
 *
 */
public class BytecodeAnalysis extends GTestCaseGeneratorPlugin {
	
	/**
	 * Graph utility functions for computing path to root
	 */
	private GraphUtil graphUtil;
	
	/**
	 * Output directory
	 */
	private String outputDir;
	
	/**
	 * Generator methods
	 */
	private static HashMap<String, TCGeneratorMethod> GENERATOR_METHODS;
	
	/**
	 * Add generator methods
	 */
	static {
		// register generation methods
		GENERATOR_METHODS = new HashMap<String, TCGeneratorMethod>();
		GENERATOR_METHODS.put("pair", new PairGenerator());
		GENERATOR_METHODS.put("seq_mark_edge", new SequenceMark(SequenceGenerator.MAX_EDGE_DEPENDENCIES));
		GENERATOR_METHODS.put("seq_remove_edge", new SequenceRemove(SequenceGenerator.MAX_EDGE_DEPENDENCIES));
		GENERATOR_METHODS.put("seq_mark_pred", new SequenceMark(SequenceGenerator.MAX_PREDECESSOR_DEPENDENCIES));
		GENERATOR_METHODS.put("seq_remove_pred", new SequenceRemove(SequenceGenerator.MAX_PREDECESSOR_DEPENDENCIES));
		GENERATOR_METHODS.put("prefix", new PrefixGenerator());
		GENERATOR_METHODS.put("prefix_max", new MaxPrefixGenerator());
		GENERATOR_METHODS.put("random", new RandomGenerator());
	}
	
	/**
	 * Output interface
	 *
	 */
	public static interface Output {
		boolean createSequenceTC(LinkedList<EventType> events);
	}
	
	int x = 0;
	
	/**
	 * Output implementation
	 */
	Output out = new Output() {
		/**
		 * Creates the test case of a event sequence
		 * @param events Event sequence
		 * @return true, if test case is successfully created
		 */
		public boolean createSequenceTC(LinkedList<EventType> events) {
			
			TestCase tc = factory.createTestCase();
			List<StepType> lStep = new ArrayList<StepType>();
			EventType event = events.getFirst();
			
			LinkedList<EventType> path = graphUtil.pathToRoot(event);
			
			// don't know why it would happen but it eventually does
			if (path == null) {
				System.out.println("No path to root for " + event.getEventId() + "!");
				return false;
			}
			path.removeLast();
			// generate path to first event
			for (EventType e : path) {
				lStep.add(Step(e.getEventId(), true));
			}
			
			lStep.add(Step(event.getEventId(), false));
			
			// generate follow path
			ListIterator<EventType> i = events.listIterator(1);
			while (i.hasNext()) {
				EventType event2 = i.next();
				path = TCUtil.bfsEvent2Event(event, event2, succs);
				// don't know why it would happen but it eventually does
				if (path == null) {
					System.out.println("No path from " + event.getEventId() + " to " + event2.getEventId() + "!");
					return false;
				}
				for (EventType e : path) {
					lStep.add(Step(e.getEventId(), true));
				}
				lStep.add(Step(event2.getEventId(), false));
				event = event2;
			}
			// write TC
			tc.setStep(lStep);
			String sTestName;
			if (events.size() > 5) {
				sTestName = TEST_NAME_PREFIX + events.getFirst().getEventId()
						+ "__" + events.getLast().getEventId()
						+ TEST_NAME_SUFIX;
			} else {
				String seperator = "";
				sTestName = TEST_NAME_PREFIX;
				for (EventType e : events) {
					sTestName += seperator + e.getEventId();
					seperator = "_";
				}
				sTestName += TEST_NAME_SUFIX;
			}
			String sPath = outputDir + File.separator + sTestName;
			
			// write test case
			XMLHandler xml = new XMLHandler();
			//xml.writeObjToFile(tc, sPath);
			
			x++;
			
			return true;
		}
		
		/**
		 * Helper to create test case steps
		 * @param id EventID of step
		 * @param reachingStep Flag if it is a reaching step
		 * @return Test case step
		 */
		private StepType Step(String id, boolean reachingStep) {
			StepType step = factory.createStepType();
			step.setEventId(id);
			step.setReachingStep(reachingStep);
			return step;
		}
		
	};
	
	@Override
	public TestCaseGeneratorConfiguration getConfiguration() {
		return new BytecodeAnalysisConfiguration();
	}
	
	@Override
	public boolean isValidArgs() {
		if (BytecodeAnalysisConfiguration.LENGTH == null)
			return false;
		if (BytecodeAnalysisConfiguration.SCOPE == null)
			return false;
		if (BytecodeAnalysisConfiguration.METHOD == null)
			return false;
		return true;
	}
	
	@Override
	public void generate(EFG efg,
						 String outputDir,
						 int nMaxNumber,
						 boolean noDuplicateEvent,
						 boolean treatTerminalEventSpecially) {
		
		// Note: noDuplicateEvent and treatTerminalEventSpecially haven't
		// been used here.
		
		TCGeneratorMethod m = GENERATOR_METHODS
				.get(BytecodeAnalysisConfiguration.METHOD.toLowerCase());
		
		if (m == null) {
			System.out.println("Method[" + BytecodeAnalysisConfiguration.METHOD + "] for testcase generation is invalid!");
			return;
		}
		
		if (efgx == null) {
			/*
			 * Use EFG as path-to-root lookup if additional graph
			 * is not specified.
			 */
			graphUtil = new GraphUtil(efg);
		} else {
			/*
			 * Use additional EFG as path-to-root lookup if given.
			 */
			graphUtil = new GraphUtil(efgx);
		}
		
		new File(outputDir).mkdir();
		this.outputDir = outputDir;
		this.efg = efg;
		initialize();
		this.efg = null;
		Map<String, Class> db = ClassDB.create(BytecodeAnalysisConfiguration.getClassScope());
		
		System.out.println("DB size: " + db.size());
		List<EventType> eventList = efg.getEvents().getEvent();
		System.out.println("Events: " + eventList.size());
		List<EventNode> edg = EventNode.createEDG(eventList, db);
		System.out.println("EDG size: " + edg.size());
		
		edg = filterSharedEvents(edg);
		
		int a = 0;
		for (EventNode n1 : edg) {
			StringBuilder sb = new StringBuilder();
			for ( Field f : n1.getWrites() ) {
				sb.append(f.toString() + "\n");
				a++;
			}
			//toFile(new Integer(a).toString(), "/Users/arlt/Desktop/s/" + n1.getEvent().getEventId() + "_w.txt");
			//toFile(sb.toString(), "/Users/arlt/Desktop/s/" + n1.getEvent().getEventId() + "_w.txt");
			a = 0;
			
			sb = new StringBuilder();
			for ( Field f : n1.getReads() ) {
				sb.append(f.getName() + "\n");
			}
			//toFile(sb.toString(), "/Users/arlt/Desktop/s/" + n1.getEvent().getEventId() + "_r.txt");
			
			
			// does the event influence other events
			if (!n1.isEmpty() && !TCUtil.isTerminalEvent(n1.getEvent()) && n1.hasWrite()) {
				// create all pairs
				for (EventNode n2 : edg) {
					if (n1 != n2 && n1.writesTo(n2)) {
						x++;
					}
				}
			}
		}
		

		
		/*Stats s = new Stats(edg);
		s.countConditionWrites();*/
		
		/*
		if ( 1 == BytecodeAnalysisConfiguration.SH ) {
			edg = filterSharedEvents(edg);
			System.out.println("Filtered: " + edg.size());
		}*/
		
		/*m.generate(edg, initialEvents, preds, succs, out, nMaxNumber,
				BytecodeAnalysisConfiguration.LENGTH);*/
		
		System.err.println(x);
	}
	
	public static boolean toFile(String text, String filename) {
		boolean success = false;
		try {
			FileWriter fw = new FileWriter(new File(filename));
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.close();
			fw.close();
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}
	
	/**
	 * Remove duplicate event listeners
	 * @param nodes Nodes
	 * @return EDG without duplicate event listeners
	 */
	private List<EventNode> filterSharedEvents(List<EventNode> nodes) {
		ArrayList<EventNode> newList = new ArrayList<EventNode>(nodes.size());
		HashSet<String> listenerSet = new HashSet<String>();
		for (EventNode n : nodes) {
			List<String> listeners = n.getEvent().getListeners();
			// filter events that are already covered, ignore events without listeners
			if (listeners.isEmpty() || listenerSet.addAll(listeners) || !n.isSharable()) {
				newList.add(n);
			}
		}
		return newList;
	}
	
	/**
	 * Main method (for debugging purposes)
	 * @param args Command-line arguments
	 */
	public static void main(String[] args) {
		// read EFG
		XMLHandler xml = new XMLHandler();
		EFG efg = (EFG) xml.readObjFromFile(args[0], EFG.class);
		
		// setup bytecode analysis
		BytecodeAnalysis pa = new BytecodeAnalysis();
		BytecodeAnalysisConfiguration.SCOPE = args[1];
		BytecodeAnalysisConfiguration.METHOD = "pair";
		BytecodeAnalysisConfiguration.LENGTH = 2;
		
		// generate test cases
		pa.generate(efg, args[2], 0, false, false);
	}
	
}
