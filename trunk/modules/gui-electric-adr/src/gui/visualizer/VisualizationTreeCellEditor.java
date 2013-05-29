package gui.visualizer;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import dataModels.visualizer.Widget;
import dataModels.visualizer.Window;



/**
 * This class is an extension of the DefaultCellEditor for use with the
 * VisualizationTree. 
 * 
 * It contains either a Widget or a Window object and a checkbox that is used
 * to set the object's selfNode's visibility on the PVisualizationCanvas.
 * 
 * The VisualizationTree class is used with the Visualization
 * portion of AndroidGUITAR.
 * 
 * @author Chris Carmel
 *
 */
public class VisualizationTreeCellEditor extends DefaultCellEditor {

	/**
	 * JCheckBox component for this VisualizationTreeCellEditor.
	 */
	private JCheckBox editor = null;

	/**
	 * VisualizationTree for this VisualizationTreeCellEditor.
	 */
	private JTree tree = null;

	/**
	 * Row in the VisualizationTree or this VisualizationTreeCellEditor.
	 */
	private int row = 0;

	/**
	 * Widget for this VisualizationTreeCellEditor if its row in its VisualizationTree represents a Widget, null otherwise.
	 */
	private Widget widget = null;

	/**
	 * Window for this VisualizationTreeCellEditor if its row in its VisualizationTree represents a Window, null otherwise.
	 */
	private Window window = null;

	/**
	 * Constructs a VisualizationTreeCellEditor.
	 */
	public VisualizationTreeCellEditor() {
		super(new JCheckBox());
	}

	/**
	 * Returns the VisualizationTreeCellEditor component of the specified tree cell.
	 * 
	 * @param tree			JTree to be used
	 * @param value			Object tree contains
	 * @param selected		boolean representing selected status of value
	 * @param expanded		boolean representing expanded status of value
	 * @param leaf			boolean representing leaf status of value
	 * @param row			int representing row of value in tree
	 * 
	 * @return				the editor VisualizationTreeCellEditor of the specified tree cell
	 */
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row) {
		this.tree = tree;
		this.row = row;

		widget = null;
		window = null;
		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object userObject = node.getUserObject();
			if (userObject instanceof Widget) {
				widget = (Widget) userObject;

				editor = (JCheckBox) (super.getComponent());
				editor.setText(widget.getTitle());
				if (widget.getWindow().isVisible()) {
					editor.setSelected(widget.isVisible());
				} else {
					editor.setSelected(false);
				}
			} else if (userObject instanceof Window) {
				window = (Window) userObject;

				editor = (JCheckBox) (super.getComponent());
				editor.setText(window.getTitle());
				editor.setSelected(window.isVisible());
			}
		}
		return editor;
	}

	/**
	 * Returns the Widget or Window object associated with this VisualizationTreeCellEditor.
	 * 
	 *  @return		the Widget or Window object associated with this VisualizationTreeCellEditor
	 */
	public Object getCellEditorValue() {
		JCheckBox editor = (JCheckBox) (super.getComponent());

		if (widget != null) {
			if (widget.getWindow().isVisible()) {
				widget.setVisible(editor.isSelected());
			} else {
				widget.setVisibleFromWindow(false);
				editor.setSelected(widget.isVisible());
			}

			return widget;
		} else if (window != null) {
			window.setVisible(editor.isSelected());

			DefaultMutableTreeNode windowTreeNode = ((DefaultMutableTreeNode) tree.getPathForRow(row).getLastPathComponent());
			int x = windowTreeNode.getChildCount();
			for(int i = 0; i < x; i++) {
				DefaultMutableTreeNode windowChild = (DefaultMutableTreeNode) windowTreeNode.getChildAt(i);
				((DefaultTreeModel) tree.getModel()).reload(windowChild);
			}

			return window;
		}

		return null;
	}
}