package Visualization;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Readers.VisualizationData;

/**
 * This class is an extension of the JFrame class that contains the
 * GUI for the PVisualizationCanvas.
 * 
 * @author Chris Carmel
 *
 */
public class PreliminaryGUI extends JFrame implements ChangeListener {

	private VisualizationData vd;
	
	protected PVisualizationCanvasPanel pvcp;
	protected JTabbedPane pvcTabs;
	protected LegendPanel legend;
	protected JPanel treeLegendContainer;
	protected JSplitPane jsp;
	
	/**
	 * Constructs a PreliminaryGUI from the incoming VisualizationData.
	 * 
	 * @param vd		VisualizationData used to generate the constructed PreliminaryGUI
	 */
	public PreliminaryGUI(VisualizationData vd) {
		super();
		
		this.vd = vd;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((int) (screenDimension.getWidth() * 0.9), (int) (screenDimension.getHeight() * 0.9));

		setTitle(vd.getApplicationTitle() + " Visualization");

		pvcp = new PVisualizationCanvasPanel(vd);
		pvcTabs = new JTabbedPane();
		pvcTabs.add("Tree", new TreePanel(new VisualizationTree(vd)));
		pvcTabs.add("Tests", new TreePanel(new TestCaseTree(vd)));

		pvcTabs.addChangeListener(this);

		legend = new LegendPanel(vd);

		treeLegendContainer = new JPanel();
		treeLegendContainer.setLayout(new BoxLayout(treeLegendContainer, BoxLayout.Y_AXIS));
		treeLegendContainer.add(pvcTabs);
		treeLegendContainer.add(legend);

		jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pvcp, treeLegendContainer);
		jsp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		jsp.setDividerLocation(0.90);
		jsp.setResizeWeight(1.00);

		getContentPane().add(jsp);
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		treeLegendContainer.remove(legend);
		legend = new LegendPanel(vd);
		treeLegendContainer.add(legend);
		
		JTabbedPane pvcTabs = (JTabbedPane) arg0.getSource();
		if (pvcTabs.getTitleAt(pvcTabs.getSelectedIndex()).equals("Tree")) {
			vd.getPVisualizationCanvas().resetForTreeTab();
			vd.setCurrentEdgeLayer(vd.getPVisualizationCanvas().getLayer(PVisualizationCanvas.EDGE_LAYER_NAME));
			vd.getCurrentEdgeLayer().setVisible(true);
		} else if (pvcTabs.getTitleAt(pvcTabs.getSelectedIndex()).equals("Tests")) {
			vd.getPVisualizationCanvas().resetForTestsTab();
			vd.setCurrentEdgeLayer(null);
		}
		
		vd.getPVisualizationCanvas().getLayer(PVisualizationCanvas.EVENT_LAYER_NAME).setVisible(true);
		vd.getPVisualizationCanvas().getLayer(PVisualizationCanvas.LABEL_LAYER_NAME).setVisible(true);
	}
}
