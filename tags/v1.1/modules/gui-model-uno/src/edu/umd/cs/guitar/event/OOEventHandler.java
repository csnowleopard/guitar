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

import com.sun.star.accessibility.XAccessible;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.OOXComponent;

/**
 * Abstraction class for all Open Office events in GUITAR
 * 
 * @author Bao Nguyen
 * 
 */
//public abstract class OOEventHandler implements AbsEventHandler {
public abstract class OOEventHandler extends GThreadEvent {
	
	
	/**
	 * Get XAccessible from a GXComponent
	 * 
	 * <p>
	 *  
	 * @param gComponent
	 * @return
	 */
	protected XAccessible getAccessible(GComponent gComponent){
		return ((OOXComponent)gComponent).getXAccessible();
	}
	

	/**
	 * Do the action available on an object This method moves the action to
	 * another thread and calls the actual action of the derived class.
	 * 
	 * <p>
	 * 
	 * @param xAccessible
	 * @param parameter
	 */
	
//	public final void actionPerform(XAccessible xAccessible, Object parameter) {
//		Thread t = new ExpandThreadHelperParameter(xAccessible, parameter);
//		t.start();
//	}

//	/* (non-Javadoc)
//	 * @see edu.umd.cs.guitar.event.AbsEventHandler#actionPerform(edu.umd.cs.guitar.model.GXComponent, java.lang.Object)
//	 */
//	public final void actionPerform(GXComponent gComponent, Object parameter) {
//		OOXComponent ooComponent = (OOXComponent) gComponent;
//		XAccessible xAccessible = ooComponent.getXAccessible();
//
//		Thread t = new ExpandThreadHelperParameter(xAccessible, parameter);
//		t.start();
//	}
//
//	/**
//	 * Helper class for thread creation with parameters
//	 * 
//	 * <p>
//	 * 
//	 * @author Bao Nguyen
//	 * 
//	 */
//	private class ExpandThreadHelperParameter extends Thread {
//		XAccessible xAccessible;
//		Object parameter;
//
//		/**
//		 * @param accessible
//		 * @param parameter
//		 */
//		public ExpandThreadHelperParameter(XAccessible accessible,
//				Object parameter) {
//			super();
//			xAccessible = accessible;
//			this.parameter = parameter;
//		}
//
//		public void run() {
//			actionPerformImp(xAccessible, parameter);
//		}
//	}

//	/**
//	 * @param xAccessible
//	 */
//	public final void actionPerform(XAccessible xAccessible) {
//
//		Thread t = new ExpandThreadHelper(xAccessible);
//		t.start();
//	}

//	/* (non-Javadoc)
//	 * @see edu.umd.cs.guitar.event.AbsEventHandler#actionPerform(edu.umd.cs.guitar.model.GXComponent)
//	 */
//	public final void actionPerform(GXComponent gComponent) {
//
//		OOXComponent ooComponent = (OOXComponent) gComponent;
//		XAccessible xAccessible = ooComponent.getXAccessible();
//
//		Thread t = new ExpandThreadHelper(xAccessible);
//		t.start();
//	}

//	/**
//	 * @author Bao Nguyen
//	 * 
//	 */
//	private class ExpandThreadHelper extends Thread {
//		XAccessible xAccessible;
//
//		/**
//		 * @param accessible
//		 * @param parameters
//		 */
//		public ExpandThreadHelper(XAccessible accessible) {
//			super();
//			xAccessible = accessible;
//		}
//
//		public void run() {
//			actionPerformImp(xAccessible);
//		}
//	}

//	/**
//	 * The actual method to perform action
//	 * 
//	 * <p>
//	 * 
//	 * @param xAccessible
//	 */
//	@Override 
//	protected abstract void actionPerformImp(GXComponent gComponent);
//
//	/**
//	 * The actual method to perform action with parameter
//	 * 
//	 * <p>
//	 * 
//	 * @param xAccessible
//	 */
//	@Override 
//	protected abstract void actionPerformImp(GXComponent gComponent,
//			Object parameters);
}
