package Visualization;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import Readers.Widget;
import Readers.Window;

/**
 * This class is an extension of the DefaultTreeCellRenderer for use with the
 * VisualizationTree. 
 * 
 * It contains a JCheckBox that is used to define the visibility of the 
 * TreeCell's associated Widget or Window object's selfNode on the 
 * PVisualizationCanvas. 
 * 
 * The VisualizationTree class is used with the Visualization
 * portion of AndroidGUITAR.
 * 
 * @author Chris Carmel
 *
 */
class VisualizationTreeCellRenderer extends DefaultTreeCellRenderer {

	/**
	 * CheckBox for this VisualizationTreeCellRenderer.
	 */
	protected JCheckBox checkBoxRenderer = new JCheckBox();

	/**
	 * Returns the VisualizationTreeCellRenderer component of the specified tree cell.
	 * 
	 * @param tree			JTree to be used
	 * @param value			Object tree contains
	 * @param selected		boolean representing selected status of value
	 * @param expanded		boolean representing expanded status of value
	 * @param leaf			boolean representing leaf status of value
	 * @param row			int representing row of value in tree
	 * @param hasFocus		boolean representing focus status of value
	 * 
	 * @return				the VisualizationTreeCellRenderer component of the specified tree cell
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object userObject = node.getUserObject();
			if (userObject instanceof Widget || userObject instanceof Window) {
				prepareVisualizationRenderer(userObject, selected);
				return checkBoxRenderer;
			}
		}
		return super.getTreeCellRendererComponent(tree, value, selected, expanded,
				leaf, row, hasFocus);
	}

	/**
	 * Prepares this VisualizationTreeCellRenderer.
	 * 
	 * @param userObject	Object used to prepare the VisualizationTreeCellRenderer 
	 * @param selected		boolean used to prepare the VisualizationTreeCellRenderer
	 */
	protected void prepareVisualizationRenderer(Object userObject, boolean selected) {
		String text = null;
		boolean visible = true;

		if (userObject instanceof Widget) {
			text = ((Widget) userObject).getTitle();
			visible = ((Widget) userObject).isVisible();
		} else if (userObject instanceof Window) {
			text = ((Window) userObject).getTitle();
			visible = ((Window) userObject).isVisible();
		}

		checkBoxRenderer.setText(text);
		checkBoxRenderer.setSelected(visible);
		if (selected) {
			checkBoxRenderer.setForeground(getTextSelectionColor());
			checkBoxRenderer.setBackground(getBackgroundSelectionColor());
		} else {
			checkBoxRenderer.setForeground(getTextNonSelectionColor());
			checkBoxRenderer.setBackground(getBackgroundNonSelectionColor());
		}
	}

}