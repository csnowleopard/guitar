package Visualization;

import java.util.TreeSet;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import Readers.VisualizationData;
import Readers.Widget;
import Readers.WidgetComparator;
import Readers.Window;


/**
 * This class is an extension of the JTree class for use with the
 * Visualization portion of AndroidGUITAR. 
 * 
 * It contains a list of the current application's Widgets in a sorted
 * order as defined by the WidgetComparator.
 * 
 * @author Chris Carmel
 *
 */
public class VisualizationTree extends JTree {

	/**
	 * TreeSet of Widgets that holds them in sorted order according to the WidgetComparator
	 */
	private static TreeSet<Widget> widgetsSorted = new TreeSet<Widget>( new WidgetComparator() );

	/**
	 * Constructor that constructs the VisualizationTree using the incoming VisualizationData.
	 * 
	 * @param vd		incoming VisualizationData used to create the VisualizationTree
	 */
	public VisualizationTree(VisualizationData vd) {
		super(getRootNode(vd));

		VisualizationTreeCellRenderer renderer = new VisualizationTreeCellRenderer();
		this.setCellRenderer(renderer);

		VisualizationTreeCellEditor editor = new VisualizationTreeCellEditor();
		this.setCellEditor(editor);
		this.setEditable(true);
	}

	/**
	 * Returns a boolean representing the editability this TreePath.
	 * 
	 * @param path		TreePath to check if is editable
	 * 
	 * @return			a boolean representing the editability this TreePath 
	 */
	public boolean isPathEditable(TreePath path) {
		Object comp = path.getLastPathComponent();
		if (comp instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) comp;
			Object userObject = node.getUserObject();
			if (userObject instanceof Widget
					|| userObject instanceof Window) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the root node of this VisualizationTree.
	 * 
	 * @param vd		VisualizationData used to populate this VisualizationTree
	 * 
	 * @return		the root node of this VisualizationTree
	 */
	protected static MutableTreeNode getRootNode(VisualizationData vd) {

		if (widgetsSorted.isEmpty()) {
			for (Widget w : vd.getWidgetsMap().values()) {
				widgetsSorted.add(w);
			}
		}

		DefaultMutableTreeNode root, parent, child;
		root = new DefaultMutableTreeNode(vd.getApplicationTitle());

		for (Window currWindow : vd.getWindows()) {
			parent = new DefaultMutableTreeNode(currWindow);
			for (Widget currWidget : widgetsSorted) {
				if (currWidget.getWindow() == currWindow) {
					child = new DefaultMutableTreeNode(currWidget);
					parent.add(child);
				}
			}
			root.add(parent);
		}
		return root;
	}
}