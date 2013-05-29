package gui.visualizer;


import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import dataModels.guitar.testCase.TestCase;
import dataModels.guitar.testCase.TestCaseStep;
import dataModels.visualizer.Event;
import dataModels.visualizer.VisualizationData;
import dataModels.visualizer.Window;


import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class is an extension of the JTree class that holds the current
 * application's test cases.
 *
 * This class also implements the TreeWillExpandListener and TreeSelectionListner
 * interfaces.
 * 
 * @author Chris Carmel
 * @author Andrew Guthrie
 *
 */
public class TestCaseTree extends JTree implements TreeWillExpandListener, TreeSelectionListener{

	/**
	 * VisualizationData that gets updated with the tree.
	 */
	private VisualizationData vd;
	/**
	 * Constructs a TestCaseTree with the given VisualizationData.
	 * 
	 * @param vd		VisualizationData used to create the TestCaseTree
	 */
	public TestCaseTree(VisualizationData vd) {
		super(getRootNode(vd));

		this.vd = vd;
		TestCaseTreeCellRenderer renderer = new TestCaseTreeCellRenderer();
		this.setCellRenderer(renderer);

		this.addTreeWillExpandListener(this);
		this.addTreeSelectionListener(this);
	}
	
	public TestCaseTree() {
		super();

		//this.vd = vd;
		TestCaseTreeCellRenderer renderer = new TestCaseTreeCellRenderer();
		this.setCellRenderer(renderer);

		this.addTreeWillExpandListener(this);
		this.addTreeSelectionListener(this);
	}

	/**
	 * Returns true if the given path is editable.
	 * 
	 * @param path		TreePath to be checked for editableness
	 */
	public boolean isPathEditable(TreePath path) {
		Object comp = path.getLastPathComponent();
		if (comp instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) comp;
			Object userObject = node.getUserObject();
			if (userObject instanceof TestCase
					|| userObject instanceof TestCaseStep) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the populated root node of this TestCaseTree.
	 * 
	 * @param vd		VisualizationData used to populated the root node
	 * @return		the populated root node of this TestCaseTree
	 */
	protected static MutableTreeNode getRootNode(VisualizationData vd) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(vd.getApplicationTitle());
		DefaultMutableTreeNode invalidParent = new DefaultMutableTreeNode("Invalid");
		DefaultMutableTreeNode validParent = new DefaultMutableTreeNode("Valid");

		for (TestCase currTestCase : vd.getTestCases()) {
			DefaultMutableTreeNode currTestCaseNode = new DefaultMutableTreeNode(currTestCase);
			for (TestCaseStep currStep : currTestCase.getSteps()) {
				currTestCaseNode.add(new DefaultMutableTreeNode(currStep));
			}

			if (currTestCase.isValid()) {
				validParent.add(currTestCaseNode);
			} else {
				invalidParent.add(currTestCaseNode);
			}
		}
		root.add(invalidParent);
		root.add(validParent);

		return root;
	}

	/**
	 * TreeWillExpandListener method.
	 */
	@Override
	public void treeWillCollapse(TreeExpansionEvent event)
			throws ExpandVetoException {
		Object currUserObject = ((DefaultMutableTreeNode) event.getPath().getLastPathComponent()).getUserObject();
		if (currUserObject instanceof TestCase) {
			for (TestCaseStep currStep : ((TestCase) currUserObject).getSteps()) {
				PPath currEdge = currStep.getSelfEdge();

				currEdge.setTransparency(1f);

				PNode currNode = (PNode) ((ArrayList)currEdge.getAttribute("nodes")).get(0);
				PNode destNode = (PNode) ((ArrayList)currEdge.getAttribute("nodes")).get(1);

				currNode.setTransparency(1f);
				((PNode) currNode.getAttribute("label")).setTransparency(1f);
				destNode.setTransparency(1f);
				((PNode) destNode.getAttribute("label")).setTransparency(1f);
			}
		}
	}

	/**
	 * TreeWillExpandListener method.
	 */
	@Override
	public void treeWillExpand(TreeExpansionEvent event)
			throws ExpandVetoException {
		Object currUserObject = ((DefaultMutableTreeNode) event.getPath().getLastPathComponent()).getUserObject();
		if (currUserObject instanceof TestCase) {
			//			collapseSiblingTestCases(event);
			this.setSelectionPath(event.getPath());
		}
	}

	/**
	 * TreeSelectionListener method.
	 */
	@Override
	public void valueChanged(TreeSelectionEvent event) {
		final Object currUserObject = ((DefaultMutableTreeNode) event.getPath().getLastPathComponent()).getUserObject();
		if (currUserObject instanceof String) {
			vd.getPVisualizationCanvas().resetForTestsTab();
		}
		if (currUserObject instanceof TestCase ||
				currUserObject instanceof TestCaseStep) {
			TestCase testCase;
			if (currUserObject instanceof TestCase) {
				testCase = ((TestCase) currUserObject);
			} else {
				testCase = ((TestCaseStep) currUserObject).getTestCase();
			}


			//			if (this.isExpanded(event.getPath()) == false) {
			//				this.expandPath(event.getPath());
			//			}

			vd.getPVisualizationCanvas().getLayer(PVisualizationCanvas.EDGE_LAYER_NAME).setVisible(false);

			PLayer eventLayer = vd.getPVisualizationCanvas().getLayer(PVisualizationCanvas.EVENT_LAYER_NAME);
			for (int i = 0; i < eventLayer.getChildrenCount(); i++) {
				PNode currEventNode = eventLayer.getChild(i);
				currEventNode.setVisible(false);
				((PText) currEventNode.getAttribute("label")).setVisible(false);
			}

			PLayer windowLayer = vd.getPVisualizationCanvas().getLayer(PVisualizationCanvas.WINDOW_LAYER_NAME);
			for (int i = 0; i < windowLayer.getChildrenCount(); i++) {
				PNode currWindowNode = windowLayer.getChild(i);
				currWindowNode.setVisible(false);
				((PText) currWindowNode.getAttribute("label")).setVisible(false);
			}

			for (TestCase currTestCase : vd.getTestCases()) {
				currTestCase.getSelfLayer().setVisible(false);
			}
			for (TestCaseStep currStep : testCase.getSteps()) {
				Event currEvent = vd.getEventsMap().get(currStep.getEventID());
				Window currWindow = currEvent.getWindow();

				currWindow.getSelfNode().setVisible(true);
				currWindow.getSelfNode().setTransparency(1f);
				currWindow.getLabelNode().setVisible(true);
				currWindow.getLabelNode().setTransparency(1f);
				currEvent.getSelfNode().setVisible(true);
				currEvent.getSelfNode().setTransparency(1f);
				currEvent.getLabelNode().setVisible(true);
				currEvent.getLabelNode().setTransparency(1f);

				currStep.getSelfEdge().setTransparency(1f);
				vd.getPVisualizationCanvas().updateEdge(currStep.getSelfEdge());
			}

			testCase.getSelfLayer().setVisible(true);

			vd.setCurrentEdgeLayer(testCase.getSelfLayer());

		}

		if (currUserObject instanceof TestCaseStep) {
			final TestCase testCase = ((TestCaseStep) currUserObject).getTestCase();

			vd.setCurrentEdgeLayer(testCase.getSelfLayer());

			PPath currEdge;
			for (TestCaseStep currStep : testCase.getSteps()) {
				currEdge = currStep.getSelfEdge();

				currEdge.setTransparency(0.2f);

				PNode currNode = (PNode) ((ArrayList)currEdge.getAttribute("nodes")).get(0);
				PNode destNode = (PNode) ((ArrayList)currEdge.getAttribute("nodes")).get(1);

				currNode.setTransparency(0.2f);
				((PNode) currNode.getAttribute("label")).setTransparency(0.2f);
				((PNode) currNode.getAttribute("windowNode")).setTransparency(0.2f);
				destNode.setTransparency(0.2f);
				((PNode) destNode.getAttribute("label")).setTransparency(0.2f);
				((PNode) destNode.getAttribute("windowNode")).setTransparency(0.2f);
			}


			currEdge = ((TestCaseStep) currUserObject).getSelfEdge();
			currEdge.setTransparency(1f);


			PNode currNode = (PNode) ((ArrayList)currEdge.getAttribute("nodes")).get(0);
			((PNode) currNode.getAttribute("windowNode")).setTransparency(1f);
			PNode destNode = (PNode) ((ArrayList)currEdge.getAttribute("nodes")).get(1);
			destNode.setTransparency(1f);
			((PNode) destNode.getAttribute("label")).setTransparency(1f);
			((PNode) destNode.getAttribute("windowNode")).setTransparency(1f);
//			destNode.addInputEventListener(new PInputEventListener() {
//
//				public void processEvent(PInputEvent event, int type) {
//					if(event.isLeftMouseButton()){
//						String[] possibilities = {"Insert new Event", "Delete this Event", "Edit this Event", "Get Event Info"};
//						String s = (String)JOptionPane.showInputDialog(
//						                    new JFrame(),
//						                    "Choose an option for this node",
//						                    "Event Editing",
//						                    JOptionPane.PLAIN_MESSAGE,
//						                    null,
//						                    possibilities,
//						                    null);
//						if(s.equals(possibilities[0])){
//							//insert
//							
//						} else if(s.equals(possibilities[1])){
//							//delete
//							
//						} else if(s.equals(possibilities[2])){
//							//edit
//							
//						} else if(s.equals(possibilities[3])){
//							//display
//							String info = "Event: " + ((TestCaseStep) currUserObject).getEventID();
//							JOptionPane.showMessageDialog(new JFrame(), info, "Event Info", JOptionPane.PLAIN_MESSAGE, null);
//							//JOptionPane.showInputDialog(new JFrame(), info, "Event Info", JOptionPane.PLAIN_MESSAGE, null, null, null);
//						}
//					}
//				}
//
//			});
		}
	}

	/**
	 * Collapses the siblings of a given TreeExpansionEvent's source node.
	 * 
	 * @param event		TreeExpansionEvent used to designate which nodes to collapse
	 */
	public void collapseSiblingTestCases(TreeExpansionEvent event) {
		TreePath grandParentPath = event.getPath().getParentPath().getParentPath();
		DefaultMutableTreeNode grandParentNode = (DefaultMutableTreeNode) grandParentPath.getLastPathComponent();

		for (int i = 0; i < grandParentNode.getChildCount(); i++) {
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) grandParentNode.getChildAt(i);
			TreePath parentPath = grandParentPath.pathByAddingChild(parentNode);

			if (this.isExpanded(parentPath)) {
				for (int j = 0; j < parentNode.getChildCount(); j++) {
					TreePath childPath = parentPath.pathByAddingChild(parentNode.getChildAt(j));
					if (this.isExpanded(childPath)) {
						this.collapsePath(parentPath.pathByAddingChild(parentNode.getChildAt(j)));
					}
				}
			}
		}
	}



}