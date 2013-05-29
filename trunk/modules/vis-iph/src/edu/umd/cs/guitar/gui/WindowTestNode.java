package edu.umd.cs.guitar.gui;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.guitar.eventhandlers.WindowTestNodeEventHandler;
import edu.umd.cs.guitar.gen.PropertyType;
import edu.umd.cs.guitar.graphbuilder.EFGBuilder;
import edu.umd.cs.guitar.helper.TestCaseNode;
import edu.umd.cs.guitar.helper.iGUITARHelper;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * <b>WindowTestNode</b> is a node similar to the WindowNode but used for the Test Case Viewer. It is also an extension of PImage.
 * It only contains one Event Node overlaid like in WindowNode and one PText node positioned slightly above the 
 * image that displays the id of the single event. It is much simpler than WindowNode but contains similar functionality.
 * 
 * @author Sigmund Gorski
 * @version 1.0
 */
public class WindowTestNode extends PImage{
	
	private static final long serialVersionUID = 1L;
	/**
	 * The event id of the single event this node represents.
	 */
	private String eventID;
	/**
	 * The parent node of this node in the PrefixTree for comparison purposes only.
	 */
	private TestCaseNode parent;
	
	/**
	 * This is the constructor for the WindowTestNode.
	 * 
	 * @param efg the EFG used to get the information about the single event
	 * @param eventID the id representing the single event
	 * @param in the screen shot image for the window
	 * @param parent the parent in the PrefixTree used for comparison purposes only
	 * @param can the TestCase Canvas that is required for the lister attached to this node
	 */
	public WindowTestNode(EFGBuilder efg, String eventID, BufferedImage in, TestCaseNode parent, PCanvas can, GraphBuilder graph){
		super(in);
		this.eventID = eventID;
		this.parent = parent;
		List<PropertyType> props = efg.getWidgetFromEvent(eventID).getAttributes().getProperty();
		int x = Integer.parseInt(iGUITARHelper.getProperty(props, "x_absolute"));
		int y = Integer.parseInt(iGUITARHelper.getProperty(props, "y_absolute"));
		int h = Integer.parseInt(iGUITARHelper.getProperty(props, "height"));;
		int w = Integer.parseInt(iGUITARHelper.getProperty(props, "width"));
		String type = iGUITARHelper.getProperty(props, "INVOKE");
		EventTestNode node = new EventTestNode(x,y,h,w,eventID,type);
			
		this.addAttribute("edges", new ArrayList<PPath>());
		this.addChild(node);
		
		PText id = new PText(eventID);
		this.addChild(id);
		id.translate(0, -15);
		
		this.addInputEventListener(new WindowTestNodeEventHandler(can,graph));
	}
	
	/**
	 * This is used to compare the parent node of this node in the PrefixTree with the node passed in.
	 * 
	 * @param node node to compare parent node with
	 * @return true is the same and false otherwise
	 */
	public boolean compParent(TestCaseNode node){
		if(parent.equals(node)){
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the event id of event node this node represents.
	 * 
	 * @return the event id
	 */
	public String getEventID(){
		return eventID;
	}
	
	/**
	 * Returns the parent node of the prefix tree
	 * 
	 * @return the parent node of the prefix tree
	 */
	public TestCaseNode getParentNode(){
		return parent;
	}
	
	/**
	 * A method used to update the edge position after the this WindowTestNode has been positioned in the canvas. This method is called in GraphBuilder.
	 * 
	 * @param edge the edge to update
	 */
	@SuppressWarnings("unchecked")
	public void updateEdge(PPath edge) {
        WindowTestNode windownode1 = ((ArrayList<WindowTestNode>)edge.getAttribute("nodes")).get(0);
        WindowTestNode windownode2 = ((ArrayList<WindowTestNode>)edge.getAttribute("nodes")).get(1);
        EventTestNode node1 = (EventTestNode)windownode1.getChild(0);
        EventTestNode node2 = (EventTestNode)windownode2.getChild(0);
        Point2D start = node1.getGlobalFullBounds().getCenter2D();
        Point2D end = node2.getGlobalFullBounds().getCenter2D();
        edge.reset();
        edge.moveTo((float)start.getX(), (float)start.getY());
        edge.lineTo((float)end.getX(), (float)end.getY());
    }
}
