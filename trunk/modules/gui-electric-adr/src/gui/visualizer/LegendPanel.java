package gui.visualizer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import dataModels.visualizer.VisualizationData;



/**
 * This class is an extension of the JPanel as the Legend of 
 * the PVisualizationCanvas.
 * 
 * @author Chris Carmel
 *
 */
public class LegendPanel extends JPanel {

	/**
	 * Constructs a LegendPanel using the incoming VisualizationData.
	 *  
	 * @param vd		VisualizationData to be used to construct the LegendPanel
	 */
	public LegendPanel(VisualizationData vd) {
		super();
		
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		this.setMinimumSize(new Dimension(276, 175));
		this.setMaximumSize(new Dimension(276, 175));
		this.setPreferredSize(new Dimension(276, 175));

		this.setLayout(new GridBagLayout());
		GridBagConstraints gbConstraints = new GridBagConstraints();
		
		LayersLegendPanel layersLegend = new LayersLegendPanel(vd);
		gbConstraints.gridx = 0;
		gbConstraints.gridy = 0;
		this.add(layersLegend, gbConstraints);
		
		EdgesLegendPanel edgesLegend = new EdgesLegendPanel();
		gbConstraints.gridx = 0;
		gbConstraints.gridy = 1;
		this.add(edgesLegend, gbConstraints);
		
		EventsLegendPanel eventsLegend = new EventsLegendPanel();
		gbConstraints.gridx = 1;
		gbConstraints.gridy = 0;
		gbConstraints.gridheight = 2;
		this.add(eventsLegend, gbConstraints);
	}
}
