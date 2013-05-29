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

package edu.umd.cs.guitar.testcase.plugin.ct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.CTDef;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.CTUse;
import edu.umd.cs.guitar.testcase.plugin.ct.util.Log;
import edu.umd.cs.guitar.testcase.plugin.ct.util.StopWatch;
import edu.umd.cs.guitar.util.GraphUtil;

/**
 * @author arlt
 */
public class CTSequenceSelector {

	/**
	 * Index of first event
	 */
	final static int FIRST = 0;

	/**
	 * Index of second event
	 */
	final static int SECOND = 1;

	/**
	 * Index of third event
	 */
	final static int THIRD = 2;

	/**
	 * Slicer
	 */
	private CTSlicer slicer;

	/**
	 * Events
	 */
	private List<EventType> events;

	/**
	 * EFG
	 */
	private EFG efg;

	/**
	 * Successors
	 */
	private Hashtable<EventType, Vector<EventType>> succs;

	/**
	 * GraphUtil
	 */
	private GraphUtil graphUtil;

	/**
	 * Stop Watch
	 */
	private StopWatch stopWatch = new StopWatch();

	/**
	 * List of chain sequences
	 */
	private List<List<EventType>> chains = new ArrayList<List<EventType>>();

	/**
	 * List of executable chain sequences
	 */
	private List<List<EventType>> execChains = new ArrayList<List<EventType>>();

	/**
	 * List of causal executable chain sequences
	 */
	private List<List<EventType>> causalExecChains = new ArrayList<List<EventType>>();

	/**
	 * List of bush sequences
	 */
	private List<List<EventType>> bushes = new ArrayList<List<EventType>>();

	/**
	 * List of executable bush sequences
	 */
	private List<List<EventType>> execBushes = new ArrayList<List<EventType>>();

	/**
	 * List of required executable bush sequences
	 */
	private List<List<EventType>> requiredExecBushes = new ArrayList<List<EventType>>();

	/**
	 * List of redundant executable bush sequences
	 */
	private List<List<EventType>> redundantExecBushes = new ArrayList<List<EventType>>();

	/**
	 * List of bushes which are also chains
	 */
	private List<List<EventType>> bushesInChains = new ArrayList<List<EventType>>();

	/**
	 * List of total sequences
	 */
	private List<List<EventType>> totalSequences = new ArrayList<List<EventType>>();

	/**
	 * C-tor
	 * @param slice Slicer
	 * @param EFG efg
	 */
	public CTSequenceSelector(CTSlicer slicer, EFG efg,
			Hashtable<EventType, Vector<EventType>> succs) {
		this.slicer = slicer;
		this.efg = efg;
		this.succs = succs;
		this.graphUtil = new GraphUtil(this.efg);
		this.events = this.efg.getEvents().getEvent();
	}

	/**
	 * Runs the sequence selector
	 * @param length Length
	 * @param optimize true = optimize set of sequences
	 */
	public void run(int length, boolean optimize) {
		stopWatch.start();

		// first select chains
		selectChains(length);
		if (optimize)
			optimizeChains();

		// then select bushes
		selectBushes(length);
		if (optimize)
			optimizeBushes();

		// create set of sequences
		totalSequences.addAll(causalExecChains);
		for (int i = 0; i < requiredExecBushes.size(); i++) {
			Log.info(String
					.format("Checking whether Bush Sequence %d of %d is also a Chain Sequence",
							i, requiredExecBushes.size()));
			List<EventType> sequence = requiredExecBushes.get(i);
			if (totalSequences.contains(sequence)) {
				bushesInChains.add(sequence);
			} else {
				totalSequences.add(sequence);
			}
		}

		stopWatch.stop();
	}

	/**
	 * Selects chains of events
	 * @param length Length of sequences
	 */
	protected void selectChains(int length) {
		for (EventType event : events) {
			Log.info("Selecting Chain Sequences for Event "
					+ event.getEventId());

			// create sequence and add event
			List<EventType> sequence = new ArrayList<EventType>();
			sequence.add(event);

			// select chain sequences
			selectChains(event, sequence, length - 1);
		}
	}

	/**
	 * Selects chain event sequences
	 * @param event Event
	 * @param sequence Sequence
	 * @param length Length
	 */
	protected void selectChains(EventType event, List<EventType> sequence,
			int length) {
		// desired length reached?
		if (0 == length) {
			// add sequence
			chains.add(sequence);

			// make sequence executable
			if (null != makeExec(sequence))
				execChains.add(sequence);

			return;
		}

		// iterate events
		for (EventType event2 : events) {
			// check dependency
			if (!haveDependingFields(event, event2))
				continue;

			// add event to sequence
			List<EventType> newSequence = new ArrayList<EventType>(sequence);
			newSequence.add(event2);

			// select chain sequences
			selectChains(event2, newSequence, length - 1);
		}
	}

	/**
	 * Selects bushes of events
	 * @param length Length of sequences
	 */
	protected void selectBushes(int length) {
		for (EventType event : events) {
			Log.info("Selecting Bush Sequences for Event " + event.getEventId());

			// create sequence and add event
			List<EventType> sequence = new ArrayList<EventType>();
			sequence.add(event);

			// select bush sequences
			selectBushes(sequence, length);
		}
	}

	/**
	 * Selects bushes event sequences
	 * @param sequence Sequence
	 * @param length Length
	 */
	protected void selectBushes(List<EventType> sequence, int length) {
		// desired length reached?
		if (sequence.size() == length) {
			// add sequence
			bushes.add(sequence);

			// make sequence executable
			if (null != makeExec(sequence))
				execBushes.add(sequence);

			return;
		}

		for (EventType event : events) {
			// create new list of events
			List<EventType> newSequence = new ArrayList<EventType>();
			newSequence.addAll(sequence);
			newSequence.add(newSequence.size() - 1, event);

			// check dependency
			boolean areDependent = true;
			for (int i = 0; i < newSequence.size() - 1; i++) {
				if (!haveDependingFields(newSequence.get(i),
						newSequence.get(newSequence.size() - 1))) {
					areDependent = false;
					break;
				}
			}

			// not dependent?
			if (!areDependent)
				continue;

			// try to select bush sequences from new list of events
			selectBushes(newSequence, length);
		}
	}

	/**
	 * Returns the set of common fields of event1 and event2
	 * @param event1 Event1
	 * @param event2 Event2
	 * @return Set of common fields
	 */
	protected Set<String> getCommonFields(EventType event1, EventType event2) {
		Set<String> intersection = new HashSet<String>();
		Set<String> defs = slicer.getEventFieldDefs(event1);
		Set<String> uses = slicer.getEventFieldUses(event2);

		if (null == defs || null == uses)
			return intersection;

		// compute intersection of defs and uses
		intersection.addAll(defs);
		intersection.retainAll(uses);
		return intersection;
	}

	/**
	 * Checks whether two events are dependent
	 * @param event1 Event 1 (which may write fields read in Event 2)
	 * @param event2 Event 2 (which may read fields written in Event 1)
	 * @return true = two events have a dependency
	 */
	protected boolean haveDependingFields(EventType event1, EventType event2) {
		return !getCommonFields(event1, event2).isEmpty();
	}

	/**
	 * Optimizes the set of chain sequences
	 */
	protected void optimizeChains() {
		for (int i = 0; i < execChains.size(); i++) {
			Log.info(String.format(
					"Performing Causal Analysis on Chain Sequence %d of %d",
					i + 1, execChains.size()));

			// optimization only works for sequence lengths >= 3
			List<EventType> sequence = execChains.get(i);
			if (sequence.size() < 3)
				continue;

			boolean isCausal = true;
			for (int j = 0; j < sequence.size() - 2; j++) {
				// drop sequence if a sub-sequence (a triple) is not causal
				List<EventType> subSequence = sequence.subList(j, j + 3);
				if (!isCausal(subSequence)) {
					isCausal = false;
					break;
				}
			}

			if (isCausal) {
				causalExecChains.add(sequence);
			}
		}
	}

	/**
	 * Checks whether three events are causal
	 * @param sequence Event Sequence
	 * @return true = the three events are causal
	 */
	protected boolean isCausal(List<EventType> sequence) {
		if (null == sequence || 3 != sequence.size())
			return false;

		// compute intersections
		Set<String> int12 = getCommonFields(sequence.get(FIRST),
				sequence.get(SECOND));
		Set<String> int23 = getCommonFields(sequence.get(SECOND),
				sequence.get(THIRD));

		// iterate common field values
		for (String fieldValue : int23) {
			Map<CTDef, Set<CTUse>> slice = slicer.getSubSlice(
					sequence.get(SECOND), fieldValue);
			for (CTDef def : slice.keySet()) {
				Set<CTUse> uses = slice.get(def);
				for (CTUse use : uses) {
					if (int12.contains(use.getFieldValue()))
						return true;
				}
			}
		}

		return false;
	}

	/**
	 * Optimizes the set of bush sequences
	 */
	protected void optimizeBushes() {
		for (int i = 0; i < execBushes.size(); i++) {
			Log.info(String.format(
					"Performing POR Analysis on Bush Sequence %d of %d", i + 1,
					execBushes.size()));

			// optimization only works for sequence lengths >= 3
			List<EventType> sequence = execBushes.get(i);
			if (sequence.size() < 3)
				continue;

			// is bush already redundant?
			if (redundantExecBushes.contains(sequence)) {
				continue;
			}

			// is bush PO-reducible?
			if (isPOReducible(sequence)) {
				// create redundant sequence (for later checking)
				List<EventType> redundantSequence = new ArrayList<EventType>();
				redundantSequence.add(sequence.get(1));
				redundantSequence.add(sequence.get(0));
				redundantSequence.add(sequence.get(2));
				redundantExecBushes.add(redundantSequence);
			}

			requiredExecBushes.add(sequence);
		}
	}

	/**
	 * Checks whether the first two events are PO-reducible
	 * @param sequence Event Sequence
	 * @return true = the first two events are PO-reducible
	 */
	protected boolean isPOReducible(List<EventType> sequence) {
		if (null == sequence || 3 != sequence.size())
			return false;

		// compute intersections
		Set<String> int13 = getCommonFields(sequence.get(FIRST),
				sequence.get(THIRD));
		Set<String> int23 = getCommonFields(sequence.get(SECOND),
				sequence.get(THIRD));

		// iterate common field values
		for (String fieldValue : int13) {
			Map<CTDef, Set<CTUse>> slice = slicer.getSubSlice(
					sequence.get(FIRST), fieldValue);
			for (CTDef def : slice.keySet()) {
				Set<CTUse> uses = slice.get(def);
				for (CTUse use : uses) {
					if (int23.contains(use.getFieldValue()))
						return false;
				}
			}
		}

		// iterate common field values
		for (String fieldValue : int23) {
			Map<CTDef, Set<CTUse>> slice = slicer.getSubSlice(
					sequence.get(SECOND), fieldValue);
			for (CTDef def : slice.keySet()) {
				Set<CTUse> uses = slice.get(def);
				for (CTUse use : uses) {
					if (int13.contains(use.getFieldValue()))
						return false;
				}
			}
		}

		return true;
	}

	/**
	 * Makes a sequence executable
	 * @param sequence Sequence
	 * @return Executable Sequence
	 */
	public List<EventType> makeExec(List<EventType> sequence) {
		if (null == sequence || sequence.isEmpty())
			return null;

		List<EventType> path = new ArrayList<EventType>();
		for (int i = 0; i < sequence.size(); i++) {
			LinkedList<EventType> subPath;
			if (0 == i) {
				subPath = graphUtil.pathToRoot(sequence.get(0));
			} else {
				subPath = bfsEvent2Event(sequence.get(i - 1), sequence.get(i));
			}

			if (null == subPath) {
				return null;
			} else {
				path.addAll(subPath);
			}
		}
		return path;
	}

	/**
	 * Breadth-first-search from event to event in the EFG
	 * @param start Start event
	 * @param goal Goal event to reach
	 * @return Event sequence between start and goal
	 */
	public LinkedList<EventType> bfsEvent2Event(EventType start, EventType goal) {
		LinkedList<EventType> retPath = null;
		EventType found = null;

		HashMap<EventType, EventType> visitedBy = new HashMap<EventType, EventType>();
		LinkedList<EventType> queue = new LinkedList<EventType>();
		queue.add(start);

		while (found == null && !queue.isEmpty()) {
			EventType e = queue.remove();
			for (EventType succ : succs.get(e)) {
				if (!visitedBy.containsKey(succ)) {
					visitedBy.put(succ, e);
					if (succ == goal) {
						found = succ;
						break;
					} else {
						queue.add(succ);
					}
				}
			}
		}

		if (found != null) {
			retPath = new LinkedList<EventType>();
			EventType cursor = visitedBy.get(found);
			while (cursor != start) {
				retPath.addFirst(cursor);
				cursor = visitedBy.get(cursor);
			}
		}

		return retPath;
	}

	/**
	 * Prints the given sequence
	 * @param sequence Sequence
	 */
	public void logSequence(List<EventType> sequence) {
		StringBuilder sb = new StringBuilder();
		for (EventType event : sequence) {
			sb.append(event.getEventId() + ", ");
		}
		Log.info(sb.toString());
	}

	/**
	 * Prints statistics of the sequence selector
	 */
	public void printStatistics() {
		Log.info("*** Statistics of Sequence Selector ***");

		// chains
		Log.info(String.format("Chains: %d", chains.size()));
		Log.info(String.format("Executable Chains: %d", execChains.size()));
		Log.info(String.format("Causal Executable Chains: %d",
				causalExecChains.size()));

		// bushes
		Log.info(String.format("Bushes: %d", bushes.size()));
		Log.info(String.format("Executable Bushes: %d", execBushes.size()));
		Log.info(String.format("Required Bushes: %d", requiredExecBushes.size()));
		Log.info(String.format("Redundant Bushes: %d",
				redundantExecBushes.size()));

		// total
		Log.info(String.format("Bushes in Chains: %d", bushesInChains.size()));
		Log.info(String.format("Total Sequences: %d", totalSequences.size()));

		// time
		Log.info(String.format("Total Time: %d ms", stopWatch.getTime()));
		Log.info(String.format("Time per Event: %d ms", stopWatch.getTime()
				/ events.size()));
	}

}