/*	
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in all copies or substantial 
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.ripper.filter;

import java.util.List;

import com.sun.star.accessibility.AccessibleRole;
import com.sun.star.accessibility.XAccessible;
import com.sun.star.accessibility.XAccessibleContext;
import com.sun.star.accessibility.XAccessibleSelection;
import com.sun.star.awt.XTopWindow;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.uno.UnoRuntime;

import edu.umd.cs.guitar.event.OOActionHandler;
import edu.umd.cs.guitar.event.OOEventHandler;
import edu.umd.cs.guitar.event.OOExploreElement;
//import edu.umd.cs.guitar.event.OOSelectFromParentHandler;
import edu.umd.cs.guitar.model.ComponentTypeAdapter;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.OOXComponent;
import edu.umd.cs.guitar.model.OOXWindow;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.wrapper.OOExtractGUIProperties;
//import edu.umd.cs.guitar.model.process.OOExtractGUIProperties;
import edu.umd.cs.guitar.util.Log;
import edu.umd.cs.guitar.util.OOConstants;

/**
 * @author Bao Nguyen
 * @author Wikum Dinalankara
 * 
 */
public class UNOTreeFilter extends GComponentFilter {

	static GComponentFilter cmIgnoreMonitor = null;

	public synchronized static GComponentFilter getInstance() {
		if (cmIgnoreMonitor == null) {
			cmIgnoreMonitor = new UNOTreeFilter();
		}
		return cmIgnoreMonitor;
	}

	private UNOTreeFilter() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.guitar.ripper.ComponentFilter#isProcess(edu.umd.cs.guitar.
	 * model.GXComponent, edu.umd.cs.guitar.model.GXWindow)
	 */
	@Override
	public boolean isProcess(GComponent component, GWindow window) {

		OOXComponent unoComponent = (OOXComponent) component;
		XAccessible xAccessible = unoComponent.getXAccessible();

		if (xAccessible == null)
			return false;

		XAccessibleContext xAccessibleContext = xAccessible
				.getAccessibleContext();

		// TODO:
		XAccessible parent = xAccessibleContext.getAccessibleParent();
		if (parent == null)
			return false;

		if (xAccessibleContext.getAccessibleRole() == (AccessibleRole.TREE)
				&& parent.getAccessibleContext().getAccessibleName()
						.startsWith("Options")) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.guitar.ripper.ComponentFilter#ripComponent(edu.umd.cs.guitar
	 * .model.GXComponent, edu.umd.cs.guitar.model.GXWindow)
	 */
	@Override
	public ComponentType ripComponent(GComponent gComponent, GWindow gWindow) {

		// Commented
		//Log log = ripper.getLog();

		ComponentType retComp = null;
		OOXComponent unoComponent = (OOXComponent) gComponent;
		XAccessible xTreeRoot = unoComponent.getXAccessible();

		OOXWindow unoWindow = (OOXWindow) gWindow;
		XTopWindow xWindow = unoWindow.getXWindow();

		if (xTreeRoot == null) {
			//log.println("Not accessible ");
			return null;
		}

		XAccessibleContext xTreeRootContext = xTreeRoot.getAccessibleContext();

		// xParent is the container of the current tree which contains the view
		// container in the right
		XAccessible xParent = xTreeRootContext.getAccessibleParent();

		retComp = ripTreeNode(xParent, xTreeRoot, xWindow);
		return retComp;
	}

	/**
	 * Recursively extend and rip the tree nodes
	 * 
	 * @param xTreeRootParent
	 * @param xNode
	 * @return
	 */

	private ComponentType ripTreeNode(XAccessible xTreeRootParent,
			XAccessible xNode, XTopWindow xWindow) {

		ComponentType retComp = null;
		if (xNode == null) {
			System.out.println("Node is unaccessible");
			return null;
		}

		System.err.println("Ripping ripTreeNode");

		retComp = OOExtractGUIProperties.extractContainer(xNode);

		List<ComponentType> retCompContainer = ((ContainerType) retComp)
				.getContents().getWidgetOrContainer();

		XAccessibleContext xNodeContext = xNode.getAccessibleContext();
		String sNodeName = xNodeContext.getAccessibleName();

		if (OOConstants.sIgnoreTreeNodeList.contains(sNodeName)) {
			System.out.println("IGNORE Tree node " + sNodeName);
			return null;
		}

		System.err.println("Tree node name: *" + sNodeName + "*");
		System.out.println("Tree node name: *" + sNodeName + "*");
		// Click to expand the tree node
		OOEventHandler eClick = new OOActionHandler();
		// AbsUNOEventHandler eClick = UNOActionHandler.getInstance();

		// Commented
		//GComponent gNode = new OOXComponent(xNode);
		//eClick.actionPerform(gNode);

		try {
			Thread.sleep(OOConstants.DELAY);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		int nIndexInParent = xNodeContext.getAccessibleIndexInParent();

		XAccessible xParent = xNodeContext.getAccessibleParent();
		XAccessibleContext xParentContext = xParent.getAccessibleContext();

		// Try to find parent and select the node itself
		XAccessibleSelection xSelection = (XAccessibleSelection) UnoRuntime
				.queryInterface(XAccessibleSelection.class, xParentContext);

		// -------------------------
		// Select tree node and rip the corresponding panel
		if (xSelection != null
				&& xNodeContext.getAccessibleRole() == AccessibleRole.LABEL) {
			try {

				System.err.println("Selecting "
						+ xParentContext.getAccessibleChild(nIndexInParent)
								.getAccessibleContext().getAccessibleName());
				xSelection.selectAccessibleChild(nIndexInParent);

				try {
					Thread.sleep(OOConstants.DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				XAccessible xContainer = getTreeContainer(xTreeRootParent,
						xNode);

				if (xContainer != null) {
					System.out.println("Corresponding panel FOUND:");

					// Commented
					/*
					GComponent gContainer = new OOXComponent(xContainer);
					GWindow gWindow = new OOXWindow(xWindow);

					ComponentType rightPanel = ripper.ripComponent(gContainer,
							gWindow);
					retCompContainer.add(rightPanel);
					*/

				} else {
					System.out.println("Corresponding panel NOT  found");
				}

			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}

		int nTreeItem = xNodeContext.getAccessibleChildCount();
		for (int i = 0; i < nTreeItem; i++) {

			try {
				ComponentType treeNodeContainer = ripTreeNode(xTreeRootParent,
						xNodeContext.getAccessibleChild(i), xWindow);

				if (treeNodeContainer != null) {

					// Commented
					/*
					ComponentTypeAdapter treeNodeContainerAdapter = new ComponentTypeAdapter(
							treeNodeContainer);
					treeNodeContainerAdapter.addValueByName(
							GUITARConstants.EVENT_TAG_NAME,
							OOSelectFromParentHandler.class.getName());
					*/
				}

				retCompContainer.add(treeNodeContainer);

			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}

		}

		return retComp;

	}

	/**
	 * 
	 * Find the container corresponding to a tree node
	 * 
	 * @param xTreeRootParent
	 * @return
	 */
	private XAccessible getTreeContainer(XAccessible xTreeRootParent,
			XAccessible xNode) {

		XAccessible xContainer = null;

		XAccessibleContext xNodeContext = xNode.getAccessibleContext();
		String sNodeName = xNodeContext.getAccessibleName();

		xContainer = OOExploreElement.getFirstChildFromNameRole(
				xTreeRootParent, sNodeName, AccessibleRole.PANEL);

		// Try one more time for an empty name panel
		if (xContainer == null) {

			xContainer = OOExploreElement.getFirstChildFromNameRole(
					xTreeRootParent, "", AccessibleRole.PANEL);
		}

		return xContainer;
	}

}
