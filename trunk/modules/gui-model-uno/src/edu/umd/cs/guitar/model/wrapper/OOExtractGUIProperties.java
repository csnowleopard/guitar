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

//import com.sun.accessibility.internal.resources.accessibility;
import com.sun.accessibility.internal.resources.*;
import com.sun.star.accessibility.XAccessible;
import com.sun.star.accessibility.XAccessibleAction;
import com.sun.star.accessibility.XAccessibleComponent;
import com.sun.star.accessibility.XAccessibleContext;
import com.sun.star.accessibility.XAccessibleExtendedComponent;
import com.sun.star.accessibility.XAccessibleStateSet;
import com.sun.star.accessibility.XAccessibleText;
import com.sun.star.accessibility.XAccessibleValue;
import com.sun.star.awt.XTopWindow;
import com.sun.star.container.XChild;
import com.sun.star.corba.iiop.ListenPoint;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.lang.XTypeProvider;
import com.sun.star.sdb.XQueriesSupplier;
import com.sun.star.uno.Type;
import com.sun.star.uno.UnoRuntime;

import edu.umd.cs.guitar.awb.NameProvider;
import edu.umd.cs.guitar.model.data.*;

/**
 * Utility class to extract GUI Properties from OO XAccessible object
 * 
 * @author Bao Nguyen
 * 
 */
public class OOExtractGUIProperties {

	static OOExtractGUIProperties instance = null;
	static ObjectFactory factory = new ObjectFactory();

	public static OOExtractGUIProperties Instance() {
		if (instance == null)
			return new OOExtractGUIProperties();
		else
			return instance;
	}

	private OOExtractGUIProperties() {
	}

	/**
	 * Extract GUI properties
	 * <p>
	 * 
	 * The root property is defined by the user, not extracted from the system
	 * This feature is used when we want to rip only one sub window of the SUT  
	 * 
	 * @param win
	 * @param isRoot
	 * @return
	 */
	public static GUIType extractGUI(XTopWindow win, Boolean isRoot) {
		GUIType gui = factory.createGUIType();

		// TODO: Need to extract more window properties

		XAccessible xAccessible = (XAccessible) UnoRuntime.queryInterface(
				XAccessible.class, win);

		ComponentType window = factory.createComponentType();
		window.setAttributes(extractAttributes(xAccessible));
		gui.setWindow(window);

		// ---------------------------------
		// Additional properties to match the old GUITAR format
//		List<PropertyType> lWinProperties = window.getAttributes()
//				.getProperty();
//		
//		// ---------------------------------
//		// State
//		PropertyType pModal = extractState(xAccessible, "Modal");
//		lWinProperties.add(pModal);
//
//		// ---------------------------------
//		// Is Root
//		PropertyType pRoot = factory.createPropertyType();
//		pRoot.setName("Rootwindow");
//		pRoot.getValue().add(isRoot.toString().toUpperCase());
//		lWinProperties.add(pRoot);

		ComponentType container = extractContainer(xAccessible);
		gui.setContainer((ContainerType) container);

		return gui;
	}

	/**
	 * Extract properties of a component. Either Container or Widget
	 * 
	 * @param xAccessible
	 * @return
	 */
	public static ComponentType extractComponent(XAccessible xAccessible) {
		
		ComponentType retComp;
		if(xAccessible==null){
			return null;
		}

		XAccessibleContext xContext = xAccessible.getAccessibleContext();
		
		if(xContext==null)
			return null;

		if (xContext.getAccessibleChildCount() == 0)
			retComp = factory.createComponentType();
		else {
			retComp = new ContainerType();
			((ContainerType) retComp).setContents(factory.createContentsType());
		}

		AttributesType attributes = extractAttributes(xAccessible);
		retComp.setAttributes(attributes);

		return retComp;
	}

	/**
	 * Force to create a container from a GUI Accessible node
	 * 
	 * @param xAccessible
	 * @return
	 */
	public static ContainerType extractContainer(XAccessible xAccessible) {

		ContainerType retComp = factory.createContainerType();
		retComp.setContents(factory.createContentsType());
		AttributesType attributes = extractAttributes(xAccessible);
		retComp.setAttributes(attributes);

		return retComp;
	}

	/**
	 * Extract all GUI attributes of a GUI node
	 * 
	 * @param xAccessible
	 * @return
	 */
	public static AttributesType extractAttributes(XAccessible xAccessible) {
		AttributesType attributes = factory.createAttributesType();

		// Guitar Attributes
		List<PropertyType> pGuitar = extractGUITARProperties(xAccessible);
		if (pGuitar != null)
			attributes.getProperty().addAll(pGuitar);

		// GUI Attributes:
		List<PropertyType> pGUI = extractGUIProperties(xAccessible);
		if (pGUI != null)
			attributes.getProperty().addAll(pGUI);

//		// Events
//		List<PropertyType> pEvent = extractGUIEvents(xAccessible);
//		if (pEvent != null)
//			attributes.getProperty().addAll(pEvent);
		return attributes;
	}

	/**
	 * Extract essential properties required by GUITAR
	 * 
	 * @param accessible
	 * @return
	 */
	private static List<PropertyType> extractGUITARProperties(
			XAccessible xAccessible) {

		List<PropertyType> retProperties;

		OOAbsTypeToProperties ttp;
		ttp = new OOTTPGuitar(xAccessible);
		retProperties = ttp.getProperties();

		return retProperties;
	}

	/**
	 * Get all GUI properties of a xAccessible element
	 * 
	 * @param xAccessible
	 * @return
	 */
	public static List<PropertyType> extractGUIProperties(
			XAccessible xAccessible) {
		List<PropertyType> result = new ArrayList<PropertyType>();
		List<PropertyType> subProperties;

		OOAbsTypeToProperties ttp;

		// ---------------------------
		// Read only GUI properties

		// XAccessibleContext
		ttp = new OOTTPContext(xAccessible);
		subProperties = ttp.getProperties();
		if (subProperties != null)
			result.addAll(subProperties);

		// XAccessibleText
		ttp = new OOTTPText(xAccessible);
		subProperties = ttp.getProperties();
		if (subProperties != null)
			result.addAll(subProperties);

		// XAccessibleComponent
		ttp = new OOTTPComponent(xAccessible);
		subProperties = ttp.getProperties();
		if (subProperties != null)
			result.addAll(subProperties);

		// XAccessibleExtendedComponent
		ttp = new OOTTPExComponent(xAccessible);
		subProperties = ttp.getProperties();
		if (subProperties != null)
			result.addAll(subProperties);

		// XAccessibleText
		ttp = new OOTTPImage(xAccessible);
		subProperties = ttp.getProperties();
		if (subProperties != null)
			result.addAll(subProperties);

		// XAccessibleStateSet
		ttp = new OOTTPStateSet(xAccessible);
		subProperties = ttp.getProperties();
		if (subProperties != null)
			result.addAll(subProperties);

		// -----------------------------
		// TODO: extract properties from the following interfaces
		// XAccessibleHypertext
		// XAccessibleKeyBinding
		// XAccessibleTextAttributes
		// XAccessibleTextMarkup

		// -------------------------
		// Event

		// Extract Events

		return result;
	}

	/**
	 * Extract all events in a GUI element This method will be
	 * used for a new GUITAR structure format
	 * 
	 * @return
	 */
	public static List<PropertyType> extractGUIEvents(XAccessible xAccessible) {
		OOAbsTypeToProperties ttp;
		ttp = new OOTTPEvent(xAccessible);
		return ttp.getProperties();
	}

	/**
	 * Extract a certain state of a GUI component
	 * 
	 * @param xAccessible
	 * @param sStateName
	 * @return
	 */
	public static PropertyType extractState(XAccessible xAccessible,
			String sStateName) {

		XAccessibleContext xContext = xAccessible.getAccessibleContext();
		XAccessibleStateSet xStateSet = xContext.getAccessibleStateSet();

		PropertyType retProperty = factory.createPropertyType();
		Boolean bStateValue = false;

		if (xStateSet != null) {
			retProperty.setName(sStateName);
			short aStates[] = xStateSet.getStates();

			int nStates = aStates.length;
			for (short i = 0; i < nStates; i++) {

				System.out.println(NameProvider.getStateName(aStates[i]));

				if (sStateName.equalsIgnoreCase(NameProvider
						.getStateName(aStates[i]))) {
					bStateValue = true;
					break;
				}
			}
		}

		retProperty.getValue().add(bStateValue.toString());

		return retProperty;
	}

	/**
	 * TODO: change to component type Get property List from a component
	 * 
	 * @param component
	 * @return
	 */
	public static List<PropertyType> getProperties(ComponentType component) {

		AttributesType attributes;
		attributes = (component).getAttributes();
		return attributes.getProperty();
	}
}
