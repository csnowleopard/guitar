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

import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.PropertyType;

/**
 * @author Bao Nguyen
 * 
 */
public class GUITypeAdapter {
	GUIType dGUIType;

	/**
	 * @param data
	 */
	public GUITypeAdapter(GUIType data) {
		super();
		this.dGUIType = data;
	}

	/**
	 * @return the data
	 */
	public GUIType getData() {
		return dGUIType;
	}

	public ComponentTypeAdapter getChildByTitle(String sSearchTitle){
		ComponentTypeAdapter container = new ComponentTypeAdapter(dGUIType.getContainer());
		return container.getChildByTitle(sSearchTitle);
	}
	
	public void setValueByName(String sTitle, String sName, String sValue) {
		ComponentType window = dGUIType.getWindow();
		ComponentTypeAdapter windowA = new ComponentTypeAdapter(window);
		windowA.setValueByName(sTitle, sName, sValue);
		
		ComponentType container = dGUIType.getContainer();
		ComponentTypeAdapter containerA = new ComponentTypeAdapter(container);
		containerA.setValueByName(sTitle, sName, sValue);
	}
	
	public void addValueByName(String sTitle, String sName, String sValue) {
		ComponentType window = dGUIType.getWindow();
		ComponentTypeAdapter windowA = new ComponentTypeAdapter(window);
		windowA.addValueByName(sTitle, sName, sValue);
		
		ComponentType container = dGUIType.getContainer();
		ComponentTypeAdapter containerA = new ComponentTypeAdapter(container);
		containerA.addValueByName(sTitle, sName, sValue);
	}
	
	
	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof GUITypeAdapter))
			return false;

		GUITypeAdapter other = (GUITypeAdapter) obj;

		String sMyTitle = getTitle();
		String sOtherTitle = other.getTitle();

		return sMyTitle.equals(sOtherTitle);
	}


	/**
	 * Get title of the window data
	 * 
	 * @return
	 */
	public String getTitle() {
		ComponentType window = dGUIType.getWindow();
		ComponentTypeAdapter winAdapter = new ComponentTypeAdapter(window);
		String sGUITitle = winAdapter
				.getFirstValueByName(GUITARConstants.TITLE_TAG_NAME);
		System.err.println("@@@@@@@@ GUI Title : " + sGUITitle);
		return sGUITitle;

	}
	
	
	public void setTitle(String sTitle) {
		ObjectFactory factory = new ObjectFactory();
		
		ComponentType window = dGUIType.getWindow();
		ComponentTypeAdapter winAdapter = new ComponentTypeAdapter(window);
		
		AttributesType attributes = window.getAttributes();
		

		
		PropertyType newProperty = factory.createPropertyType();
		newProperty.setName(GUITARConstants.TITLE_TAG_NAME);
		List<String> value = new ArrayList<String>();
		value.add(sTitle);
		
		List<PropertyType> lProperty = attributes.getProperty();
		PropertyType p;
		for(int i = 0;i<lProperty.size();i++){
			p = lProperty.get(i);
			
			if(p.getName().equals(GUITARConstants.TITLE_TAG_NAME)){
				lProperty.add(i, newProperty);
				lProperty.remove(p);
			}
		}
		
	}
	
	public boolean isRoot(){
		ComponentType window = dGUIType.getWindow();
		ComponentTypeAdapter windowA = new ComponentTypeAdapter(window);
		String isRoot= windowA.getFirstValueByName(GUITARConstants.ROOTWINDOW_TAG_NAME);
		
		return (isRoot.equalsIgnoreCase("true"));
	
	}

}
