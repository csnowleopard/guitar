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

import com.sun.star.accessibility.AccessibleRole;
import com.sun.star.accessibility.XAccessible;
import com.sun.star.accessibility.XAccessibleContext;
import com.sun.star.accessibility.XAccessibleSelection;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.uno.UnoRuntime;

import edu.umd.cs.guitar.event.OOSelectFromParentHandler;
//import edu.umd.cs.guitar.model.ComponentTypeAdapter;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.OOXComponent;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.wrapper.OOExtractGUIProperties;
//import edu.umd.cs.guitar.model.process.OOExtractGUIProperties;
import edu.umd.cs.guitar.util.Log;
import edu.umd.cs.guitar.util.GUITARLog;
import edu.umd.cs.guitar.util.OOConstants;

/**
 * A special filter to ripping tab component in OOo.
 * 
 * <p> 
 * 
 * @author Bao Nguyen
 * @author Wikum Dinalankara
 */
public class UNOTabFilter extends GComponentFilter {

	static GComponentFilter cmIgnoreMonitor = null;

	public synchronized static GComponentFilter getInstance() {
		if (cmIgnoreMonitor == null) {
			cmIgnoreMonitor = new UNOTabFilter();
		}
		return cmIgnoreMonitor;
	}
	
	private UNOTabFilter(){}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.ripper.ComponentFilter#isProcess(edu.umd.cs.guitar.model.GXComponent)
	 */
	@Override
	public boolean isProcess(GComponent component,  GWindow window) {
		
		OOXComponent ooComponent = (OOXComponent )component;
		XAccessible xAccessible = ooComponent.getXAccessible();
		
			if (xAccessible == null)
				return false;
			XAccessibleContext xAccessibleContext = xAccessible .getAccessibleContext();

			if (xAccessibleContext.getAccessibleRole() == (AccessibleRole.PAGE_TAB_LIST))
				return true;
			return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.ripper.ComponentFilter#ripComponent(edu.umd.cs.guitar.model.GXComponent)
	 */
	@Override
	public ComponentType ripComponent(GComponent component,  GWindow window) {
		
		// Commented
		//Logger log = ripper.getLog(); 
		//log.println("Ripping Tab Panel...");
		ComponentType retComp = null;
		OOXComponent unoComponent = (OOXComponent )component;
		XAccessible xPanelRoot = unoComponent.getXAccessible();

		if (xPanelRoot == null) {
			// Commented
			//ripper.getLog().println("Tab panel unaccessible");
			return null;
		}

		retComp = OOExtractGUIProperties.extractComponent(xPanelRoot);

		// -------------------------
		// Selecting tab

		XAccessibleContext xPanelContext = xPanelRoot.getAccessibleContext();

		XAccessibleSelection xSelection = (XAccessibleSelection) UnoRuntime
				.queryInterface(XAccessibleSelection.class, xPanelContext);

		if (xSelection == null) {
			System.out.println("XSection is not supported");
			return null;
		}

		int nTabItem = xPanelContext.getAccessibleChildCount();
		System.err.println("nTabItem: " + nTabItem);

		//UNOConstants
		// Commented
		/*
		for (int i = 0; i < nTabItem; i++) {
			try {
				// Commented
				//log.println("Selecting tab #" + i);
				xSelection.selectAccessibleChild(i);
				Thread.sleep(OOConstants.DELAY * 2);

				XAccessible xTabContent = xPanelContext.getAccessibleChild(i);

				if (OOConstants.sIgnoreTabList.contains(xTabContent.getAccessibleContext()
						.getAccessibleName())) {
					continue;
				}
				
				GComponent gTab = new OOXComponent(xTabContent);
				
				ComponentType guiChild = ripper.ripComponent(gTab, window);
				
				ComponentTypeAdapter guiChildAdapter = new ComponentTypeAdapter(guiChild);
				guiChildAdapter.addValueByName(GUITARConstants.EVENT_TAG_NAME, OOSelectFromParentHandler.class.getName());
					
				((ContainerType) retComp).getContents().getWidgetOrContainer()
						.add(guiChild);

			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
		return retComp;
	}

}
