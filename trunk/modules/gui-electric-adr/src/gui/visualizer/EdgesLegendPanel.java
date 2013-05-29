package gui.visualizer;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class is an extension of the JPanel class that contains the Edges
 * portion of the LegendPanel.
 * 
 * @author Chris Carmel
 *
 */
public class EdgesLegendPanel extends JPanel {
	
	private float dashPattern[] = { 10.0f };
	
	/**
	 * String array of the types of Edges.
	 */
	private String[] edgeTypes = { "Normal", "Reaching" };
	
	/**
	 * Strokes array of the types of Edge strokes. 
	 */
	private BasicStroke[] edgeStrokes = { new BasicStroke(), new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f) };

	/**
	 * Constructs an EdgesLegendPanel.
	 */
	public EdgesLegendPanel() {
		super();

		this.setBorder(BorderFactory.createTitledBorder("Edges"));
		
		this.setMinimumSize(new Dimension(112, 72));
		this.setPreferredSize(new Dimension(112, 72));
		this.setMaximumSize(new Dimension(112, 72));
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints layoutConstraints = new GridBagConstraints();
		
		for (int i = 0; i < edgeTypes.length; i++) {
			layoutConstraints.gridy = i;
			layoutConstraints.gridx = 0;
			layoutConstraints.weightx = 1.0;

			EdgeLegendCanvas edgeCanvas = new EdgeLegendCanvas(edgeStrokes[i]);
			this.add(edgeCanvas, layoutConstraints);
			
			layoutConstraints.gridx = 1;
			layoutConstraints.weightx = 0.0;
			JLabel edgeLabel = new JLabel(edgeTypes[i]);
			this.add(edgeLabel, layoutConstraints);
		}
	}
}
