package edu.umd.cs.guitar.gui;

/**
 * <b>ExtEdgeRef</b> is used to store the EventNode and WindowNode references that are associated with an external edge.
 * 
 * @author Sigmund Gorski
 * @version 1.0
 */
public class ExtEdgeRef {
	
	/**
	 * The WindowNode reference.
	 */
	WindowNode window;
	/**
	 * The EventNode reference.
	 */
	EventNode event;
	
	/**
	 * The constructor.
	 * 
	 * @param win the WindowNode reference.
	 * @param node the EventNode reference.
	 */
	public ExtEdgeRef(WindowNode win, EventNode node){
		window = win;
		event = node;
	}
	
	/**
	 * The get method for the WindowNode.
	 * 
	 * @return the WindowNode
	 */
	public WindowNode getWindow(){
		return window;
	}
	
	/**
	 * The get method for the EventNode.
	 * 
	 * @return the EventNode
	 */
	public EventNode getEvent(){
		return event;
	}
}
