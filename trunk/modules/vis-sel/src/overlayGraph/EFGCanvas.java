package overlayGraph;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import overlayGraph.util.*;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


public class EFGCanvas extends PCanvas {
	private static final long serialVersionUID = 1L;

	final private String initialURLofRip;
	final private String nodeClickedURL;
	final private String ripName;
	final private SiteInfo rippedSiteInfo;

	private PLayer backgroundLayer= this.getLayer();
	private PLayer efgEdgeLayer = new PLayer();
	private PLayer guiNodeLayer = new PLayer();
	private PLayer currTCedgeLayer = new PLayer();
	private PLayer currTCnodeLayer = new PLayer();

	//Keep track of all nodes and edges so we can efficiently hide and remove them w/o recomputing
	private List<PPath> efgEdges= new LinkedList<PPath>(); 	
	private Map<String, PNode> guiNodes= new HashMap<String, PNode>();	// "WidgetId" -> (PNode on canvas)
	private List<PPath> currTCedges= new LinkedList<PPath>(); 
	private List<PNode> currTCnodes= new LinkedList<PNode>();

	final static boolean EFG_EDGE= true;
	final static boolean TC_EDGE= false;

	/**
	 * PCanvas with a screenCap as background,
	 *  onto which Node and Edge layers are overlaid.
	 * 
	 * @param ripName should be relative path to /screencaps/, relative to workspace
	 */
	public EFGCanvas(final String initalURLofRip, final String rippedUrl, final String ripName, final SiteInfo rippedSiteInfo) {
		this.initialURLofRip= initalURLofRip;
		this.nodeClickedURL= rippedUrl;
		this.ripName= ripName;
		this.rippedSiteInfo= rippedSiteInfo;

		this.setBackgroundScreenCap();

		this.setLayers();
		// Remove default panning event handler
		//this.removeInputEventListener(this.getPanEventHandler());

		this.createAndAddGUInodes();
		this.createAndAddEFGedges();
	}

	private void setBackgroundScreenCap() {
		String screenCapFilePath= ripName + "/screencaps/" + parseURLscreenCapRelPath(nodeClickedURL);
		File screenCapFile= new File(screenCapFilePath);
		Image screenCapImage= null;	

		try {
			screenCapImage= ImageIO.read(screenCapFile);
		} catch (IOException e) {
			System.err.println("Missing screenshot: " + screenCapFilePath);
			try {
				screenCapImage= ImageIO.read(captureImageOnTheFly(nodeClickedURL));
			} catch (IOException e1) {
				System.err.println("Could not capture-on-the-fly: " + screenCapFilePath);	
				e1.printStackTrace();
			}

		}

		this.backgroundLayer.setPickable(false);
		// set background as screenCapImage
		this.backgroundLayer.addChild(new PImage(screenCapImage));

		this.setPreferredSize(new Dimension(screenCapImage.getWidth(this),
				screenCapImage.getHeight(this)));
	}

	private File captureImageOnTheFly(String url) throws IOException{
		WebDriver webDriver= new FirefoxDriver();
		webDriver.get(url);
		File screenshot= ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
		String screenCapFile= parseURLscreenCapRelPath(url);
		File image = null;

		try {
			//Copy the file to screenshot folder 
			FileUtils.copyFile(screenshot, image = new File(ripName + "/screencaps/" + screenCapFile+".png")); 
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
		webDriver.close();
		return image;
	}	

	/**
	 * Returns path subString of screen-capped url, cleaned to be a relative fileSystem path.
	 * ex: Given "http://128.8.126.15/wordpress/" or "http://128.8.126.15/wordpress.html"
	 * returns "http:/128.8.126.15/wordpress.png"
	 */
	private String parseURLscreenCapRelPath(String url) {
		String imagePath;
		URL u = null;
		try {
			u= new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		imagePath = u.getProtocol() + ":" + File.separator + u.getHost() + u.getPath();

		if (u.getFile().endsWith("/")) {  // url ends with "/", so cut it off
			imagePath= imagePath.substring(0, imagePath.length()-1);
		} else if(url.lastIndexOf("/") < url.lastIndexOf(".")){ // url ends with ".html", ".php", etc., so cut if off
			imagePath= imagePath.substring(0, imagePath.lastIndexOf('.'));
		}

		return imagePath + ".png";
	}

	/*
	 * backgroundLayer does not need setting.
	 */
	public void setLayers(){

		// Add NodeLayers after EdgeLayers, so that Nodes appear ON TOP if Edges
		getRoot().addChild(efgEdgeLayer);
		getCamera().addLayer(efgEdgeLayer);
		getRoot().addChild(guiNodeLayer);
		getCamera().addLayer(guiNodeLayer);
		getRoot().addChild(currTCedgeLayer);
		getCamera().addLayer(currTCedgeLayer);
		getRoot().addChild(currTCnodeLayer);
		getCamera().addLayer(currTCnodeLayer);
	}

	private void createAndAddGUInodes() {
		for (Component currComponent : rippedSiteInfo.getPageComponents(nodeClickedURL).values()) {
			PNode node= PPath.createEllipse(currComponent.x,  currComponent.y, 11, 11);

			node.addAttribute("component", currComponent);
			node.setPaint(Color.RED);

			guiNodes.put(currComponent.getWidgetID(), node);
			guiNodeLayer.addChild(node);
		}
	}

	private void addVisualizationEdge(PNode srcNode, PNode destNode, boolean isEFGedge) {

		if(isEFGedge){
			EFGedge newEFGedge = new EFGedge(srcNode, destNode);
			efgEdgeLayer.addChild(newEFGedge);
			efgEdges.add(newEFGedge);
		} else {	//isTCedge
			TCedge newTCedge = new TCedge(srcNode, destNode);
			currTCedgeLayer.addChild(newTCedge);
			currTCedges.add(newTCedge);
		}
	}

	/*
	 * Also updates appropriate Nodes to be linkable
	 */
	private void createAndAddEFGedges() {
		// Create EFGedges
		for (final PNode currNode : guiNodes.values()) {
			Component currComp= (Component) currNode.getAttribute("component");

			final HashSet<String> offPageNeighborURLs = new HashSet<String>();
			boolean shouldLink = false;	

			for(final Component neighbor : currComp.getNeighbors()) {
				PNode visibleNeighborNode = guiNodes.get(neighbor.getWidgetID());
				if (visibleNeighborNode != null) {  // neighbor is on this page, draw an edge to it
					this.addVisualizationEdge(currNode, visibleNeighborNode, EFG_EDGE);
				} else { // neighbor is on different page, it should be linked to (but no edge drawn)     
					shouldLink = true;
					//can't call makeNodeLinkable here as that would open a Window for each currNeighborComponent!
					offPageNeighborURLs.add(neighbor.getContainingPageURL());
				}
			}

			if(shouldLink){
				makeNodeLinkable(currNode, offPageNeighborURLs);
			}
		}
	}

	private void makeNodeLinkable(PNode node, final Set<String> offPageNeighborURLs){

		node.setPaint(Color.ORANGE);   
		node.addInputEventListener(new PBasicInputEventHandler() {

			@Override
			public void mouseEntered(PInputEvent event) {
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(PInputEvent event) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));                            
			}

			@Override
			public void mouseClicked(PInputEvent event) {
				if (event.getButton() == MouseEvent.BUTTON1){
					for (String currOffSiteURL : offPageNeighborURLs) {
						(new Simulator(initialURLofRip, currOffSiteURL, ripName, rippedSiteInfo)).setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					}
				}
			}
		});
	}

	public void showGUInodes() {

		guiNodeLayer.addChildren(guiNodes.values());
	}
	public void hideGUInodes() {

		guiNodeLayer.removeAllChildren();

	}

	public void showEFGedges() {

		efgEdgeLayer.addChildren(efgEdges);
	}
	public void hideEFGedges() {

		efgEdgeLayer.removeAllChildren();
	}

	/**
	 * 
	 * @param testId
	 * @param eventIds
	 */
	public void showCurrTC(final String testId, List<String> eventIds) {
		List<PNode> testCasePath= new ArrayList<PNode>();
		final List<String> remainingTCs= new ArrayList<String>(eventIds);
		PNode lastNode= null;

		// Create/add TCnodes
		for(String eventId : eventIds){
			String widgetId = eventId.replace("e", "w");		
			PNode currEventNode = guiNodes.get(widgetId);
			PNode currEventTCnode = null;

			if(currEventNode!=null){
				Component currEventTCcomponent= (Component) currEventNode.getAttribute("component");
				
				currEventTCnode= PPath.createEllipse(currEventTCcomponent.x,  currEventTCcomponent.y, 11, 11);		
				currEventTCnode.addAttribute("component", currEventTCcomponent);
			}
			//= (PNode) guiNodes.get(widgetId).clone();  //is a particular GUInode


			if (currEventTCnode == null) { // TC going to different page, since otherwise currEventTCnode would be set with an on-page neighbor
				final Component neighbor = rippedSiteInfo.getComponentFromId(widgetId);
				lastNode.setPaint(Color.GREEN);

				lastNode.addInputEventListener(new PBasicInputEventHandler() {
					public void mouseEntered(PInputEvent event) {
						setCursor(new Cursor(Cursor.HAND_CURSOR));
					}
					public void mouseExited(PInputEvent event) {
						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));                            
					}
					public void mouseClicked(PInputEvent event) {
						if (event.getButton() == MouseEvent.BUTTON1){
							 // make new Simulator displaying current testcase
		                    (new Simulator(initialURLofRip, neighbor.getContainingPageURL(), ripName, rippedSiteInfo, 
		                            testId, remainingTCs)).setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
						}
					}
				});
				break;
			} else {
				currEventTCnode.setPaint(Color.RED);
			}

			currTCnodeLayer.addChild(currEventTCnode);
			currTCnodes.add(currEventTCnode);

			testCasePath.add(currEventTCnode);

			remainingTCs.remove(0);
			lastNode = currEventTCnode;
		}

		// Add/draw TCedges
		for(int i=0;i<testCasePath.size()-1;i++){
			PNode srcNode= testCasePath.get(i);
			PNode destNode = testCasePath.get(i+1);

			addVisualizationEdge(srcNode, destNode, TC_EDGE);
		}
	}

	public void hideCurrTC(){

		currTCnodeLayer.removeAllChildren();
		currTCedgeLayer.removeAllChildren();
	}
}