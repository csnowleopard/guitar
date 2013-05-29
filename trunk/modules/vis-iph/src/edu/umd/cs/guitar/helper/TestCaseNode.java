package edu.umd.cs.guitar.helper;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * <b>TestCaseNode</b> is used as a node in the test case prefix tree. It stores various information about
 * the relationships between events and other events in terms of test cases.
 * 
 * @author Sigmund Gorski
 * @version 1.0
 */
public class TestCaseNode {

	/** This is a reference to the parent node of this event.*/
	private TestCaseNode parent;
	/** List of all the children events of this event*/
	private ArrayList<TestCaseNode> children;
	/** This is an string representing the window id associated with this evetn*/
	private String windowID;
	/** This is the screen shot of the window*/
	private BufferedImage img;
	/** This is the eventID.*/
	private String eventID;
	
	/**
	 * This is a constructor for the node which initializes the information to whatever is passed in.
	 * 
	 * @param parent the parent node
	 * @param windowID the windows id
	 * @param eventID the event id
	 */
	public TestCaseNode(TestCaseNode parent, String windowID, String eventID){
		this.parent = parent;
		this.windowID = windowID;
		this.eventID = eventID;
		children = new ArrayList<TestCaseNode>();
		this.img = null;
	}
	
	/**
	 * This is a constructor which initializes the node to nothing.
	 */
	public TestCaseNode(){
		this.parent = null;
		this.windowID = null;
		this.img = null;
		this.eventID = null;
		children = null;
	}
	
	/**
	 * This is a node which initializes just the parent node.
	 * 
	 * @param parent the parent node
	 */
	public TestCaseNode(TestCaseNode parent){
		this.parent = parent;
		this.windowID = null;
		this.eventID = null;
		children = null;
		this.img = null;
	}
	
	/**
	 * Adds a child node to the child node list
	 * 
	 * @param l the child node to add
	 */
	public void addChildren(ArrayList<TestCaseNode> l){
		children = l;
	}
	
	/**
	 * This returns the children node list.
	 * 
	 * @return list of children nodes
	 */
	public ArrayList<TestCaseNode> getChildren(){
		return children;
	}
	
	/**
	 * The getter for the window id.
	 * 
	 * @return the window id
	 */
	public String getWindowID(){
		return windowID;
	}
	
	/**
	 * The getter for the event id.
	 * 
	 * @return the event id
	 */
	public String getEventID(){
		return eventID;
	}
	
	/**
	 * The getter for the parent node.
	 * 
	 * @return the parent node
	 */
	public TestCaseNode getParent(){
		return parent;
	}
	
	/**
	 * The setter for the window id.
	 * 
	 * @param windowID the window id
	 */
	public void setWindowID(String windowID){
		this.windowID = windowID;
	}
	
	/**
	 * The setter for the event id.
	 * 
	 * @param eventID the event id
	 */
	public void setEventID(String eventID){
		this.eventID = eventID;
	}
	
	/**
	 * The setter for the parent node.
	 * 
	 * @param parent the parent node
	 */
	public void setParent(TestCaseNode parent){
		this.parent = parent;
	}
	
	/**
	 * The to string method that prints out various information about the node.
	 */
	public String toString(){
		return "[ " + (parent == null ? "Is NULL" : "Not NULL") + ", " + (children == null ? 0 : children.size()) + ", " + (windowID == null ? "Is NULL" : windowID) + ", " + (eventID == null ? "Is NULL" : eventID) + " ]";
	}
	
	/**
	 * The setter for the screen shot.
	 * 
	 * @param img the screen shot
	 */
	public void setImg(BufferedImage img){
		this.img = img;
	}
	
	/**
	 * The getter for the screen shot.
	 * 
	 * @return the screen shot
	 */
	public BufferedImage getImg(){
		return img;
	}
	
}
