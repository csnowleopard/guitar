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

import java.util.Arrays;
import java.util.List;

import com.sun.star.accessibility.AccessibleRole;
import com.sun.star.accessibility.XAccessible;
import com.sun.star.accessibility.XAccessibleContext;
import com.sun.star.uno.UnoRuntime;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.OOXComponent;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.util.OOConstants;

/**
 * 
 * Ignore the component and don't record its GUI information This filter is used
 * when we don't want to record the details GUI information which increase the
 * size GUI file
 * 
 * <p>
 * 
 * @author Bao Nguyen
 * @author Wikum Dinalankara
 * 
 */
public class UNOIgnoreRipFilter extends GComponentFilter {

	static GComponentFilter cmIgnoreMonitor = null;

	public synchronized static GComponentFilter getInstance() {
		if (cmIgnoreMonitor == null) {
			cmIgnoreMonitor = new UNOIgnoreRipFilter();
		}
		return cmIgnoreMonitor;
	}

	List<Short> IGNORE_WIDGET_ROLES = Arrays.asList(AccessibleRole.LIST_ITEM,
			AccessibleRole.TABLE_CELL);

	/**
	 * 
	 */
	public UNOIgnoreRipFilter() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.ripper.ComponentFilter#isProcess(edu.umd.cs.guitar.model.GXComponent,
	 *      edu.umd.cs.guitar.model.GXWindow)
	 */
	@Override
	public boolean isProcess(GComponent component, GWindow window) {

		OOXComponent unoComponent = (OOXComponent) component;
		XAccessible xAccessible = unoComponent.getXAccessible();

		if (xAccessible == null)
			return false;
		XAccessibleContext xContext = xAccessible.getAccessibleContext();

		try{
			// Ignore some special roles
			Short nRole = xContext.getAccessibleRole();

			if (IGNORE_WIDGET_ROLES.contains(nRole))
				return true;
		
			// -- Added
			String sWidgetName = xContext.getAccessibleName();
			XAccessible xWindow = (XAccessible) UnoRuntime.queryInterface(
					XAccessible.class, window);
			if (OOConstants.sIgnoreWidgetList.contains(sWidgetName))
				return true;
			
			String sWindowName = xWindow.getAccessibleContext().getAccessibleName();
	
			String sFullName = sWindowName + GUITARConstants.NAME_SEPARATOR
					+ sWidgetName;
			String sFullNameRole = sFullName + GUITARConstants.NAME_SEPARATOR + nRole;
	
			if (OOConstants.sIgnoreWidgetList.contains(sFullName))
				return true;
	
			if (OOConstants.sIgnoreWidgetList.contains(sFullNameRole))
				return true;
			// --- End Added
		}catch (Exception ex){}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.ripper.ComponentFilter#ripComponent(edu.umd.cs.guitar.model.GXComponent,
	 *      edu.umd.cs.guitar.model.GXWindow)
	 */
	@Override
	public ComponentType ripComponent(GComponent component, GWindow window) {
		return null;
	}

}
