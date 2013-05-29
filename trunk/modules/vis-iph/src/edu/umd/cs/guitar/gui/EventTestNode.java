package edu.umd.cs.guitar.gui;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;

import edu.umd.cs.piccolo.nodes.PPath;

/**
 * <b>EventTestNode</b> is used in a similar fashion as EventNode just with fewer functions. It is an extension of
 * PPath but is restricted to a rectangle shape. It has the added ability to determine its color and size based on 
 * what is passed in. It is utilized for the Test Case Visualization only.
 * 
 * @author Sigmund Gorski
 * @version 1.0
 */
public class EventTestNode extends PPath{

	private static final long serialVersionUID = 1L;
	/**
	 * The event id of node.
	 */
	private String id;
	/**
	 * The color of the edges associated with this event when it is active.
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
	public EventTestNode(int x, int y, int h, int w, String id, String type){
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
	}
	
	/**
	 * The get method for the event id associated with this node.
	 * 
	 * @return the event id
	 */
	public String getID(){
		return id;
	}
	
	/**
	 * The set method for the event id.
	 * 
	 * @param id the event id
	 */
	public void setID(String id){
		this.id = id;
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
