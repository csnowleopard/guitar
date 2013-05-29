package edu.umd.cs.guitar.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.umd.cs.guitar.gen.EventType;
import edu.umd.cs.guitar.gen.PropertyType;
import edu.umd.cs.guitar.graphbuilder.EFGBuilder;
import edu.umd.cs.guitar.helper.iGUITARHelper;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * <b>WindowNode</b> is an extension of PImage with other nodes added on to it as children. It is utilized in the EFG Viewer for the display of a single view. 
 * The image is a screen shot of a single view taken during the ripping process. It is then overlaid with Rectangle PPath nodes representing the different 
 * widgets detected during the ripping process. 
 * <P>
 * 
 * @author Sigmund Gorski
 * @version 1.0
 */
public class WindowNode extends PImage{
	
	private static final long serialVersionUID = 1L;
	/**
	 * The object for looking up and getting various information for the EFG and GUI.
	 */
	private EFGBuilder efg;
	/**
	 * Array representing all of the possible colors of the edges when they are displayed.
	 */
	private Color colors[];
	/**
	 * The lock counter used to determine the next color to be selected when displaying the edges for a event node.
	 */
	private int colorCount[];
	/**
	 * The window id associated with this window.
	 */
	private String windowID;
	/**
	 * A list of external edges to be added after this node is created by the GraphBuilder.
	 */
	private ArrayList<EventType[]> externalEdges;
	
	/**
	 * Constructor for the WindoeNode with 4 arguments.
	 * 
	 * @param windowID represents the window (read from the GUI file)
	 * @param efg holds all data parsed from EFG and GUI
	 * @param in the image used as the background of the node
	 * @param edgelayer layer for edges used when adding edges
	 */
	@SuppressWarnings("unchecked")
	public WindowNode(String windowID, EFGBuilder efg, BufferedImage in, PLayer edgelayer){
		super(in);
		this.efg = efg;
		this.windowID = windowID;
		externalEdges = new ArrayList<EventType[]>();
		
		colors = new Color[]{Color.GREEN, Color.black, Color.blue, Color.red, Color.magenta, Color.yellow, new Color(0,100,0), Color.LIGHT_GRAY, new Color(184, 134, 11), new Color(128,0,0), Color.gray};
		colorCount = new int[]{0,0,0,0,0,0,0,0,0,0,0};
		
		ArrayList<EventType> eventList = efg.getEventsByView(windowID);
		for(EventType e : eventList){
			
			List<PropertyType> props = efg.getWidgetFromEvent(e.getEventId()).getAttributes().getProperty();
			String id = e.getEventId();
			int x = Integer.parseInt(iGUITARHelper.getProperty(props, "x_absolute"));
			int y = Integer.parseInt(iGUITARHelper.getProperty(props, "y_absolute"));
			int h = Integer.parseInt(iGUITARHelper.getProperty(props, "height"));;
			int w = Integer.parseInt(iGUITARHelper.getProperty(props, "width"));
			String type = iGUITARHelper.getProperty(props, "INVOKE");
			EventNode node = new EventNode(x,y,h,w,id,type);
			
			node.addAttribute("intedges", new ArrayList<PPath>());
			node.addAttribute("extedges", new ArrayList<PPath>());
			this.addChild(node);
		}
		List<EventNode> children = this.getChildrenReference();
		for(EventNode c : children){
			addEdges(c, edgelayer);
		}
		this.addAttribute("extedges", new ArrayList<PPath>());
	}
	
	/**
	 * This method is used by the constructor to add the edges to the window once all events have been added to the window.
	 * If any of the edges that need to be added lead to another window, it is added to a list for processing later once 
	 * all windows have been added to the EFG Viewer. Also, note no edge is added more than once, if a relationship between
	 * two EventNodes is detected a second time it is skipped.
	 * 
	 * @param cur the node that the edges are to be added to
	 * @param edgelayer layer to add the edges
	 */
	@SuppressWarnings({ "unchecked" })
	private void addEdges(EventNode cur, PLayer edgelayer){
		ArrayList<EventType> endings = efg.getEventFromEdges(cur.getID());
		for(EventType e: endings){
			if(!cur.adjCont(e.getEventId()) && !cur.getID().equals(e.getEventId())){
				if(efg.getViewByEvent(e.getEventId()).equals(windowID)){
					EdgeNode edge = new EdgeNode(this, null);
					EventNode other = findEventNode(this.getChildrenReference(), e.getEventId());
					
					((ArrayList<PPath>)cur.getAttribute("intedges")).add(edge);
					((ArrayList<PPath>)other.getAttribute("intedges")).add(edge);
					edge.addAttribute("nodes", new ArrayList<EventNode>());
					((ArrayList<EventNode>)edge.getAttribute("nodes")).add(cur);
					((ArrayList<EventNode>)edge.getAttribute("nodes")).add(other);
					
					edge.setVisible(false);
					edge.setStroke(new BasicStroke(1.6f));
					cur.adjAdd(e.getEventId());
					other.adjAdd(cur.getID());
					edgelayer.addChild(edge);
					
				}else{
					this.externalEdges.add(new EventType[]{efg.getEvent(cur.getID()), e});
				}
			}
		}
	}
	
	/**
	 * A get method for the external edges list generated when addEdges is run.
	 * 
	 * @return a list of all the event pairs that lead to a new window
	 */
	public ArrayList<EventType[]> getExternalEdges(){
		return externalEdges;
	}
	
	/**
	 * A get method for thw window ID.
	 * 
	 * @return windowID
	 */
	public String getWindowID(){
		return windowID;
	}
	
	/**
	 * Retrieves an event node with the event id given related to this window node.
	 * 
	 * @param eventID the event id
	 * @return the event node associated with the event id and this window
	 */
	@SuppressWarnings("unchecked")
	public EventNode getEventNode(String eventID){
		List<EventNode> l = this.getChildrenReference();
		for(EventNode e : l){
			if(e.getID().equals(eventID)){
				return e;
			}
		}
		return null;
	}
	
	/**
	 * Grabs the next color for the edges displayed when an EventNode is clicked.
	 * 
	 * @return a color
	 */
	public Color getNextColor(){
		int curMin = 0;
		for(int i = 0; i < colorCount.length; i++){
			if(colorCount[curMin] > colorCount[i]){
				curMin = i;
			}
		}
		colorCount[curMin]++;
		return colors[curMin];
	}
	
	/**
	 * Removes a lock from a color by decrementing the counter associated with it.
	 * 
	 * @param c a color
	 */
	public void freeColor(Color c){
		for(int i = 0; i < colors.length; i++){
			if(c.equals(colors[i])){
				colorCount[i]--;
			}
		}
	}
	
	/**
	 * This simply finds an event node in a list with a given id.
	 * 
	 * @param l a list of EventNodes
	 * @param id a string for a specific EventNode
	 * @return the EventNode associated with the event id given
	 */
	private EventNode findEventNode(List<EventNode> l, String id){
		for(EventNode e : l){
			if(e.equals(id)){
				return e;
			}
		}
		return null;
	}
	
	/**
	 * This must be run after all windows are added in order to update the edge to the current coordinates of a window.
	 * This is because the windows are moved around when added to the EFG Viewer and therefore would have different coordinates
	 * than when the edges were originally added. Note this is only for internal edges. Two edge coordinate groups are created, 
	 * one based on the center of the events associated with an edge and one that is randomized and adjusted to try to minimize
	 * the overlap of edges.
	 * 
	 * @param edge the edge whose position is about to be updated
	 */
	@SuppressWarnings("unchecked")
	public void updateEdge(EdgeNode edge) {
        EventNode node1 = ((ArrayList<EventNode>)edge.getAttribute("nodes")).get(0);
        EventNode node2 = ((ArrayList<EventNode>)edge.getAttribute("nodes")).get(1);
        Point2D start = node1.getGlobalFullBounds().getCenter2D();
        Point2D end = node2.getGlobalFullBounds().getCenter2D();
        Random r = new Random();
        double offset = 4.0d;
        double offset2 = 6.0d;
        Point2D node1adj = new Point2D.Double(node1.getGlobalFullBounds().getX() + offset + r.nextDouble()*(node1.getGlobalFullBounds().getWidth() - offset2), node1.getGlobalFullBounds().getY() + offset + r.nextDouble()*(node1.getGlobalFullBounds().getHeight() - offset2));
        Point2D node2adj = new Point2D.Double(node2.getGlobalFullBounds().getX() + offset + r.nextDouble()*(node2.getGlobalFullBounds().getWidth() - offset2), node2.getGlobalFullBounds().getY() + offset + r.nextDouble()*(node2.getGlobalFullBounds().getHeight() - offset2));
        edge.setCenter(node1.getID(), start);
        edge.setCenter(node2.getID(), end);
        edge.setAdj(node1.getID(), node1adj);
        edge.setAdj(node2.getID(), node2adj);
        edge.reset();
        edge.moveTo((float)start.getX(), (float)start.getY());
        edge.lineTo((float)end.getX(), (float)end.getY());
    }
	
	/**
	 * This method is called by GraphBuilder to update the coordinates of all the internal edges 
	 * associated this window.
	 */
	@SuppressWarnings("unchecked")
	public void updateEdges(){
		List<EventNode> children = this.getChildrenReference();
		ArrayList<EdgeNode> done = new ArrayList<EdgeNode>();
		for(EventNode cur : children){
			ArrayList<EdgeNode> list = (ArrayList<EdgeNode>)cur.getAttribute("intedges");
			for(EdgeNode e : list){
				if(!done.contains(e)){
					updateEdge(e);
					done.add(e);
				}
			}
		}
		genEdgeAdj();
	}
	
	/**
	 * Used to generate edge adjustments to try and minimize the overlap of edges for a single event node.
	 */
	@SuppressWarnings("unchecked")
	private void genEdgeAdj(){
		List<EventNode> l = this.getChildrenReference();
		for(EventNode node : l){
			ArrayList<ArrayList<EdgeNode>> overlap = genOverlapAnal(node);
			for(ArrayList<EdgeNode> i : overlap){
				String endN1 = i.get(0).getAdjKeys()[0].equals(node.getID()) ? i.get(0).getAdjKeys()[1] : i.get(0).getAdjKeys()[0];
				String endN2 = i.get(1).getAdjKeys()[0].equals(node.getID()) ? i.get(1).getAdjKeys()[1] : i.get(1).getAdjKeys()[0];
				Point2D end1 = i.get(0).getAdj(endN1);
				Point2D end2 = i.get(1).getAdj(endN2);
				double moveoverx = 0;
				double moveovery = 0;
				boolean first = false;
				
				for(EdgeNode e : i){
					if(first){
						EventNode other = e.grabOther(node.getID());
						PBounds b = other.getGlobalFullBounds();
						Point2D end = e.getAdj(e.getAdjKeys()[0].equals(node.getID()) ? e.getAdjKeys()[1] : e.getAdjKeys()[0]);
						double finalx = 0;
						double finaly = 0;
						
						moveoverx += 8;
						moveovery += 4;
						if(!(moveovery < b.getHeight() && moveovery > 0)){
							moveovery = moveovery - b.getHeight() + 3;
						}
						if(!(moveoverx < b.getWidth() && moveoverx > 0)){
							moveoverx = moveoverx - b.getWidth() + 3;
						}
						if(equalD(end1.getY(), end2.getY(),0.00001)){
							finalx = end.getX();
							finaly = b.getY() + moveovery;
						}else if(equalD(end1.getX(), end2.getX(),0.00001)){
							finaly = end.getY();
							finalx = b.getX() + moveoverx;
						}else{
							finalx = b.getX() + moveoverx;
							finaly = b.getY() + moveovery;
						}
						e.setAdj(other.getID(), new Point2D.Double(finalx, finaly));
					}else{
						first = true;
					}
				}
			}
		}
	}
	
	/**
	 * Analyzes the angle of spacing between the edges with the same root event node to determine if there is overlap.
	 * 
	 * @param node the node whose edges are to be analyzed
	 * @return an array list of lists of edges that have been determined to overlap
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<ArrayList<EdgeNode>> genOverlapAnal(EventNode node){
		ArrayList<EdgeNode> list = (ArrayList<EdgeNode>)node.getAttribute("intedges");
		ArrayList<ArrayList<EdgeNode>> ret = new ArrayList<ArrayList<EdgeNode>>();
		ArrayList<EdgeNode> done = new ArrayList<EdgeNode>();
		for(EdgeNode e : list){
			ArrayList<EdgeNode> temp = new ArrayList<EdgeNode>();
			temp.add(e);
			for(EdgeNode e2 : list){
				if(!e.equals(e2) && !done.contains(e2)){
					if(overlap(e,e2)){
						temp.add(e2);
						done.add(e2);
					}
				}
			}
			if(temp.size() > 1){
				ret.add(temp);
			}
			done.add(e);
		}
		return ret;
	}
	
	/**
	 * Determines if two edges overlap for a given node.
	 * 
	 * @param e the first edge
	 * @param e2 the second edge
	 * @return true if overlap and false otherwise
	 */
	private boolean overlap(EdgeNode e, EdgeNode e2){
		Point2D[] earr = e.getAdjAll();
		Point2D[] e2arr = e2.getAdjAll();
		double ret = (earr[0].getX() - earr[1].getX())*(e2arr[0].getY() - e2arr[1].getY()) - (earr[0].getY() - earr[1].getY())*(e2arr[0].getX() - e2arr[1].getX());
		
		if(equalD(ret,0.0d, 0.00001d)){
			return true;
		}else{
			String[] l1 = e.getAdjKeys();
			String[] l2 = e2.getAdjKeys();
			String send1 = null;
			String send2 = null;
			String scenter = null;
			Point2D end1 = null;
			Point2D end2 = null;
			Point2D center = null;
			if(l1[0].equals(l2[0])){
				send1 = l1[1];
				send2 = l2[1];
				scenter = l1[0];
			}else if(l1[0].equals(l2[1])){
				send1 = l1[1];
				send2 = l2[0];
				scenter = l1[0];
			}else if(l1[1].equals(l2[0])){
				send1 = l1[0];
				send2 = l2[1];
				scenter = l1[1];
			}else{
				send1 = l1[0];
				send2 = l2[0];
				scenter = l1[1];
			}
			end1 = e.getAdj(send1);
			end2 = e2.getAdj(send2);
			center = e.getCenter(scenter);
			double diffAngle = calcAngle(center,end1,end2);
			
			if(Double.isNaN(diffAngle) || diffAngle < 5.0d){
				return true;
			}else{
				return false;
			}
		}
	}
	
	/**
	 * Determine the distance between two points.
	 * 
	 * @param a
	 * @param b
	 * @return the distance between two points
	 */
	public static double distance(Point2D a, Point2D b){
		double vx = a.getX() - b.getX();
		double vy = a.getY() - b.getY();
		return Math.sqrt(vx*vx + vy*vy);
	}
	
	/**
	 * Calculates the angle between two points centered at the center points given.
	 * 
	 * @param center
	 * @param end1
	 * @param end2
	 * @return the angle in degrees representing the angle between the two edges represented by the coordinates given.
	 */
	public static double calcAngle(Point2D center, Point2D end1, Point2D end2){
		double relx1 = Math.abs(end1.getX() - center.getX());
		double rely1 = Math.abs(end1.getY() - center.getY());
		double relx2 = Math.abs(end2.getX() - center.getX());
		double rely2 = Math.abs(end2.getY() - center.getY());
		double adotb = relx1*relx2 + rely1*rely2;
		double maga = Math.sqrt(relx1*relx1 + rely1*rely1);
		double magb = Math.sqrt(relx2*relx2 + rely2*rely2);
		return (180*Math.acos(adotb/(maga*magb)))/Math.PI;
	}
	
	/**
	 * Method used to determine if two doubles are equal with a precision given.
	 * 
	 * @param a
	 * @param b
	 * @param epi the amount of precision wanted
	 * @return true if equal and false otherwise
	 */
	public boolean equalD(double a, double b, double epi){
		return Math.abs(a - b) <= epi * Math.max(1.0d, Math.max(Math.abs(a), Math.abs(b)));
	}
}
