package Visualization;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import Readers.TestCase;
import Readers.TestCaseStep;

/**
 * This class is an extension of the DefaultTreeCellRenderer used to
 * render the TestCaseTree.
 * 
 * @author Chris Carmel
 *
 */
class TestCaseTreeCellRenderer extends DefaultTreeCellRenderer {

	/**
	 * Icon associated with invalid test cases.
	 */
	ImageIcon invalidTestCaseIcon = new ImageIcon("Images/invalidTestCaseIcon.png");

	/**
	 * Icon associated with invalid test case steps.
	 */
	ImageIcon invalidTestCaseStepIcon = new ImageIcon("Images/invalidTestCaseStepIcon.png");

	/**
	 * Icon associated with valid test cases.
	 */
	ImageIcon validTestCaseIcon = new ImageIcon("Images/validTestCaseIcon.png");

	/**
	 * Icon associated with valid test cases steps.
	 */
	ImageIcon validTestCaseStepIcon = new ImageIcon("Images/validTestCaseStepIcon.png");

	/**
	 * Returns the TreeCellRenderer component.
	 */
	public Component getTreeCellRendererComponent(
			JTree tree,
			Object value,
			boolean sel,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(
				tree, value, sel,
				expanded, leaf, row,
				hasFocus);
		
		if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
			Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
			if (userObject instanceof String) {
				setText((String) userObject);
				setIcon(getClosedIcon());
			} else if (userObject instanceof TestCase) {
				setText(((TestCase) userObject).getTitle());
				if (((TestCase) userObject).isValid()) {
					setIcon(validTestCaseIcon);
				} else {
					setIcon(invalidTestCaseIcon);
				}
			} else if (userObject instanceof TestCaseStep) {
				setText(((TestCaseStep) userObject).getEventID());
				if (((TestCaseStep) userObject).isValid()) {
					setIcon(validTestCaseStepIcon);
				} else {
					setIcon(invalidTestCaseStepIcon);
				}
			}
		}

		return this;
	}
}