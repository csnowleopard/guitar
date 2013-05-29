package gui.visualizer;

import javax.swing.JScrollPane;

import dataModels.visualizer.VisualizationData;



/**
 * Deprecated: use TreePanel instead.
 * 
 * This class is an extension of the JScrollPane that displays
 * the VisualizationTree of the current application.
 * 
 * The VisualizationTree class is used with the Visualization
 * portion of AndroidGUITAR.
 * 
 * @author Chris Carmel
 *
 */

@Deprecated
public class VisualizationTreePanel extends JScrollPane {
	
	/**
	 * Constructs a VisualizationTreePanel with a VisualizationTree.
	 * 
	 * @param vd		used to construct the VisualizationTree inside the VisualizationTreePanel
	 */
	public VisualizationTreePanel(VisualizationData vd) {
		super(new VisualizationTree(vd));
	}
	
}