package gui.visualizer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.io.FileUtils;

import utils.guitar.testCase.TSTWriter;

import dataModels.guitar.testCase.TestCase;
import dataModels.guitar.testCase.TestCaseStep;
import dataModels.visualizer.Event;
import dataModels.visualizer.VisualizationData;
import dataModels.visualizer.Widget;
import dataModels.visualizer.Window;



import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class is an extension of the Piccolo Canvas for specialized use with
 * visualizing a given ripped application's EFG and GUI files.
 * 
 * @author Chris Carmel
 * @author Andrew Guthrie
 * @author Asif Chowdhurry
 *
 */
public class PVisualizationCanvas extends PCanvas {

	/**
	 * VisualizationData containing data for this PVisualizationCanvas.
	 */
	protected VisualizationData vd;

	/**
	 * ArrayList of Piccolo Nodes that have been selecting on the PVisualizationCanvas.
	 */
	private ArrayList<PNode> clickedNodes;

	/**
	 * ArrayList of Piccolo Nodes that have been selected for test case generation
	 */
	protected ArrayList<PNode> testCaseNodes;

	/**
	 * ArrayList of Piccolo Nodes corresponding to whether or not an edge is reaching
	 */
	protected ArrayList<Boolean> testCaseEdges;

	/**
	 * Piccolo Layer for the application's Events.
	 */
	PLayer eventLayer;

	/**
	 * Piccolo Layer for the application's Edges.
	 */
	PLayer edgeLayer;

	/**
	 * Piccolo Layer for the application's Windows.
	 */
	PLayer windowLayer;

	/**
	 * Piccolo Layer for the application's Event Labels.
	 */
	PLayer labelLayer;
	
	/**
	 * Piccolo Layer for the initial loading of the application's Home panel.
	 */
	PLayer homeLayer;

	/**
	 * HashMap of Event type colors.
	 */
	private HashMap<String, Color> colors = new HashMap<String, Color>();

	/**
	 * Offset for each Window's Piccolo Node X-coordinate on the PVisualizationCanvas.
	 */
	final int WINDOW_OFFSET = 250;

	/**
	 * Height offset for each Label Piccolo Node.
	 */
	final int LABEL_HEIGHT = 20;

	/**
	 * Final String value representing the title of the Edge Layer ("edge").
	 */
	public static final String EDGE_LAYER_NAME = "edge";

	/**
	 * Final String value representing the title of the Event Layer ("event").
	 */
	public static final String EVENT_LAYER_NAME = "event";

	/**
	 * Final String value representing the title of the Label Layer ("label").
	 */
	public static final String LABEL_LAYER_NAME = "label";

	/**
	 * Final String value representing the title of the Window Layer ("window").
	 */
	public static final String WINDOW_LAYER_NAME = "window";
	
	/**
	 * Constructs and populates a PVisualizationCanvas.
	 * 
	 * @param vd		VisualizationData used to populate the PVisualizationCanvas.
	 */
	public PVisualizationCanvas(final VisualizationData vd) {
		this.vd = vd;

		clickedNodes = new ArrayList<PNode>();
		testCaseNodes = new ArrayList<PNode>();
		testCaseEdges = new ArrayList<Boolean>();

		Color[] colorsArray = { Color.RED, Color.ORANGE, Color.GREEN, Color.BLUE, Color.YELLOW };
		String[] eventTypesArray = { "TERMINAL", "RESTRICTED FOCUS", "UNRESTRICED FOCUS", "SYSTEM INTERACTION", "MENU OPEN" };
		for (int i = 0; i < colorsArray.length; i++) {
			colors.put(eventTypesArray[i], colorsArray[i]);
		}

		// set dimensions on the window
		setMinimumSize(new Dimension(0, 0));
		setPreferredSize(new Dimension(900, 800));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		// different layers
		eventLayer = getLayer();
		edgeLayer = new PLayer();
		windowLayer = new PLayer();
		labelLayer = new PLayer();
		homeLayer = new PLayer();

		// adding the layers to the main layer 
		getRoot().addChild(edgeLayer);
		getRoot().addChild(windowLayer);
		getRoot().addChild(labelLayer);
		getRoot().addChild(homeLayer);

		// ordering the layers for the camera view
		getCamera().addLayer(0, windowLayer);
		getCamera().addLayer(1, edgeLayer);
		getCamera().addLayer(2, labelLayer);
		getCamera().addLayer(3, homeLayer);

		// populate the various layers 
		populateWindowLayer();
		populateEventLayer();
		populateEdgeLayer();
		populateLabelLayer();

		// creating and adding test layers
		populateTestLayers();

		// this mouse dragging listener listens for window node movement
		windowLayer.addInputEventListener(new PDragEventHandler() {
			{
				PInputEventFilter filter = new PInputEventFilter();
				filter.setOrMask(InputEvent.BUTTON1_MASK | InputEvent.BUTTON3_MASK);
				setEventFilter(filter);
			}

			// do nothing if mouse hovering over window
			public void mouseEntered(PInputEvent e) {
				super.mouseEntered(e);
			}

			// do nothing if mouse stops hovering over window
			public void mouseExited(PInputEvent e) {
				super.mouseExited(e);
			}

			// handle event occur when first dragging
			protected void startDrag(PInputEvent e) {
				super.startDrag(e);
				e.getPickedNode().moveToFront();
				e.setHandled(true);
			}

			// when dragging window, run update window method 
			protected void drag(PInputEvent e) {
				if (e.getPickedNode().getAttribute("type").equals("window")) {
					super.drag(e);
					PVisualizationCanvas.this.updateWindow(e.getPickedNode());	
				}
			}
		});

		eventLayer.addInputEventListener(new PDragEventHandler() {
			{
				PInputEventFilter filter = new PInputEventFilter();
				filter.setOrMask(InputEvent.BUTTON1_MASK | InputEvent.BUTTON3_MASK);
				setEventFilter(filter);
			}

			public void mouseEntered(PInputEvent e) {
				if(clickedNodes.size() == 0 && testCaseNodes.size() == 0){
					super.mouseEntered(e);
					PNode currEventNode = e.getPickedNode();

					ArrayList<PPath> fromSelf = (ArrayList<PPath>) currEventNode.getAttribute("edgesFromSelfTo");
					ArrayList<PNode> destNodes = new ArrayList<PNode>();
					ArrayList<PNode> destWindows = new ArrayList<PNode>();
					destWindows.add((PNode) currEventNode.getAttribute("windowNode")); 
					for (PPath pp : fromSelf) {
						destNodes.add(((ArrayList<PNode>) pp.getAttribute("nodes")).get(1));
						destWindows.add((PNode) ((PNode) ((ArrayList<PNode>) pp.getAttribute("nodes")).get(1)).getAttribute("windowNode"));
					}

					for (int i = 0; i < edgeLayer.getChildrenCount(); i++) {
						PPath currEdge = (PPath) edgeLayer.getChild(i);
						if (fromSelf.contains(currEdge) == false) {
							currEdge.setTransparency(0.2f);
						} else {
							if (currEdge.getAttribute("edgeType").equals("normal")) {
								currEdge.setStroke(new BasicStroke(3));
							} else {
								float dash[] = { 10.0f };
								currEdge.setStroke(new BasicStroke(5.0f,
										BasicStroke.CAP_BUTT,
										BasicStroke.JOIN_MITER,
										10.0f, dash, 0.0f));
							}
						}
					}

					for (int i = 0; i < eventLayer.getChildrenCount(); i++) {
						PNode otherNode = eventLayer.getChild(i);
						if (otherNode == currEventNode) continue;
						if (destNodes.contains(otherNode) == false) {
							otherNode.setTransparency(0.2f);
							((PText) otherNode.getAttribute("label")).setTransparency(0.2f);
						}
					}

					for (int i = 0; i < windowLayer.getChildrenCount(); i++) {
						PNode otherWindowNode = windowLayer.getChild(i);
						if (destWindows.contains(otherWindowNode) == false) {
							otherWindowNode.setTransparency(0.2f);
						}
					}
				}
			}

			public void mouseExited(PInputEvent e) {
				if(clickedNodes.size() == 0 && testCaseNodes.size() == 0){
					super.mouseExited(e);
					PNode currEventNode = e.getPickedNode();

					ArrayList<PPath> fromSelf = (ArrayList<PPath>) currEventNode.getAttribute("edgesFromSelfTo");
					ArrayList<PNode> destNodes = new ArrayList<PNode>();
					ArrayList<PNode> destWindows = new ArrayList<PNode>();
					destWindows.add((PNode) currEventNode.getAttribute("windowNode")); 
					for (PPath pp : fromSelf) {
						destNodes.add(((ArrayList<PNode>) pp.getAttribute("nodes")).get(1));
						destWindows.add((PNode) ((PNode) ((ArrayList<PNode>) pp.getAttribute("nodes")).get(1)).getAttribute("windowNode"));
					}

					for (int i = 0; i < edgeLayer.getChildrenCount(); i++) {
						PPath currEdge = (PPath) edgeLayer.getChild(i);
						if (fromSelf.contains(currEdge) == false) {
							currEdge.setTransparency(1f);
						} else {
							if (currEdge.getAttribute("edgeType").equals("normal")) {
								currEdge.setStroke(new BasicStroke(1));
							} else {
								float dash[] = { 10.0f };
								currEdge.setStroke(new BasicStroke(3.0f,
										BasicStroke.CAP_BUTT,
										BasicStroke.JOIN_MITER,
										10.0f, dash, 0.0f));
							}
						}
					}

					for (int i = 0; i < eventLayer.getChildrenCount(); i++) {
						PNode otherNode = eventLayer.getChild(i);
						if (otherNode == currEventNode) continue;
						if (destNodes.contains(otherNode) == false) {
							otherNode.setTransparency(1f);
							((PText) otherNode.getAttribute("label")).setTransparency(1f);
						}
					}

					for (int i = 0; i < windowLayer.getChildrenCount(); i++) {
						PNode otherWindowNode = windowLayer.getChild(i);
						if (destWindows.contains(otherWindowNode) == false) {
							otherWindowNode.setTransparency(1f);
						}
					}
				}
			}

			public void mouseClicked(PInputEvent e){
				super.mouseClicked(e);
				PNode currEventNode = e.getPickedNode();
				//Reset the window
				if(e.isShiftDown() && e.isControlDown() && clickedNodes.size() == 0 && testCaseNodes.size() != 0){
					ArrayList<String> eventIDs = new ArrayList<String>();
					for (PNode P : testCaseNodes){
						eventIDs.add(((Event) P.getAttribute("event")).getId());
					}
					testCaseNodes.clear();
					String tst = TSTWriter.createTST(eventIDs, testCaseEdges);
					//Hardcoded filename currently as 001
					String input = (String) JOptionPane.showInputDialog(
					        new JFrame(),
					        "Please enter the filename for the testcase",
					        "Save Test Case", JOptionPane.INFORMATION_MESSAGE,
					        null, null, null);
					TSTWriter.saveTST(tst, vd.getApplicationTitle(), input);
					testCaseEdges.clear();
				} else if(e.isControlDown() && testCaseNodes.size() == 0){
					ArrayList<PPath> fromSelf = new ArrayList<PPath>();
					ArrayList<PNode> destWindows = new ArrayList<PNode>();
					for(PNode P : clickedNodes){
						fromSelf.addAll((ArrayList<PPath>) P.getAttribute("edgesFromSelfTo"));
						destWindows.add((PNode) P.getAttribute("windowNode"));
					}
					ArrayList<PNode> destNodes = new ArrayList<PNode>(); 
					for (PPath pp : fromSelf) {
						destNodes.add(((ArrayList<PNode>) pp.getAttribute("nodes")).get(1));
						destWindows.add((PNode) ((PNode) ((ArrayList<PNode>) pp.getAttribute("nodes")).get(1)).getAttribute("windowNode"));
					}

					for (int i = 0; i < edgeLayer.getChildrenCount(); i++) {
						PPath currEdge = (PPath) edgeLayer.getChild(i);
						if (fromSelf.contains(currEdge) == false) {
							currEdge.setTransparency(1f);
						} else {
							if (currEdge.getAttribute("edgeType").equals("normal")) {
								currEdge.setStroke(new BasicStroke(1));
							} else {
								float dash[] = { 10.0f };
								currEdge.setStroke(new BasicStroke(3.0f,
										BasicStroke.CAP_BUTT,
										BasicStroke.JOIN_MITER,
										10.0f, dash, 0.0f));
							}
						}
					}

					for (int i = 0; i < eventLayer.getChildrenCount(); i++) {
						PNode otherNode = eventLayer.getChild(i);
						if(clickedNodes.contains(otherNode)) continue;
						if (destNodes.contains(otherNode) == false) {
							otherNode.setTransparency(1f);
							((PText) otherNode.getAttribute("label")).setTransparency(1f);
						}
					}

					for (int i = 0; i < windowLayer.getChildrenCount(); i++) {
						PNode otherWindowNode = windowLayer.getChild(i);
						if (destWindows.contains(otherWindowNode) == false) {
							otherWindowNode.setTransparency(1f);
						}
					}
					if(clickedNodes.contains(currEventNode)){
						//checks to see if it was a right mouse click
						//if((e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) 
						clickedNodes.remove(currEventNode);
					} else {
						//checks to make sure it isnt a right mouse click
						//if(!((e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK))
						clickedNodes.add(currEventNode);
					}
					//If there are no currently clicked nodes, we are done here
					if(clickedNodes.size() == 0){
						return;
					} else { //redraw
						ArrayList<PPath> fromClicked = new ArrayList<PPath>();
						for(PNode P: clickedNodes){
							fromClicked.addAll((ArrayList<PPath>) P.getAttribute("edgesFromSelfTo"));
						}
						ArrayList<PNode> clickedDestNodes = new ArrayList<PNode>();
						ArrayList<PNode> clickedDestWindows = new ArrayList<PNode>();
						for(PNode P: clickedNodes){
							clickedDestWindows.add((PNode) P.getAttribute("windowNode")); 
						}
						for (PPath pp : fromClicked) {
							clickedDestNodes.add(((ArrayList<PNode>) pp.getAttribute("nodes")).get(1));
							clickedDestWindows.add((PNode) ((PNode) ((ArrayList<PNode>) pp.getAttribute("nodes")).get(1)).getAttribute("windowNode"));
						}

						for (int i = 0; i < edgeLayer.getChildrenCount(); i++) {
							PPath currEdge = (PPath) edgeLayer.getChild(i);
							if (fromClicked.contains(currEdge) == false) {
								currEdge.setTransparency(0.2f);
							} else {
								if (currEdge.getAttribute("edgeType").equals("normal")) {
									currEdge.setStroke(new BasicStroke(3));
								} else {
									float dash[] = { 10.0f };
									currEdge.setStroke(new BasicStroke(5.0f,
											BasicStroke.CAP_BUTT,
											BasicStroke.JOIN_MITER,
											10.0f, dash, 0.0f));
								}
							}
						}

						for (int i = 0; i < eventLayer.getChildrenCount(); i++) {
							PNode otherNode = eventLayer.getChild(i);
							if (otherNode == currEventNode) continue;
							if (clickedDestNodes.contains(otherNode) == false) {
								otherNode.setTransparency(0.2f);
								((PText) otherNode.getAttribute("label")).setTransparency(0.2f);
							}
						}
						for (int i = 0; i < windowLayer.getChildrenCount(); i++) {
							PNode otherWindowNode = windowLayer.getChild(i);
							if (clickedDestWindows.contains(otherWindowNode) == false) {
								otherWindowNode.setTransparency(0.2f);
							}
						}
					}
				} else if(e.isShiftDown()){
					if(testCaseNodes.size() == 0){
						testCaseNodes.add(currEventNode);
					} else {
						//need to check to make sure can reach this node
						PNode prev = testCaseNodes.get(testCaseNodes.size() - 1);
						ArrayList<PPath> fromSelf = new ArrayList<PPath>();
						for(PNode P : testCaseNodes){
							fromSelf.addAll((ArrayList<PPath>) P.getAttribute("edgesFromSelfTo"));
						}
						ArrayList<PNode> destNodes = new ArrayList<PNode>(); 
						for (PPath pp : fromSelf) {
							destNodes.add(((ArrayList<PNode>) pp.getAttribute("nodes")).get(1));
						}
						if(!destNodes.contains(currEventNode)){
							return;
						} else {
							testCaseNodes.add(currEventNode);
							//add edge to testCaseEdges, need to be implemented, consult chris
							//							for (PPath pp : fromSelf) {
							//								if(((ArrayList<PNode>) pp.getAttribute("nodes")).get(1).equals(currEventNode)){
							//									if(pp.getAttribute("")){
							//										
							//									}
							//								}
							//							}
							testCaseEdges.add(new Boolean(false));
						}
					}
					//make it so that only the current node is highlighted, with the travelling
					//edge slightly brighter than the others
					if(testCaseNodes.size() > 1){
						PNode prev = testCaseNodes.get(testCaseNodes.size() - 2);
						ArrayList<PPath> fromSelf = (ArrayList<PPath>) prev.getAttribute("edgesFromSelfTo");
						ArrayList<PNode> destNodes = new ArrayList<PNode>();
						ArrayList<PNode> destWindows = new ArrayList<PNode>();
						destWindows.add((PNode) prev.getAttribute("windowNode")); 
						for (PPath pp : fromSelf) {
							destNodes.add(((ArrayList<PNode>) pp.getAttribute("nodes")).get(1));
							destWindows.add((PNode) ((PNode) ((ArrayList<PNode>) pp.getAttribute("nodes")).get(1)).getAttribute("windowNode"));
						}

						for (int i = 0; i < edgeLayer.getChildrenCount(); i++) {
							PPath currEdge = (PPath) edgeLayer.getChild(i);
							if (fromSelf.contains(currEdge) == false) {
								currEdge.setTransparency(1f);
							} else {
								if (currEdge.getAttribute("edgeType").equals("normal")) {
									currEdge.setStroke(new BasicStroke(1));
								} else {
									float dash[] = { 10.0f };
									currEdge.setStroke(new BasicStroke(3.0f,
											BasicStroke.CAP_BUTT,
											BasicStroke.JOIN_MITER,
											10.0f, dash, 0.0f));
								}
							}
						}

						for (int i = 0; i < eventLayer.getChildrenCount(); i++) {
							PNode otherNode = eventLayer.getChild(i);
							if (otherNode == prev) continue;
							if (destNodes.contains(otherNode) == false) {
								otherNode.setTransparency(1f);
								((PText) otherNode.getAttribute("label")).setTransparency(1f);
							}
						}

						for (int i = 0; i < windowLayer.getChildrenCount(); i++) {
							PNode otherWindowNode = windowLayer.getChild(i);
							if (destWindows.contains(otherWindowNode) == false) {
								otherWindowNode.setTransparency(1f);
							}
						}
					}
					ArrayList<PPath> fromSelf = (ArrayList<PPath>) currEventNode.getAttribute("edgesFromSelfTo");
					ArrayList<PNode> destNodes = new ArrayList<PNode>();
					ArrayList<PNode> destWindows = new ArrayList<PNode>();
					destWindows.add((PNode) currEventNode.getAttribute("windowNode")); 
					for (PPath pp : fromSelf) {
						destNodes.add(((ArrayList<PNode>) pp.getAttribute("nodes")).get(1));
						destWindows.add((PNode) ((PNode) ((ArrayList<PNode>) pp.getAttribute("nodes")).get(1)).getAttribute("windowNode"));
					}

					for (int i = 0; i < edgeLayer.getChildrenCount(); i++) {
						PPath currEdge = (PPath) edgeLayer.getChild(i);
						if (fromSelf.contains(currEdge) == false) {
							currEdge.setTransparency(0.2f);
						} else {
							if (currEdge.getAttribute("edgeType").equals("normal")) {
								currEdge.setStroke(new BasicStroke(3));
							} else {
								float dash[] = { 10.0f };
								currEdge.setStroke(new BasicStroke(5.0f,
										BasicStroke.CAP_BUTT,
										BasicStroke.JOIN_MITER,
										10.0f, dash, 0.0f));
							}
						}
					}

					for (int i = 0; i < eventLayer.getChildrenCount(); i++) {
						PNode otherNode = eventLayer.getChild(i);
						if (otherNode == currEventNode) continue;
						if (destNodes.contains(otherNode) == false) {
							otherNode.setTransparency(0.2f);
							((PText) otherNode.getAttribute("label")).setTransparency(0.2f);
						}
					}

					for (int i = 0; i < windowLayer.getChildrenCount(); i++) {
						PNode otherWindowNode = windowLayer.getChild(i);
						if (destWindows.contains(otherWindowNode) == false) {
							otherWindowNode.setTransparency(0.2f);
						}
					}
				//Test Case editing
				} 
			}

			public void startDrag(PInputEvent e) {
				return;
			}

			public void drag(PInputEvent e) {
				return;
			}

		});

		edgeLayer.addInputEventListener(new PDragEventHandler() {
			{
				PInputEventFilter filter = new PInputEventFilter();
				filter.setOrMask(InputEvent.BUTTON1_MASK | InputEvent.BUTTON3_MASK);
				setEventFilter(filter);
			}

			public void mouseEntered(PInputEvent e) {
				if(clickedNodes.size() == 0 && testCaseNodes.size() == 0){
					super.mouseEntered(e);
					PPath currEdge = (PPath) e.getPickedNode();

					ArrayList<PNode> nodes = (ArrayList<PNode>) currEdge.getAttribute("nodes");
					for (int i = 0; i < edgeLayer.getChildrenCount(); i++) {
						PPath otherEdge = (PPath) edgeLayer.getChild(i);
						if (otherEdge == currEdge) {
							if (otherEdge.getAttribute("edgeType").equals("normal")) {
								otherEdge.setStroke(new BasicStroke(3));
							} else {
								float dash[] = { 10.0f };
								otherEdge.setStroke(new BasicStroke(5.0f,
										BasicStroke.CAP_BUTT,
										BasicStroke.JOIN_MITER,
										10.0f, dash, 0.0f));
							}
							continue;
						}
						otherEdge.setTransparency(0.2f);
					}

					for (int i = 0; i < eventLayer.getChildrenCount(); i++) {
						PNode currEventNode = (PNode) eventLayer.getChild(i);
						if (nodes.contains(currEventNode) == false) {
							currEventNode.setTransparency(0.2f);
							((PText) currEventNode.getAttribute("label")).setTransparency(0.2f);
						}
					}

					ArrayList<PNode> windows = new ArrayList<PNode>();
					for (PNode n : nodes) {
						windows.add((PNode) n.getAttribute("windowNode"));
					}

					for (int i = 0; i < windowLayer.getChildrenCount(); i++) {
						PNode otherWindowNode = windowLayer.getChild(i);
						if (windows.contains(otherWindowNode) == false) {
							otherWindowNode.setTransparency(0.2f);
						}
					}
				}
			}

			public void mouseExited(PInputEvent e) {
				if(clickedNodes.size() == 0 && testCaseNodes.size() == 0){
					super.mouseExited(e);
					PPath currEdge = (PPath) e.getPickedNode();

					ArrayList<PNode> nodes = (ArrayList<PNode>) currEdge.getAttribute("nodes");
					for (int i = 0; i < edgeLayer.getChildrenCount(); i++) {
						PPath otherEdge = (PPath) edgeLayer.getChild(i);
						if (otherEdge == currEdge) {
							if (otherEdge.getAttribute("edgeType").equals("normal")) {
								otherEdge.setStroke(new BasicStroke(1));
							} else {
								float dash[] = { 10.0f };
								otherEdge.setStroke(new BasicStroke(3.0f,
										BasicStroke.CAP_BUTT,
										BasicStroke.JOIN_MITER,
										10.0f, dash, 0.0f));
							}
							continue;
						}
						otherEdge.setTransparency(1f);
					}

					for (int i = 0; i < eventLayer.getChildrenCount(); i++) {
						PNode currEventNode = (PNode) eventLayer.getChild(i);
						if (nodes.contains(currEventNode) == false) {
							currEventNode.setTransparency(1f);
							((PText) currEventNode.getAttribute("label")).setTransparency(1f);
						}
					}

					ArrayList<PNode> windows = new ArrayList<PNode>();
					for (PNode n : nodes) {
						windows.add((PNode) n.getAttribute("windowNode"));
					}

					for (int i = 0; i < windowLayer.getChildrenCount(); i++) {
						PNode otherWindowNode = windowLayer.getChild(i);
						if (windows.contains(otherWindowNode) == false) {
							otherWindowNode.setTransparency(1f);
						}
					}
				}
			}



			public void startDrag(PInputEvent e) {
				return;
			}

			public void drag(PInputEvent e) {
				return;
			}

		});
	}

	/**
	 * Populates the Edge Layer with each Event's Edge.
	 */
	public void populateEdgeLayer() {
		// for loop that populates each nodes' edge lists and creates edges
		for (int i = 0; i < eventLayer.getChildrenCount(); i++) {
			PNode currEventNode = eventLayer.getChild(i);
			Event currEvent = vd.getEventsMap().get(currEventNode.getName());

			for (int j = 0; j < eventLayer.getChildrenCount(); j++) {
				PNode destNode = eventLayer.getChild(j);


				PPath edge = null;
				if (currEvent.getNormalEdgesFromSelfTo().contains(destNode.getName())) {
					edge = new PPath();
					((ArrayList) currEventNode.getAttribute("edgesFromSelfTo")).add(edge);
					((ArrayList) destNode.getAttribute("edgesToSelfFrom")).add(edge);

					edge.setStrokePaint(colors.get(currEvent.getType()));
					edge.addAttribute("edgeType", "normal");
				} else if (currEvent.getReachingEdgesFromSelfTo().contains(destNode.getName())) {
					edge = new PPath();
					((ArrayList) currEventNode.getAttribute("edgesFromSelfTo")).add(edge);
					((ArrayList) destNode.getAttribute("edgesToSelfFrom")).add(edge);

					edge.setStrokePaint(colors.get(currEvent.getType()));

					float dash[] = { 10.0f };
					edge.setStroke(new BasicStroke(3.0f,
							BasicStroke.CAP_BUTT,
							BasicStroke.JOIN_MITER,
							10.0f, dash, 0.0f));

					edge.addAttribute("edgeType", "reaching");
				}

				if (edge != null) {
					edge.addAttribute("nodes", new ArrayList());
					((ArrayList)edge.getAttribute("nodes")).add(currEventNode);
					((ArrayList)edge.getAttribute("nodes")).add(destNode);

					edgeLayer.addChild(edge);

					updateEdge(edge);
				}


			}

		}
	}

	/**
	 * Populates the Event Layer with each Event.
	 * 
	 * This method is also where each Event and Widget get their labelNode.
	 */
	public void populateEventLayer() {
		if(vd.getEventsMap() == null){
			return;
		}
		// for loop that creates an event node for each event
		for (Event currEvent : vd.getEventsMap().values()) {
			Widget currWidget = currEvent.getWidget();

			float x = (float) currWidget.getCoord().getX();
			float y = (float) currWidget.getCoord().getY();

			PPath node = PPath.createEllipse(x, y, 10, 10);
			node.setName(currEvent.getId());
			node.addAttribute("edgesToSelfFrom", new ArrayList());
			node.addAttribute("edgesFromSelfTo", new ArrayList());
			node.addAttribute("type", "event");
			node.addAttribute("window", currWidget.getWindow());
			node.setPaint(colors.get(currEvent.getType()));

			node.addAttribute("event", currEvent);
			node.addAttribute("widget", currWidget);

			currEvent.setSelfNode(node);
			currWidget.setSelfNode(node);

			for (int i = 0; i < windowLayer.getChildrenCount(); i++) {
				PNode currWindow = (PNode) windowLayer.getChild(i);
				if (currWidget.getWindow().getTitle().equals(currWindow.getName())) {
					((ArrayList) currWindow.getAttribute("nodes")).add(node);
					node.addAttribute("windowNode", currWindow);
					node.setOffset(currWindow.getX(), currWindow.getY());
					eventLayer.addChild(node);
				}
			}
		}
	}

	/**
	 * Populates the Label Layer with each Event Label.
	 * 
	 * This method is also where each Event and Widget get their selfNode.
	 */
	public void populateLabelLayer() {
		if(vd.getWindows() == null){
			return;
		}
		for (Window w : vd.getWindows()) {
			PNode windowNode = w.getSelfNode();

			PText currWindowLabelNode = new PText(w.getTitle());
			currWindowLabelNode.setName(windowNode.getName());
			currWindowLabelNode.addAttribute("type", "label");
			currWindowLabelNode.addAttribute("window", w);
			currWindowLabelNode.addAttribute("labelType", "window");
			currWindowLabelNode.addAttribute("windowNode", windowNode);
			currWindowLabelNode.setTextPaint(Color.gray);
			currWindowLabelNode.setX(0);
			currWindowLabelNode.setY(-LABEL_HEIGHT);

			currWindowLabelNode.setOffset(w.getSelfNode().getX(), w.getSelfNode().getY());

			w.getSelfNode().addAttribute("label", currWindowLabelNode);
			w.setLabelNode(currWindowLabelNode);

			labelLayer.addChild(currWindowLabelNode);
		}

		for (Event e : vd.getEventsMap().values()) {
			PNode eventNode = e.getSelfNode();

			PText currEventLabelNode = new PText(e.getWidget().getTitle());
			currEventLabelNode.setName(eventNode.getName());
			currEventLabelNode.addAttribute("type", "label");
			currEventLabelNode.addAttribute("window", e.getWindow());
			currEventLabelNode.addAttribute("labelType", "event");
			currEventLabelNode.addAttribute("eventNode", eventNode);
			currEventLabelNode.setTextPaint(Color.white);
			currEventLabelNode.setX(eventNode.getX());
			currEventLabelNode.setY(eventNode.getY() - LABEL_HEIGHT);

			currEventLabelNode.setOffset(e.getWindow().getSelfNode().getX(), e.getWindow().getSelfNode().getY());

			e.getSelfNode().addAttribute("label", currEventLabelNode);
			e.setLabelNode(currEventLabelNode);

			labelLayer.addChild(currEventLabelNode);
		}
	}

	/**
	 * Populates the Window Layer with each Window.
	 * 
	 * This method is also where each Window gets their selfNode.
	 */
	public void populateWindowLayer() {
		if(vd.getWindows() == null){
			return;
		}
		// for loop that creates the image and label nodes for the windows layer
		for (int i = 0; i < vd.getWindows().size(); i++) {
			Window currWindow = vd.getWindows().get(i);

			float offset = (WINDOW_OFFSET * i) + 10;
			PImage imageNode = new PImage();

			// set image and coordinates
			imageNode.setImage(vd.getScreenshotsDirectory() + currWindow.getTitle() + ".png");
			imageNode.setX(offset);
			imageNode.setY(30);

			// add attributes to nodes
			imageNode.addAttribute("nodes", new ArrayList());
			imageNode.addAttribute("type", "window");
			imageNode.setName(currWindow.getTitle());

			// add window node to the window and windows layer
			currWindow.setSelfNode(imageNode);
			windowLayer.addChild(imageNode);
		}
	}

	/**
	 * Updates the incoming Piccolo Path representation of the given edge
	 * on the PVisualizationCanvas.
	 * 
	 * Used with the WindowLayerListener to keep the Edges up to date while moving 
	 * other the Window Piccolo Nodes.
	 * 
	 * @param edge		Piccolo Path representing the edge to be updated
	 */
	public void updateEdge(PPath edge) {
		// get end nodes
		PNode node1 = (PNode) ((ArrayList)edge.getAttribute("nodes")).get(0);
		PNode node2 = (PNode) ((ArrayList)edge.getAttribute("nodes")).get(1);

		edge.setVisible(node1.getVisible() && node2.getVisible());

		// get end nodes new coordinates
		Point2D start = node1.getFullBoundsReference().getCenter2D();
		Point2D end = node2.getFullBoundsReference().getCenter2D();

		// reset the edge to the proper orientation and length
		edge.reset();
		edge.moveTo((float)start.getX(), (float)start.getY());
		edge.lineTo((float)end.getX(), (float)end.getY());
	}

	/**
	 * Updates the Event Piccolo Nodes of the Events Layer and the Edge
	 * Piccolo Paths of the Edges Layer.
	 * 
	 * Used with the WindowLayerListener to keep the Edges and Events update
	 * while moving the Window Piccolo Nodes around.
	 * 
	 * @param window	Piccolo Node representing the Window to be updated
	 */
	public void updateWindow(PNode window) {

		((PText) window.getAttribute("label")).setOffset(window.getFullBoundsReference().getX(), window.getFullBoundsReference().getY());

		// gets the list of all nodes contained in current window
		// and sets their coordinates to their appropriate spot
		ArrayList containedNodes = (ArrayList) window.getAttribute("nodes");
		for (int i = 0; i < containedNodes.size(); i++) {
			PNode currEventNode = (PNode) containedNodes.get(i);
			currEventNode.setOffset(window.getFullBoundsReference().getX(), window.getFullBoundsReference().getY());
			((PText) currEventNode.getAttribute("label")).setOffset(window.getFullBoundsReference().getX(), window.getFullBoundsReference().getY());
		}

		// once all the nodes are properly situated
		// runs update edge method for each edge
		for (int i = 0; i < containedNodes.size(); i++) {
			ArrayList edges = (ArrayList) ((PNode) containedNodes.get(i)).getAttribute("edgesToSelfFrom");
			for (int j = 0; j < edges.size(); j++) {
				updateEdge((PPath) edges.get(j));
			}
			edges = (ArrayList) ((PNode) containedNodes.get(i)).getAttribute("edgesFromSelfTo");
			for (int j = 0; j < edges.size(); j++) {
				updateEdge((PPath) edges.get(j));
			}
		}

		// and for the testcases
		for (TestCase currTestCase : vd.getTestCases()) {
			for (TestCaseStep currStep : currTestCase.getSteps()) {
				updateEdge(currStep.getSelfEdge());
			}
		}
	}

	/**
	 * Returns the layer requested by name.
	 * 
	 * This method is expecting one of four strings: "edge", "event", 
	 * "label" or "window" for the respective layer. Other inputs will to this
	 * method returns null.
	 * 
	 * @param layerName		name of the layer being requested
	 * @return				layer of the PVisualizationCanvas with the given name, null if layer not found
	 */
	public PLayer getLayer(String layerName) {
		if (layerName.equalsIgnoreCase("edge")) {
			return edgeLayer;
		} else if (layerName.equalsIgnoreCase("event")) {
			return eventLayer;
		} else if (layerName.equalsIgnoreCase("label")) {
			return labelLayer;
		} else if (layerName.equalsIgnoreCase("window")) {
			return windowLayer;
		}

		return null;
	}

	/**
	 * Creates and populates a layer for each TestCase.
	 * 
	 * This is also where each TestCaseStep gets their selfEdge.
	 */
	private void populateTestLayers() {
		if(vd.getTestCases() == null){
			return;
		}
		for (int i = 0; i < vd.getTestCases().size(); i++) {
			TestCase currTestCase = vd.getTestCases().get(i);
			PLayer currTestCaseLayer = new PLayer();

			TestCaseStep prevStep = currTestCase.getSteps().get(0);
			Event currEvent = vd.getEventsMap().get(prevStep.getEventID());

			PPath edge = new PPath();
			edge.setStrokePaint(colors.get(currEvent.getType()));
			edge.addAttribute("edgeType", "normal");
			edge.addAttribute("nodes", new ArrayList());

			((ArrayList)edge.getAttribute("nodes")).add(vd.getEventsMap().get(prevStep.getEventID()).getSelfNode());
			((ArrayList)edge.getAttribute("nodes")).add(vd.getEventsMap().get(prevStep.getEventID()).getSelfNode());

			prevStep.setSelfEdge(edge);
			currTestCaseLayer.addChild(edge);

			updateEdge(edge);

			for (int j = 1; j < currTestCase.getSteps().size(); j++) {
				TestCaseStep currStep = currTestCase.getSteps().get(j);
				currEvent = vd.getEventsMap().get(currStep.getEventID());

				edge = new PPath();

				if (currStep.isReachingStep()) {
					edge.setStrokePaint(colors.get(currEvent.getType()));
					float dash[] = { 10.0f };
					edge.setStroke(new BasicStroke(3.0f,
							BasicStroke.CAP_BUTT,
							BasicStroke.JOIN_MITER,
							10.0f, dash, 0.0f));
					edge.addAttribute("edgeType", "reaching");
				} else {
					edge.setStrokePaint(colors.get(currEvent.getType()));
					edge.addAttribute("edgeType", "normal");
				}

				edge.addAttribute("nodes", new ArrayList());

				((ArrayList)edge.getAttribute("nodes")).add(vd.getEventsMap().get(prevStep.getEventID()).getSelfNode());
				((ArrayList)edge.getAttribute("nodes")).add(vd.getEventsMap().get(currStep.getEventID()).getSelfNode());

				currStep.setSelfEdge(edge);
				currTestCaseLayer.addChild(edge);

				updateEdge(edge);

				prevStep = currStep;
			}

			currTestCase.setSelfLayer(currTestCaseLayer);
			currTestCase.getSelfLayer().setVisible(false);

			getRoot().addChild(currTestCaseLayer);
			getCamera().addLayer(getCamera().getLayerCount(), currTestCaseLayer);
		}
	}
	
	public void displayHomeCanvas() {
		PImage imageNode = new PImage();

		// set image and coordinates
		Image image = null;
		try {
			image = ImageIO.read(getClass().getResource("/resources/logo.jpeg"));
			imageNode.setImage(image);
			imageNode.setX(250);
			imageNode.setY(95);
		} catch (IOException e) {
			System.out.println("Image could not be loaded: " + getClass().getResource("/resources/logo.jpeg"));
		};//new ImageIcon(getClass().getResource("/resources/logo.jpeg").getFile());
		
		

		// add attributes to nodes
		/*imageNode.addAttribute("nodes", new ArrayList());
		imageNode.addAttribute("type", "window");
		imageNode.setName(currWindow.getTitle());*/

		// add window node to the window and windows layer
		//currWindow.setSelfNode(imageNode);
		
		// change background color of home panel here
		PNode background = new PNode();
		background.setPaint(Color.WHITE);
		background.setBounds(0, 0, 2000, 2000);
		
		homeLayer.addChild(background);
		homeLayer.addChild(imageNode);
		
	}
	
	/**
	 * Resets the PVisualizationCanvas for use with the Home Panel.
	 */
	public void resetForHomeTab() {
		
		if(vd.getTestCases() != null){
			for (TestCase tc : vd.getTestCases()) {
				tc.getSelfLayer().setVisible(false);
			}
		}
		
		if(vd.getWidgetsMap() != null){
			for (Widget w : vd.getWidgetsMap().values()) {
				w.setVisible(false);
				w.getLabelNode().setVisible(false);
			}
		}
		
		if(vd.getWindows() !=null){
			for (Window w : vd.getWindows()) {
				w.setVisible(false);
			}
		}

		edgeLayer.setVisible(false);
		
		this.displayHomeCanvas();
		
		homeLayer.setVisible(true);
	}
	
	/**
	 * Resets the PVisualizationCanvas for use with the Capture/Replay Panel.
	 */
	public void resetForCaptureReplayTab() {
		if(vd.getTestCases() != null){
			for (TestCase tc : vd.getTestCases()) {
				tc.getSelfLayer().setVisible(false);
			}
		}
		
		if(vd.getWidgetsMap() != null && vd.getWidgetsMap().values() != null){
			for (Widget w : vd.getWidgetsMap().values()) {
				w.setVisible(false);
				w.getLabelNode().setVisible(false);
			}
		}
		
		if(vd.getWindows() != null ){
			for (Window w : vd.getWindows()) {
				w.setVisible(false);
			}
		}

		edgeLayer.setVisible(false);
		homeLayer.setVisible(false);
	}

	/**
	 * Resets the PVisualizationCanvas for use with the VisualizationTree.
	 */
	public void resetForTreeTab() {
		
		homeLayer.setVisible(false);
		
		for (Widget w : vd.getWidgetsMap().values()) {
			w.setVisible(true);
		}

		for (Window w : vd.getWindows()) {
			w.setVisible(true);
		}

		edgeLayer.setVisible(true);
		eventLayer.setVisible(true);
		labelLayer.setVisible(true);
		windowLayer.setVisible(true);

		for (TestCase currTestCase : vd.getTestCases()) {
			currTestCase.getSelfLayer().setVisible(false);
		}
	}

	/**
	 * Resets the PVisualizationCanvas for use with the TestCaseTree.
	 */
	public void resetForTestsTab() {
		for (TestCase tc : vd.getTestCases()) {
			tc.getSelfLayer().setVisible(false);
		}
		
		for (Widget w : vd.getWidgetsMap().values()) {
			w.setVisible(false);
			w.getLabelNode().setVisible(false);
		}

		for (Window w : vd.getWindows()) {
			w.setVisible(false);
		}

		edgeLayer.setVisible(false);
		
		homeLayer.setVisible(false);
	}
	
	
	public void showScreenShot(String fileName){
		homeLayer.removeAllChildren();
	
		PImage imageNode = new PImage();
		try{
			if(fileName.compareTo("null") != 0 ){
				imageNode.setImage(fileName);	
			}else{
				Image image = ImageIO.read(getClass().getResource("/resources/noScreenShot.jpg"));
				imageNode.setImage(image);
			}
		}
		catch(Exception e){
			System.out.println("Image could not be loaded: " + getClass().getResource("/resources/logo.jpeg"));
		}
		finally{
		imageNode.setX(250);
		imageNode.setY(95);
		homeLayer.addChild(imageNode);
		}
	}
}