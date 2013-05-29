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
package edu.umd.cs.guitar.model.wrapper;

import java.util.ArrayList;
import java.util.List;

import com.sun.star.accessibility.XAccessible;
import com.sun.star.accessibility.XAccessibleStateSet;
import com.sun.star.container.XIndexAccess;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.UnoRuntime;

import edu.umd.cs.guitar.model.data.PropertyType;

/**
 * 
 * Query all states of an object
 * 
 * @author Bao Nguyen
 * 
 */

public class OOTTPStateSet extends OOAbsTypeToProperties {

	public OOTTPStateSet(XAccessible accessible) {
		super(accessible);
	}

	@Override
	public List<PropertyType> getProperties() {
		List<PropertyType> result = new ArrayList<PropertyType>();
		XAccessibleStateSet xStateSet = xAccessibleContext
				.getAccessibleStateSet();

		if (xStateSet != null) {
			
			
			
			PropertyType p = new PropertyType();
			result.add(p);

			String sState = "State";
			p.setName(sState);
//			
//			 XIndexAccess xStates = 
//                 (XIndexAccess) UnoRuntime.queryInterface (
//                     XIndexAccess.class, xStateSet);
//             for (int i=0; i<xStates.getCount(); i++)
//             {
//                 if (i > 0)
//					try {
//						p.getValue().add((xStates.getByIndex(i).toString()));
//					} catch (IndexOutOfBoundsException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (WrappedTargetException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//             }

			short aStates[] = xStateSet.getStates();

			int nStates = aStates.length;
			for (int i = 0; i < nStates; i++) {
				p.getValue().add(Integer.toString(aStates[i]));
			}
		}
		return result;
	}

}
