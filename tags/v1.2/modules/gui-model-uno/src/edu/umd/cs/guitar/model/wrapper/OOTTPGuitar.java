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

//import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.*;
import com.sun.star.accessibility.XAccessible;

import edu.umd.cs.guitar.awb.NameProvider;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.util.OOConstants;
import edu.umd.cs.guitar.util.EventType;

/**
 * 
 * Extract properties required by GUITAR for test case generation
 * 
 * @author Bao Nguyen
 * 
 */
public class OOTTPGuitar extends OOAbsTypeToProperties {

	

	/**
	 * @param accessible
	 */
	public OOTTPGuitar(XAccessible accessible) {
		super(accessible);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.process.OOAbsTypeToProperties#getProperties()
	 */
	@Override
	public List<PropertyType> getProperties() {
		List<PropertyType> retProperties = null;

		// XAccessibleContext xAccessibleContext = (XAccessibleContext)
		// UnoRuntime
		// .queryInterface(XAccessibleContext.class, xAccessibleContext);

		if (xAccessibleContext != null) {
			retProperties = new ArrayList<PropertyType>();

			String sID = getIDVal();
			PropertyType pID = 
				createSingleProperty(GUITARConstants.TITLE_TAG_NAME, sID);
			retProperties.add(pID);

			String sClass = getClassVal();
			PropertyType pClass = 
				createSingleProperty(GUITARConstants.CLASS_TAG_NAME, sClass);
			retProperties.add(pClass);
			

			String sType = getTypeVal();
			PropertyType pType = 
				createSingleProperty(GUITARConstants.TYPE_TAG_NAME, sType);
			retProperties.add(pType);
			

		} else {
			retProperties = null;
		}
		return retProperties;
	}

	/**
	 * Get ID for GUI component, which is used to identity it  
	 * For Open Office we use a combination of Name and Role as an identifier
	 * 
	 * @return
	 */
	public  String  getIDVal() {
		
		String retProperty;
		
		String sName = xAccessibleContext
		.getAccessibleName();
		
		int nRole = xAccessibleContext.getAccessibleRole();
		retProperty =  sName+GUITARConstants.NAME_SEPARATOR+nRole;
		
		return retProperty;
	}

	/**
	 * Get class of a GUI component  
	 * @return
	 */
	public String getClassVal() {
		String retProperty;
		
		int nRole = xAccessibleContext.getAccessibleRole();
		retProperty = NameProvider.getRoleName(nRole);
		
		return retProperty;
	}

	/**
	 * Get GUITAR Event type
	 * Refer to {@link EventType} for more details
	 *  
	 * @return
	 */
	public  String getTypeVal() {
		
		String sID = getIDVal();
		String retProperty;
		
		if(OOConstants.sTerminalWidgetList.contains(sID))
			retProperty = EventType.TERMINAL;
		else 
			retProperty = EventType.SYSTEM_INTERACTION;
		return retProperty;
	}

}
