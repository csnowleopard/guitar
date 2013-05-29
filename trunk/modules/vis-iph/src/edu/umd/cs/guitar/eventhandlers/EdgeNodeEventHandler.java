package edu.umd.cs.guitar.eventhandlers;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.guitar.gui.EdgeNode;
import edu.umd.cs.guitar.gui.EventNode;
import edu.umd.cs.guitar.gui.ExtEdgeRef;
import edu.umd.cs.guitar.gui.WindowNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * <b>EdgeNodeEventHandler</b> is a handler for the clicking of edges in the EFG Viewer. For internal edges, the 
 * viewer either ignores the click or behaves as though an EventNode was clicked if the click occurred within
 * the EventNode border. If an external edge is clicked then the canvas is centered on the window this external 
 * edge leads to.
 * 
 * @author Sigmund Gorski
 * @version 1.0
 */
public class EdgeNodeEventHandler extends PBasicInputEventHandler{

	/** The window with which the edges are associated.*/
	private WindowNode window;
	/** The canvas of the EFG viewer.*/
	private PCanvas can;
	
	/**
	 * The constructor.
	 * 
	 * @param window the window with which the edges are associated
	 * @param can the EFG Viewer canvas
	 */
	public EdgeNodeEventHandler(WindowNode window, PCanvas can){
		super();
		this.window = window;
		this.can = can;
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
	 * This handles the toggling of internal edges.
	 * 
	 * @param node an EventNode that could have been clicked
	 * @param xPos the x position of the mouse click
	 * @param yPos the y position of the mouse click
	 */
	@SuppressWarnings("unchecked")
	private void hanldeRegEdge(EventNode node, double xPos, double yPos){
		PBounds bound = node.getGlobalFullBounds();
		if(bound.getX() <= xPos && bound.getY() <= yPos && (bound.getX() + bound.getWidth()) >= xPos && (bound.getY() + bound.getHeight()) >= yPos){
			ArrayList<EdgeNode> list = (ArrayList<EdgeNode>)node.getAttribute("intedges");
			boolean colorFreeded = false;
			boolean colorUsed = false;
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
		}
	}
	
	/**
	 * Handles the toggling of the external edges
	 * 
	 * @param node the possible EventNode to be toggled
	 * @param xPos the x position of the mouse
	 * @param yPos the y position of the mouse
	 */
	@SuppressWarnings("unchecked")
	private void handleExtEdges(EventNode node, double xPos, double yPos){
		PBounds bound = node.getGlobalFullBounds();
		if(bound.getX() <= xPos && bound.getY() <= yPos && (bound.getX() + bound.getWidth()) >= xPos && (bound.getY() + bound.getHeight()) >= yPos){
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
	
	/**
	 * Handles the toggling of the EventNode edges or if a external edge is clicked outside of the EventNode's bounds
	 * it will center the canvas on the WindowNode that this edge leads to
	 * 
	 * @param e the input event used to get the thing clicked and the mouse position
	 */
	@SuppressWarnings("unchecked")
	private void toggleVisible(PInputEvent e){
		double xPos = e.getPosition().getX();
		double yPos = e.getPosition().getY();
		Object temp = e.getPickedNode().getAttribute("nodes");
		
		if(temp instanceof ArrayList<?>){
			ArrayList<EventNode> edgel = (ArrayList<EventNode>)temp;
			WindowNode winn = (WindowNode)edgel.get(0).getParent();
			List<EventNode> l = winn.getChildrenReference();
			
			for(EventNode node : l){
				hanldeRegEdge(node, xPos, yPos);
				handleExtEdges(node, xPos, yPos);
			}
		}else{
			ExtEdgeRef holder = (ExtEdgeRef)temp;
			EventNode node = holder.getEvent();
			
			hanldeRegEdge(node, xPos, yPos);
			handleExtEdges(node, xPos, yPos);
			
			if(can != null){
			PBounds bound = node.getGlobalFullBounds();
				if(!(bound.getX() <= xPos && bound.getY() <= yPos && (bound.getX() + bound.getWidth()) >= xPos && (bound.getY() + bound.getHeight()) >= yPos)){
					WindowNode window = holder.getWindow();
					PBounds wbound = window.getGlobalFullBounds();
					wbound = new PBounds(wbound.getX()-40, wbound.getY()-40, wbound.getWidth()+80, wbound.getHeight()+80);
					can.getCamera().animateViewToCenterBounds(wbound, true, 1000);
				}
			}
		}
	}
}
