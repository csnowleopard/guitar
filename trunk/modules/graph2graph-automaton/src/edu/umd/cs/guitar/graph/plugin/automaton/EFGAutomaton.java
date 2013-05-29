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

package edu.umd.cs.guitar.graph.plugin.automaton;

import java.util.ArrayList;
import java.util.List;

import dk.brics.automaton.Automaton;
import edu.umd.cs.guitar.model.data.EventType;

/**
 * EFGAutomaton
 */
public class EFGAutomaton extends Automaton {
	
	/**
	 * States
	 */
	private List<EFGState> states = new ArrayList<EFGState>();
	
	/**
	 * Current Transition Char (starts with '0')
	 */
	private char currentTransitionChar = '0';
	
	/**
	 * C-tor
	 */
	public EFGAutomaton() {
		// do nothing
	}
	
	/**
	 * Adds a new state
	 * @return EFG state
	 */
	public EFGState addEFGState() {
		EFGState state = new EFGState(currentTransitionChar++);
		states.add(state);
		return state;
	}
	
	/**
	 * Returns the list of states
	 * @return List of states
	 */
	public List<EFGState> getEFGStates() {
		return states;
	}
	
	/**
	 * Looks up an existing state for an event
	 * @param event Event Type
	 * @return State
	 */
	public EFGState lookupState(EventType event) {
		EFGState state = null;
		
		// search state
		for ( EFGState s : states ) {
			EventType e = s.getEvent();
			if ( null != e && e.getEventId().equals(event.getEventId()) ) {
				state = s;
				break;
			}
		}
		
		// add state
		if ( null == state ) {
			state = addEFGState();
			state.setEvent(event);
		}
		
		return state;
	}
	
	/**
	 * Looks up an existing state for a transition char
	 * @param transitionChar Transition char
	 * @return State
	 */
	public EFGState lookupState(char transitionChar) {
		for ( EFGState s : states ) {
			if ( s.getTransitionChar() == transitionChar )
				return s;
		}
		return null;
	}
	
	/**
	 * Looks up a transition char for an event
	 * @param eventID Event ID
	 * @return Transition char
	 */
	public char lookupTransitionChar(String eventID) {
		for ( EFGState s : states ) {
			EventType e = s.getEvent();
			if ( null != e && e.getEventId().equals(eventID) )
				return s.getTransitionChar();
		}
		return 0;
	}
	
}
