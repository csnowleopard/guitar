package gui.visualizer;


import javax.swing.JScrollPane;

import dataModels.visualizer.VisualizationData;



/**
 * This class is an extension of the JScrollPane that displays
 * the VisualizationTree or the TestCaseTree of the current 
 * application.
 * 
 * The VisualizationTree and TestCaseTree classes are used with 
 * the Visualization portion of AndroidGUITAR.
 * 
 * @author Chris Carmel
 *
 */
public class TreePanel extends JScrollPane {
	
	/**
	 * Constructs a TreePanel with a VisualizationTree.
	 * 
	 * @param vt		VisualizationTree used to construct the TreePanel
	 */
	public TreePanel(VisualizationTree vt) {
		super(vt);
	}
	
	public TreePanel() {
		super();
	}
	
	/**
	 * Constructs a TreePanel with a TestCaseTree.
	 * 
	 * @param vd		TestCaseTree used to construct the TreePanel
	 */
	public TreePanel(TestCaseTree tct) {
		super(tct);
	}
}