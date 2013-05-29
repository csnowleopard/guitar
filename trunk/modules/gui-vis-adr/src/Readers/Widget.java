package Readers;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * GUI widget, inherits from the GUI element class.
 * TODO: add more inforation about widgets
 * @author Chris Carmel
 *
 */
public class Widget extends GUIElement {

	/**
	 * String representing this Widget's Android class.
	 */
	private String widgetClass = null;

	/**
	 * String representing this Widget's replayable action.
	 */
	private String replayableAction = null;

	/**
	 * Event representing this Widget's associated Event.
	 */
	private Event event = null;

	/**
	 * Boolean representing this Widget's node's visibility on the PVisualizationCanvas.
	 */
	private boolean visible = true;

	/**
	 * PNode representing this Widget's label PNode in the PVisualizationCanvas.
	 */
	private PNode labelNode;

	/**
	 * Constructs an empty Widget.
	 */
	public Widget() {
		super();
	}

	/**
	 * Constructs an empty Widget except for the incoming ID.
	 * 
	 * @param id		value to set this Widget's ID to
	 */
	public Widget(String id) {
		super();
		this.setId(id);
	}

	/**
	 * Constructs a Widget populated with the specified incoming values.
	 * 
	 * @param id				the Widget's ID
	 * @param type				the Widget's type
	 * @param parentWidget		the Widget's Widget's parent Widget
	 * @param window			the Window containing the Widget 
	 * @param coord				the Widget's Widget's coordinates (relative to the containing Window)
	 * @param title				the Widget's Widget's title
	 * @param visible			the Widget's visibility on the PVisualizationCanvas
	 * @param widgetClass		the Widget's Widget's Android class
	 * @param replayableAction	the Widget's replayable action value
	 */
	public Widget(String id, String type, Widget parentWidget, Window window, Point2D.Float coord, String title, boolean visible, String widgetClass, String replayableAction) {
		super(id, type, parentWidget, window, coord, title, visible);
		this.widgetClass = widgetClass;
		this.replayableAction = replayableAction;
	}

	/**
	 * Returns this Widget's Android Widget class.
	 * 
	 * @return		this Widget's Android Widget class
	 */
	public String getWidgetClass() {
		return widgetClass;
	}

	/**
	 * Returns this Widgets' replayable action.
	 * 
	 * @return		this Widget's replayable action
	 */
	public String getReplayableAction() {
		return replayableAction;
	}

	/**
	 * Returns a reference to this Widget's associated Event.
	 * 
	 * @return		a reference to this Widget's associated Event
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * Returns a reference to this Widget's label PNode on the PVisualizationCanvas.
	 * 
	 * @return		a reference to this Widget's label PNode on the PVisualizationCanvas
	 */
	public PNode getLabelNode() {
		return labelNode;
	}

	/**
	 * Returns a boolean representing the visibility of this Widget.
	 * 
	 * @return		a boolean representing the visibility of this Widget
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets this Widget's Android Widget class to the incoming value.
	 * 
	 * @param widgetClass		value to set this Widget's Android Widget class to
	 */
	public void setWidgetClass(String widgetClass) {
		this.widgetClass = widgetClass;
	}

	/**
	 * Sets this Widget's replayable action to the incoming value.
	 * 
	 * @param replayableAction		value to set this Widget's replayable action to
	 */
	public void setReplayableAction(String replayableAction) {
		this.replayableAction = replayableAction;
	}

	/**
	 * Sets this Widget's associated Event to the incoming value.
	 * 
	 * @param event		value to set this Widget's associated Event to
	 */
	public void setEvent(Event event) {
		this.event = event;
	}

	/**
	 * Sets this Widget's attributes to the incoming values.
	 * 
	 * @param id				value to set this Widget's ID to
	 * @param type				value to set this Widget's type to
	 * @param parentWidget		value to set this Widget's parent Widget to
	 * @param window			the Window containing value to set this Widget to
	 * @param coord				value to set this Widget's coordinates (relative to the containing Window) to
	 * @param title				value to set this Widget's title to
	 * @param visible			value to set this Widget's visibility on the PVisualizationCanvas
	 * @param widgetClass		value to set this Widget's Android Widget class to 
	 * @param replayableAction	value to set this Widget's replayable action to
	 */
	public void setWidgetAttributes(String id, String type, Widget parentWidget, Window window, Point2D.Float coord, String title, boolean visible, String widgetClass, String replayableAction) {
		super.setAttributes(id, type, parentWidget, window, coord, title, visible);
		this.widgetClass = widgetClass;
		this.replayableAction = replayableAction;
	}

	/**
	 * Sets the visibility of this Event's node and associated edges on the PVisualizationCanvas.
	 * 
	 * @param visibility		value to set this Event's visibility to
	 */
	public void setVisible(boolean visibility) {
		visible = visibility;

		if  (event.isVisible() != visible) {
			event.setVisible(visible);	
		}

		PNode selfNode = getSelfNode();
		selfNode.setVisible(visible);

		getLabelNode().setVisible(visible);

		ArrayList edges = (ArrayList) selfNode.getAttribute("edgesFromSelfTo");
		for (int i = 0; i < edges.size(); i++) {
			PPath currEdge = (PPath) edges.get(i);

			PNode node1 = (PNode) ((ArrayList)currEdge.getAttribute("nodes")).get(0);
			PNode node2 = (PNode) ((ArrayList)currEdge.getAttribute("nodes")).get(1);

			currEdge.setVisible(node1.getVisible() && node2.getVisible());
		}
		edges = (ArrayList) selfNode.getAttribute("edgesToSelfFrom");
		for (int i = 0; i < edges.size(); i++) {
			PPath currEdge = (PPath) edges.get(i);

			PNode node1 = (PNode) ((ArrayList)currEdge.getAttribute("nodes")).get(0);
			PNode node2 = (PNode) ((ArrayList)currEdge.getAttribute("nodes")).get(1);

			currEdge.setVisible(node1.getVisible() && node2.getVisible());
		}
	}

	/**
	 * Sets the visibility of this Widget's selfNode and associated edges on the PVisualizationCanvas
	 * but preserves the visibility of this Widget.
	 * 
	 * This method is used specifically to preserve the checkbox value of this Widget's TreeCell in the
	 * VisualizationTree portion of the Visualization.
	 * 
	 * @param windowVisibility		visibility of this Widgets's Window's node
	 */
	public void setVisibleFromWindow(boolean windowVisibility) {
		PNode selfNode = getSelfNode();

		if (windowVisibility == true) {
			selfNode.setVisible(visible);

			getLabelNode().setVisible(visible);		
		} else {
			selfNode.setVisible(false);

			getLabelNode().setVisible(false);
		}

		ArrayList edges = (ArrayList) selfNode.getAttribute("edgesFromSelfTo");
		for (int i = 0; i < edges.size(); i++) {
			PPath currEdge = (PPath) edges.get(i);

			PNode node1 = (PNode) ((ArrayList)currEdge.getAttribute("nodes")).get(0);
			PNode node2 = (PNode) ((ArrayList)currEdge.getAttribute("nodes")).get(1);

			currEdge.setVisible(node1.getVisible() && node2.getVisible());
		}
		edges = (ArrayList) selfNode.getAttribute("edgesToSelfFrom");
		for (int i = 0; i < edges.size(); i++) {
			PPath currEdge = (PPath) edges.get(i);

			PNode node1 = (PNode) ((ArrayList)currEdge.getAttribute("nodes")).get(0);
			PNode node2 = (PNode) ((ArrayList)currEdge.getAttribute("nodes")).get(1);

			currEdge.setVisible(node1.getVisible() && node2.getVisible());
		}
	}



	/**
	 * Sets this Widget's label PNode to the incoming value.
	 * 
	 * @param labelNode		value to set this Widget's label PNode value to
	 */
	public void setLabelNode(PNode labelNode) {
		this.labelNode = labelNode;
		if (event.getLabelNode() != labelNode) {
			event.setLabelNode(labelNode);
		}
	}

	/**
	 * Returns a string version of this Widget's current state.
	 */
	public String toString() {
		//		String s  = "";
		//		
		//		s += "\tTitle:\t\t" + getTitle() + "\n";
		//		s += "\tWindow:\t\t" + getWindow() + "\n";
		//		s += "\tWidgetClass:\t" + getWidgetClass() + "\n";
		//		s += "\tType:\t\t" + getType() + "\n";
		//		s += "\tCoord:\t\t( " + getCoord().getX() + " , " + getCoord().getY() + " )\n";
		//		s += "\tReplayableAction:\t" + getReplayableAction() + "\n";
		//		s += "\tEventID:\t\t" + getEvent().getId() + "\n";
		//		s += "\tID:\t\t" + getId() + "\n";
		//
		//		return s;

		return getTitle();
	}

	/**
	 * Compares this Widget to the incoming Widget.
	 * 
	 * @param otherWidget 		Widget to compare this Widget to
	 * 
	 * @return 					-1 if this Widget's containing Window's title is alphabetically before the otherWidget's Window's title
	 * 							-1 if the containing Window's are the same and this Widget is located closer to the top right corner than the otherWidget
	 * 							0 if the Widget's are contained in the same Window and located at the same coordinates
	 * 							1 if this Widget's containing Window's title is alphabetically after the otherWidget's  Window's title
	 * 							1 if the containing Window's are the same and this Widget is located further from the top right corner than the otherWidget
	 */
	public int compareTo(Widget otherWidget) {
		int returnValue = 0;

		if (this.getWindow().getTitle().compareTo(otherWidget.getWindow().getTitle()) == 0) {
			if (this.getCoord().getY() < otherWidget.getCoord().getY()) {
				returnValue = -1;
			} else if (this.getCoord().getY() > otherWidget.getCoord().getY()) {
				returnValue = 1;
			} else {
				if (this.getCoord().getX() < otherWidget.getCoord().getX()) {
					returnValue = -1;
				} else if (this.getCoord().getX() < otherWidget.getCoord().getX()) {
					returnValue = 1;
				} else {
					returnValue = this.getTitle().compareTo(otherWidget.getTitle());
				}
			}
		} else {
			returnValue = this.getWindow().getTitle().compareTo(otherWidget.getWindow().getTitle());
		}

		return returnValue;
	}
} 