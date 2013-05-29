package edu.umd.cs.guitar.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import edu.umd.cs.guitar.eventhandlers.EdgeTestEventHandler;
import edu.umd.cs.guitar.eventhandlers.KeyEventHandler;
import edu.umd.cs.guitar.eventhandlers.PMouseWheelZoomEventHandler;
import edu.umd.cs.guitar.eventhandlers.WindowNodeEventHandler;
import edu.umd.cs.guitar.gen.EventType;
import edu.umd.cs.guitar.gen.TestCase;
import edu.umd.cs.guitar.graphbuilder.EFGBuilder;
import edu.umd.cs.guitar.graphbuilder.TestCaseBuilder;
import edu.umd.cs.guitar.helper.FileImporter;
import edu.umd.cs.guitar.helper.PrefixTree;
import edu.umd.cs.guitar.helper.TestCaseNode;
import edu.umd.cs.guitar.parser.XMLParser;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * <b>GraphBuilder</b> is the main application class that controls what is displayed in the application. It is capable of switching between EFG viewer and test case viewer using simple key input.
 * Essentially this is the verification viewer program.
 * 
 * @author Sigmund Gorski
 * @version 1.0
 */
public class GraphBuilder extends JFrame implements ActionListener{
	
	/** The default size of the frame.*/
	private static final Dimension DEFAULT_FRAME_DIMENSION = new Dimension(400, 400);

	/** The default position of the frame.*/
    private static final Point DEFAULT_FRAME_POSITION = new Point(100, 100);

    /** Used to allow version binary streams for serializations. */
    private static final long serialVersionUID = 1L;

    /** The graphics device onto which the PFrame is being displayed. */
    private final GraphicsDevice graphicsDevice;

    /** Listener that listens for escape key. */
    private transient EventListener escapeFullScreenModeListener;
	
    /** An list of WindowNode representing the nodes displayed in the EFG Viewer.*/
	private ArrayList<WindowNode> list = new ArrayList<WindowNode>();
	
	/** The file menu bar.*/
	private JMenuBar menuBar;
	/** The two menus for the file menu bar.*/
	private JMenu menuFile, menuView;
	/** The various menu items for the two menus.*/
	private JMenuItem close, resetZoomAndPan, panUp, panDown, panRight, panLeft, zoomIn, zoomOut, switchCan, open;
	/** GUI list of test case files for the test case view.*/
	private JList fileSelect;
	/** The list for JList*/
	private DefaultListModel listModel;
	/** Scroll Pane for the fileSelect*/
	private JScrollPane scroll;
	/** Panels used as panes for the side bar in test case view.*/
	private JPanel contentPane, sidePane;
	/** Button used to update the prefix tree displayed based on the test cases selected.*/
	private JButton updateTest;
	/** Button used to enable the deletion of test case files*/
	private JButton deleteTest;
	
	/** A reference to all the files imported used for EFG, GUI, TestCase, and screen shot building and reading.*/
	private FileImporter input;
	/** A string representing if the viewer is in EFG or TestCase mode.*/
	private String curOp;
	/** A structure representing all the data imported from the EFG and GUI files.*/
	private EFGBuilder efg;
	/** A structure holding all the information read in from all the test case files.*/
	private TestCaseBuilder testCase;
	/** Canvas being displayed on this PFrame, either EFG or Test Case canvas. */
	private PCanvas efgCan, testCan;
	/** A XMLParser used for parsing the xml files produced from the ripper.*/
	private XMLParser xml;
	/** The various layers for the two canvases.*/
	private PLayer layerEFG, layerEdgeEFG, layerTestCase, layerEdgeTestCase;
	/** A height and width value representing the desired size of the frame.*/
	private int height, width;
	/** A handler for the keyboard input.*/
	private KeyEventHandler keyHandler;
	/** A mapping of file names to parsed xml data structure for the test case files.*/
	private HashMap<String, TestCase> testCaseMap;
	/** The border surrounding test case prefix tree.*/
	@SuppressWarnings("unused")
	private PBounds borderTest;
	
	/** A minimum border size used for the default startup view and reset when the test case canvas is active.*/
	private PBounds minBorderTest;
	/** The number of rows in the prefix tree.*/
	private double ptreeSize;
	/** The number of nodes in on the root level.*/
	private double ptreeLength;
	/** The max width of the WindowTestNodes being displayed in the test case viewer.*/
	private double ptreeWidth;
	/** The max height of the WindowTestNodes being displayed in the test case viewer.*/
	private double ptreeHeight;
	/** The test case file names of the currently displayed ones.*/
	private ArrayList<String> curTestDisp;
	/** Used to determine if the efg was started first or if the test case was started first.*/
	private boolean efgNotFirst;
	/** Used to determine if in delete mode for the test case viewer*/
	private static boolean deleteMode;
	
	/**
	 * Creates the default PFrame for the GraphBuilder and constructs the all the information needed to start the viewer
	 * 
	 * @param workDir the working directory
	 * @param op either "efg" or "test"
	 */
	public GraphBuilder(String workDir, String op){
		this("EFG and Test Case Viewer", false, workDir, op);
	}
	
	/**
     * Creates a PFrame with the given title and either in full screen mode or not.
     * 
     * @param title title to display at the top of the frame
     * @param fullScreenMode whether to display a full screen frame or not
     * @param workDir the working directory
	 * @param op either "efg" or "test"
     */
    public GraphBuilder(final String title, final boolean fullScreenMode, String workDir, String op) {
        this(title, GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(), fullScreenMode, workDir, op);
    }
    
    /**
     * Creates a PFrame with the given title and with the default canvas being
     * displayed on the provided device.
     * 
     * @param title title to display at the top of the frame
     * @param device device onto which PFrame is to be displayed
     * @param fullScreen whether to display a full screen frame or not
     * @param workDir the working directory
	 * @param op either "efg" or "test"
     */
    public GraphBuilder(final String title, final GraphicsDevice device, final boolean fullScreen, String workDir, String op) {
        super(title, device.getDefaultConfiguration());

        graphicsDevice = device;

        setBackground(null);
        setBounds(getDefaultFrameBounds());

        try {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        catch (final SecurityException e) {
            // expected from Applets
            System.out.println("Ignoring security exception. Assuming Applet Context.");
        }

        
        efgCan = new PCanvas();
        testCan = new PCanvas();
        
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);
        setFullScreenMode(fullScreen);
        
        try {
			input = new FileImporter(workDir);
		} catch (InputException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
        
        curOp = op;
        
        deleteMode = false;
        curTestDisp = new ArrayList<String>();

        // Manipulation of Piccolo's scene graph should be done from Swings
        // event dispatch thread since Piccolo2D is not thread safe. This code
        // calls initialize() from that thread once the PFrame is initialized,
        // so you are safe to start working with Piccolo2D in the initialize()
        // method.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GraphBuilder.this.initialize();
                repaint();
                if(curOp.equals("efg")){
                	 resetEFGView();
                	 efgNotFirst = false;
        		}else{
        			sidePane.setVisible(true);
        			efgNotFirst = true;
        		}
                getCanvas().requestFocusInWindow();
            }
        });
    }
    
    /**
     * This is used to setup the frame with all the common java swing GUI stuff. It has nothing to do with the Piccolo2D setup.
     */
    private void setupFrame(){
    	 menuBar = new JMenuBar();
         menuFile = new JMenu("File");
         menuView = new JMenu("View");
         switchCan = new JMenuItem("Switch to EFG or Test Case Viewer (Shift+V)");
         open = new JMenuItem("Open New Program (Closes Current) (Ctrl+O)");
         close = new JMenuItem("Exit (Ctrl+C)");
         resetZoomAndPan = new JMenuItem("Reset Zoom and Pan (Shift+R)");
         panUp = new JMenuItem("Pan Up (Up)");
         panDown = new JMenuItem("Pan Down (Down)");
         panLeft = new JMenuItem("Pan Left (Left)");
         panRight = new JMenuItem("Pan Right (Right)");
         zoomIn = new JMenuItem("Zoom In (Shift+Up)");
         zoomOut = new JMenuItem("Zoom Out (Shift+Out)");
         
         menuBar.add(menuFile);
         menuBar.add(menuView);
         
         resetZoomAndPan.addActionListener(this);
         menuView.add(resetZoomAndPan);
         panUp.addActionListener(this);
         menuView.add(panUp);
         panDown.addActionListener(this);
         menuView.add(panDown);
         panLeft.addActionListener(this);
         menuView.add(panLeft);
         panRight.addActionListener(this);
         menuView.add(panRight);
         zoomIn.addActionListener(this);
         menuView.add(zoomIn);
         zoomOut.addActionListener(this);
         menuView.add(zoomOut);
         
         switchCan.addActionListener(this);
         menuFile.add(switchCan);
         open.addActionListener(this);
         menuFile.add(open);
         close.addActionListener(this);
         menuFile.add(close);
         
         Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
         
         height = 0;
         width = 0;
         if(dim.height <= 650){
				height = 600;
         }else{
			height = dim.height - (dim.height % 100) - 100;
         }
         if(dim.width <= 800){
        	 width = 700;
         }else{
        	 width = dim.width - (dim.width % 100) - 100;
         }
         
         this.setBounds(0, 0, width, height);
         
         contentPane.add("North", menuBar);
    }
    
    /**
     * This sets up the EFG builder by creating an EFGBuilder object and then processing the information to 
     * generate the various WindowNode. It also adds edges to the efg viewer and add the listeners to the windows.
     */
    private void setupEFG(){
    	xml = input.getNewXMLParser();
    	if(xml != null){
    		try{
		    	efg = new EFGBuilder(xml, input);
		    	
		    	layerEFG = efgCan.getLayer();
				layerEdgeEFG = new PLayer();
				
				efgCan.getRoot().addChild(layerEdgeEFG);
		        efgCan.getCamera().addLayer(1, layerEdgeEFG);
		        
		        HashMap<String, BufferedImage> images = efg.getAllImagesByView();
		        
		        ArrayList<String> sortedW = new ArrayList<String>(images.keySet());
		        Collections.sort(sortedW);
		        for(String s : sortedW){
		        	list.add(new WindowNode(s, efg, images.get(s), layerEdgeEFG));
		        }
		        windowPosition();
		        
				PBounds bound = list.get(0).getGlobalFullBounds();
				bound = new PBounds(bound.getX()-40, bound.getY()-40, bound.getWidth()+80, bound.getHeight()+80);
				efgCan.removeInputEventListener(efgCan.getZoomEventHandler());
		        PMouseWheelZoomEventHandler mouseWheelZoomEventHandler = new PMouseWheelZoomEventHandler();
		        efgCan.addInputEventListener(mouseWheelZoomEventHandler);
		        keyHandler = new KeyEventHandler(this);
		        efgCan.getRoot().getDefaultInputManager().setKeyboardFocus(keyHandler);
	    	}catch(InputException e){
	    		JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
	    	}
    	}else{
    		JOptionPane.showMessageDialog(null, "Error: Could not generate XMLParser!\nThe program will now exit.", "Error", JOptionPane.ERROR_MESSAGE);
    		System.exit(1);
    	}
    }
    
    /**
     * This is used to reset the view of the efg viewer to what is displayed when the program first starts up.
     */
    public void resetEFGView(){
    	try{
	    	PBounds bound = list.get(0).getGlobalFullBounds();
			bound = new PBounds(bound.getX()-40, bound.getY()-40, bound.getWidth()+80, bound.getHeight()+80);
			efgCan.getCamera().setViewScale(1.0d);
			efgCan.getCamera().setViewTransform(new AffineTransform());
			efgCan.getCamera().animateViewToCenterBounds(bound, true, 1000);
    	}catch(Exception e){}
    }
    
    /**
     * This is used to reset the view of the test case viewer to what is displayed when a new prefix tree is requested by the user.
     */
    public void resetTestView(){
    	try{
    		this.setupBorder(ptreeSize, ptreeHeight, ptreeWidth, ptreeLength, true);
			PBounds bound = new PBounds(minBorderTest.getX()-20, minBorderTest.getY()-20, minBorderTest.getWidth()+40, minBorderTest.getHeight()+40);
			testCan.getCamera().setViewScale(1.0d);
			testCan.getCamera().setViewTransform(new AffineTransform());
			testCan.getCamera().animateViewToCenterBounds(bound, true, 1000);
    	}catch(Exception e){}
    }
    /**
     * This is used to setup the test case viewer by importing all the test case files and setting up the side pane. It then waits for user input.
     */
    private void setupTestCase(){
    	testCase = new TestCaseBuilder(xml, efg);
    	if(testCase != null){
	    	testCaseMap = testCase.getTestCaseFiles();
	    	layerTestCase = testCan.getLayer();
	    	layerEdgeTestCase = new PLayer();
	    	
	    	testCan.getRoot().addChild(layerEdgeTestCase);
	        testCan.getCamera().addLayer(1, layerEdgeTestCase);
	        
			testCan.removeInputEventListener(efgCan.getZoomEventHandler());
		    PMouseWheelZoomEventHandler mouseWheelZoomEventHandler = new PMouseWheelZoomEventHandler();
		    testCan.addInputEventListener(mouseWheelZoomEventHandler);
		    testCan.getRoot().getDefaultInputManager().setKeyboardFocus(keyHandler);
			
		    listModel = new DefaultListModel();
		    Object[] m = testCaseMap.keySet().toArray();
		    String[] names = Arrays.copyOf(m, m.length, String[].class);
		    Arrays.sort(names, String.CASE_INSENSITIVE_ORDER);
		    for(int i = 0; i < names.length; i++){
		    	listModel.addElement(names[i]);
		    }
	        fileSelect = new JList(listModel);
	        fileSelect.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	        scroll = new JScrollPane(fileSelect, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        scroll.setPreferredSize(new Dimension(180, height));
	        updateTest = new JButton("Update Graph");
	        updateTest.addActionListener(this);
	        deleteTest = new JButton("Delete Test Cases");
	        deleteTest.addActionListener(this);
	        sidePane = new JPanel();
	        sidePane.setLayout(new BoxLayout(sidePane, BoxLayout.Y_AXIS));
	        sidePane.setAlignmentY(Component.CENTER_ALIGNMENT);
	        sidePane.add(scroll);
	        sidePane.add(new Box.Filler(new Dimension(), new Dimension(180,20), new Dimension()));
	        JPanel temp = new JPanel();
	        temp.add(new Box.Filler(new Dimension(), new Dimension(1,1), new Dimension()));
	        temp.add(updateTest);
	        JPanel temp2 = new JPanel();
	        temp2.add(new Box.Filler(new Dimension(), new Dimension(1,1), new Dimension()));
	        temp2.add(deleteTest);
	        sidePane.add(new JPanel().add(temp));
	        sidePane.add(new Box.Filler(new Dimension(), new Dimension(180,13), new Dimension()));
	        sidePane.add(new JPanel().add(temp2));
	        sidePane.add(new Box.Filler(new Dimension(), new Dimension(180,20), new Dimension()));
	        sidePane.setPreferredSize(new Dimension(200, height));
	        sidePane.setVisible(false);
	        contentPane.add(sidePane, BorderLayout.EAST);
    	}else{
    		JOptionPane.showMessageDialog(null, "Error: Reading and Processing Test Case Files!\nThe program will now exit.", "Error", JOptionPane.ERROR_MESSAGE);
    		System.exit(1);
    	}
    }
	
    /**
     * This is the initialize method which sets up everything on startup so the user can quickly switch between the two viewers.
     */
	public void initialize() {
        setupFrame();
        setupEFG();
        setupTestCase();
		
        contentPane.add("Center", getCanvas());
        validate();
	}
	
	/**
	 * This method controls the switching between the EFG viewer and the test case viewer. It simply disables certain displayed components and enables others.
	 */
	public void switchCanvas(){
		if(curOp.equals("efg")){
			contentPane.remove(efgCan);
			contentPane.add("Center", testCan);
			sidePane.setVisible(true);
			curOp = "test";
			testCan.requestFocus();
			repaint();
			validate();
			setFullScreenMode(isFullScreenMode());
		}else{
			contentPane.remove(testCan);
			contentPane.add("Center", efgCan);
			sidePane.setVisible(false);
			curOp = "efg";
			efgCan.requestFocus();
			repaint();
			validate();
			setFullScreenMode(isFullScreenMode());
			if(efgNotFirst){
				resetEFGView();
				efgNotFirst = false;
			}
		}
	}
	
	/**
	 * This method is used to determine the window positions for the EFG viewer and then place them on the canvas.
	 * It then updates the internal edge positions of the various windows, adds the external edges to other windows,
	 * and adds the window listeners.
	 */
	private void windowPosition(){
		double startPosX = 0;
		double startPosY = 0;
		double offsetX = 50;
		double offsetY = 50;
		//pos[0] = right, pos[1] = left, pos[2] = up, pos[3] = down, pos[4] = upper left, pos[5] = upper right, pos[6] = lower left, pos[7] = lower right
		double pos[][] = new double[][] {new double[] {startPosX, startPosY},new double[] {startPosX, startPosY},
				new double[] {startPosX, startPosY},new double[] {startPosX, startPosY},new double[] {startPosX, startPosY},
				new double[] {startPosX, startPosY},new double[] {startPosX, startPosY},new double[] {startPosX, startPosY}};
		int counter = -1;
		for(WindowNode window : list){
			layerEFG.addChild(window);
			PBounds b = window.getBounds();
			if(counter == -1){
				pos[0][0] += b.getWidth() + offsetX;
				pos[1][0] -= offsetX + b.getWidth();
				pos[2][1] -= offsetY + b.getHeight();
				pos[3][1] += b.getHeight() + offsetY;
				pos[4][0] -= offsetX + b.getWidth();
				pos[4][1] -= offsetY + b.getHeight();
				pos[5][0] += b.getWidth() + offsetX;
				pos[5][1] -= offsetY + b.getHeight();
				pos[6][0] -= offsetX + b.getWidth();
				pos[6][1] += b.getHeight() + offsetY;
				pos[7][0] += b.getWidth() + offsetX;
				pos[7][1] += b.getHeight() + offsetY;
			}else{
				int cur = counter % 8;
				window.translate(pos[cur][0], pos[cur][1]);
				if(cur == 0){//doing left
					pos[0][0] += b.getWidth() + offsetX;
				}else if(cur == 1){
					pos[1][0] -= offsetX + b.getWidth();
				}else if(cur == 2){
					pos[2][1] -= offsetY + b.getHeight();
				}else if(cur == 3){
					pos[3][1] += offsetY + b.getHeight();
				}else if(cur == 4){
					pos[4][0] -= offsetX + b.getWidth();
					pos[4][1] -= offsetY + b.getHeight();
				}else if(cur == 5){
					pos[5][0] += b.getWidth() + offsetX;
					pos[5][1] -= offsetY + b.getHeight();
				}else if(cur == 6){
					pos[6][0] -= offsetX + b.getWidth();
					pos[6][1] += b.getHeight() + offsetY;
				}else{
					pos[7][0] += b.getWidth() + offsetX;
					pos[7][1] += b.getHeight() + offsetY;
				}
			}
			counter++;
		}
		
		Rectangle canvasBounds = efgCan.getBounds();
		Point2D canvasCenter = new Point2D.Double(canvasBounds.getCenterX(), canvasBounds.getCenterY());
		PBounds b = list.get(0).getBounds();
		layerEFG.translate(canvasCenter.getX() - (b.getWidth()/2), canvasCenter.getY() - (b.getHeight()/2));
		
		for(WindowNode window : list){
			window.updateEdges();
			addExternalEdges(window);
			window.addInputEventListener(new WindowNodeEventHandler(efgCan));
		}
	}
	
	/**
	 * This method is used to add external edges to the window passed in. It grabs the external edges to add from the list stored in 
	 * the window node, a result from a previous action of adding edges to the window node. 
	 * 
	 * @param window the node to add external edges to 
	 */
	@SuppressWarnings("unchecked")
	private void addExternalEdges(WindowNode window){
		ArrayList<EventType[]> ext = window.getExternalEdges();
		for(EventType[] e : ext){
			EventType cur = e[0];
			WindowNode windowExt = getWindowNode(efg.getViewByEvent(e[1].getEventId()));
			EventNode node = window.getEventNode(cur.getEventId());
			EdgeNode edge = new EdgeNode(window, efgCan);
			
			((ArrayList<PPath>)node.getAttribute("extedges")).add(edge);
			((ArrayList<PPath>)windowExt.getAttribute("extedges")).add(edge);
			edge.addAttribute("nodes", new ExtEdgeRef(windowExt, node));
			
			Point2D start = node.getGlobalFullBounds().getCenter2D();
			Point2D end = windowExt.getGlobalFullBounds().getCenter2D();
			
			edge.reset();
	        edge.moveTo((float)start.getX(), (float)start.getY());
	        edge.lineTo((float)end.getX(), (float)end.getY());
	        this.layerEdgeEFG.addChild(edge);
	        edge.setStroke(new BasicStroke(4f));
	        edge.setStrokePaint(Color.CYAN);
	        edge.setVisible(false);
		}
	}
	
	/**
	 * Returns a WindowNode with the associated window ID.
	 * 
	 * @param windowID the window id
	 * @return the WindowNode
	 */
	private WindowNode getWindowNode(String windowID){
		for(WindowNode w : list){
			if(w.getWindowID().equals(windowID)){
				return w;
			}
		}
		return null;
	}
	
	/**
	 * This sets up the border information used when starting up or reseting the view of the test case viewer. 
	 * 
	 * @param size the number of levels of the prefix tree
	 * @param height the max height of the nodes being added to the view
	 * @param width the max width of the nodes being added to the view
	 * @param length the number of nodes in the root level
	 * @param reset weither or not reset called this method
	 */
	private void setupBorder(double size, double height, double width, double length, boolean reset){
		double totalHeight = size*height + (size - 1)*50;
		double totalWidth = length*width + (length - 1)*50;
		if(!reset){
			borderTest = new PBounds(0,0,totalWidth,totalHeight);
		}
		
		Dimension n = testCan.getSize();
		int holdsNode = (int)n.getWidth()/100 - 1;
		double wid = 0;
		double x = 0;
		if((int)length <= holdsNode){
			wid = totalWidth;
		}else{
			double middle;
			double temp;
			if(holdsNode <= 0){
				middle = width/2;
				temp = width;
			}else{
				middle = ((int)length/2)*width + ((int)length/2)*50 + width/2;
				temp = holdsNode*width + (holdsNode - 1)*50;
				
			}
			x = middle - temp/2;
			wid = temp;
		}
		int holdsHNode = (int)n.getHeight()/100;
		double hei = 0;
		if((int)size <= holdsHNode){
			hei = totalHeight;
		}else{
			if(holdsHNode <= 0){
				hei = height;
			}else{
				hei = holdsHNode*height + (holdsHNode - 1)*50;
			}
		}
		this.minBorderTest = new PBounds(x,0,wid,hei);
	}
	
	/**
	 * This is used to create the display for a new prefix tree based off of the test cases a user selects.
	 * 
	 * @param tree the prefix tree
	 */
	@SuppressWarnings("unchecked")
	public void handleNewTree(PrefixTree tree){
		double size = tree.size();
		double height = tree.getHeight();
		double width = tree.getWidth();
		double heightOffset = (size - 1)*height + (size - 1)*50;
		double widthOffset = 0;
		
		ArrayList<ArrayList<TestCaseNode>> rgroupList = tree.getRear();
		ArrayList<WindowTestNode> nodes = new ArrayList<WindowTestNode>();
		ArrayList<WindowTestNode> prevnodes;
		int length = 0;
		for(ArrayList<TestCaseNode> group: rgroupList){
			length += group.size();
			for(TestCaseNode node : group){
				if(node.getEventID() != null){
					WindowTestNode n = new WindowTestNode(efg, node.getEventID(), node.getImg(), node.getParent(), testCan, this);
					layerTestCase.addChild(n);
					n.translate(widthOffset, heightOffset);
					nodes.add(n);
				}
				widthOffset += width + 50;
			}
		}
		ptreeLength = length;
		ptreeSize = size;
		ptreeWidth = width;
		ptreeHeight = height;
		setupBorder(size,height,width, length, false);
		prevnodes = nodes;
		for(int i = 1; i < tree.size(); i++){
			widthOffset = 0;
			heightOffset -= height + 50;
			nodes = new ArrayList<WindowTestNode>();
			ArrayList<ArrayList<TestCaseNode>> groupList = tree.getRear();
			for(ArrayList<TestCaseNode> group: groupList){
				for(TestCaseNode node: group){
					double numChildren = node.getChildren().size();
					double totalWidth = numChildren*width + (numChildren - 1)*50;
					if(node.getEventID() != null){
						double woffset = widthOffset + totalWidth/2 - width/2;
						//get image of screen somehow
						WindowTestNode n = new WindowTestNode(efg, node.getEventID(), node.getImg(), node.getParent(), testCan, this);
						layerTestCase.addChild(n);
						n.translate(woffset, heightOffset);
						
						ArrayList<WindowTestNode> children = getChildrenWin(prevnodes, node);
						for(WindowTestNode w : children){
							PPath edge = new PPath();
							
							((ArrayList<PPath>)n.getAttribute("edges")).add(edge);
							((ArrayList<PPath>)w.getAttribute("edges")).add(edge);
							edge.addAttribute("nodes", new ArrayList<WindowTestNode>());
							((ArrayList<WindowTestNode>)edge.getAttribute("nodes")).add(n);
							((ArrayList<WindowTestNode>)edge.getAttribute("nodes")).add(w);
							w.updateEdge(edge);
							
							edge.setStroke(new BasicStroke(4f));
							edge.setStrokePaint(Color.green);
							edge.addInputEventListener(new EdgeTestEventHandler(testCan));
							edge.setVisible(true);
							layerEdgeTestCase.addChild(edge);
						}
						
						nodes.add(n);
					}
					widthOffset += totalWidth + 50;
				}
			}
			prevnodes = nodes;
		}
		resetTestView();
	}
	
	/**
	 * Grabs all of the children nodes for a single WindowTestNode.
	 * 
	 * @param nodes list of the nodes a level below the parent node
	 * @param node  the parent node of the PrefixTree structure
	 * @return a list of all the children nodes
	 */
	public ArrayList<WindowTestNode> getChildrenWin(ArrayList<WindowTestNode> nodes, TestCaseNode node){
		ArrayList<WindowTestNode> ret = new ArrayList<WindowTestNode>();
		for(WindowTestNode w : nodes){
			if(w.compParent(node)){
				ret.add(w);
			}
		}
		return ret;
	}
	
	/**
	 * Toggles the mouse input listener for the test case viewer between delete mode
	 * and regular mode.
	 */
	public static void setDeleteMode(boolean val){
		deleteMode = val;
	}
	
	/**
	 * Gets the delete mode value.
	 * 
	 * @return the delete mode value
	 */
	public static boolean getDeleteMode(){
		return deleteMode;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() instanceof JButton){
			JButton b = (JButton)e.getSource();
			if(b.equals(updateTest)){
				Object[] arr = this.fileSelect.getSelectedValues();
				ArrayList<String> arr2 = new ArrayList<String>();
				for(int i = 0; i < arr.length; i++){
					arr2.add((String)arr[i]);
				}
				PrefixTree tree = testCase.getPrefixTree(arr2);
				layerTestCase.removeAllChildren();
				layerEdgeTestCase.removeAllChildren();
				handleNewTree(tree);
				curTestDisp = arr2;
			}else if(b.equals(deleteTest)){
				Object[] arr = this.fileSelect.getSelectedValues();
				ArrayList<String> arr2 = new ArrayList<String>();
				for(int i = 0; i < arr.length; i++){
					arr2.add((String)arr[i]);
				}
				boolean[] suc = input.deleteTestCaseFiles(arr2);
				for(int i = 0; i < suc.length; i++){
					if(suc[i]){
						listModel.removeElement(arr2.get(i));
					}
				}
				testCase.deleteTestCases(arr2);
				boolean redraw = false;
				for(String s : arr2){
					if(curTestDisp.contains(s)){
						redraw = true;
						curTestDisp.remove(s);
					}
				}
				if(redraw){
					layerTestCase.removeAllChildren();
					layerEdgeTestCase.removeAllChildren();
				}
				if(redraw && curTestDisp.size() != 0){
					PrefixTree tree = testCase.getPrefixTree(curTestDisp);
					handleNewTree(tree);
				}
			}
		}else{
			JMenuItem item = (JMenuItem)e.getSource();
			int amount = 15;
			double scaleFactor = 0.1d;
			if(item.equals(close)){
				System.exit(0);
			}else if(item.equals(resetZoomAndPan)){
				PBounds bound = list.get(0).getGlobalFullBounds();
				bound = new PBounds(bound.getX()-40, bound.getY()-40, bound.getWidth()+80, bound.getHeight()+80);
				
				getCanvas().getCamera().setViewScale(1.0d);
				getCanvas().getCamera().setViewTransform(new AffineTransform());
				getCanvas().getCamera().animateViewToCenterBounds(bound, true, 1000);
			}else if(item.equals(panUp)){
				getCanvas().getCamera().translateView(0, -amount);
			}else if(item.equals(panDown)){
				getCanvas().getCamera().translateView(0, amount);
			}else if(item.equals(panLeft)){
				getCanvas().getCamera().translateView(-amount, 0);
			}else if(item.equals(panRight)){
				getCanvas().getCamera().translateView(amount, 0);
			}else if(item.equals(zoomIn)){
				double scale = 1.0d + scaleFactor;
				Rectangle canvasBounds = getCanvas().getBounds();
		        getCanvas().getCamera().scaleViewAboutPoint(scale, canvasBounds.getCenterX(), canvasBounds.getCenterY());
			}else if(item.equals(zoomOut)){
				double scale = 1.0d - scaleFactor;
				Rectangle canvasBounds = getCanvas().getBounds();
		        getCanvas().getCamera().scaleViewAboutPoint(scale, canvasBounds.getCenterX(), canvasBounds.getCenterY());
			}else if(item.equals(switchCan)){
				switchCanvas();
			}else if(item.equals(open)){
				SwingUtilities.invokeLater(new Runnable() {
		            public void run() {
		            	GUIBuilder.createAndShowGUI();
		            }
            	});
				this.setVisible(false);
				this.dispose();
			}
		}
		getCanvas().requestFocusInWindow();
	}
	
	/**
     * Returns the current canvas being displayed on this frame.
     * 
     * @return canvas being displayed on this frame
     */
    public PCanvas getCanvas() {
    	if(curOp.equals("efg")){
    		return efgCan;
    	}
        return testCan;
    }

    /**
     * Returns the default frame bounds.
     * 
     * @return default frame bounds
     */
    public Rectangle getDefaultFrameBounds() {
        return new Rectangle(DEFAULT_FRAME_POSITION, DEFAULT_FRAME_DIMENSION);
    }

    /**
     * Returns whether the frame is currently in full screen mode.
     * 
     * @return whether the frame is currently in full screen mode
     */
    public boolean isFullScreenMode() {
        return graphicsDevice.getFullScreenWindow() != null;
    }

    /**
     * Switches full screen state.
     * 
     * @param fullScreenMode whether to place the frame in full screen mode or
     *            not.
     */
    public void setFullScreenMode(final boolean fullScreenMode) {
        if (fullScreenMode != isFullScreenMode() || !isVisible()) {
            if (fullScreenMode) {
                switchToFullScreenMode();
            }
            else {
                switchToWindowedMode();
            }
        }
    }

    /**
     * Switches to fullscreen mode.
     */
    private void switchToFullScreenMode() {
        addEscapeFullScreenModeListener();

        if (isDisplayable()) {
            dispose();
        }

        setUndecorated(true);
        setResizable(false);
        graphicsDevice.setFullScreenWindow(this);

        if (graphicsDevice.isDisplayChangeSupported()) {
            chooseBestDisplayMode(graphicsDevice);
        }
        validate();
    }

    /**
     * Switches back to window mode
     */
    private void switchToWindowedMode() {
        removeEscapeFullScreenModeListener();

        if (isDisplayable()) {
            dispose();
        }

        setUndecorated(false);
        setResizable(true);
        graphicsDevice.setFullScreenWindow(null);
        validate();
        setVisible(true);
    }

    /**
     * Sets the display mode to the best device mode that can be determined.
     * 
     * Used in full screen mode.
     * 
     * @param device The graphics device being controlled.
     */
    protected void chooseBestDisplayMode(final GraphicsDevice device) {
        final DisplayMode best = getBestDisplayMode(device);
        if (best != null) {
            device.setDisplayMode(best);
        }
    }

    /**
     * Finds the best display mode the graphics device supports. Based on the
     * preferred modes.
     * 
     * @param device the device being inspected
     * 
     * @return best display mode the given device supports
     */
    @SuppressWarnings("rawtypes")
	protected DisplayMode getBestDisplayMode(final GraphicsDevice device) {
        final Iterator itr = getPreferredDisplayModes(device).iterator();
        while (itr.hasNext()) {
            final DisplayMode each = (DisplayMode) itr.next();
            final DisplayMode[] modes = device.getDisplayModes();
            for (int i = 0; i < modes.length; i++) {
                if (modes[i].getWidth() == each.getWidth() && modes[i].getHeight() == each.getHeight()
                        && modes[i].getBitDepth() == each.getBitDepth()) {
                    return each;
                }
            }
        }

        return null;
    }

    /**
     * By default return the current display mode. Subclasses may override this
     * method to return other modes in the collection.
     * 
     * @param device the device being inspected
     * @return preferred display mode
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected Collection getPreferredDisplayModes(final GraphicsDevice device) {
        final ArrayList result = new ArrayList();

        result.add(device.getDisplayMode());
        /*
         * result.add(new DisplayMode(640, 480, 32, 0)); result.add(new
         * DisplayMode(640, 480, 16, 0)); result.add(new DisplayMode(640, 480,
         * 8, 0));
         */

        return result;
    }

    /**
     * This method adds a key listener that will take this PFrame out of full
     * screen mode when the escape key is pressed. This is called for you
     * automatically when the frame enters full screen mode.
     */
    public void addEscapeFullScreenModeListener() {
        removeEscapeFullScreenModeListener();
        escapeFullScreenModeListener = new KeyAdapter() {
            public void keyPressed(final KeyEvent aEvent) {
                if (aEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    setFullScreenMode(false);
                }
            }
        };
        getCanvas().addKeyListener((KeyListener) escapeFullScreenModeListener);
    }

    /**
     * This method removes the escape full screen mode key listener. It will be
     * called for you automatically when full screen mode exits, but the method
     * has been made public for applications that wish to use other methods for
     * exiting full screen mode.
     */
    public void removeEscapeFullScreenModeListener() {
        if (escapeFullScreenModeListener != null) {
            getCanvas().removeKeyListener((KeyListener) escapeFullScreenModeListener);
            escapeFullScreenModeListener = null;
        }
    }

    /**
     * Grabs the string representing which viewer is currently active.
     * @return the string representing the currently active viewer
     */
    public String getCurOp(){
    	return curOp;
    }
    
    /**
     * Grabs the file importer object
     * @return the file importer object
     */
    public FileImporter getFileImporter(){
    	return input;
    }
    
    /**
     * Grabs the ListModel used in the test case viewer for 
     * the test case file listings.	
     * @return the ListModel
     */
    public DefaultListModel getListModel(){
    	return listModel;
    }
    
    /**
     * Grabs the TestCaseBuilder object.
     * @return the test case builder object
     */
    public TestCaseBuilder getTestCase(){
    	return testCase;
    }
	
    /**
     * Returns a list of the currently displayed test cases.
     * @return the currently displayed test cases
     */
    public ArrayList<String> getCurTestDisp(){
    	return curTestDisp;
    }
    
    /**
     * Returns the layer of window nodes for test case viewer.
     * @return the layer of window nodes for the test case viewer
     */
    public PLayer getLayerTestCase(){
    	return layerTestCase;
    }
    
    /**
     * Returns the edge layer for the test case viewer.
     * @return the edge layer for the test case viewer
     */
    public PLayer getLayerEdgeTestCase(){
    	return layerEdgeTestCase;
    }
    
}
