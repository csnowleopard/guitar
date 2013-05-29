package edu.umd.cs.guitar.gui;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.umd.cs.guitar.eventhandlers.EdgeNodeEventHandler;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * <b>EdgeNode</b> is an extension of a PPath used to represent an edge in the EFG visualization. It has the added functionality
 * of being able to determine when or if the two ends of the edge believe it to be active. It also stores center positioning 
 * for the two ends of the edge as well as an adjusted positioning used to try and prevent the overlapping of edges from the 
 * same root point.
 * 
 * @author Sigmund Gorski
 * @version 1.0
 */
public class EdgeNode extends PPath{

	private static final long serialVersionUID = 1L;
	/**
	 * A string used to determine if an edge is active with one node end.
	 */
	private String lock0;
	/**
	 * A string used to determine if an edge is active with the other end.
	 */
	private String lock1;
	/**
	 * A mapping of event id's to the center point of the EventNodes associated with the event id.
	 */
	private Map<String, Point2D> center;
	/**
	 * A mapping of event id's to the adjusted point of the EventNodes associated with the event id.
	 */
	private Map<String, Point2D> adj;
	
	/**
	 * The constructor for the edge node.
	 * 
	 * @param window the window node associated with this edge used for the listener
	 * @param can the canvas for the EFG Viewer used for the listener
	 */
	public EdgeNode(WindowNode window, PCanvas can){
		super();
		lock0 = null;
		lock1 = null;
		center = new HashMap<String, Point2D>();
		adj = new HashMap<String, Point2D>();
		
		this.addInputEventListener(new EdgeNodeEventHandler(window, can));
	}
	
	/**
	 * Adds a point to the mapping of the event id to the center point of the event.
	 * 
	 * @param id the event id
	 * @param point the center point
	 */
	public void setCenter(String id, Point2D point){
		center.put(id, point);
	}
	
	/**
	 * Gets the center point associated with the event id.
	 * 
	 * @param id the event id
	 * @return the center point associated with the event id
	 */
	public Point2D getCenter(String id){
		return center.get(id);
	}
	
	/**
	 * Gets both of the center points in array form
	 * 
	 * @return array of center points
	 */
	public Point2D[] getCenterAll(){
		return center.values().toArray(new Point2D[0]);
		
	}
	
	/**
	 * Gets the event id's of the two points associated with this edge.
	 * 
	 * @return an array of event id's associated with this edge
	 */
	public String[] getCenterKeys(){
		return center.keySet().toArray(new String[0]);
	}
	
	/**
	 *  Adds a point to the mapping of the event id to the adjusted point of the event.
	 * 
	 * @param id the event id
	 * @param point the point to add
	 */
	public void setAdj(String id, Point2D point){
		adj.put(id, point);
	}
	
	/**
	 * Gets the adjusted point associated with the event id.
	 * 
	 * @param id the event id
	 * @return the adjusted point associated with the event id
	 */
	public Point2D getAdj(String id){
		return adj.get(id);
	}
	
	/**
	 * Gets both of the adjusted points in array form
	 * 
	 * @return array of adjusted points
	 */
	public Point2D[] getAdjAll(){
		return adj.values().toArray(new Point2D[0]);
	}
	
	/**
	 * Gets the event id's of the two points associated with this edge.
	 * 
	 * @return an array of event id's associated with this edge
	 */
	public String[] getAdjKeys(){
		return adj.keySet().toArray(new String[0]);
	}
	
	/**
	 * Gets the size of the adjusted point mapping.
	 * 
	 * @return the size
	 */
	public int sizeAdj(){
		return adj.size();
	}
	
	/**
	 * Locks this edge on one or both of event nodes so that it stays active.
	 * 
	 * @param id event id of the event to lock on
	 */
	public void lock(String id){
		if(lock0 != null){
			lock1 = id;
		}else{
			lock0 = id;
		}
	}
	
	/**
	 * Removes one lock allowing the node to become not visible now or in the future when the other node calls unlock.
	 * 
	 * @param id the event id of the event node that will be unlocked
	 */
	public void unlock(String id){
		if(id.equals(lock1)){
			lock1 = null;
		}else{
			lock0 = null;
		}
	}
	
	/**
	 * Determines if the edge is locked on a certain event node.
	 * 
	 * @param id the event node id
	 * @return true if it is locked on the event id and false otherwise
	 */
	public boolean isLockedOn(String id){
		return (id.equals(lock0) || id.equals(lock1));
	}
	
	/**
	 * Determines if the edge is currently active (ie visible).
	 * 
	 * @return true if it is and false otherwise
	 */
	public boolean notActive(){
		return !(lock0 != null || lock1 != null);
	}
	
	/**
	 * Returns the event node associated with the event id that this edge links to.
	 * 
	 * @param id the event id
	 * @return the event node associated with this edge and the event id
	 */
	@SuppressWarnings("unchecked")
	public EventNode grabThis(String id){
		return ((ArrayList<EventNode>)this.getAttribute("nodes")).get(0).getID().equals(id) ? ((ArrayList<EventNode>)this.getAttribute("nodes")).get(0) : ((ArrayList<EventNode>)this.getAttribute("nodes")).get(1);
	}
	
	/**
	 * Returns the event node that is not associated with the event id given but is associated with the edge.
	 * 
	 * @param id the event id
	 * @return the event node associated with this edge but not the event id given
	 */
	@SuppressWarnings("unchecked")
	public EventNode grabOther(String id){
		return ((ArrayList<EventNode>)this.getAttribute("nodes")).get(0).getID().equals(id) ? ((ArrayList<EventNode>)this.getAttribute("nodes")).get(1) : ((ArrayList<EventNode>)this.getAttribute("nodes")).get(0);
	}
	
}
