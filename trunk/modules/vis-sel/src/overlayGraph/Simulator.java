package overlayGraph;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.plaf.basic.BasicBorders;

import overlayGraph.util.SiteInfo;
import overlayGraph.util.TestCaseParser;


/**
 * Simulates the this that appears after a user clicks on a siteMap node.
 */
public class Simulator extends JFrame {
	private static final long serialVersionUID = 1L;

	final String initalURLofRip;
	final String nodeClickedURL;
	final EFGVisualizer efgVisualizer;
	final JScrollPane scrollPane;
	final SiteInfo siteInfo;

	public Simulator(String initialURLofRip, String nodeClickedURL, String ripName, SiteInfo siteInfo) {
		super(nodeClickedURL);

		this.initalURLofRip= initialURLofRip;
		this.nodeClickedURL= nodeClickedURL;
		this.siteInfo = siteInfo;

		// add canvas
		efgVisualizer= new EFGVisualizer(initialURLofRip, nodeClickedURL, ripName, siteInfo);
		scrollPane= new JScrollPane(efgVisualizer);

		scrollPane.getVerticalScrollBar().setUnitIncrement(16); // make scrollwheel faster
		this.setContentPane(scrollPane);

		// set controls
		this.setJMenuBar(createControls(efgVisualizer, ripName, true, null, null));

		this.setMinimumSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// display
		this.setVisible(true);
	}
	
	/**
	 * Simulator intialized to display passed in a passed in test.
	 * @param initialURLofRip
	 * @param nodeClickedURL
	 * @param ripName
	 * @param siteInfo
	 * @param testId The ID of test to be displayed
	 * @param testWidgets ID's of the widgets of tested in test <code>testId</code>.
	 */
	public Simulator(String initialURLofRip, String nodeClickedURL, String ripName, SiteInfo siteInfo, String testId, 
	        java.util.List<String> testWidgets) {

	    super("Viewing Testcase: " + nodeClickedURL);

	    this.initalURLofRip= initialURLofRip;
	    this.nodeClickedURL= nodeClickedURL;
	    this.siteInfo = siteInfo;

	    // add canvas
	    efgVisualizer= new EFGVisualizer(initialURLofRip, nodeClickedURL, ripName, siteInfo);
	    EFGCanvas canvas = efgVisualizer.getCanvas();
	    canvas.hideEFGedges();
	    canvas.hideGUInodes();
	    canvas.showCurrTC(testId, testWidgets);
	    
	    scrollPane = new JScrollPane(efgVisualizer);

	    scrollPane.getVerticalScrollBar().setUnitIncrement(16); // make scrollwheel faster
	    this.setContentPane(scrollPane);

	    // set controls
        this.setJMenuBar(createControls(efgVisualizer, ripName, false, testId, testWidgets));
        
	    this.setMinimumSize(new Dimension(800, 600));
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    // display
	    this.setVisible(true);
	}

	/**
	 * Creates a menu bar which controls EFG, GUI, and testcase display on an EFG+GUI Visualizer
	 * @param overlayPanel
	 * @param showGraph If the GUI/EFG toggle should be checked or not.
	 * @param testId Non-null if we only want to display one specific test in the testcase menu.
	 * @param testWidgets A List of widget id's contained in the test with <code>testId</code>.
	 * @return
	 */
	private JMenuBar createControls(final EFGVisualizer overlayPanel, String ripName, boolean showGraph, final String testId, 
	        final java.util.List<String> testWidgets) {
		JMenuBar menuBar = new JMenuBar();

		/*
		 *  Build EFG/GUI JMenu
		 */
		JMenu efgGUImenu = new JMenu("Toggle GUI/EFG Visualizations");
		efgGUImenu.setBorder(new BasicBorders.ButtonBorder(Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY));
		menuBar.add(efgGUImenu);

		final JCheckBox guiCheck = new JCheckBox("Show GUI component nodes");
		guiCheck.getModel().setSelected(showGraph);
		guiCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
				boolean selected = abstractButton.getModel().isSelected();
				if (selected) {
					overlayPanel.getCanvas().showGUInodes();
				} else {
					overlayPanel.getCanvas().hideGUInodes();
				}
			}
		});
		efgGUImenu.add(guiCheck);

		final JCheckBox efgCheck = new JCheckBox("Show EFG transition edges");
		efgCheck.getModel().setSelected(showGraph);
		efgCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
				boolean selected = abstractButton.getModel().isSelected();
				if (selected) {
					overlayPanel.getCanvas().showEFGedges();
				} else {
					overlayPanel.getCanvas().hideEFGedges();
				}
			}
		});
		efgGUImenu.add(efgCheck);

		/*
		 * Build test case JMenu
		 */
//		// Get test cases
//		String tcPath = ripName + File.separator + "TC";
//		TestCaseParser tcParser = new TestCaseParser(tcPath);
//		//TestCase name -> List of TC's contained eventID's
//		final HashMap<String,ArrayList<String>> testCases = tcParser.getTestCases();
//		if(!nodeClickedURL.equals(initalURLofRip)){	//TestCases should not be shown
		if(!nodeClickedURL.equals(initalURLofRip) && testId == null){	//TestCases should not be shown

			menuBar.add(new JMenuItem("no TestCases to show"));
		} else {
		     // Get test cases
	        String tcPath = ripName + File.separator + "TC";
	        TestCaseParser tcParser = new TestCaseParser(tcPath);
	        final HashMap<String,ArrayList<String>> testCases = tcParser.getTestCases();
	        
		    JMenu tcMenu = new JMenu("Toggle TestCase Visualizations");

			tcMenu.setBorder(new BasicBorders.ButtonBorder(Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY));
			menuBar.add(tcMenu);
			ButtonGroup tcButtonGroup = new ButtonGroup();//prevents multiple buttons from appearing selected simultaneously
			
			//"Show NONE" button
			/*
			 * "None" Button
			 */
			JRadioButtonMenuItem noneButton= new JRadioButtonMenuItem("Show NONE");
			
			noneButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					overlayPanel.getCanvas().hideCurrTC();
					if(guiCheck.isSelected()){  //turn on any turned off Nodes
						overlayPanel.getCanvas().showGUInodes();
					}
				}
			});
			noneButton.setSelected(true);  //select it by default
			tcButtonGroup.add(noneButton);
			tcMenu.add(noneButton);
			
			/**
			 * TC buttons
			 */
			// dynamically build the testcase menu from the testCases, adding a radioButton for each
			if (testId == null) {
    			for (final String testCaseName : testCases.keySet()) {
    				String testCaseDisplayName= "Show \"" + testCaseName + "\"";
    				JRadioButtonMenuItem rbMenuItem= new JRadioButtonMenuItem(testCaseDisplayName);

    				tcButtonGroup.add(rbMenuItem);
    				tcMenu.add(rbMenuItem);
    
    				rbMenuItem.addActionListener(new ActionListener() {
    					public void actionPerformed(ActionEvent actionEvent) {
    						overlayPanel.getCanvas().hideCurrTC();
    						overlayPanel.getCanvas().showCurrTC(testCaseName, testCases.get(testCaseName));
    					}
    				});
    			}
			} else {
                String testCaseDisplayName= "Show \"" + testId + "\"";
                JRadioButtonMenuItem rbMenuItem= new JRadioButtonMenuItem(testCaseDisplayName);

                tcButtonGroup.add(rbMenuItem);
                tcMenu.add(rbMenuItem);

                rbMenuItem.setSelected(true);
                rbMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        overlayPanel.getCanvas().hideCurrTC();
                        overlayPanel.getCanvas().showCurrTC(testId, testWidgets);
                    }
                });
			}
		}


		/*
		 * Perspective JMenu
		 */
		JMenu perspectiveMenu = new JMenu("Perspective [Zoom/Pan]");
		perspectiveMenu.setBorder(new BasicBorders.ButtonBorder(Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY));
		menuBar.add(perspectiveMenu);

//		final JRadioButtonMenuItem initialPanRadioButton = new JRadioButtonMenuItem("top-right pan");
//		initialPanRadioButton.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent zoomClicked) {
//				efgVisualizer.setPan(0, 0);
//			}
//		});
		//initialPanRadioButton.setSelected(true);

		JRadioButtonMenuItem zoom25RadioButton = new JRadioButtonMenuItem("25%");
		zoom25RadioButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent zoomClicked) {
				efgVisualizer.setZoom(0.25, 400, 0);
				//initialPanRadioButton.setSelected(false);
			}
		});
		JRadioButtonMenuItem zoom50RadioButton = new JRadioButtonMenuItem("50%");
		zoom50RadioButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent zoomClicked) {efgVisualizer.setZoom(0.5, 250, 0);
			//initialPanRadioButton.setSelected(false);
			}
		});
		JRadioButtonMenuItem zoom75RadioButton = new JRadioButtonMenuItem("75%");
		zoom75RadioButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent zoomClicked) {efgVisualizer.setZoom(0.75, 120, 0);
			//initialPanRadioButton.setSelected(false);
			}
		});
		final JRadioButtonMenuItem zoom100RadioButton = new JRadioButtonMenuItem("100%");
		zoom100RadioButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent zoomClicked) {efgVisualizer.setZoom(1.0, 0, 0);
			//initialPanRadioButton.setSelected(true);
			}
		});
		zoom100RadioButton.setSelected(true);
		JRadioButtonMenuItem zoom150RadioButton = new JRadioButtonMenuItem("150%");
		zoom150RadioButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent zoomClicked) {efgVisualizer.setZoom(1.5, 0, 0);
			//initialPanRadioButton.setSelected(false);
			}
		});
		JRadioButtonMenuItem zoom200RadioButton = new JRadioButtonMenuItem("200%");
		zoom200RadioButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent zoomClicked) {efgVisualizer.setZoom(2.0, 0, 0);
			//initialPanRadioButton.setSelected(false);
			}
		});
		JRadioButtonMenuItem zoom400RadioButton = new JRadioButtonMenuItem("400%");
		zoom400RadioButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent zoomClicked) {efgVisualizer.setZoom(4.0, 0, 0);
			//initialPanRadioButton.setSelected(false);
			}
		});

		final ButtonGroup zoomButtonGroup= new ButtonGroup();
		zoomButtonGroup.add(zoom25RadioButton);
		zoomButtonGroup.add(zoom50RadioButton);
		zoomButtonGroup.add(zoom75RadioButton);
		zoomButtonGroup.add(zoom100RadioButton);
		zoomButtonGroup.add(zoom150RadioButton);
		zoomButtonGroup.add(zoom200RadioButton);
		zoomButtonGroup.add(zoom400RadioButton);
		JMenu zoomMenu = new JMenu("Zoom");
		zoomMenu.add("Manual: right-click/drag screen");
		zoomMenu.add(zoom25RadioButton);
		zoomMenu.add(zoom50RadioButton);
		zoomMenu.add(zoom75RadioButton);
		zoomMenu.add(zoom100RadioButton);
		zoomMenu.add(zoom150RadioButton);
		zoomMenu.add(zoom200RadioButton);
		zoomMenu.add(zoom400RadioButton);

		JMenuItem resetItem = new JMenuItem("Reset Perspective");
		resetItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent resetPerspectiveClicked) {
				//				//Resets GUI & EFG
				//				guiCheck.setSelected(true);
				//				overlayPanel.getCanvas().showGUInodes();
				//				efgCheck.setSelected(true);
				//				overlayPanel.getCanvas().showEFGedges();
				//Resets zoom
				efgVisualizer.setZoom(1.0, 0, 0);
				zoom100RadioButton.setSelected(true);
			}
		});
		perspectiveMenu.add(resetItem);

		JMenu panMenu=  new JMenu("Pan");
		perspectiveMenu.add(panMenu);
		panMenu.add("Manual: left-click/drag screen");
		//panMenu.add(initialPanRadioButton);

		perspectiveMenu.add(zoomMenu);		

//		if(efgVisualizer.getCanvas().getZoomEventHandler().isDragging()){
//
//			zoomButtonGroup.clearSelection();
//		}
//		if(efgVisualizer.getCanvas().getZoomEventHandler().isDragging()){
//
//			initialPanRadioButton.setSelected(false);
//		}

		return menuBar;
	}
}