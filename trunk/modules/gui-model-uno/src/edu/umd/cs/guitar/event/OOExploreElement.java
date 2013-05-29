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

import com.qizx.apps.studio.gui.GUI;
import com.sun.star.accessibility.XAccessible;
import com.sun.star.accessibility.XAccessibleContext;
import com.sun.star.awt.XExtendedToolkit;
import com.sun.star.awt.XTopWindow;
import com.sun.star.awt.XWindow;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XInterface;

import edu.umd.cs.guitar.awb.SimpleOffice;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.util.OOConstants;

/**
 * Suppose to be replaced by the methods in OOo test runner Right now only used
 * for testing purpose
 * 
 * @author Bao Nguyen
 * 
 */
public class OOExploreElement {
	/**
	 * Get an object by its name, starting from root element
	 * 
	 * @param xRoot
	 * @param sName
	 * @return
	 */
	public static XAccessible getFirstXAccessibleFromName(XAccessible xRoot,
			String sName) {
		AbsCondition condition = new NameCondition(sName);
		return getFirstXAccessible(xRoot, condition);
	}

	public static XAccessible getFirstXAccessibleFromRole(XAccessible xRoot,
			int role) {
		AbsCondition condition = new RoleCondition(role);
		return getFirstXAccessible(xRoot, condition);
	}

	/**
	 * @param xRoot
	 * @param sName
	 * @param i
	 * @return
	 */
	public static XAccessible getFirstXAccessibleFromNameRole(
			XAccessible xRoot, String sName, int i) {
		AbsCondition condition = new NameRoleCondition(sName, i);
		return getFirstXAccessible(xRoot, condition);
	}
	
	public static XAccessible getFirstChildFromNameRole(
			XAccessible xRoot, String sName, int i) {
		AbsCondition condition = new NameRoleCondition(sName, i);
		return getFirstChild(xRoot, condition);
	}

	/**
	 * Find a top window from its name
	 * 
	 * @param sName
	 * @param xExtendedToolkit
	 * @return
	 */
	@Deprecated
	public static XTopWindow getTopWindowFromName(String sName,
			XExtendedToolkit tk) {

		// Search for pattern
		boolean isSearchPattern = false;
		if (sName.endsWith(GUITARConstants.NAME_PATTERN_SUFFIX)) {
			sName = sName.substring(0, sName.length() - 1);
			System.err.println("Pattern: *" + sName + "*");
			isSearchPattern = true;
		}

		System.out.println("Finding TopWindow");

		int count = tk.getTopWindowCount();
		XTopWindow xTopWindow = null;

		for (int i = 0; i < count; i++) {
			try {
				xTopWindow = tk.getTopWindow(i);
				XAccessible xAccessible = (XAccessible) UnoRuntime
						.queryInterface(XAccessible.class, xTopWindow);
				if (xAccessible == null)
					continue;
				XAccessibleContext xAccessibleContext = xAccessible
						.getAccessibleContext();

				if (xAccessibleContext == null)
					continue;
				String accName = xAccessibleContext.getAccessibleName();
				// System.out.println("*" + accName + "*");
				if (!isSearchPattern) {
					if (accName.equals(sName)) {
						return xTopWindow;
					}
				} else {
					if (accName.startsWith(sName)) {
						return xTopWindow;
					}
				}

			} catch (IndexOutOfBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	
	/**
	 * @param sName
	 * @param office
	 * @return
	 */
	public static XTopWindow getTopWindowFromName(String sName,
			SimpleOffice office) {

		XExtendedToolkit tk = office.getExtendedToolkit();
		
		// Search for pattern
		boolean isSearchPattern = false;
		if (sName.endsWith(GUITARConstants.NAME_PATTERN_SUFFIX)) {
			sName = sName.substring(0, sName.length() - 1);
			System.err.println("Pattern: *" + sName + "*");
			isSearchPattern = true;
		}

		System.out.println("Finding TopWindow");

		int count = tk.getTopWindowCount();
		XTopWindow xTopWindow = null;

		for (int i = 0; i < count; i++) {
			try {
				xTopWindow = tk.getTopWindow(i);
				XAccessible xAccessible = (XAccessible) UnoRuntime
						.queryInterface(XAccessible.class, xTopWindow);
				if (xAccessible == null)
					continue;
				XAccessibleContext xAccessibleContext = xAccessible
						.getAccessibleContext();

				if (xAccessibleContext == null)
					continue;
				String accName = xAccessibleContext.getAccessibleName();
				// System.out.println("*" + accName + "*");
				if (!isSearchPattern) {
					if (accName.equals(sName)) {
						return xTopWindow;
					}
				} else {
					if (accName.startsWith(sName)) {
						return xTopWindow;
					}
				}

			} catch (IndexOutOfBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	/**
	 * Helper function, finding an element in a BFS order
	 * 
	 * @param xRoot
	 * @param name
	 * @return
	 */
	private static XAccessible getFirstXAccessible(XAccessible xRoot,
			AbsCondition condition) {
		XAccessible result = null;

		// No root element
		if (xRoot == null)
			return null;

		XAccessibleContext xAccessibleContext = xRoot.getAccessibleContext();

		// No AccessibleContext element
		if (xAccessibleContext == null)
			return null;

		if (condition.isTrue(xAccessibleContext))
			return xRoot;
		else {
			int nChildren = xAccessibleContext.getAccessibleChildCount();
			for (int i = 0; i < nChildren; i++) {

				try {
					result = getFirstXAccessible(xAccessibleContext
							.getAccessibleChild(i), condition);

					if (result != null)
						return result;

				} catch (IndexOutOfBoundsException e) {
					result = null;
					e.printStackTrace();
					return result;
				}
			}
		}
		return result;
	}
	
	
	private static XAccessible getFirstChild(XAccessible xRoot,
			AbsCondition condition) {
		XAccessible result = null;

		// No root element
		if (xRoot == null)
			return null;

		XAccessibleContext xContext = xRoot.getAccessibleContext();

		// No AccessibleContext element
		if (xContext == null)
			return null;

		int nChildren = xContext.getAccessibleChildCount();
		for (int i = 0; i < nChildren; i++) {

			try {
				XAccessible child = xContext
						.getAccessibleChild(i);
				if (condition.isTrue(child.getAccessibleContext())){
					
//					System.err.println("FOUND!!!!"+child.getAccessibleContext().getAccessibleName());
					return child;
					
				}
				

			} catch (IndexOutOfBoundsException e) {
				result = null;
				e.printStackTrace();
				return result;
			}
		}

		return result;
	}
}
