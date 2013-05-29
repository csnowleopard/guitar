package gui.visualizer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dataModels.visualizer.VisualizationData;


import edu.umd.cs.piccolo.PLayer;


/**
 * This class is an extension of the JPanel class that contains the Layers
 * portion of the LegendPanel. It also implements the ItemListener interface
 * for use with addressing state changes of the various check boxes.
 * 
 * @author Chris Carmel
 *
 */
public class LayersLegendPanel extends JPanel implements ItemListener {

	/**
	 * PVisualizationCanvas the LegendPanel refers to.
	 */
	private PVisualizationCanvas pvc = null;

	/**
	 * JCheckBox representing the state of visibility of the edges layer of the PVisualizationCanvas.
	 */
	private JCheckBox edgesCheckBox = null;

	/**
	 * JCheckBox representing the state of visibility of the events layer of the PVisualizationCanvas.
	 */
	private JCheckBox eventsCheckBox = null;

	/**
	 * JCheckBox representing the state of visibility of the labels layer of the PVisualizationCanvas.
	 */
	private JCheckBox labelsCheckBox = null;
	
	/**
	 * VisualizationData used for updating state of application.
	 */
	private VisualizationData vd = null;

	/**
	 * Constructs LayersLegendPanel using the incoming VisualizationData.
	 * 
	 * @param vd		VisualizationData used to construct the LayersLegendPanel
	 */
	public LayersLegendPanel(VisualizationData vd) {
		super();
		
		this.vd = vd;

		this.pvc = vd.getPVisualizationCanvas();

		this.setBorder(BorderFactory.createTitledBorder("Layers"));

		this.setMinimumSize(new Dimension(112, 101));
		this.setPreferredSize(new Dimension(112, 101));
		this.setMaximumSize(new Dimension(112, 101));

		this.setLayout(new GridBagLayout());
		GridBagConstraints gbConstraints = new GridBagConstraints();

		// creating and setting the JCheckBox and label for the edges layer
		edgesCheckBox = new JCheckBox();
		edgesCheckBox.addItemListener(this);
		gbConstraints.gridy = 0;
		gbConstraints.gridx = 0;
		this.add(edgesCheckBox, gbConstraints);

		JLabel edgesLayerCheckBoxLabel = new JLabel("Edges");
		edgesLayerCheckBoxLabel.setHorizontalAlignment(JLabel.LEFT);
		gbConstraints.gridx = 1;
		this.add(edgesLayerCheckBoxLabel, gbConstraints);

		// creating and setting the JCheckBox and label for the events layer
		eventsCheckBox = new JCheckBox();
		eventsCheckBox.addItemListener(this);
		gbConstraints.gridy = 1;
		gbConstraints.gridx = 0;
		this.add(eventsCheckBox, gbConstraints);

		JLabel eventsLayerCheckBoxLabel = new JLabel("Events");
		eventsLayerCheckBoxLabel.setHorizontalAlignment(JLabel.LEFT);
		gbConstraints.gridx = 1;
		this.add(eventsLayerCheckBoxLabel, gbConstraints);

		// creating and setting the JCheckBox and label for the labels layer
		labelsCheckBox = new JCheckBox();
		labelsCheckBox.addItemListener(this);
		gbConstraints.gridy = 2;
		gbConstraints.gridx = 0;
		this.add(labelsCheckBox, gbConstraints);

		JLabel labelsLayerCheckBoxLabel = new JLabel("Labels");
		labelsLayerCheckBoxLabel.setHorizontalAlignment(JLabel.LEFT);
		gbConstraints.gridx = 1;
		this.add(labelsLayerCheckBoxLabel, gbConstraints);
		
		edgesCheckBox.setSelected(true);
		eventsCheckBox.setSelected(true);
		labelsCheckBox.setSelected(true);
	}

	/**
	 * Sets the visibility of the given layer on the PVisualizationCanvas 
	 * depending on the state of the check box being listened to.
	 */
	public void itemStateChanged(ItemEvent e) {
		JCheckBox checkBoxSource = (JCheckBox) e.getSource();

		PLayer currLayer = null;

		if (checkBoxSource == edgesCheckBox) {
			if (vd.getCurrentEdgeLayer() != null) {
				currLayer = vd.getCurrentEdgeLayer();
			} else {
				currLayer = pvc.getLayer(PVisualizationCanvas.EDGE_LAYER_NAME);
			}
		} else if (checkBoxSource == eventsCheckBox) {
			currLayer = pvc.getLayer(PVisualizationCanvas.EVENT_LAYER_NAME);
		} else if (checkBoxSource == labelsCheckBox) {
			currLayer = pvc.getLayer(PVisualizationCanvas.LABEL_LAYER_NAME);
		}

		if (e.getStateChange() == ItemEvent.SELECTED) {
			currLayer.setVisible(true);
		} else {
			currLayer.setVisible(false);
		}
	}
}