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

import com.sun.star.accessibility.XAccessible;
import com.sun.star.accessibility.XAccessibleContext;
import com.sun.star.awt.XTopWindow;
import com.sun.star.uno.UnoRuntime;

import edu.umd.cs.guitar.awb.SimpleOffice;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GObject;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.OOXComponent;
import edu.umd.cs.guitar.model.OOXWindow;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.util.OOConstants;

/**
 * 
 * Don't try to expand the component but still record its information
 * 
 * <p>
 * 
 * @author Bao Nguyen
 * @author Wikum Dinalankara
 * 
 */
public class UNOIgnoreExpandFilter extends GComponentFilter {

	static GComponentFilter cmIgnoreMonitor = null;

	public synchronized static GComponentFilter getInstance() {
		if (cmIgnoreMonitor == null) {
			cmIgnoreMonitor = new UNOIgnoreExpandFilter();
		}
		return cmIgnoreMonitor;
	}

	private UNOIgnoreExpandFilter() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.ripper.ComponentFilter#isProcess(edu.umd.cs.guitar.model.GXComponent)
	 */
	@Override
	public boolean isProcess(GComponent gComponent, GWindow gWindow) {

		OOXComponent unoComponent = (OOXComponent) gComponent;
		OOXWindow unoWindow = (OOXWindow) gWindow;

		XAccessible accessible = unoComponent.getXAccessible();
		XTopWindow window = unoWindow.getXWindow();

		if (accessible == null)
			return true;

		XAccessibleContext xContext = accessible.getAccessibleContext();

		if (xContext == null)
			return true;

		// Ignore some special roles
		Short nRole = xContext.getAccessibleRole();
		String sWidgetName = xContext.getAccessibleName();

		// Don't use for now as we are ignoring widget based on name
		XAccessible xWindow = (XAccessible) UnoRuntime.queryInterface(
				XAccessible.class, window);

		String sWindowName = xWindow.getAccessibleContext().getAccessibleName();

		String sFullName = sWindowName + GUITARConstants.NAME_SEPARATOR
				+ sWidgetName;
		String sFullNameRole = sFullName + GUITARConstants.NAME_SEPARATOR + nRole;
		/*
		// rip = Rip these titles, ignore everything else
		String[] rip = {};
		if ( sWidgetName.isEmpty() )
			return false;
		for( String s : rip ){
			if ( sWidgetName.contains(s) ){
				return false;
			}
		}
		return true;
		*/
		
		System.out.println("------ IgnoreExpandFilter  : [" + sWidgetName + "] [" + sFullName + "] [" + sFullNameRole + "] ");
		
		if (OOConstants.sIgnoreWidgetList.contains(sWidgetName))
			return true;

		if (OOConstants.sIgnoreWidgetList.contains(sFullName))
			return true;

		if (OOConstants.sIgnoreWidgetList.contains(sFullNameRole))
			return true;
		
		return false;
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.ripper.ComponentMonitor#ripComponent(edu.umd.cs.guitar.model.GXComponent)
	 */

	@Override
	public ComponentType ripComponent(GComponent component, GWindow window) {
		return component.extractProperties(); //extractComponent();
	}
}
