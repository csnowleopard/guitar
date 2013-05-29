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
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.CTDef;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.CTMethod;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.CTUse;
import edu.umd.cs.guitar.testcase.plugin.ct.util.FileIO;
import edu.umd.cs.guitar.testcase.plugin.ct.util.Log;

/**
 * @author arlt
 */
public class CTSlicer {

	/**
	 * Body Transformer
	 */
	private CTBodyTransformer bodyTransformer;

	/**
	 * Event field defs
	 */
	private Map<EventType, Set<CTDef>> eventFieldDefs = new HashMap<EventType, Set<CTDef>>();

	/**
	 * Event field uses
	 */
	private Map<EventType, Set<CTUse>> eventFieldUses = new HashMap<EventType, Set<CTUse>>();

	/**
	 * Slice
	 */
	private Map<CTDef, Set<CTUse>> slice = new HashMap<CTDef, Set<CTUse>>();

	/**
	 * C-tor
	 */
	public CTSlicer(CTBodyTransformer bodyTransformer) {
		this.bodyTransformer = bodyTransformer;
	}

	/**
	 * Runs the slicer
	 * @param events Events
	 */
	public void run(List<EventType> events) {
		// iterate events
		for (EventType event : events) {
			Log.info("Slicing event " + event.getEventId());

			Set<CTDef> eventDefs = new HashSet<CTDef>();
			Set<CTUse> eventUses = new HashSet<CTUse>();

			// iterate listeners
			for (String listener : event.getListeners()) {
				queryEventFieldDefs(getEventSignature(listener),
						new HashSet<String>(), eventDefs);
				queryEventFieldUses(getEventSignature(listener),
						new HashSet<String>(), eventUses);

				eventFieldDefs.put(event, eventDefs);
				eventFieldUses.put(event, eventUses);

				querySlice(event);
			}
		}
	}

	/**
	 * Queries all field defs in the event
	 * @param methodName
	 * @param methods
	 * @param eventDefs
	 */
	protected void queryEventFieldDefs(String methodName, Set<String> methods,
			Set<CTDef> eventDefs) {
		if (methods.contains(methodName))
			return; // method was already analyzed
		else
			methods.add(methodName);

		CTMethod method = bodyTransformer.getMethod(methodName);
		if (null == method)
			return; // method was not analyzed

		Set<CTDef> methodDefs = bodyTransformer.getMethodFieldDefs(method);
		if (null != methodDefs) {
			// add method defs
			for (CTDef methodDef : methodDefs) {
				eventDefs.add(methodDef);
			}
		}

		// analyze invokes
		for (CTUse invoke : method.getInvokes()) {
			queryEventFieldDefs(invoke.getMethodSignature(), methods, eventDefs);
		}
	}

	/**
	 * Queries all field uses in the event
	 * @param methodName
	 * @param methods
	 * @param eventDefs
	 */
	protected void queryEventFieldUses(String methodName, Set<String> methods,
			Set<CTUse> eventUses) {
		if (methods.contains(methodName))
			return; // method was already analyzed
		else
			methods.add(methodName);

		CTMethod method = bodyTransformer.getMethod(methodName);
		if (null == method)
			return; // method was not analyzed

		Set<CTUse> methodUses = bodyTransformer.getMethodFieldUses(method);
		if (null != methodUses) {
			// add method uses
			for (CTUse methodUse : methodUses) {
				eventUses.add(methodUse);
			}
		}

		// analyze invokes
		for (CTUse invoke : method.getInvokes()) {
			queryEventFieldUses(invoke.getMethodSignature(), methods, eventUses);
		}
	}

	/**
	 * Slices the field defs in the listener
	 * @param event Event
	 */
	protected void querySlice(EventType event) {
		Set<CTDef> eventDefs = eventFieldDefs.get(event);
		if (null == eventDefs)
			return;

		// query uses for defs
		for (CTDef eventDef : eventDefs) {
			Set<CTUse> eventDefSlice;
			if (slice.containsKey(eventDef)) {
				eventDefSlice = slice.get(eventDef);
			} else {
				eventDefSlice = new HashSet<CTUse>();
				slice.put(eventDef, eventDefSlice);
			}
			queryUses(eventDef, eventDefSlice, event);
		}
	}

	/**
	 * Queries for uses
	 * @param def Def
	 * @param eventDef EventDef
	 */
	protected void queryUses(CTDef def, Set<CTUse> eventDefSlice,
			EventType event) {
		Set<CTUse> uses = bodyTransformer.getUnitUses(def.getUnit());
		if (null == uses)
			return;

		for (CTUse use : uses) {
			// ignore use if it is already analyzed
			if (eventDefSlice.contains(use))
				continue;

			// query def for use
			if (!queryDefs(use, use.getUnit().getMethod(), eventDefSlice, event)) {
				if (!use.isFieldValue())
					continue;

				// analyze invoked methods
				for (CTMethod method : getInvokes(use)) {
					queryDefs(use, method, eventDefSlice, event);
				}

				// analyze invokedBy methods
				for (CTMethod method : getInvokedBys(use, event)) {
					queryDefs(use, method, eventDefSlice, event);
				}
			}
		}
	}

	/**
	 * Queries for defs
	 * @param use Use
	 * @param method Method
	 * @param eventDefSlice EventDefSlice
	 * @param event Event
	 */
	protected boolean queryDefs(CTUse use, CTMethod method,
			Set<CTUse> eventDefSlice, EventType event) {
		// add use to slice
		eventDefSlice.add(use);

		// get defs in unit
		Set<CTDef> defs = bodyTransformer.getMethodDefs(method);
		if (null == defs)
			return false;

		boolean success = false;
		for (CTDef def : defs) {
			// ignore def if it is declared after the current use
			// (works only within the same method)
			if (def.getUnit().getMethod() == use.getUnit().getMethod())
				if (def.getUnit().getID() > use.getUnit().getID())
					continue;

			String defValue = def.getValue();
			String useValue = use.getValue();

			// compare field values?
			if (def.isFieldValue() && use.isFieldValue()) {
				defValue = def.getFieldValue();
				useValue = use.getFieldValue();
			}

			// ignore def if it does not equal use
			if (!defValue.equals(useValue))
				continue;

			// query use(s) for def
			success = true;
			queryUses(def, eventDefSlice, event);
		}

		return success;
	}

	/**
	 * Returns the set of methods invoked by the method of the given use
	 * @param use Use
	 * @return Methods
	 */
	public List<CTMethod> getInvokes(CTUse use) {
		List<CTMethod> methods = new ArrayList<CTMethod>();
		getInvokes(use, methods);
		return methods;
	}

	/**
	 * Returns the set of methods invoked by the method of the given use
	 * @param use Use
	 * @param methods Methods
	 */
	protected void getInvokes(CTUse use, List<CTMethod> methods) {
		Set<CTUse> invokes = use.getUnit().getMethod().getInvokes();
		for (CTUse invoke : invokes) {
			CTMethod invokedMethod = bodyTransformer.getMethod(invoke
					.getMethodSignature());
			if (methods.contains(invokedMethod))
				continue;

			methods.add(invokedMethod);
			getInvokes(invoke, methods);
		}
	}

	/**
	 * Returns the set of methods which invoke the method of the given use
	 * @param use Use
	 * @param event Event
	 * @return Methods
	 */
	public List<CTMethod> getInvokedBys(CTUse use, EventType event) {
		List<CTMethod> methods = new ArrayList<CTMethod>();
		getInvokedBys(use, event, new ArrayList<CTMethod>(), methods);
		return methods;
	}

	/**
	 * Returns the set of methods which invoke the method of the given use
	 * @param use Use
	 * @param event Event
	 * @param tmpMethods Temporary methods
	 * @param methods Methods
	 * @return Methods
	 */
	protected void getInvokedBys(CTUse use, EventType event,
			List<CTMethod> tmpMethods, List<CTMethod> methods) {
		// does method match with event signature?
		CTMethod method = use.getUnit().getMethod();
		if (method.getSignature().equals(getEventSignature(event))) {
			for (CTMethod tmpMethod : tmpMethods) {
				if (!methods.contains(tmpMethod))
					methods.add(tmpMethod);
			}
			return;
		}

		Set<CTUse> invokedBys = method.getInvokedBy();
		for (CTUse invokedBy : invokedBys) {
			// method already visited?
			CTMethod invokedByMethod = invokedBy.getUnit().getMethod();
			if (tmpMethods.contains(invokedByMethod))
				continue;

			// create new list of temporary methods
			List<CTMethod> newMethods = new ArrayList<CTMethod>(tmpMethods);
			newMethods.add(invokedByMethod);
			getInvokedBys(invokedBy, event, newMethods, methods);
		}
	}

	/**
	 * Returns the field defs for an event
	 * @param event Event
	 * @return Field defs
	 */
	public Set<String> getEventFieldDefs(EventType event) {
		Set<CTDef> defs = eventFieldDefs.containsKey(event) ? eventFieldDefs
				.get(event) : null;
		if (null == defs)
			return null;

		// iterate defs
		Set<String> uniqueDefs = new HashSet<String>();
		for (CTDef def : defs) {
			uniqueDefs.add(def.getFieldValue());
		}
		return uniqueDefs;
	}

	/**
	 * Returns the field uses for an event
	 * @param event Event
	 * @return Field uses
	 */
	public Set<String> getEventFieldUses(EventType event) {
		Set<CTUse> uses = eventFieldUses.containsKey(event) ? eventFieldUses
				.get(event) : null;
		if (null == uses)
			return null;

		// iterate uses
		Set<String> uniqueUses = new HashSet<String>();
		for (CTUse use : uses) {
			uniqueUses.add(use.getFieldValue());
		}
		return uniqueUses;
	}

	/**
	 * Computes a sub-slice for the given event and field value
	 * @param event Event
	 * @param fieldValue Field value
	 * @return Sub-Slice
	 */
	public Map<CTDef, Set<CTUse>> getSubSlice(EventType event, String fieldValue) {
		Map<CTDef, Set<CTUse>> subSlice = new HashMap<CTDef, Set<CTUse>>();

		Set<CTDef> defs = eventFieldDefs.get(event);
		for (CTDef def : defs) {
			if (!def.getFieldValue().equals(fieldValue))
				continue;

			Set<CTUse> uses = slice.get(def);
			subSlice.put(def, uses);
		}
		return subSlice;
	}

	/**
	 * Returns the signature of a given event
	 * @param event Event
	 * @return Signature of an event
	 */
	protected String getEventSignature(EventType event) {
		List<String> listeners = event.getListeners();
		if (null == listeners || listeners.isEmpty())
			return null;

		return getEventSignature(listeners.get(0));
	}

	/**
	 * Returns the signature of a given (event) listener
	 * @param listener Listener
	 * @return Signature of a (event) listener
	 */
	protected String getEventSignature(String listener) {
		return String.format(
				"<%s: void actionPerformed(java.awt.event.ActionEvent)>",
				listener);
	}

	/**
	 * Prints the slice
	 */
	public void logSlice() {
		StringBuilder sb = new StringBuilder();
		for (CTDef def : slice.keySet()) {
			sb.append(String.format("# field: %s%n", def.getFieldValue()));
			for (CTUse use : slice.get(def)) {
				sb.append(String.format("# -->: %s%n", use.getValue()));
			}
		}
		Log.info(sb.toString());
	}

	/**
	 * Prints statistics of the slicer
	 */
	public void printStatistics() {
		// Log.info("*** Statistics of Slicer ***");

		StringBuilder sb = new StringBuilder();
		for (CTDef def : slice.keySet()) {
			Set<CTUse> uses = slice.get(def);
			sb.append(String.format("%d%n", uses.size()));
		}
		FileIO.toFile(sb.toString(), "/tmp/slice.txt");
	}

}
