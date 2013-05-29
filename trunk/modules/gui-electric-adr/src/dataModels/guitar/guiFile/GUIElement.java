package dataModels.guitar.guiFile;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;

import dataModels.visualizer.Widget;
import dataModels.visualizer.Window;


import edu.umd.cs.piccolo.PNode;

/**
 * Abstract data structure that holds a GUI Element, which can be an event or a widget.
 * @author Chris Carmel
 *
 */
public abstract class GUIElement {

	/**
	 * Id of the GUI element
	 */
	private String id = null;
	/**
	 * Type of the element, this information is used in the visualization part to decide what color it should have
	 */
	private String type = null;
	/**
	 * Reference to its parent widget, is null if it does not have an associated parent widget
	 */
	private Widget parentWidget = null;
	/**
	 * Reference to the window on which the element resides
	 */
	private Window window = null;
	/**
	 * Coordinates of the element on the GUI, the coordinates are usually relative to the top right of the containing Window
	 */
	private Point2D.Float coord = new Point2D.Float(0, 0);
	/**
	 * Title of the element
	 */
	private String title = null;
	/**
	 * Variable that indicates whether the element should be visible in the visualization
	 */
	private boolean visible = true;
	/**
	 * This is the corresponding Piccolo Node for this element in the visualization tool
	 */
	private PNode selfNode = null;
	/**
	 * This is the corresponding Piccolo Node for the label of this element
	 */
	private PNode labelNode = null;
	
	/**
	 * Constructs an empty GUIElement.
	 */
	public GUIElement() {
		
	}
	
	/**
	 * Constructs a populated GUIElement.
	 * 
	 * @param id				the GUIElement's ID
	 * @param type				the GUIElement's type
	 * @param parentWidget		the GUIElement's Widget's parent Widget
	 * @param window			the Window containing the GUIElement
	 * @param coord				the GUIElement's Widget's coordinates (relative to the containing Window)
	 * @param title				the GUIElement's Widget's title
	 * @param visible			the GUIElement's visibility on the PVisualizationCanvas
	 * @param selfNode			the Piccolo Node representation of this GUIElement
	 * @param labelNode			the Piccolo Label Node representation of this GUIElement
	 */
	public GUIElement(String id, String type, Widget parentWidget,
			Window window, Point2D.Float coord, String title, boolean visible,
			PNode selfNode, PNode labelNode) {
		this.id = id;
		this.type = type;
		this.parentWidget = parentWidget;
		this.window = window;
		this.coord = coord;
		this.title = title;
		this.visible = visible;
		this.selfNode = selfNode;
		this.labelNode = labelNode;
	}
	
	/**
	 * 
	 * Constructs a partially populated GUIElement.
	 * 
	 * This is generally the preferred initializer when reading from the EFG or
	 * GUI files, populating values from their contents. Later the <code>selfNode</code> and 
	 * <code>labelNode</code> of this GUIElement will be set using the respective setters 
	 * when initializing the PVisualizationCanvas.
	 * 
	 * @param id				the GUIElement's ID
	 * @param type				the GUIElement's type
	 * @param parentWidget		the GUIElement's Widget's parent Widget
	 * @param window			the Window containing the GUIElement
	 * @param coord				the GUIElement's Widget's coordinates (relative to the containing Window)
	 * @param title				the GUIElement's Widget's title
	 * @param visible			the GUIElement's visibility on the PVisualizationCanvas
	 */
	public GUIElement(String id, String type, Widget parentWidget,
			Window window, Float coord, String title, boolean visible) {
		this.id = id;
		this.type = type;
		this.parentWidget = parentWidget;
		this.window = window;
		this.coord = coord;
		this.title = title;
		this.visible = visible;
		this.selfNode = null;
		this.labelNode = null;
	}
	
	/**
	 * Returns this GUIElement's ID.
	 * 
	 * @return this GUIElement's ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns this GUIElement's type.
	 * 
	 * The type refers to the Android event type.
	 * 
	 * @return	this GUIElement's type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Returns this GUIElement's parent Widget.
	 * 
	 * This refers to the Widget containing the Event or Widget on the Android application.
	 * 
	 * @return	this GUIElement's parent Widget
	 */
	public Widget getParentWidget() {
		return parentWidget;
	}
	
	/**
	 * Returns the Window containing this GUIElement.
	 * 
	 * @return	the Window containing this GUIElement
	 */
	public Window getWindow() {
		return window;
	}
	
	/**
	 * Returns the <code>Point2D</code> representing this GUIElement's coordinates.
	 * 
	 * These coordinates are relative to this GUIElement's containing Window's top right corner.
	 * 
	 * @return the coordinates of this GUIElement
	 */
	public Point2D.Float getCoord() {
		return coord;
	}
	
	/**
	 * Returns this GUIElement's title.
	 * 
	 * @return this GUIElement's title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Returns a boolean representing this GUIElement's visibility.
	 * 
	 * @return	boolean representing this GUIElement's visibility
	 */
	public boolean isVisible() {
		return visible;
	}
	
	/**
	 * Returns a reference to the Piccolo Node representing this GUIElement.
	 * 
	 * @return	a reference to the Piccolo Node representing this GUIElement
	 */
	public PNode getSelfNode() {
		return selfNode;
	}
	
	/**
	 * Returns a reference to the Piccolo Node representing this GUIElement's label.
	 * 
	 * @return	a reference to the Piccolo Node representing this GUIElement's label
	 */
	public PNode getLabelNode() {
		return labelNode;
	}
	
	/**
	 * Sets this GUIElement's ID to the incoming value.
	 * 
	 * @param id	the value to set this GUIElement's ID to
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Sets this GUIElement's type to the incoming value.
	 * 
	 * @param type	the value to set this GUIElement's type to
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Sets this GUIElement's parent Widget to the incoming Widget.
	 * 
	 * @param parentWidget	the Widget to set this GUIElement's parent Widget to
	 */
	public void setParentWidget(Widget parentWidget) {
		this.parentWidget = parentWidget;
	}
	
	/**
	 * Sets this GUIElement's Window to the incoming Window.
	 * 
	 * @param window	the Window to set this GUIElement's Window to
	 */
	public void setWindow(Window window) {
		this.window = window;
	}
	
	/**
	 * Sets this GUIElememnt's coordinates to the incoming value.
	 * 
	 * @param coord		the value to set this GUIElement's coordinates to
	 */
	public void setCoord(Point2D.Float coord) {
		this.coord = coord;
	}
	
	/**
	 * Sets this GUIElement's title to the incoming value.
	 * 
	 * @param title		the value to set this GUIElement's title to 
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Sets this GUIElement's visibility.
	 * 
	 * @param visible	the value to set this GUIElement's visibility to
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
		this.selfNode.setVisible(visible);
	}
	
	/**
	 * Sets this GUIElement's Piccolo Node to the incoming value.
	 * 
	 * @param selfNode		the Piccolo Node to set this GUIElement's selfNode to
	 */
	public void setSelfNode(PNode selfNode) {
		this.selfNode = selfNode;
	}
	
	/**
	 * Sets this GUIElement's label Piccolo Node to the incoming value.
	 *  
	 * @param labelNode		the label Piccolo Node to set this GUIElement's labelNode to
	 */
	public void setLabelNode(PNode labelNode) {
		this.labelNode = labelNode;
	}
	
	/**
	 * Sets this GUIElement's attributes to the incoming attribute values. 
	 * 
	 * @param id				the GUIElement's ID
	 * @param type				the GUIElement's type
	 * @param parentWidget		the GUIElement's Widget's parent Widget
	 * @param window			the Window containing the GUIElement
	 * @param coord				the GUIElement's Widget's coordinates (relative to the containing Window)
	 * @param title				the GUIElement's Widget's title
	 * @param visible			the GUIElement's visibility on the PVisualizationCanvas
	 */
	public void setAttributes(String id, String type, Widget parentWidget, Window window, Point2D.Float coord, String title, boolean visible) {
			this.id = id;
			this.type = type;
			this.parentWidget = parentWidget;
			this.window = window;
			this.coord = coord;
			this.title = title;
			this.visible = visible;
			this.selfNode = null;
			this.labelNode = null;
	}

}