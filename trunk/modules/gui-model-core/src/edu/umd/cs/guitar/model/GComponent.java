/*
 *  Copyright (c) 2009-@year@. The  GUITAR group  at the University of
 *  Maryland. Names of owners of this group may be obtained by sending
 *  an e-mail to atif@cs.umd.edu
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files
 *  (the "Software"), to deal in the Software without restriction,
 *  including without limitation  the rights to use, copy, modify, merge,
 *  publish,  distribute, sublicense, and/or sell copies of the Software,
 *  and to  permit persons  to whom  the Software  is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY,  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO  EVENT SHALL THE  AUTHORS OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR  OTHER LIABILITY,  WHETHER IN AN  ACTION OF CONTRACT,
 *  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.umd.cs.guitar.model;

import java.awt.AWTException;
import java.awt.image.BufferedImage;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.ContentsType;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.PropertyTypeWrapper;

/**
 * Abstract class for accessible components (widget/container) in GUITAR
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 * 
 */
public abstract class GComponent implements GObject {
	/**
	 * SECTION: DATA
	 */

	// Unique ID generator
	private static int ID_COUNTER = 0;

	// Instance ID
	private int ID;

	/**
	 * Container window
	 */
	GWindow window;

	/**
	 * SECTION: LOGIC
	 */

	/**
     * 
     */
	public GComponent(GWindow window) {
		super();
		this.window = window;
		this.ID = ID_COUNTER++;
	}

	/**
	 * 
	 * Extract component properties and convert it to GUITAR data format
	 * 
	 * <p>
	 * 
	 * @return Data for a component
	 */
	public ComponentType extractProperties() {

		ComponentType retComp;

		if (!hasChildren()) {
			retComp = factory.createComponentType();
		} else {
			retComp = factory.createContainerType();
			ContentsType contents = factory.createContentsType();
			((ContainerType) retComp).setContents(contents);
		}

		ComponentTypeWrapper retCompAdapter = new ComponentTypeWrapper(retComp);

		// Class
		String sClass = getClassVal();
		retCompAdapter.addValueByName(GUITARConstants.CLASS_TAG_NAME, sClass);

		// Type
		String sType = getTypeVal();
		retCompAdapter.addValueByName(GUITARConstants.TYPE_TAG_NAME, sType);

		int x = getX();
		retCompAdapter.addValueByName(GUITARConstants.X_TAG_NAME,
				Integer.toString(x));

		int y = getY();
		retCompAdapter.addValueByName(GUITARConstants.Y_TAG_NAME,
				Integer.toString(y));

		// Events
		List<GEvent> lEvents = getEventList();
		if (lEvents != null) {
			for (GEvent event : lEvents)
				retCompAdapter.addValueByName(GUITARConstants.EVENT_TAG_NAME,
						event.getClass().getName());
		}
		// Other GUI Properties
		retComp = retCompAdapter.getDComponentType();

		AttributesType attributes = retComp.getAttributes();
		List<PropertyType> lProperties = attributes.getProperty();
		List<PropertyType> lGUIProperties = getGUIProperties();

		// Update list
		if (lGUIProperties != null)
			lProperties.addAll(lGUIProperties);

		return retComp;
	}

	/**
	 * get a list of properties used to identify the component.
	 *  
	 * @return
	 */
	@Override
	public abstract List<PropertyType> getIDProperties();
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		List<PropertyType> guiProperties = getGUIProperties();

		final int prime = 31;
		int result = 1;
		if (guiProperties == null)
			return 0;

		for (PropertyType property : guiProperties) {
			String name = property.getName();
			result = prime * result + (name == null ? 0 : name.hashCode());
			result = Math.abs(result);

			List<String> valueList = property.getValue();
			result = prime * result
					+ (valueList == null ? 0 : valueList.hashCode());
			result = Math.abs(result);
		}

		return result;
	}

	/**
	 * Hierarchically search this component for a sub-component given by an
	 * input image file of that component.
	 * 
	 * @returns GComponent of sum-component found. null on no match.
	 */
	public abstract GObject searchComponentByImage(String sFilePath)
			throws IOException;

	/**
	 * Get the class of the component
	 * 
	 * @return class name
	 */
	public abstract String getClassVal();

	/**
	 * Get the list of events can be performed by the component
	 * 
	 * @return List of {@link GEvent} supported by the component
	 */
	public abstract List<GEvent> getEventList();

	/**
	 * Get all children of the component.
	 * 
	 * <p>
	 * 
	 * @return list of children components
	 */
	public abstract List<GComponent> getChildren();

	/**
	 * Get the direct parent of the component.
	 * 
	 * <p>
	 * 
	 * @return parent compnent
	 */
	public abstract GObject getParent();

	/**
	 * 
	 * Get the GUITAR type of event supported by the component (i.e. TERMINAL,
	 * SYSTEM INTERACTION, etc).
	 * 
	 * <p>
	 * 
	 * @see GUITARConstants
	 * 
	 * @return type of event
	 */
	public abstract String getTypeVal();

	/**
	 * Check if the component has children
	 * 
	 * <p>
	 * 
	 * @return type of the the component
	 */
	public abstract boolean hasChildren();

	/**
	 * @return <code> true </code> if the component is terminal
	 */
	public abstract boolean isTerminal();

	/**
	 * Check if the component is enable
	 * 
	 * @return true if the component is enabled
	 */
	public abstract boolean isEnable();

	/**
	 * 
	 * Get first the child by comparing its properties to a list of
	 * {@link edu.umd.cs.guitar.model.data.PropertyType}. This list works as an
	 * identifier for widgets on the GUI.
	 * 
	 * <p>
	 * 
	 * @param lIDProperties
	 *            the list of {@link PropertyTypeWrapper} working as widget
	 *            identifier.
	 * @return the first child component matching the ID properties
	 */
	public GComponent getFirstChild(List<PropertyTypeWrapper> lIDProperties) {
		ComponentType comp = extractProperties();
		List<PropertyType> lProperties = comp.getAttributes().getProperty();

		List<PropertyTypeWrapper> lPropertyTypeAdapters = new ArrayList<PropertyTypeWrapper>();

		for (PropertyType p : lProperties) {
			lPropertyTypeAdapters.add(new PropertyTypeWrapper(p));
		}

		if (lPropertyTypeAdapters.containsAll(lIDProperties)) {
			return this;
		}

		List<GComponent> gChildren = getChildren();
		GComponent result = null;

		for (GComponent gChild : gChildren) {
			result = gChild.getFirstChild(lIDProperties);
			if (result != null)
				return result;
		}

		return null;
	}

	/**
	 * Get a child whose properties match a certain property set
	 * 
	 * @param properties
	 * @return list of {@link GComponent} matching with the property set
	 */
	public GObject getChildByPropertySet(List<PropertyType> properties) {
		{
			List<GComponent> gChildren = getChildren();
			GObject result = null;
			for (GComponent gChild : gChildren) {
				result = gChild.getChildByPropertySet(properties);
				if (result != null)
					return result;
			}
			return null;
		}
	}

} // End of class
