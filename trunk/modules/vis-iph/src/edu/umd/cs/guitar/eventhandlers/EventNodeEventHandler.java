package edu.umd.cs.guitar.eventhandlers;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.umd.cs.guitar.gui.EdgeNode;
import edu.umd.cs.guitar.gui.EventNode;
import edu.umd.cs.guitar.gui.WindowNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * <b>EventNodeEventHandler</b> is a handler for the mouse click on an EventNode in the EFG Viewer. It basically
 * toggles the edges of the event.
 * 
 * @author Sigmund Gorski
 * @version 1.0
 */
public class EventNodeEventHandler extends PBasicInputEventHandler{

	/**
	 * Sets up the handler the same way as PBasicInputEventHandler.
	 */
	public EventNodeEventHandler(){
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void mouseClicked(PInputEvent e){
		super.mouseClicked(e);
		e.setHandled(true);
		toggleVisible(e);
	}
	
	/**
	 * This is used to toggle the edges visible for an event node. External nodes are always toggled.
	 * The internal nodes are toggled only if the other end of the edge is not currently active as well.
	 * 
	 * @param e the event which is used to get the EventNode clicked
	 */
	@SuppressWarnings("unchecked")
	private void toggleVisible(PInputEvent e){
		ArrayList<EdgeNode> list = (ArrayList<EdgeNode>)e.getPickedNode().getAttribute("intedges");
		EventNode node = (EventNode)e.getPickedNode();
		boolean colorFreeded = false;
		boolean colorUsed = false;
		WindowNode window = (WindowNode)node.getParent();
		Color col = window.getNextColor();
		for(EdgeNode edge : list){
			if(edge.getVisible()){
				if(edge.isLockedOn(node.getID())){
					edge.unlock(node.getID());
					if(edge.notActive()){
						Point2D start = edge.getCenterAll()[0];
						Point2D end = edge.getCenterAll()[1];
						edge.reset();
				        edge.moveTo((float)start.getX(), (float)start.getY());
				        edge.lineTo((float)end.getX(), (float)end.getY());
						edge.setVisible(false);
						if(!colorFreeded){
							window.freeColor(node.curEdgeColor);
							node.curEdgeColor = null;
							colorFreeded = true;
						}
					}else{
						EventNode other = edge.grabOther(node.getID());
						if(edge.sizeAdj() > 0){
							Point2D end = edge.getAdj(node.getID());
							if(end != null){
								Point2D start = edge.getCenter(other.getID());
								edge.reset();
						        edge.moveTo((float)start.getX(), (float)start.getY());
						        edge.lineTo((float)end.getX(), (float)end.getY());
						        edge.setStrokePaint(other.curEdgeColor);
						        if(!colorFreeded){
									window.freeColor(node.curEdgeColor);
									node.curEdgeColor = null;
									colorFreeded = true;
								}
							}
						}
					}
				}else{
					Point2D start = edge.getCenterAll()[0];
					Point2D end = edge.getCenterAll()[1];
					edge.reset();
			        edge.moveTo((float)start.getX(), (float)start.getY());
			        edge.lineTo((float)end.getX(), (float)end.getY());
			        if(node.curEdgeColor == null){
			        	node.curEdgeColor = col;
			        	colorUsed = true;
			        }
			        edge.setStrokePaint(node.curEdgeColor);
					edge.lock(node.getID());
				}
			}else{
				edge.lock(node.getID());
				EventNode other = edge.grabOther(node.getID());
				if(edge.sizeAdj() > 0){
					Point2D end = edge.getAdj(other.getID());
					if(end != null){
						Point2D start = edge.getCenter(node.getID());
						edge.reset();
				        edge.moveTo((float)start.getX(), (float)start.getY());
				        edge.lineTo((float)end.getX(), (float)end.getY());
					}
				}
				if(node.curEdgeColor == null){
		        	node.curEdgeColor = col;
		        	colorUsed = true;
		        }
		        edge.setStrokePaint(node.curEdgeColor);
				edge.setVisible(true);
			}
		}
		if(colorUsed){
			node.curEdgeColor = col;
		}else{
			window.freeColor(col);
		}
		ArrayList<EdgeNode> list2 = (ArrayList<EdgeNode>)node.getAttribute("extedges");
		for(EdgeNode edge : list2){
			if(edge.getVisible()){
				edge.setVisible(false);
			}else{
				edge.setVisible(true);
			}
		}
	}
}
