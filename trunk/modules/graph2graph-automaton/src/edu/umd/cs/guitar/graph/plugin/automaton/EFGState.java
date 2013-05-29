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

import dk.brics.automaton.State;
import edu.umd.cs.guitar.model.data.EventType;

/**
 * EFGState
 */
public class EFGState extends State {
	
	/**
	 * Event
	 */
	private EventType event;
	
	/**
	 * Transition Char
	 */
	private char transitionChar;
	
	/**
	 * C-tor
	 * @param transitionChar Transition Char
	 */
	public EFGState(char transitionChar) {
		this.transitionChar = transitionChar;
		setAccept(true); // in an EFG, each state is accepting
	}
	
	/**
	 * Returns the event
	 * @return Event
	 */
	public EventType getEvent() {
		return event;
	}
	
	/**
	 * Assigns the event
	 * @param event Event
	 */
	public void setEvent(EventType event) {
		this.event = event;
	}	
	
	/**
	 * Returns the transition character
	 * @return Transition Char
	 */
	public char getTransitionChar() {
		return transitionChar;
	}

	/**
	 * Assigns the transition character
	 * @param transitionChar
	 */
	public void setTransitionChar(char transitionChar) {
		this.transitionChar = transitionChar;
	}

}
