package Readers;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * GUI event, inherits from the GUI element class.
 * TODO: add more inforation about events
 * @author Chris Carmel
 *
 */
public class Event extends GUIElement {
	

	/**
	 * Representation of the Event's presence on the first activity of the Android application.
	 */
	private boolean initial;

	/**
	 * Representation of the Event's action.
	 */
	private String action;

	/**
	 * Array of Event IDs that this Event has a normal edge to.
	 */
	private ArrayList<String> normalEdgesToSelfFrom = new ArrayList<String>();

	/**
	 * Array of Event IDs that this Event has a reaching edge to.
	 */
	private ArrayList<String> reachingEdgesToSelfFrom = new ArrayList<String>();

	/**
	 * Array of Event IDs that have a normal edge to this Event.
	 */
	private ArrayList<String> normalEdgesFromSelfTo = new ArrayList<String>();

	/**
	 * Array of Event IDs that have a reaching edge to this Event.
	 */
	private ArrayList<String> reachingEdgesFromSelfTo = new ArrayList<String>();

	/**
	 * Widget associated with this Event in the Android application.
	 */
	private Widget widget = null;
	
	/**
	 * Label Node on the PVisualizationCanvas for this Event.
	 */
	private PNode labelNode = null;
	
	/**
	 * Boolean representing this Event's visibility on the PVisualizationCanvas.
	 */
	public boolean visible = true;

	/**
	 * Constructs an empty Event.
	 */
	public Event() {
		super();
		this.initial = false;
		this.action = null;
		this.widget = null;
	}
	
	/**
	 * Constructs a fully populated Event.
	 * 
	 * @param id				the Event's ID
	 * @param type				the Event's type
	 * @param parentWidget		the Event's parent Widget
	 * @param window			the Window containing this Event 
	 * @param coord				the Event's coordinates (relative to the containing Window)
	 * @param title				the Event's title
	 * @param visible			the Event's visibility on the PVisualizationCanvas
	 * @param widgetClass		the Event's Android class
	 * @param replayableAction	the Event's replayable action value
	 * @param initial			the Event's presence on the Android application's initial activity
	 * @param action			the Event's action
	 * @param widget			the Event's associated Widget
	 */
	public Event(	String id, 
			String type, 
			Widget parentWidget, 
			Window window, 
			Float coord, 
			String title, 
			boolean visible, 
			String widgetClass, 
			String replayableAction,
			boolean initial,
			String action, 
			Widget widget) {
		super(id, type, parentWidget, window, coord, title, visible);
		this.initial = initial;
		this.action = action;
		this.widget = widget;
	}

	/**
	 * Returns whether the Event is on the Android application's initial activity.
	 * 
	 * @return		<code>true</code> if the Event is on the Android application's initial activity, <code>false</code> if not
	 */
	public boolean isInitial() {
		return initial;
	}

	/**
	 * Returns this Event's action.
	 * 
	 * @return	this Event's action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Returns the ArrayList of Event IDs that have normal edges to this Event.
	 * 
	 * @return	ArrayList of Event IDs that have normal edges to this Event
	 */
	public ArrayList<String> getNormalEdgesToSelfFrom() {
		return normalEdgesToSelfFrom;
	}
	
	/**
	 * Returns the ArrayList of Event IDs that have reaching edges to this Event.
	 * 
	 * @return	ArrayList of Event IDs that have reaching edges to this Event
	 */
	public ArrayList<String> getReachingEdgesToSelfFrom() {
		return reachingEdgesToSelfFrom;
	}

	/**
	 * Returns the ArrayList of Event IDs that this Event has normal edges to.
	 * 
	 * @return	ArrayList of Event IDs that this Event has normal edges to
	 */
	public ArrayList<String> getNormalEdgesFromSelfTo() {
		return normalEdgesFromSelfTo;
	}

	/**
	 * Returns the ArrayList of Event IDs that this Event has reaching edges to.
	 * 
	 * @return	ArrayList of Event IDs that this Event has reaching edges to
	 */
	public ArrayList<String> getReachingEdgesFromSelfTo() {
		return reachingEdgesFromSelfTo;
	}

	/**
	 * Returns a reference to the Widget associated with this Event
	 * 
	 * @return	a reference to the Widget associated with this Event
	 */
	public Widget getWidget() {
		return widget;
	}

	/**
	 * Returns a boolean representing the visibility of this Window
	 * 
	 * @return		a boolean representing the visibility of this Window
	 */
	public boolean isVisible() {
		return visible;
	}
	
	/**
	 * Returns a reference to this Event's label PNode on the PVisualizationCanvas.
	 * 
	 * @return		a reference to this Event's label PNode on the PVisualizationCanvas
	 */
	public PNode getLabelNode() {
		return labelNode;
	}
	
	/**
	 * Sets this Events <code>initial</code> value.
	 * 
	 * @param initial	value to set this Event's <code>initial</code> value to 
	 */
	public void setInitial(boolean initial) {
		this.initial = initial;
	}

	/**
	 * Sets this Events action value.
	 * 
	 * @param action	value to set this Event's action value to 
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * Adds a specified Event ID string to the array of Event IDs with normal edges to this Event.
	 * 
	 * @param eventId	ID of Event with normal edge to this Event
	 */
	public void addNormalEdgeToSelfFrom(String eventId) {
		normalEdgesToSelfFrom.add(eventId);
	}

	/**
	 * Adds a specified Event ID string to the array of Event IDs with reaching edges to this Event.
	 * 
	 * @param eventId	ID of Event with reaching edge to this Event
	 */
	public void addReachingEdgeToSelfFrom(String eventId) {
		reachingEdgesToSelfFrom.add(eventId);
	}

	/**
	 * Adds a specified Event ID to the array of Event IDs that this Event has a normal edge to.
	 *  
	 * @param eventId	ID of Event this Event has a normal edge to
	 */
	public void addNormalEdgeFromSelfTo(String eventId) {
		normalEdgesFromSelfTo.add(eventId);
	}

	/**
	 * Adds a specified Event ID to the array of Event IDs that this Event has a reaching edge to.
	 *  
	 * @param eventId	ID of Event this Event has a reaching edge to
	 */
	public void addReachingEdgeFromSelfTo(String eventId) {
		reachingEdgesFromSelfTo.add(eventId);
	}

	/**
	 * Sets the specified Widget to this Event.
	 * 
	 * @param widget	Widget to be added
	 */
	public void setWidget(Widget widget) {
		this.widget = widget;
	}

	/**
	 * Sets the visibility of this Event's node and associated edges on the PVisualizationCanvas.
	 * 
	 * @param visibility		value to set this Event's visibility to
	 */
	public void setVisible(boolean visibility) {
		visible = visibility;
		
		if  (widget.isVisible() != visible) {
			widget.setVisible(visible);	
		}
		
		PNode selfNode = getSelfNode();
		selfNode.setVisible(visible);
		
		getLabelNode().setVisible(visible);
		
		ArrayList edges = (ArrayList) selfNode.getAttribute("edgesFromSelfTo");
		for (int i = 0; i < edges.size(); i++) {
			((PPath) edges.get(i)).setVisible(visible);
		}
		edges = (ArrayList) selfNode.getAttribute("edgesToSelfFrom");
		for (int i = 0; i < edges.size(); i++) {
			((PPath) edges.get(i)).setVisible(visible);
		}
	}
	
	/**
	 * Sets this Event's label PNode to the incoming value.
	 * 
	 * @param labelNode		value to set this Event's label PNode value to
	 */
	public void setLabelNode(PNode labelNode) {
		this.labelNode = labelNode;
		if (widget.getLabelNode() != labelNode) {
			widget.setLabelNode(labelNode);
		}
	}
	
	/**
	 * Returns a string version of the current state of this Event.
	 */
	public String toString() {
//		String s  = "";
//		s += "\tTitle:\t" + getTitle() + "\n";
//		s += "\tEventId:\t" + getId() + "\n";
//		s += "\tWidgetId:\t" + widget.getId() + "\n";
//		s += "\tWindow:\t\t"	+ getWindow() + "\n";
//		s += "\tType:\t\t" + getType() + "\n";
//		s += "\tInitial:\t" + isInitial() + "\n";
//		s += "\tAction:\t\t" + getAction() + "\n";
//
//		s += "\tNormal Edge (To) Nodes:\n";
//		if (getNormalEdgesToSelfFrom().isEmpty()) {
//			s += "\t\tnone\n";
//		} else {
//			for (String k : getNormalEdgesToSelfFrom()) {
//				s += "\t\t" + k + "\n";
//			}
//		}
//
//		s += "\tReaching Edge Nodes (To):\n";
//		if (getReachingEdgesToSelfFrom().isEmpty()) {
//			s += "\t\tnone\n";
//		} else {
//			for (String k : getReachingEdgesToSelfFrom()) {
//				s += "\t\t" + k + "\n";
//			}
//		}
//
//		s += "\tNormal Edge Nodes (From):\n";
//		if (getNormalEdgesFromSelfTo().isEmpty()) {
//			s += "\t\tnone\n";
//		} else {
//			for (String k : getNormalEdgesFromSelfTo()) {
//				s += "\t\t" + k + "\n";
//			}
//		}
//
//		s += "\tReaching Edge Nodes (From):\n";
//		if (getReachingEdgesFromSelfTo().isEmpty()) {
//			s += "\t\tnone\n";
//		} else {
//			for (String k : getReachingEdgesFromSelfTo()) {
//				s += "\t\t" + k + "\n";
//			}
//		}
//
//		s += "\tWidget:\n" + getWidget().toString() + "\n";
//
//		return s;
		
		return getTitle();
	}
}