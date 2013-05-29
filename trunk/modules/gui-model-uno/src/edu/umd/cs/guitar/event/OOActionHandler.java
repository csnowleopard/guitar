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
package edu.umd.cs.guitar.event;

import java.util.Hashtable;
import java.util.List;

import com.sun.star.accessibility.XAccessible;
import com.sun.star.accessibility.XAccessibleAction;
import com.sun.star.accessibility.XAccessibleContext;
import com.sun.star.awt.XTopWindow;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.uno.UnoRuntime;

import edu.umd.cs.guitar.model.GComponent;

import edu.umd.cs.guitar.model.OOXComponent;

/**
 * Handle the click event
 * 
 * @author Bao Nguyen
 * 
 */
public class OOActionHandler extends OOEventHandler {

	// private static AbsOOEventHandler SINGLETON_INSTANCE = null;
	//
	// public static AbsOOEventHandler getInstance() {
	// if (SINGLETON_INSTANCE == null) {
	// SINGLETON_INSTANCE = new OOActionHandler();
	// }
	// return SINGLETON_INSTANCE;
	// }
	public OOActionHandler() {
	}

	protected void actionPerformImpl(GComponent gComponent) {

		XAccessible xAccessible = getAccessible(gComponent);

		XAccessibleContext xAccessibleContext = xAccessible
				.getAccessibleContext();
		if (xAccessibleContext == null)
			return;

		XAccessibleAction xAction = (XAccessibleAction) UnoRuntime
				.queryInterface(XAccessibleAction.class, xAccessibleContext);

		if (xAction == null)
			return;
		try {
			int nActions = xAction.getAccessibleActionCount();
			for (int j = 0; j < nActions; j++) {
				xAction.doAccessibleAction(j);
			}
		} catch (IndexOutOfBoundsException e) {
			System.err.println("Cannot click");
			e.printStackTrace();
		}
	}

	protected void actionPerformImpl(GComponent gComponent, Object parameters) {
		// Just ignore the parameters
		actionPerformImpl(gComponent);
	}

	// @Override
	public void perform(GComponent gComponent, Object parameters,
			Hashtable<String, List<String>> optionalData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void perform(GComponent gComponent,
			Hashtable<String, List<String>> optionalData) {
		// TODO Auto-generated method stub
		actionPerformImpl(gComponent);
	}

	@Override
	public boolean isSupportedBy(GComponent gComponent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void performImpl(GComponent gComponent,
			Hashtable<String, List<String>> optionalData) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void performImpl(GComponent gComponent, Object parameters,
			Hashtable<String, List<String>> optionalData) {
		// TODO Auto-generated method stub

	}

}
