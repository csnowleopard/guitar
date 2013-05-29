package edu.umd.cs.guitar.eventhandlers;

import java.util.ArrayList;

import edu.umd.cs.guitar.gui.WindowTestNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * <b>EdgeTestEventHandler</b> is an listener for the edges in the Test Case Viewer. It allows for
 * clicking on an edge to alternate between the two window nodes at either end of the edge.
 * 
 * @author Sigmund Gorski
 * @version 1.0
 */
public class EdgeTestEventHandler extends PBasicInputEventHandler{

	/** The canvas associated with the edges.*/
	private PCanvas can;
	/** The current node the window is focused on so we can tell which end of the edge to go to when clicked.*/
	private WindowTestNode cur;
	
	/**
	 * The constructor.
	 * 
	 * @param can the canvas associated with the edges
	 */
	public EdgeTestEventHandler(PCanvas can){
		super();
		this.can = can;
		cur = null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void mouseClicked(PInputEvent e){
		super.mouseClicked(e);
		e.setHandled(true);
		WindowTestNode windownode1 = ((ArrayList<WindowTestNode>)((PPath)e.getPickedNode()).getAttribute("nodes")).get(0);
        WindowTestNode windownode2 = ((ArrayList<WindowTestNode>)((PPath)e.getPickedNode()).getAttribute("nodes")).get(1);
        WindowTestNode window;
		if(cur == null){
			window = windownode2;
			cur = windownode2;
		}else if(cur.equals(windownode1)){
			window = windownode2;
			cur = windownode2;
		}else{
			window = windownode1;
			cur = windownode1;
		}
		
		PBounds wbound = window.getGlobalFullBounds();
		wbound = new PBounds(wbound.getX()-40, wbound.getY()-40, wbound.getWidth()+80, wbound.getHeight()+80);
		can.getCamera().animateViewToCenterBounds(wbound, true, 1000);
	}
}
