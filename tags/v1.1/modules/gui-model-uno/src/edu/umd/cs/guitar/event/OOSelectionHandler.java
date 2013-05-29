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
import com.sun.star.accessibility.XAccessibleSelection;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.uno.UnoRuntime;

import edu.umd.cs.guitar.model.GComponent;


public class OOSelectionHandler extends OOEventHandler {

	/**
	 * 
	 */
	private static final int GUITAR_DEFAULT_SELECTED_VAL = 0;

	
	protected void actionPerformImpl(GComponent gComponent) {
		actionPerformImpl (gComponent, "50");
	}

	/*
	 * Select at a value
	 * 
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.event.AbstractEventHandler#actionPerform(com.sun.star.accessibility.XAccessible,
	 *      java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	
	protected void actionPerformImpl(GComponent gComponent, Object parameters) {
		
		XAccessible accessible = getAccessible(gComponent);
		
		if (!(parameters instanceof List))
			return;
		
		List<String> lParameters = (List<String>) parameters;

		try {
			String sChildName = lParameters.get(0);
			selectChildByName(accessible, sChildName);
			
		} catch (Exception e) {
			selectChildByID(accessible, GUITAR_DEFAULT_SELECTED_VAL);
		}

	}

	// protected void actionPerformImp(XAccessible accessible, Object
	// parameters) {
	//
	// int nRelativePostion = GUITAR_DEFAULT_SELECTED_VAL;
	//
	// if (parameters instanceof List) {
	//
	// List<String> lParameter = (List<String>) parameters;
	//
	// try {
	// System.out.println(lParameter.get(0));
	// nRelativePostion = Integer.parseInt(lParameter.get(0));
	// } catch (Exception e) {
	// nRelativePostion = GUITAR_DEFAULT_SELECTED_VAL;
	// }
	//
	// }
	//
	// XAccessibleContext xAccessibleContext = accessible
	// .getAccessibleContext();
	//
	// XAccessibleSelection xEvent = (XAccessibleSelection) UnoRuntime
	// .queryInterface(XAccessibleSelection.class, xAccessibleContext);
	//
	// // System.err.println(xAccessibleContext.getAccessibleDescription());
	//
	// if (xEvent != null) {
	//
	// try {
	// Integer nChildCount = xAccessibleContext
	// .getAccessibleChildCount();
	//
	// int nAbsolutePosition = (int) Math
	// .round(((nChildCount * nRelativePostion) / 100.0));
	//
	// System.out.println(nAbsolutePosition);
	// // System.out.println("nChildCount: "+nChildCount);
	//
	// xEvent.selectAccessibleChild(nAbsolutePosition);
	//
	// } catch (IndexOutOfBoundsException e) {
	// System.err.println("Cannot select");
	// e.printStackTrace();
	// }
	// } else {
	// System.err.println("No selection supported");
	// }
	// }

	/**
	 * Select child by its name
	 * 
	 * @param accessible
	 */
	private void selectChildByName(XAccessible accessible, String sName) {

		if (accessible == null)
			return;

		XAccessibleContext xContext = accessible.getAccessibleContext();

		if (xContext == null)
			return;

		Integer nChildCount = xContext.getAccessibleChildCount();

		for (int i = 0; i < nChildCount; i++) {
			try {
				XAccessible xChild;
				xChild = xContext.getAccessibleChild(i);
				if (xChild == null)
					continue;

				XAccessibleContext xChildContext = xChild
						.getAccessibleContext();

				if (xChildContext == null)
					continue;

				if (sName.equals(xChildContext.getAccessibleName())) {
					selectChildByID(accessible, i);
				}

			} catch (IndexOutOfBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void selectChildByID(XAccessible accessible, int id) {
		if (accessible == null)
			return;
		XAccessibleContext xAccessibleContext = accessible
				.getAccessibleContext();

		if (xAccessibleContext == null)
			return;

		XAccessibleSelection xEvent = (XAccessibleSelection) UnoRuntime
				.queryInterface(XAccessibleSelection.class, xAccessibleContext);
		if (xEvent == null)
			return;

		try {
			xEvent.selectAccessibleChild(id);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

    //@Override
	public void perform(GComponent gComponent, Object parameters,
			Hashtable<String, List<String>> optionalData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void perform(GComponent gComponent,
			Hashtable<String, List<String>> optionalData) {
		// TODO Auto-generated method stub
		
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