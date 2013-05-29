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
package edu.umd.cs.guitar.ripper;

import java.util.Iterator;
import java.util.LinkedList;

import com.sun.star.accessibility.XAccessible;
import com.sun.star.accessibility.XAccessibleContext;
import com.sun.star.awt.XTopWindow;
import com.sun.star.uno.UnoRuntime;

/**
 * 
 * TODO:
 * 
 * @author Bao Nguyen
 * 
 */
class WinStack {

	LinkedList<XTopWindow> data = new LinkedList<XTopWindow>();

	private boolean lock = true;

	/**
	 * @return the lock
	 */
	public boolean isLock() {
		return lock;
	}

	/**
	 * @param lock
	 *            the lock to set
	 */
	public void setLock(boolean lock) {
		this.lock = lock;
	}

	public void push(XTopWindow window) {
		if (!lock)
			this.data.addLast(window);
	}

	public void remove(XTopWindow window) {
		this.data.remove(window);
	}

	public XTopWindow pop() {
		if (!lock)
			return this.data.getLast();
		else
			return null;
	}

	public void reset() {
		this.data.clear();
	}

	public boolean isEmpty() {
		return (data.size() == 0);
	}

	/**
	 * @return the data
	 */
	public LinkedList<XTopWindow> getData() {
		cleanUp();
		return data;
	}

	public void cleanUp() {
		for (XTopWindow win : data) {
			if (!isValidWin(win, data))
				data.remove(win);
		}
	}

	private boolean isValidWin(XTopWindow win, LinkedList<XTopWindow> list) {

		XAccessible xWin = (XAccessible) UnoRuntime.queryInterface(
				XAccessible.class, win);

		if (xWin == null) {
			return false;
		}

		XAccessibleContext xWinContext = xWin.getAccessibleContext();

		if (xWinContext == null)
			return false;

		String sWinTitle = xWinContext.getAccessibleName();
		short nWinRole = xWinContext.getAccessibleRole();

		for (XTopWindow aWin : list) {

			if (aWin.equals(win))
				continue;

			XAccessible xCapturedWin = (XAccessible) UnoRuntime.queryInterface(
					XAccessible.class, aWin);

			if (xCapturedWin == null)
				continue;

			XAccessibleContext xSubWinContext = xCapturedWin
					.getAccessibleContext();

			if (xSubWinContext == null)
				continue;
			String sSubWinTitle = xSubWinContext.getAccessibleName();
			short nSubWinRole = xSubWinContext.getAccessibleRole();

			if (nSubWinRole == nWinRole && sWinTitle.equals(sSubWinTitle))
				return false;
		}
		return true;
	}
}
