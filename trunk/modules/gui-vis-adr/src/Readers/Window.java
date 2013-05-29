package Readers;

import java.util.ArrayList;

import edu.umd.cs.piccolo.PNode;

/**
 * Data structure that holds the information to visualize one window of the GUI.
 * 
 * In this instance, the term "Window" is used to refer to the GUI of an 
 * activity of a given Android application. 
 * 
 * @author Chris Carmel
 *
 */
public class Window extends GUIElement {
	
	/**
	 * Boolean representing this Window's visiblity.
	 */
	private boolean visible = true;
	
	/**
	 * ArrayList of Piccolo Nodes representing Event/Widget contained within this Window.
	 */
	private ArrayList<PNode> nodes = new ArrayList<PNode>();
	
	/**
	 * ArrayList of Events contained within this Window.
	 */
	private ArrayList<Event> events = new ArrayList<Event>();
	
	/**
	 * ArrayList of Widgets contained within this Window.
	 */
	private ArrayList<Widget> widgets = new ArrayList<Widget>();
	
	/**
	 * Constructs an empty Window.
	 */
	public Window() {
		super();
	}
	
	/**
	 * Constructs a Window and populates it's title only.
	 * 
	 * @param title		value to set the constructed Window's title to
	 */
	public Window(String title) {
		super();
		this.setTitle(title);
	}
	
	/**
	 * Constructs a Window and populates it with the incoming values.
	 * 
	 * @param title		value to set the constructed Window's title to
	 * @param selfNode	value to set the constructed Window's Piccolo Node to
	 * @param nodes		value to set the constructed Window's ArrayList of containing Event/Widget Piccolo Nodes to
	 * @param events		value to set the constructed Window's ArrayList of containing Events to
	 * @param widgets		value to set the constructed Window's ArrayList of containing Widgets to
	 */
	public Window(String title, PNode selfNode, ArrayList<PNode> nodes, ArrayList<Event> events, ArrayList<Widget> widgets) {
		super();
		this.setTitle(title);
		this.setSelfNode(selfNode);
		this.nodes = nodes;
		this.events = events;
		this.widgets = widgets;
	}

	/**
	 * Returns this Window's ArrayList of containing Event/Widget Piccolo Nodes.
	 * 
	 * @return		this Window's ArrayList of containing Event/Widget Piccolo Nodes
	 */
	public ArrayList<PNode> getNodes() {
		return nodes;
	}
	
	/**
	 * Returns this Window's ArrayList of containing Events.
	 * 
	 * @return		this Window's ArrayList of containing Events
	 */
	public ArrayList<Event> getEvents() {
		return events;
	}
	
	/**
	 * Returns this Window's ArrayList of containing Widgets.
	 * 
	 * @return		this Window's ArrayList of containing Widgets
	 */
	public ArrayList<Widget> getWidgets() {
		return widgets;
	}
	
	/**
	 * Returns this Window's visibility.
	 * 
	 * @return		this Window's visibility
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets this Window's ArrayList of containing Event/Widget Piccolo Nodes.
	 * 
	 * @param nodes		value to set this Window's ArrayList of containing Event/Widget Piccolo Nodes to
	 */
	public void setNodes(ArrayList<PNode> nodes) {
		this.nodes = nodes;
	}
	
	/**
	 * Adds the incoming value to this Window's Event/Array Piccolo Nodes ArrayList.
	 * 
	 * @param node		value to add to this Window's Event/Array Piccolo Nodes ArrayList
	 * 
	 * @return				<code>true</code> if the value was added successfully, <code>false</code> if not
	 */
	public boolean addNode(PNode node) {
		return nodes.add(node);
	}
	
	/**
	 * Removes the specified value from this Window's Event/Widget Piccolo Nodes ArrayList.
	 * 
	 * @param node		value to be removed from this Window's Event/Widget Piccolo Node ArrayList
	 * 
	 * @return			<code>true</code> if the value was removed successfully, <code>false</code> if not
	 */
	public boolean removeNode(PNode node) {
		return nodes.remove(node);
	}
	
	/**
	 * Adds the incoming value to this Window's Event ArrayList.
	 * 
	 * @param event		value to be added to this Window's Event ArrayList
	 * 
	 * @return			<code>true</code> if the value was added successfully, <code>false</code> if not
	 */
	public boolean addEvent(Event event) {
		widgets.add(event.getWidget());
		return events.add(event);
	}
	
	/**
	 * Removes specified value from this Window's Event ArrayList.
	 * 
	 * @param event		value to be removed from this Window's Event ArrayList
	 * 
	 * @return			<code>true</code> if the value was removed successfully, <code>false</code> if not
	 */
	public boolean removeEvent(Event event) {
		widgets.remove(event.getWidget());
		return events.remove(event);
	}
	
	/**
	 * Adds the incoming value to this Window's Widget ArrayList.
	 * 
	 * @param widget	value to be added to this Window's Widget ArrayList
	 * 
	 * @return			<code>true</code> if the value was added successfully, <code>false</code> if not
	 */
	public boolean addWidget(Widget widget) {
		events.add(widget.getEvent());
		return widgets.add(widget);
	}
	
	/**
	 * Removes the specified value from this Window's Widget ArrayList.
	 *  
	 * @param widget	value to be removed from this Window's Widget ArrayList
	 * 
	 * @return			<code>true</code> if the value was removed successfully, <code>false</code> if not
	 */
	public boolean removeWidget(Widget widget) {
		events.remove(widget.getEvent());
		return widgets.remove(widget);
	}
	
	/**
	 * Sets the visibility of this Window on the PVisualizationCanvas.
	 * 
	 * @param visibility	value to set this Window's visibility to
	 */
	public void setVisible(boolean visibility) {
		visible = visibility;
		getSelfNode().setVisible(visible);
		getLabelNode().setVisible(visible);
		
		for (Widget w : widgets) {
			w.setVisibleFromWindow(visible);
		}
	}
		
	/**
	 * Returns a string version of this Window's current state.
	 */
	public String toString() {
//		String s = "";
//		
//		s += "\tWindow:\t\t" + getTitle() + "\n";
//		
//		s += "\t\tHas Node?\t\t" + (getSelfNode() != null) + "\n";
//		
//		s += "\t\tEvents:\n";
//		for (Event e : events) {
//			s += "\t\t\t" + e.getId() + "\n";
//		}
//		
//		s += "\t\tWidget:\n";
//		for (Widget w : widgets) {
//			s += "\t\t\t" + w.getId() +"\n";
//		}
//		
//		return s;
		
		return getTitle();
	}
}
