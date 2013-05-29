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
package edu.umd.cs.guitar.model;

import java.util.List;

import com.sun.star.accessibility.AccessibleStateType;
import com.sun.star.accessibility.XAccessible;
import com.sun.star.accessibility.XAccessibleContext;
import com.sun.star.accessibility.XAccessibleStateSet;
import com.sun.star.awt.XTopWindow;
import com.sun.star.awt.XWindow;
import com.sun.star.uno.UnoRuntime;

import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.wrapper.OOExtractGUIProperties;
import edu.umd.cs.guitar.ripper.Debugger;
import edu.umd.cs.guitar.util.OOConstants;

/**
 * 
 * A wrapper class handling windows in Open Office
 * 
 * <p>
 * 
 * @author Bao Nguyen
 * 
 */
public class OOXWindow extends GWindow {

	XTopWindow xWindow;

	/**
	 * @param window
	 */
	public OOXWindow(XTopWindow window) {
		super();
		xWindow = window;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXWindow#extractWindowInfo()
	 */
	public GUIType extractGUIProperties() {

		// Check for the accessibility
		XAccessible xAccessible = (XAccessible) UnoRuntime.queryInterface(
				XAccessible.class, xWindow);

		if (xAccessible == null) {
			return null;
		}
		XAccessibleContext xWindowContext = xAccessible.getAccessibleContext();

		if (xWindowContext == null) {
			return null;
		}

		XWindow xWin = (XWindow) UnoRuntime.queryInterface(XWindow.class,
				xWindow);

		if (xWin != null)
			xWin.setFocus();

		GUIType retGUI;
		retGUI = OOExtractGUIProperties.extractGUI(xWindow, isRoot);

		return retGUI;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXWindow#getComponent()
	 */
	@Override
	public GComponent getContainer() {

		XAccessible xAccessible = (XAccessible) UnoRuntime.queryInterface(
				XAccessible.class, xWindow);

		if (xAccessible == null) {
			return null;
		}
		XAccessibleContext xNewWindowContext = xAccessible
				.getAccessibleContext();

		if (xNewWindowContext == null) {
			return null;
		}

		return new OOXComponent(xAccessible, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXObject#getID()
	 */
	
	public String getID() {

		XAccessible xAccessible = (XAccessible) UnoRuntime.queryInterface(
				XAccessible.class, xWindow);
		if (xAccessible == null)
			return null;
		XAccessibleContext xContext = xAccessible.getAccessibleContext();

		if (xContext == null)
			return null;

		String id = xContext.getAccessibleName() + GUITARConstants.NAME_SEPARATOR
				+ xContext.getAccessibleRole();

		return id;
	}

	/**
	 * @return the xWindow
	 */
	public XTopWindow getXWindow() {
		return xWindow;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXWindow#isModal()
	 */
	@Override
	public boolean isModal() {
		// Check for the accessibility
		XAccessible xAccessible = (XAccessible) UnoRuntime.queryInterface(
				XAccessible.class, xWindow);
		XAccessibleContext xContext = xAccessible.getAccessibleContext();
		XAccessibleStateSet xStateSet = xContext.getAccessibleStateSet();

		if (xStateSet != null) {
			short aStates[] = xStateSet.getStates();
			int nStates = aStates.length;
			for (short i = 0; i < nStates; i++) {
				Debugger.println("----------------------");
				Debugger.println(aStates[i]);
				if (aStates[i] == AccessibleStateType.MODAL)
					return true;
			}
		}

		return false;

	}

	/* (non-Javadoc)
	 * @see edu.umd.cs.guitar.model.GXWindow#getGUIProperties()
	 */
	@Override
	public List<PropertyType> getGUIProperties() {
		// TODO Auto-generated method stub
		return null;
	}
	
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object window){
    	
        if(! (window instanceof OOXWindow))
            return false;
        
        OOXWindow ooWindow = (OOXWindow ) window;
        return this.getTitle().equals(ooWindow.getTitle());
    }

	@Override
	public String getTitle() {
		XAccessible xAccessible = (XAccessible) UnoRuntime.queryInterface(
				XAccessible.class, xWindow);
		if (xAccessible == null)
			return null;
		XAccessibleContext xContext = xAccessible.getAccessibleContext();

		if (xContext == null)
			return null;

		return (xContext.getAccessibleName());
	}

	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}
}
