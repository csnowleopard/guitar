package edu.umd.cs.guitar.gui;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import edu.umd.cs.guitar.eventhandlers.EventNodeEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * <b>EventNode</b> is used to create an overlay on the WindowNode of a single event. It is positioned over
 * the input widget on the screen shot image and is colored based on the type of input event.
 * 
 * @author Sigmund Gorski
 * @version 1.0
 */
public class EventNode extends PPath{

	private static final long serialVersionUID = 1L;
	/**
	 * The event id that is associated with this node.
	 */
	private String id;
	/**
	 * A list of the event id's of nodes that have been found to be connected to this one. 
	 * This is used so that multiple edges don't get added for the same node pair.
	 */
	private ArrayList<String> adj;
	/**
	 * The current color of the internal edges associated with this node if they are active.
	 */
	public Color curEdgeColor = null;
	
	/**
	 * The constructor for the node.
	 * 
	 * @param x upper left x coordinate
	 * @param y upper left y coordinate
	 * @param h height of rectangle
	 * @param w width of rectangle
	 * @param id the event id
	 * @param type the type of event (touch, text, wheel)
	 */
	public EventNode(int x, int y, int h, int w, String id, String type){
		super(new Rectangle(x,y,w,h));
		this.id = new String(id);
		int r = 0;
		int g = 0;
		int b = 0;
		if(type.equals("TOUCH")){
			r = 255;
		}else if(type.equals("PICKER_WHEEL")){
			r = 255;
			g = 255;
		}else if(type.equals("TEXT_FIELD")){
			b = 255;
		}else{
			g = 0;
			b = 255;
			r = 255;
		}
		this.setStroke(new BasicStroke(1.5f));
		this.setStrokePaint(new Color(r,g,b,100));
		this.setPaint(new Color(r,g,b,100));
		this.setBounds(x,y,w,h);
		adj = new ArrayList<String>();
		
		this.addInputEventListener(new EventNodeEventHandler());
	}
	
	/**
	 * THe getter for the event id.
	 * 
	 * @return the event id
	 */
	public String getID(){
		return id;
	}
	
	/**
	 * The setter for the event id.
	 * 
	 * @param id the event id
	 */
	public void setID(String id){
		this.id = id;
	}
	
	/**
	 * Checks to see if an edge between this event node and another has been created already.
	 * 
	 * @param adjID the event id of the other event node
	 * @return true if there is already an existing edge to the node and false otherwise
	 */
	public boolean adjCont(String adjID){
		return adj.contains(adjID);
	}
	
	/**
	 * Adds a string to the list of event node id's that this node is already associated with.
	 * 
	 * @param adjID the event id to add
	 * @return weather or not it was successful
	 */
	public boolean adjAdd(String adjID){
		return adj.add(adjID);
	}
	
	/**
	 * A getter for the entire list of nodes that are linked to this node.
	 * 
	 * @return list of event id's
	 */
	public ArrayList<String> adjGet(){
		return adj;
	}
	
	/**
	 * Standard equals method with the added functionality of taking in a string and returning that it equals the event id of this node.
	 */
	public boolean equals(Object o){
		if(o == null){
			return false;
		}else if(o.getClass() ==  getClass()){
			return this == o;
		}else if (id.getClass() == o.getClass()){
			return id.equals(o);
		}else{
			return false;
		}
	}
}
