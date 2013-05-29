package gui;

import gui.homePanel.HomePanel;
import gui.visualizer.LegendPanel;
import gui.visualizer.PVisualizationCanvas;
import gui.visualizer.PVisualizationCanvasPanel;
import gui.visualizer.TestCaseTree;
import gui.visualizer.TreePanel;
import gui.visualizer.VisualizationTree;

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

import dataModels.electric.projects.Project;
import dataModels.electric.projects.ProjectManagement;
import dataModels.visualizer.VisualizationData;


/**
 * This class is an extension of the JFrame class that contains the
 * GUI ElectricGUITAR
 * 
 * @author Chris Carmel
 *
 */
public class MainWindow extends JFrame implements ChangeListener {

	//vd by default right now is TippyTipper - gets updated when Visualization tab is selected
	private VisualizationData vd;
	
	public PVisualizationCanvasPanel pvcp;
	protected JTabbedPane pvcTabs;
	protected LegendPanel legend;
	protected JPanel treeLegendContainer;
	protected JSplitPane jsp;
	protected ProjectManagement pm;
	
	/**
	 * Panel that holds the project information
	 * */
	protected HomePanel homePanel;
	
	/**
	 * Panel for the visualization
	 */
	private TreePanel visualizationTreePanel;
	
	/**
	 * Panel for test case viewer
	 */
	private TreePanel testTreePanel;
	
	/**
	 * Constructs a PreliminaryGUI from the incoming VisualizationData.
	 */
	public MainWindow() {
		super();
		
		VisualizationData vd = new VisualizationData();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((int) (screenDimension.getWidth() * 0.9), (int) (screenDimension.getHeight() * 0.9));

		//setTitle(vd.getApplicationTitle() + " Visualization");
		setTitle("AndroidElectricGUITAR");
		
		pm = new ProjectManagement();

		pvcp = new PVisualizationCanvasPanel(vd);
		
		homePanel = new HomePanel(pm, this);
		
		visualizationTreePanel = new TreePanel(new VisualizationTree());
		testTreePanel = new TreePanel(new TestCaseTree());;
		
		pvcTabs = new JTabbedPane();
		pvcTabs.add("Home", homePanel);
		pvcTabs.add("Visualization", visualizationTreePanel);
		pvcTabs.add("Capture/Replay", new JPanel());
		pvcTabs.add("Tests", testTreePanel);
		
		pvcTabs.addChangeListener(this);

		pvcTabs.setEnabledAt(1, false);
		pvcTabs.setEnabledAt(2, false);
		pvcTabs.setEnabledAt(3, false);
		
		legend = new LegendPanel(vd);

		treeLegendContainer = new JPanel();
		treeLegendContainer.setLayout(new BoxLayout(treeLegendContainer, BoxLayout.Y_AXIS));
		treeLegendContainer.add(pvcTabs);
		
		jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeLegendContainer, pvcp);
		jsp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		jsp.setDividerLocation(0.2);
		//jsp.setResizeWeight(1.00);

		vd.getPVisualizationCanvas().resetForHomeTab();
		//vd.setCurrentEdgeLayer(null);
		
		getContentPane().add(jsp);
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		//treeLegendContainer.remove(legend);
		//legend = new LegendPanel(vd);
		//treeLegendContainer.add(legend);
		
		JTabbedPane pvcTabs = (JTabbedPane) arg0.getSource();
		
		if (pvcTabs.getTitleAt(pvcTabs.getSelectedIndex()).equals("Home")) {
			System.out.println("home");
			treeLegendContainer.remove(legend);
			vd.getPVisualizationCanvas().resetForHomeTab();
			if(homePanel.list.getSelectedIndex() != -1){
				homePanel.showScreenshot();
			}
			vd.setCurrentEdgeLayer(null);
		} 
		else if (pvcTabs.getTitleAt(pvcTabs.getSelectedIndex()).equals("Visualization")) {
			System.out.println("Visualization");
			Project project = (Project) homePanel.list.getSelectedValue();
			System.out.println("Data Loaded");
			
			vd = project.getVisualizationData();
					//new VisualizationData(homePanel.list.getSelectedValue().toString());
			
			if(visualizationTreePanel == null){
				visualizationTreePanel = new TreePanel(new VisualizationTree(vd));
				pvcTabs.repaint();
			}
			
			
			visualizationTreePanel.setViewportView(new VisualizationTree(vd));
			treeLegendContainer.remove(legend);
			jsp.removeAll();
			legend = new LegendPanel(vd);
			treeLegendContainer.add(legend);
			pvcp = new PVisualizationCanvasPanel(vd);
			jsp.add(treeLegendContainer, jsp.LEFT);
			jsp.add(pvcp, jsp.RIGHT);
			jsp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			jsp.setDividerLocation(0.2);
			vd.getPVisualizationCanvas().resetForTreeTab();
			vd.setCurrentEdgeLayer(vd.getPVisualizationCanvas().getLayer(PVisualizationCanvas.EDGE_LAYER_NAME));
			vd.getCurrentEdgeLayer().setVisible(true);
			pvcTabs.setEnabledAt(1, true);
			pvcTabs.setEnabledAt(2, true);
			pvcTabs.setEnabledAt(3, true);
		} 
		else if (pvcTabs.getTitleAt(pvcTabs.getSelectedIndex()).equals("Capture/Replay")) {
			System.out.println("Capture/Replay");
			Project project = (Project) homePanel.list.getSelectedValue();
			vd = project.getVisualizationData();
			treeLegendContainer.add(legend);
			vd.getPVisualizationCanvas().resetForCaptureReplayTab();
			vd.setCurrentEdgeLayer(null);
		} 
		else if (pvcTabs.getTitleAt(pvcTabs.getSelectedIndex()).equals("Tests")) {
			System.out.println("Tests");
			Project project = (Project) homePanel.list.getSelectedValue();
			System.out.println("Data Loaded");
			vd = project.getVisualizationData();

			if(testTreePanel == null){
				testTreePanel = new TreePanel(new TestCaseTree(vd));
				pvcTabs.repaint();
			}
			testTreePanel.setViewportView(new TestCaseTree(vd));
			
			treeLegendContainer.remove(legend);
			jsp.removeAll();
			
			legend = new LegendPanel(vd);
			treeLegendContainer.add(legend);
			
			pvcp = new PVisualizationCanvasPanel(vd);
			
			jsp.add(treeLegendContainer, jsp.LEFT);
			jsp.add(pvcp, jsp.RIGHT);
			jsp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			jsp.setDividerLocation(0.2);
		
			vd.getPVisualizationCanvas().resetForTestsTab();
			vd.setCurrentEdgeLayer(null);
			
			pvcTabs.setEnabledAt(1, true);
			pvcTabs.setEnabledAt(2, true);
			pvcTabs.setEnabledAt(3, true);
			
			treeLegendContainer.add(legend);
			vd.getPVisualizationCanvas().resetForTestsTab();
			//vd.setCurrentEdgeLayer(null);
		}
		
		vd.getPVisualizationCanvas().getLayer(PVisualizationCanvas.EVENT_LAYER_NAME).setVisible(true);
		vd.getPVisualizationCanvas().getLayer(PVisualizationCanvas.LABEL_LAYER_NAME).setVisible(true);
	}


	public JSplitPane getjsp(){
		return this.jsp;
	}
	
	public void makeClickable() {
		pvcTabs.setEnabledAt(1, true);
		pvcTabs.setEnabledAt(2, true);
		pvcTabs.setEnabledAt(3, true);
	}

	public void makeUnClickable() {
		pvcTabs.setEnabledAt(1, false);
		pvcTabs.setEnabledAt(2, false);
		pvcTabs.setEnabledAt(3, false);
	}
}
