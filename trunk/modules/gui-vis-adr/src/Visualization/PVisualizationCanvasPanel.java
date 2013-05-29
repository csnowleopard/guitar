package Visualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import Readers.VisualizationData;
import Readers.Event;
import Readers.Window;
import Readers.Widget;

/**
 * This class is an extension of the JPanel class used for holding the
 * PVisualizationCanvas and giving it a border.
 * 
 * @author Chris Carmel
 *
 */
public class PVisualizationCanvasPanel extends JPanel {
	
	/**
	 * Constructs a PVisualizationCanvasPanel using the incoming VisualizationData.
	 * 
	 * @param vd		VisualizationData to be used to construct the PVisualizationCanvasPanel
	 */
	public PVisualizationCanvasPanel(VisualizationData vd) {
		super();

		PVisualizationCanvas pvc = vd.getPVisualizationCanvas();
		
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createLineBorder(Color.gray));
		this.add(BorderLayout.CENTER, pvc);
	}
	
}