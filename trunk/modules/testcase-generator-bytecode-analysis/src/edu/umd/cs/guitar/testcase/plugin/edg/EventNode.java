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

package edu.umd.cs.guitar.testcase.plugin.edg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.umd.cs.guitar.model.data.EventType;

/**
 * EventNode
 *
 */
public class EventNode {

	/**
	 * Empty Set
	 */
	private static final Set<Field> EMPTY_SET = Collections.emptySet();

	/**
	 * Event
	 */
	private EventType event;
	
	/**
	 * Methods
	 */
	private Set<MethodDescriptor> methods;

	/**
	 * Reads
	 */
	private Set<Field> reads;

	/**
	 * Writes
	 */
	private Set<Field> writes;

	/**
	 * Empty
	 */
	private boolean empty;

	/**
	 * Sharable
	 */
	private boolean sharable;

	/**
	 * C-tor
	 * @param event Event
	 */
	public EventNode(EventType event) {
		this(event, null, EMPTY_SET, EMPTY_SET, true);
	}

	/**
	 * C-tor
	 * @param event Event
	 * @param methods Methods
	 * @param reads Reads
	 * @param writes Writes
	 */
	public EventNode(EventType event, Set<MethodDescriptor> methods, Set<Field> reads, Set<Field> writes) {
		this(event, methods, reads, writes, false);
	}

	/**
	 * C-tor
	 * @param event Event
	 * @param methods Methods
	 * @param reads Reads
	 * @param writes Writes
	 * @param empty Empty
	 */
	private EventNode(EventType event, Set<MethodDescriptor> methods, Set<Field> reads, Set<Field> writes, boolean empty) {
		this.event = event;
		this.methods = methods;
		this.reads = reads;
		this.writes = writes;
		this.empty = empty;
		this.sharable = true;
	}

	/**
	 * Returns the event
	 * @return Event
	 */
	public EventType getEvent() {
		return event;
	}

	/**
	 * Returns the methods
	 * @return Methods
	 */
	public Set<MethodDescriptor> getMethods() {
		return methods;
	}

	/**
	 * Returns the reads
	 * @return Reads
	 */
	public Set<Field> getReads() {
		return reads;
	}

	/**
	 * Returns the writes
	 * @return Writes
	 */
	public Set<Field> getWrites() {
		return writes;
	}

	/**
	 * Returns the flag "empty"
	 * @return Empty
	 */
	public boolean isEmpty() {
		return empty;
	}

	/**
	 * Returns the flag "sharable"
	 * @return Sharable
	 */
	public boolean isSharable() {
		return sharable;
	}

	/**
	 * Assigns the flag "sharable"
	 * @param sharable Sharable
	 */
	public void setSharable(boolean sharable) {
		this.sharable = sharable;
	}

	/**
	 * Determines if the the event has reads
	 * @return true = event has reads
	 */
	public boolean hasRead() {
		return !reads.isEmpty();
	}

	/**
	 * Determines if the the event has writes
	 * @return true = event has writes
	 */
	public boolean hasWrite() {
		return !writes.isEmpty();
	}

	/**
	 * Determines if the event writes to a set of fields read
	 * @param _reads Reads
	 * @return true = event writes to a set of fields read
	 */
	public boolean writesTo(Set<Field> _reads) {
		Iterator<Field> i = this.writes.iterator();
		while (i.hasNext()) {
			Field f = i.next();
			if (_reads.contains(f)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the event writes to another event
	 * @param e Event
	 * @return true = event writes to another event
	 */
	public boolean writesTo(EventNode e) {
		return this.writesTo(e.reads);
	}

	/**
	 * Computes the dependency of a set of read and a set of writes
	 * @param _reads Reads
	 * @param _writes Writes
	 * @return Dependency
	 */
	public static int dependency(Set<Field> _reads, Set<Field> _writes) {
		int depCount = 0;
		Iterator<Field> i = _reads.iterator();
		while (i.hasNext()) {
			Field f = i.next();
			if (_writes.contains(f)) {
				depCount++;
			}
		}
		return depCount;
	}

	/**
	 * Computes the dependency of a reading event and a writing event
	 * @param reader Reading event
	 * @param writer Writing event
	 * @return Dependency
	 */
	public static int dependency(EventNode reader, EventNode writer) {
		return dependency(reader.reads, writer.writes);
	}

	/**
	 * Computes the dependency to a writing event
	 * @param writer Writing event
	 * @return Dependency
	 */
	public int dependencyToWriter(EventNode writer) {
		return dependency(this.reads, writer.writes);
	}

	/**
	 * Computes the dependency to a reading event
	 * @param reader Reading event
	 * @return Dependency
	 */
	public int dependencyToReader(EventNode reader) {
		return dependency(reader.reads, this.writes);
	}

	/**
	 * This function creates the EDG(Event Dependency Graph).
	 * The edges in the EDG are determined by the fields that are read and written by the events.
	 * A event e1 depends on an event e2, if the set of all reads of e1 has a non-empty intersection with the set of all writes of e2.
	 * To allow a more comfortable handling of the events as nodes, they are wrapped into RWEntries, which contain all the reads/writes of an event.
	 * @param es Set of all events
	 * @param db ClassDB
	 * @return Set of RWEntries
	 */
	public static List<EventNode> createEDG(List<EventType> es, Map<String, Class> db) {
		ArrayList<EventNode> dep = new ArrayList<EventNode>(es.size());
		for (EventType e : es) {
			createNode(e, dep, db);
		}
		return dep;
	}

	/**
	 * Creates the EventNode for a single event.
	 * @param e Event that should be wrapped into an EventNode
	 * @param nodes Set of EventNodes
	 * @param db ClassDB
	 */
	private static void createNode(EventType e, List<EventNode> nodes, Map<String, Class> db) {
		List<String> listeners = e.getListeners();
		if (listeners != null) {
			boolean isEmpty = true;
			boolean sharable = true;
			HashSet<MethodDescriptor> methods = new HashSet<MethodDescriptor>();
			HashSet<Field> reads = new HashSet<Field>();
			HashSet<Field> writes = new HashSet<Field>();
			for (String s : listeners) {
				Class c = db.get(s.replace('.', '/'));
				if (c != null && c.isDeclared()) {
					Method m = c.getMethod("actionPerformed");
					if (m != null) {
						MethodDescriptor md = m.getDescriptor("(Ljava/awt/event/ActionEvent;)V");
						if (md != null && !md.isEmpty()) {
							methods.add(md);
							rwHelper(md, reads, writes, new HashSet<MethodDescriptor>());
							isEmpty = false;
							sharable &= md.isSharable();
						}
					}
				}
			}
			EventNode newnode;
			if (isEmpty) {
				newnode = new EventNode(e);
			} else {
				newnode = new EventNode(e, methods, emptyfy(reads), emptyfy(writes));
			}
			newnode.setSharable(sharable);
			nodes.add(newnode);
		} else {
			nodes.add(new EventNode(e));
		}
	}

	/**
	 * Replaces empty sets with a more efficient representation
	 * @param set Set of fields
	 * @return Empty set of fields
	 */
	private static Set<Field> emptyfy(Set<Field> set) {
		return (set.isEmpty()) ? EMPTY_SET : set;
	}

	/**
	 * Recursively traverses the ClassDB, taking invokes of other methods into account, to accumulate the reads and writes.
	 * @param method Method
	 * @param reads In/Out parameter containing reads
	 * @param writes In/Out parameter containing writes
	 * @param visited Already visited methods
	 */
	private static void rwHelper(MethodDescriptor method, Set<Field> reads, Set<Field> writes, Set<MethodDescriptor> visited) {
		// maybe just add read field if they are not already marked as written
		reads.addAll(method.getRead());
		writes.addAll(method.getWrite());

		// avoid cycles and already visited methods
		visited.add(method);
		
		for (MethodDescriptor m : method.getInvokes()) {
			if (!visited.contains(m)) {
				rwHelper(m, reads, writes, visited);
			}
		}
	}

}
