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
import com.sun.star.accessibility.XAccessibleAction;
import com.sun.star.accessibility.XAccessibleEditableText;
import com.sun.star.accessibility.XAccessibleSelection;
import com.sun.star.accessibility.XAccessibleValue;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XInterface;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.util.OOConstants;



/**
 * This class is used for the old GUI format where all events are put under
 * the ReplayableAction name
 * @author Bao Nguyen
 *
 */
public class OOTTPEvent extends OOAbsTypeToProperties {

	/**
	 * @param accessible
	 */
	public OOTTPEvent(XAccessible accessible) {
		super(accessible);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see edu.umd.cs.guitar.model.process.OOAbsTypeToProperties#getProperties()
	 */
	@Override
	public List<PropertyType> getProperties() {
        List<PropertyType> result = new ArrayList<PropertyType>();
        
        PropertyType p = new PropertyType();
        result.add(p);
        
        String sReplayableAction = GUITARConstants.EVENT_TAG_NAME;
        p.setName(sReplayableAction);
        
        XInterface xInterface;
        
        // XAccessibleAction
        xInterface= (XAccessibleAction) UnoRuntime
                .queryInterface(XAccessibleAction.class, xAccessibleContext);
        if (xInterface!= null) {
        	p.getValue().add(OOConstants.ACTION_EVENT);
        }
        
        
        // XAccessibleEditableText
        xInterface= (XAccessibleEditableText) UnoRuntime
                .queryInterface(XAccessibleEditableText.class, xAccessibleContext);
        if (xInterface!= null) {
        	p.getValue().add(OOConstants.TEXT_EVENT);
        }
        
        // XAccessibleSelection
        xInterface= (XAccessibleSelection) UnoRuntime
                .queryInterface(XAccessibleSelection.class, xAccessibleContext);
        if (xInterface!= null) {
        	p.getValue().add(OOConstants.SELECT_EVENT);
        }
        
        
        // XAccessibleValue
        xInterface= (XAccessibleValue) UnoRuntime
                .queryInterface(XAccessibleValue.class, xAccessibleContext);
        if (xInterface!= null) {
        	p.getValue().add(OOConstants.VALUE_EVENT);
        }
         
        return (p.getValue().size()==0)? null: result;
        //return result;
	}

}
