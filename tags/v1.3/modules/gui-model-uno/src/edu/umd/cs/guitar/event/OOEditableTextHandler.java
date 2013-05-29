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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.sun.star.accessibility.XAccessible;
import com.sun.star.accessibility.XAccessibleAction;
import com.sun.star.accessibility.XAccessibleContext;
import com.sun.star.accessibility.XAccessibleEditableText;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.uno.UnoRuntime;

import edu.umd.cs.guitar.model.GComponent;



/**
 * 
 * Handle the text entering event 
 * 
 * @author Bao Nguyen
 *
 */
public class OOEditableTextHandler extends OOEventHandler {

	/**
	 * 
	 */
	private static final String GUITAR_DEFAULT_TEXT = "GUITAR DEFAULT TEXT";

	
	public void actionPerformImpl(GComponent gComponent) {
		
		XAccessible accessible = getAccessible(gComponent);
		List<String> args = new ArrayList<String>();
		args.add(GUITAR_DEFAULT_TEXT);
		actionPerformImpl(gComponent,args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.event.AbstractEventHandler#actionPerformImp(com.sun.star.accessibility.XAccessible,
	 *      java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	
	protected void actionPerformImpl(GComponent gComponent, Object parameters) {
		if (parameters instanceof List) {
			
			XAccessible xAccessible =getAccessible(gComponent);

			List<String> lParameter = (List<String>) parameters;
			String sInputText = (lParameter.size() != 0) ? lParameter.get(0)
					: GUITAR_DEFAULT_TEXT;

			XAccessibleContext xAccessibleContext = xAccessible
					.getAccessibleContext();

			if (xAccessibleContext == null){
				System.err.println("XContext doesn't support");
				return;
				
			}
				

			XAccessibleEditableText xText = (XAccessibleEditableText) UnoRuntime
					.queryInterface(XAccessibleEditableText.class, xAccessibleContext);
			
			if(xText==null){
				System.err.println(this.getClass().getName() + " doesn't support");
				return;
			}
			
			xText.setText(sInputText);
//			System.out.println(sInputText);
//			try {
//				xText.insertText(sInputText,0);
//			} catch (IndexOutOfBoundsException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			

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
