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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.sun.star.accessibility.XAccessible;
import com.sun.star.accessibility.XAccessibleComponent;
import com.sun.star.accessibility.XAccessibleContext;
import com.sun.star.awt.Point;
import com.sun.star.awt.XWindow;
import com.sun.star.lang.XComponent;
import com.sun.star.uno.UnoRuntime;

import edu.umd.cs.guitar.model.data.PropertyType;

public class OOTTPComponent extends OOAbsTypeToProperties {

	public OOTTPComponent(XAccessible accessible) {
		super(accessible);
	}

	public List<PropertyType> getProperties() {
		List<PropertyType> result = new ArrayList<PropertyType>();

		//System.err.println(xAccessible == null);

		XAccessibleComponent xAccessibleComponent = (XAccessibleComponent) UnoRuntime
				.queryInterface(XAccessibleComponent.class, xAccessibleContext);

		if (xAccessibleComponent != null) {

			// Location
			Point location = xAccessibleComponent.getLocation();
			String sLocation = location.X + "," + location.Y;
			result.add(this.createSingleProperty("Location", sLocation));

			// Bound
			com.sun.star.awt.Rectangle bound = xAccessibleComponent.getBounds();
			String sBound = bound.X + "," + bound.Y + "," + bound.Height + ","
					+ bound.Width;
			result.add(this.createSingleProperty("Bound", sBound));

			// Ignore the location on screen

			// Size
			com.sun.star.awt.Size size = xAccessibleComponent.getSize();
			String sSize = size.Width + "," + size.Height;
			result.add(this.createSingleProperty("Size", sSize));

			// Foreground
			int foreground = xAccessibleComponent.getForeground();
			String sForeground = +(foreground >> 16 & 0xff) + "G"
					+ (foreground >> 8 & 0xff) + "B" + (foreground >> 0 & 0xff)
					+ "A" + (foreground >> 24 & 0xff);
			result.add(this.createSingleProperty("Foreground", sForeground));

			// Background
			int backround = xAccessibleComponent.getForeground();
			String sBackround = +(backround >> 16 & 0xff) + "G"
					+ (backround >> 8 & 0xff) + "B" + (backround >> 0 & 0xff)
					+ "A" + (backround >> 24 & 0xff);
			result.add(this.createSingleProperty("Background", sBackround));
		} else {
			result = null;
		}

		return result;
	}
}
