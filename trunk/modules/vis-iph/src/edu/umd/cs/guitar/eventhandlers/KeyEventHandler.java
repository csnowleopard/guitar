package edu.umd.cs.guitar.eventhandlers;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;

import edu.umd.cs.guitar.gui.GUIBuilder;
import edu.umd.cs.guitar.gui.GraphBuilder;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;

/**
 * <b>KeyEventHandler</b> handles the keyboard events for the application. They are only active when the 
 * canvas is selected. Ie the side bar and the file menu bar do not apply to this keyboard 
 * listener. However, in most cases focus if brought back to the canvas when an operation is 
 * completed on the menu bar or the side bar.
 * 
 * @author Sigmund Gorski
 * @version 1.0
 */
public final class KeyEventHandler extends PBasicInputEventHandler {
	
    /** Default scale factor, <code>0.1d</code>. */
    static final double DEFAULT_SCALE_FACTOR = 0.1d;
    /** Scale factor. */
    private double scaleFactor = DEFAULT_SCALE_FACTOR;
    /** This determines what keys are currently pressed.*/
    private boolean[] press = new boolean[11];
    /** This is the graph object which is required in order to prefrom some operations.*/
    private GraphBuilder graph;
    /** Panning operation value*/
    final private int LEFT = 0;
    /** Panning operation value*/
    final private int RIGHT = 1;
    /** Panning operation value*/
    final private int UP = 2;
    /** Panning operation value*/
    final private int DOWN = 3;
    
    /**
     * This is the constructor.
     * 
     * @param graph the GraphBuilder this handler is for
     */
    public KeyEventHandler(GraphBuilder graph) {
        super();
        PInputEventFilter eventFilter = new PInputEventFilter();
        this.graph = graph;
        eventFilter.rejectAllEventTypes();
        eventFilter.setAcceptsKeyPressed(true);
        eventFilter.setAcceptsKeyReleased(true);
        setEventFilter(eventFilter);
    }

    /**
     * Return the scale factor for this mouse wheel zoom event handler.  Defaults to <code>DEFAULT_SCALE_FACTOR</code>.
     *
     * @see #DEFAULT_SCALE_FACTOR
     * @return the scale factor for this mouse wheel zoom event handler
     */
    public double getScaleFactor() {
        return scaleFactor;
    }

    /**
     * Set the scale factor for this mouse wheel zoom event handler to <code>scaleFactor</code>.
     *
     * @param scaleFactor scale factor for this mouse wheel zoom event handler
     */
    public void setScaleFactor(final double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
    
    /** {@inheritDoc} */
    public void keyReleased(PInputEvent event){
    	
    	if(event.getKeyCode() == KeyEvent.VK_SHIFT){
    		press[0] = false;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_UP){
    		press[1] = false;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_DOWN){
    		press[2] = false;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_R){
    		press[3] = false;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_CONTROL){
    		press[4] = false;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_C){
    		press[5] = false;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_LEFT){
    		press[6] = false;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_RIGHT){
    		press[7] = false;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_V){
    		press[8] = false;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_O){
    		press[9] = false;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_D){
    		press[10] = false;
    	}
    	
    	if(!press[0] || !press[10]){
    		GraphBuilder.setDeleteMode(false);
    	}
    }

    /** {@inheritDoc} */
    public void keyPressed(PInputEvent event) {
    	
    	if(event.getKeyCode() == KeyEvent.VK_SHIFT){
    		press[0] = true;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_UP){
    		press[1] = true;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_DOWN){
    		press[2] = true;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_R){
    		press[3] = true;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_CONTROL){
    		press[4] = true;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_C){
    		press[5] = true;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_LEFT){
    		press[6] = true;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_RIGHT){
    		press[7] = true;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_V){
    		press[8] = true;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_O){
    		press[9] = true;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.VK_D){
    		press[10] = true;
    	}
    	
    	if(press[0] && press[1]){
    		zoomIn();
    	}else if(press[1]){
    		pan(UP);
    	}
    	if(press[0] && press[2]){
    		zoomOut();
    	}else if(press[2]){
    		pan(DOWN);
    	}
    	if(press[0] && press[8]){
    		graph.switchCanvas();
    	}
    	if(press[0] && press[3]){
    		if(graph.getCurOp().equals("efg")){
    			graph.resetEFGView();
    		}else{
    			graph.resetTestView();
    		}
    	}
    	if(press[4] && press[5]){
    		System.exit(0);
    	}
    	if(press[6]){
    		pan(LEFT);
    	}
    	if(press[7]){
    		pan(RIGHT);
    	}
    	if(press[4] && press[9]){
    		SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	GUIBuilder.createAndShowGUI();
	            }
        	});
			graph.setVisible(false);
			graph.dispose();
    	}
    	if(press[0] && press[10]){
    		GraphBuilder.setDeleteMode(true);
    	}
    }
    
    /**
     * Controls which direction to pan the canvas.
     * 
     * @param dir integer representing the direction to pan
     */
    private void pan(int dir){
    	int amount = 15;
    	if(dir == LEFT){
    		graph.getCanvas().getCamera().translateView(-amount, 0);
    	}else if(dir == RIGHT){
    		graph.getCanvas().getCamera().translateView(amount, 0);
    	}else if(dir == UP){
    		graph.getCanvas().getCamera().translateView(0, -amount);
    	}else if(dir == DOWN){
    		graph.getCanvas().getCamera().translateView(0, amount);
    	}
    }
    
    /**
     * Zoom's in on the current canvas.
     */
    private void zoomIn(){
        double scale = 1.0d + scaleFactor;
        scaleViewAboutPoint(scale);
    }
    
    /**
     * Zoom's out on the current canvas.
     */
    private void zoomOut(){
        double scale = 1.0d - scaleFactor;
        scaleViewAboutPoint(scale);
    }

    /**
     * Scales the view about the center of the canvas.
     *
     * @param scale the amount to scale
     */
    private void scaleViewAboutPoint(double scale) {
        Rectangle canvasBounds = graph.getCanvas().getBounds();
        graph.getCanvas().getCamera().scaleViewAboutPoint(scale, canvasBounds.getCenterX(), canvasBounds.getCenterY());
    }
}
