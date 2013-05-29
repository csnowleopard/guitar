/*	
 *  Copyright (c) 2012. The GREYBOX group at the University of Freiburg, Chair of Software Engineering.
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

package edu.umd.cs.guitar.graph.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

import edu.umd.cs.guitar.graph.Graph2Graph;
import edu.umd.cs.guitar.graph.Graph2GraphConfiguration;
import edu.umd.cs.guitar.graph.plugin.automaton.EFGAutomaton;
import edu.umd.cs.guitar.graph.plugin.automaton.EFGState;
import edu.umd.cs.guitar.graph.plugin.automaton.EFGTransition;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventGraphType;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.EventsType;
import edu.umd.cs.guitar.model.data.RowType;

/**
 * AutomatonG2GPlugin
 * 
 */
public class AutomatonG2GPlugin extends EFG2EIG {
	
	/**
	 * Counter for Event IDs
	 */
	private Map<String,Integer> eventIDcounter = new HashMap<String,Integer>();
	
	/**
	 * C-tor
	 * @param inputGraph Event Flow Graph
	 */
	public AutomatonG2GPlugin(EFG inputGraph) {
		super(inputGraph);
	}
	
	@Override
	public boolean parseGraph() {
		parseFollowRelations();
		parseInitialEvents();
		
		// convert EFG to EFG Automaton
		EFGAutomaton efg = convertEFG2Automaton();
		
		// convert Event Sequence to Event Sequence Automaton
		Automaton seq = convertEventSequence2Automaton(efg, Graph2GraphConfiguration.EXTRA_ARG.split(","));
		
		// subtract Event Sequence Automaton from EFG Automaton 
		Automaton min = efg.minus(seq);
		
		// convert EFG Automaton to EFG
		convertAutomaton2EFG(min, efg);
		
		//text2file(min.toDot(), "C:/Temp/EFG.dot");
		
		return true;
	}
	
	/**
	 * Converts an EFG to an EFG Automaton
	 * @return EFG Automaton
	 */
	protected EFGAutomaton convertEFG2Automaton() {
		// create automaton
		EFGAutomaton efg = new EFGAutomaton();
		
		// add initial state
		EFGState initialState = efg.addEFGState();
		efg.setInitialState(initialState);
		
		List<EventType> events = inputGraph.getEvents().getEvent();
		for ( EventType event : events ) {
			EFGState state = efg.lookupState(event);
			
			// add initial events
			if ( event.isInitial() ) {
				initialState.addTransition(new EFGTransition(state));
			}
			
			// add succeeding events
			for ( EventType event2 : succs.get(event) ) {
				EFGState state2 = efg.lookupState(event2);
				state.addTransition(new EFGTransition(state2));
			}
		}
		
		return efg;
	}
	
	/**
	 * Converts an Event Sequence to an Event Sequence Automaton
	 * @param efg EFG Automaton
	 * @param eventIDs Event IDs
	 * @return Automaton
	 */
	protected Automaton convertEventSequence2Automaton(EFGAutomaton efg, String[] eventIDs) {
		// create automaton
		Automaton seq = new Automaton();
		
		// add initial state
		State initialState = new State();
		initialState.setAccept(false);
		seq.setInitialState(initialState);
		
		State prevState = initialState;
		for ( String eventID : eventIDs ) {
			// add state
			State nextState = new State();
			nextState.setAccept(false);
			
			// add transition
			char c = efg.lookupTransitionChar(eventID);
			prevState.addTransition(new Transition(c, nextState));
			prevState = nextState;
		}
		
		// last state is accepting
		prevState.setAccept(true);
		
		// add transitions to last state
		for ( EFGState state : efg.getEFGStates() ) {
			if ( null != state.getEvent() )
				prevState.addTransition(new Transition(state.getTransitionChar(), prevState));
		}
		
		return seq;
	}
	
	/**
	 * Converts the resulting automaton in an EFG
	 * @param min Automaton
	 * @param efg EFG Automaton
	 */
	protected void convertAutomaton2EFG(Automaton min, EFGAutomaton efg) {
		// setup events
		EventsType eventList = factory.createEventsType();
		List<EventType> events = new ArrayList<EventType>();
		eventList.setEvent(events);
		
		// setup rows
		EventGraphType eventGraph = factory.createEventGraphType();
		List<RowType> rows = new ArrayList<RowType>();
		eventGraph.setRow(rows);
		
		// setup EFG
		outputGraph = factory.createEFG();
		outputGraph.setEvents(eventList);
		outputGraph.setEventGraph(eventGraph);
		
		// a state-event map
		Map<State,EventType> stateEventMap = new HashMap<State,EventType>();
		
		// add events
		for ( State state : min.getStates() ) {
			for ( Transition transition : state.getTransitions() ) {
				// state and event
				State destState = transition.getDest();
				EventType event = null;
				
				if ( stateEventMap.containsKey(destState) ) {
					// get event
					event = stateEventMap.get(destState);
				} else {
					// search EFG state
					char c = transition.getMin();
					EFGState s = efg.lookupState(c);
					EventType oldEvent = s.getEvent();
					
					// create event
					event = new EventType();
					event.setEventId(nextEventID(oldEvent.getEventId()));
					event.setWidgetId(oldEvent.getWidgetId());
					event.setType(oldEvent.getType());
					event.setAction(oldEvent.getAction());
					event.setListeners(oldEvent.getListeners());
					
					// add event
					stateEventMap.put(destState, event);
					events.add(event);
				}
				
				// is initial state?			
				if ( min.getInitialState() == state ) {
					event.setInitial(true);
				}
			}
		}
		
		// add rows
		for ( int i = 0; i < events.size(); i++ ) {
			RowType row = factory.createRowType();
			for ( int j = 0; j < events.size(); j++ ) {
				row.getE().add(j, 0);
			}
			rows.add(i, row);
		}
		
		// change rows
		for ( State state : stateEventMap.keySet() ) {
			for ( Transition transition : state.getTransitions() ) {
				// get source event
				EventType eSrc = stateEventMap.get(state);
				int iSrc = events.indexOf(eSrc);
				
				// get destination event
				EventType eDest = stateEventMap.get(transition.getDest());
				int iDest = events.indexOf(eDest);
				
				// change row
				RowType row = rows.get(iSrc);
				row.getE().set(iDest, 1);
			}
		}
	}
	
	/**
	 * Generates the next event ID
	 * @param eventID original Event ID
	 * @return next Event ID
	 */
	protected String nextEventID(String eventID) {
		int count = 0;
		
		// lookup up event ID count		
		if ( eventIDcounter.containsKey(eventID) ) {
			count = eventIDcounter.get(eventID);
		}
		
		// increment event ID count
		eventIDcounter.put(eventID, ++count);
		
		// return original event ID
		if ( 1 == count ) {
			return eventID;
		}
		
		// return next event ID; "v" stands for "version"
		return eventID + "v" + count;
	}
	
	/**
	 * Stores text to a file
	 * @param text Text
	 * @param filename Filename
	 * @return true = success
	 */
	public static boolean text2file(String text, String filename) {
		boolean success = false;
		try {
			FileWriter fw = new FileWriter(new File(filename));
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.close();
			fw.close();
			success = true;
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return success;
	}
	
	@Override
	public boolean shouldGenerateGraph() {
		return true;
	}

	@Override
	public boolean shouldGenerateMap() {
		return false;
	}

	@Override
	public boolean requireExtraArgs() {
		return true;
	}

	@Override
	public void printInfo() {
		
	}	
	
	/**
	 * Main method (for debugging purposes)
	 * @param args Command-line arguments
	 */
	public static void main(String[] args) {
		Graph2Graph.main(args);
	}

}
