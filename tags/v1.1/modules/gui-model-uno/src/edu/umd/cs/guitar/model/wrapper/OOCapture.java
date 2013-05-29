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

//import com.sun.star.accessibility.XAccessible;
import java.util.ArrayList;
import java.util.List;

import com.sun.star.accessibility.XAccessibleContext;
import com.sun.star.awt.XWindow;

import edu.umd.cs.guitar.model.data.*;


/**
 * Capture open office elements and convert to GUITAR format
 * @author Bao Nguyen
 *
 */
public class OOCapture{
    
    /**
     * Don't use this method for now 
     * Copy all GUI attributes of an element to a GUITAR Attributes array
     * 
     * @param xAccessibleContext
     * @return
     */
    public AttributesType XACToAttributes(XAccessibleContext xAccessibleContext){
    	AttributesType attributes = new AttributesType();
        
        List<PropertyType> lProperties  = new ArrayList<PropertyType>();

        PropertyType  property ;
        
        // Title
        property = new PropertyType();
        lProperties.add(property);
        property.setName(OOPropertiesName.TITLE);
        property.getValue().add(xAccessibleContext.getAccessibleName());
        
        return attributes ;
    }
    
    /**
     * Capture all widgets in a GUI 
     * @return
     */
    public GUIType extractGUI(XWindow xWindow){
    	GUIType result = new GUIType();
    	
    	return result; 
    	
    }

}
