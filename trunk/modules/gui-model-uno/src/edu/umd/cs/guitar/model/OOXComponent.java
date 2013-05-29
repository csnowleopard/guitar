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
package edu.umd.cs.guitar.model;

import com.sun.star.accessibility.AccessibleRole;
import com.sun.star.accessibility.AccessibleStateType;
import com.sun.star.accessibility.XAccessible;
import com.sun.star.accessibility.XAccessibleAction;
import com.sun.star.accessibility.XAccessibleContext;
import com.sun.star.accessibility.XAccessibleEditableText;
import com.sun.star.accessibility.XAccessibleSelection;
import com.sun.star.accessibility.XAccessibleStateSet;
import com.sun.star.accessibility.XAccessibleValue;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XInterface;

import edu.umd.cs.guitar.awb.NameProvider;
import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.event.OOActionHandler;
import edu.umd.cs.guitar.event.OOEditableTextHandler;
import edu.umd.cs.guitar.event.OOEventHandler;
import edu.umd.cs.guitar.event.OOSelectFromParentHandler;
import edu.umd.cs.guitar.event.OOSelectionHandler;
import edu.umd.cs.guitar.event.OOValueHandler;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.ContentsType;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.OOExtractGUIProperties;
import edu.umd.cs.guitar.ripper.Debugger;
import edu.umd.cs.guitar.util.EventType;
import edu.umd.cs.guitar.util.OOConstants;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.IOException;

/**
 * @author Bao Nguyen
 * 
 */
public class OOXComponent extends GComponent {

	XAccessible xAccessible;

	/**
	 * @param accessible
	 */
	public OOXComponent(XAccessible accessible, GWindow window) {
		super(window);

		xAccessible = accessible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXComponent#getChildren()
	 */
	@Override
	public List<GComponent> getChildren() {
		List<GComponent> retList = new ArrayList<GComponent>();

		XAccessibleContext xContext = xAccessible.getAccessibleContext();

		if (xContext == null) {
			return retList;
		}

		int nChildren = xContext.getAccessibleChildCount();
		// Debugger.println(xContext.getAccessibleName() + ": " + nChildren);

		for (int i = 0; i < nChildren; i++) {

			try {

				XAccessible xChild = xContext.getAccessibleChild(i);
				GComponent gChild = new OOXComponent(xChild, window);
				retList.add(gChild);

			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}

		}

		return retList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXComponent#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		XAccessibleContext xContext = xAccessible.getAccessibleContext();

		if (xContext == null)
			return false;

		int nChildren = xContext.getAccessibleChildCount();
		return (nChildren > 0);
	}

	private static int ID;

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXComponent#getID()
	 */

	public String getID() {
		return "w" + (ID++);
	}

	/**
	 * Back up method
	 * 
	 * @return
	 */
	public String getID_bak() {

		if (xAccessible == null) {
			Debugger.pause("Accessible null");
			return null;
		}
		XAccessibleContext xContext = xAccessible.getAccessibleContext();

		if (xContext == null) {
			Debugger.pause("Context null");
			return null;
		}

		String id = xContext.getAccessibleName()
				+ GUITARConstants.NAME_SEPARATOR + xContext.getAccessibleRole();
		return id;
	}

	/**
	 * @return the xAccessible
	 */
	public XAccessible getXAccessible() {
		return xAccessible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXComponent#getParent()
	 */
	@Override
	public GComponent getParent() {

		if (xAccessible == null)
			return null;

		XAccessibleContext xContext = xAccessible.getAccessibleContext();

		if (xContext == null)
			return null;

		XAccessible xParent = xContext.getAccessibleParent();
		return new OOXComponent(xParent, window);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXComponent#getEventList()
	 */
	@Override
	public List<GEvent> getEventList() {

		List<GEvent> retEvents = new ArrayList<GEvent>();

		XAccessibleContext xContext = xAccessible.getAccessibleContext();

		XInterface xInterface;

		// XAccessibleAction
		xInterface = (XAccessibleAction) UnoRuntime.queryInterface(
				XAccessibleAction.class, xContext);
		if (xInterface != null)
			retEvents.add(new OOActionHandler());

		// XAccessibleEditableText
		xInterface = (XAccessibleEditableText) UnoRuntime.queryInterface(
				XAccessibleEditableText.class, xContext);
		if (xInterface != null)
			retEvents.add(new OOEditableTextHandler());

		// XAccessibleSelection
		xInterface = (XAccessibleSelection) UnoRuntime.queryInterface(
				XAccessibleSelection.class, xContext);
		if (xInterface != null)
			retEvents.add(new OOSelectionHandler());

		// XAccessibleValue
		xInterface = (XAccessibleValue) UnoRuntime.queryInterface(
				XAccessibleValue.class, xContext);
		if (xInterface != null)
			retEvents.add(new OOValueHandler());

		// Filter out the events of interest
		if (hasChildren())
			retEvents = getExpandEvents(retEvents);

		return retEvents;
	}

	// --------------------------------------
	// A list for filtering expandable event
	private static List<String> EXPANDABLE_LIST = Arrays.asList(
			OOActionHandler.class.getSimpleName(),
			OOSelectFromParentHandler.class.getSimpleName());

	/**
	 * @param eAllEvents
	 * @return
	 */
	private List<GEvent> getExpandEvents(List<GEvent> eAllEvents) {

		List<GEvent> retEvents = new ArrayList<GEvent>();

		for (GEvent event : eAllEvents) {
			if (EXPANDABLE_LIST.contains(event.getClass().getSimpleName()))
				retEvents.add(event);
		}
		return retEvents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXComponent#getType()
	 */
	@Override
	public String getClassVal() {
		if (xAccessible == null) {
			// System.err.println("___________ returning null ");
			return null;
		}
		XAccessibleContext xAccessibleContext = xAccessible
				.getAccessibleContext();
		if (xAccessibleContext == null) {
			// System.err.println("___________ returning null ");
			return null;
		}
		int nRole = xAccessibleContext.getAccessibleRole();
		return NameProvider.getRoleName(nRole);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXComponent#getTypeVal()
	 */
	@Override
	public String getTypeVal() {

		String sID = getID();
		String retProperty;

		// System.out.println( "This is where we are now: " +
		// System.getProperty("user.dir") );

		if (OOConstants.sTerminalWidgetList.contains(sID))
			retProperty = EventType.TERMINAL;
		else
			retProperty = EventType.SYSTEM_INTERACTION;
		return retProperty;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GXComponent#getGUIProperties()
	 */
	@Override
	public List<PropertyType> getGUIProperties() {
		return new ArrayList<PropertyType>();
	}

	@Override
	public String getTitle() {
		if (xAccessible == null) {
			// System.err.println("___________ returning null ");
			return null;
		}
		XAccessibleContext xContext = xAccessible.getAccessibleContext();

		if (xContext == null) {
			// System.err.println("___________ returning null ");
			return null;
		}

		// System.err.println("___________ returning " +
		// xContext.getAccessibleName());
		return (xContext.getAccessibleName());
	}

	@Override
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

		// // Add ID
		// String ID = getID();
		// retCompAdapter.addValueByName(GUITARConstants.ID_TAG_NAME, ID);

		// String sID = getFullID();
		// retCompAdapter.addValueByName(GUITARConstants.FULL_ID_TAG_NAME, sID);

		// Title
		String sTitle = getTitle();
		retCompAdapter.addValueByName("Title", sTitle);

		// Class
		String sClass = getClassVal();
		retCompAdapter.addValueByName(GUITARConstants.CLASS_TAG_NAME, sClass);

		// Type
		String sType = getTypeVal();
		retCompAdapter.addValueByName(GUITARConstants.TYPE_TAG_NAME, sType);

		int x = getX();
		retCompAdapter.addValueByName(GUITARConstants.X_TAG_NAME, Integer
				.toString(x));

		int y = getY();
		retCompAdapter.addValueByName(GUITARConstants.Y_TAG_NAME, Integer
				.toString(y));

		// Hash code
		// String sHashcode = Integer.toString(this.hashCode());
		// retCompAdapter.addValueByName(GUITARConstants.HASHCODE_TAG_NAME,
		// sHashcode);

		// Events
		List<GEvent> lEvents = getEventList();
		for (GEvent event : lEvents)
			retCompAdapter.addValueByName(GUITARConstants.EVENT_TAG_NAME, event
					.getClass().getName());

		// Other GUI Properties
		retComp = retCompAdapter.getDComponentType();

		AttributesType attributes = retComp.getAttributes();
		List<PropertyType> lProperties = attributes.getProperty();
		List<PropertyType> lGUIProperties = getGUIProperties();

		// Update list
		if (lGUIProperties != null)
			lProperties.addAll(lGUIProperties);

		attributes.setProperty(lProperties);
		retComp.setAttributes(attributes);

		return retComp;
	}

	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isTerminal() {
		if (!isClickable())
			return false;

		String sName = getTitle();

		if (sName == null)
			return false;
		
		List<AttributesTypeWrapper> termSig = OOConstants.sTerminalWidgetSignature;
		for (AttributesTypeWrapper sign : termSig) {
			String titleVals = sign
					.getFirstValByName(GUITARConstants.TITLE_TAG_NAME);

			if (titleVals == null)
				continue;

			if (titleVals.equalsIgnoreCase(sName))
				return true;

		}

		return false;

	}

	@Override
	public boolean isEnable() {
		// TODO Auto-generated method stub
		if (xAccessible == null)
			return false;

		XAccessibleContext xAccessibleContext = xAccessible
				.getAccessibleContext();

		if (xAccessibleContext == null)
			return false;

		XAccessibleStateSet xStateSet = xAccessibleContext
				.getAccessibleStateSet();

		if (xStateSet == null)
			return false;

		return xStateSet.contains(AccessibleStateType.ENABLED);
	}

	// Unclickable widget list
	// Sometimes used for dealing with widget implemented incorrectly
	List<Short> UNCLICKABLE_WIDGET_LIST = Arrays.asList(AccessibleRole.PANEL,
			AccessibleRole.LABEL, AccessibleRole.TEXT,
			AccessibleRole.PASSWORD_TEXT,
			AccessibleRole.SEPARATOR
			);

	private boolean isClickable() {

		if (xAccessible == null)
			return false;

		XAccessibleContext xAccessibleContext = xAccessible
				.getAccessibleContext();
		if (xAccessibleContext == null)
			return false;

		short role = xAccessibleContext.getAccessibleRole();
		if (UNCLICKABLE_WIDGET_LIST.contains(role))
			return false;

		return true;
	}

   /**
    * Hierarchically search "this" component for matching widget.
    * The search-item is provided as an image via a file name.
    *
    * This is a place holder function. This functionality is not
    * supported yet.
    *
    * @param sFilePath Search item's file path.
    */
   public GComponent
   searchComponentByImage(String sFilePath)
   throws IOException
   {
      return null;
   }
}
