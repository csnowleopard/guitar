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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.data.EventType;

/**
 * TCUtil
 *
 */
public class TCUtil {
	
	/**
	 * Returns true if the event is a terminal event
	 * @param e Event
	 * @return true if terminal event
	 */
	public static boolean isTerminalEvent(EventType e) {
		return GUITARConstants.TERMINAL.equals(e.getType());
	}
	
	/**
	 * Breadth-first-search from event to event in the EFG
	 * @param start Start event
	 * @param goal Goal event to reach
	 * @param successor Map with successor lists for given events
	 * @return Event sequence between start and goal
	 */
	public static LinkedList<EventType> bfsEvent2Event(
			EventType start, EventType goal, Hashtable<EventType, Vector<EventType>> successor) {
		
		LinkedList<EventType> retPath = null;
		EventType found = null;
		
		// to avoid cycles and trace the path
		HashMap<EventType, EventType> visitedBy = new HashMap<EventType, EventType>();
		LinkedList<EventType> queue = new LinkedList<EventType>();
		queue.add(start);
		
		// BFS to find the shortest path from start to goal
		while (found == null && !queue.isEmpty()) {
			EventType e = queue.remove();
			// expand node
			for (EventType succ : successor.get(e)) {
				if (!visitedBy.containsKey(succ)) {
					visitedBy.put(succ, e);
					if (succ == goal) {
						found = succ;
						break; // stop searching
					} else {
						queue.add(succ);
					}
				}
			}
		}
		
		// generate path
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
	
}
