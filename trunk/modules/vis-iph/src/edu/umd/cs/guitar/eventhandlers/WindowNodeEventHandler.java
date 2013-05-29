package edu.umd.cs.guitar.eventhandlers;

import edu.umd.cs.guitar.gui.WindowNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * <b>WindowNodeEventHandler</b> is a handler for the WindowNode that just zooms in on a clicked WindowNode so 
 * long as the screen shot itself is clicked and not any of the overlay information.
 * 
 * @author Sigmund Gorski
 * @version 1.0
 */
public class WindowNodeEventHandler extends PBasicInputEventHandler{

	/** The canvas where the WindowNode is located.*/
	private PCanvas can;
	
	/**
	 * The constructor.
	 * 
	 * @param can canvas that the WindowNode is displayed on
	 */
	public WindowNodeEventHandler(PCanvas can){
		this.can = can;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void mouseClicked(PInputEvent e){
		super.mouseClicked(e);
		Object temp = e.getPickedNode();
		if(temp instanceof WindowNode){
			WindowNode window = (WindowNode)temp;
			PBounds wbound = window.getGlobalFullBounds();
			wbound = new PBounds(wbound.getX()-40, wbound.getY()-40, wbound.getWidth()+80, wbound.getHeight()+80);
			can.getCamera().animateViewToCenterBounds(wbound, true, 1000);
			e.setHandled(true);
		}
	}
}
