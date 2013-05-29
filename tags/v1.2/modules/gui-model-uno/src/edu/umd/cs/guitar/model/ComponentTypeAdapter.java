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

import java.util.ArrayList;
import java.util.List;

//import com.sun.org.apache.bcel.internal.generic.DCMPG;
import com.sun.org.apache.bcel.internal.generic.*;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.ContentsType;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.util.OOConstants;

/**
 * @author Bao Nguyen
 * 
 */
public class ComponentTypeAdapter {

	ComponentType dComponentType;
	static ObjectFactory factory = new ObjectFactory();
	

	/**
	 * @return the dComponentType
	 */
	public ComponentType getDComponentType() {
		return dComponentType;
	}

	/**
	 * @param componentType
	 */
	public ComponentTypeAdapter(ComponentType componentType) {
		super();
		dComponentType = componentType;
	}

	public PropertyType getFirstPropertyByName(String sName) {
		AttributesType attributes = this.dComponentType.getAttributes();
		List<PropertyType> lProperty = attributes.getProperty();

		for (PropertyType p : lProperty) {
			if (p.getName().equals(sName)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Find Property by name
	 * 
	 * <p>
	 * 
	 * @param sGUIName
	 * @param guiStructure
	 * @return
	 */

	public String getFirstValueByName(String sName) {

		PropertyType property = getFirstPropertyByName(sName);
		if (property != null && property.getValue().size() > 0)
			return property.getValue().get(0);
		return null;
	}

	/**
	 * Get a list of property by name
	 * 
	 * @param sName
	 * @return
	 */
	public List<String> getValueListByName(String sName) {
		AttributesType attributes = this.dComponentType.getAttributes();
		List<PropertyType> lProperty = attributes.getProperty();

		for (PropertyType p : lProperty) {
			if (p.getName().equals(sName)) {
				return p.getValue();
			}
		}

		return new ArrayList<String>();
	}

	/**
	 * Set a property of child object
	 * 
	 * @param sName
	 * @return
	 */
	public void setValueByName(String sTitle, String sName, String sValue) {
		String sMyTitle = getFirstValueByName(GUITARConstants.TITLE_TAG_NAME);

		if (sTitle.equals(sMyTitle)) {
			AttributesType attributes = this.dComponentType.getAttributes();

			List<PropertyType> lProperty = attributes.getProperty();

			for (PropertyType p : lProperty) {

				if (p.getName().equals(sName)) {
					lProperty.remove(p);
					List<String> lValue = new ArrayList<String>();
					lValue.add(sValue);
					p.setValue(lValue);
					lProperty.add(p);
				}
			}
			attributes.setProperty(lProperty);
			dComponentType.setAttributes(attributes);
		} else
		if (dComponentType instanceof ContainerType) {
			
			ContainerType container = (ContainerType) dComponentType;
			ContentsType contents = container.getContents();
			List<ComponentType> lChildren= container.getContents().getWidgetOrContainer();
			List<ComponentType> lNewChildren = new ArrayList<ComponentType>();
			
			for(ComponentType child: lChildren){
				ComponentTypeAdapter childA = new ComponentTypeAdapter(child);
				childA .setValueByName(sTitle, sName, sValue);
				lNewChildren.add(childA.getDComponentType());
			}
			
			contents.setWidgetOrContainer(lNewChildren);
			((ContainerType) dComponentType).setContents(contents);
		}
	}

	
	/**
	 * 
	 * Add an attribute value to the current ComponentType
	 * 
	 * <p>
	 * 
	 * @param sName
	 * @param sValue
	 */
	public void addValueByName(String sName, String sValue) {
		
		AttributesType attributes = dComponentType.getAttributes();
		
		if(attributes==null)
			attributes = factory.createAttributesType();
			
		List<PropertyType> lProperty =  attributes.getProperty();
		
		PropertyType property = null;
		for(PropertyType aProperty: lProperty){
			if(sName.equals(aProperty.getName())){
				lProperty.remove(aProperty);
				property = aProperty;
				break;
			}
		}
		if(property==null){
			property = new PropertyType();
			property.setName(sName);
		}
		List<String> lValue = property.getValue();
		lValue.add(sValue);
		property.setValue(lValue);
		lProperty.add(property);
		attributes.setProperty(lProperty);
		dComponentType.setAttributes(attributes);
	}
	
	
	/**
	 * 
	 * Add an attribute value to the child ComponentType having name Title
	 * 
	 * @param sTitle
	 * @param sName
	 * @param sValue
	 */
	public void addValueByName(String sTitle, String sName, String sValue) {
		String sMyTitle = getFirstValueByName(GUITARConstants.TITLE_TAG_NAME);
		
		if (sTitle.equals(sMyTitle)) {
			
			AttributesType attributes = this.dComponentType.getAttributes();

			List<PropertyType> lProperty = attributes.getProperty();
			PropertyType p = new PropertyType();
			p.setName(sName);
			List<String> lValue = new ArrayList<String>();
			lValue.add(sValue);
			p.setValue(lValue);
			lProperty.add(p);
			
			attributes.setProperty(lProperty);
			dComponentType.setAttributes(attributes);
		} else
		if (dComponentType instanceof ContainerType) {
			
			ContainerType container = (ContainerType) dComponentType;
			ContentsType contents = container.getContents();
			List<ComponentType> lChildren= container.getContents().getWidgetOrContainer();
			List<ComponentType> lNewChildren = new ArrayList<ComponentType>();
			
			for(ComponentType child: lChildren){
				ComponentTypeAdapter childA = new ComponentTypeAdapter(child);
				childA .addValueByName(sTitle, sName, sValue);
				lNewChildren.add(childA.getDComponentType());
			}
			
			contents.setWidgetOrContainer(lNewChildren);
			((ContainerType) dComponentType).setContents(contents);
		}
	}
	
	
	public ComponentTypeAdapter getChildByTitle(String sSearchTitle) {
		String sTitle = getFirstValueByName(GUITARConstants.TITLE_TAG_NAME);
		if (sSearchTitle.equals(sTitle)) {
			return this;
		} else if (dComponentType instanceof ContainerType) {
			ContainerType container = (ContainerType) dComponentType;
			List<ComponentType> lChildrend = (List<ComponentType>) container
					.getContents().getWidgetOrContainer();

			ComponentTypeAdapter retComp = null;

			for (ComponentType child : lChildrend) {
				ComponentTypeAdapter childAdapter = new ComponentTypeAdapter(
						child);
				retComp = childAdapter.getChildByTitle(sSearchTitle);
				if (retComp != null)
					return retComp;
			}
		}
		return null;
	}

}
