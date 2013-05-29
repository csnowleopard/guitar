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

import java.util.List;

import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.GUIType;

/**
 * @author Bao Nguyen
 * 
 */
public class GUIStructureAdapter {
	GUIStructure dGUIStructure;

	/**
	 * @param data
	 */
	public GUIStructureAdapter(GUIStructure data) {
		super();
		this.dGUIStructure = data;
	}

	// public void addGUI(GUIType gui){
	// this.dGUIStructure.getGUI().add(gui);
	// }

	public void addGUI(GUIType dGUI) {
		this.dGUIStructure.getGUI().add(dGUI);
	}

	public void addGUI(GUITypeAdapter mGUI) {
		this.dGUIStructure.getGUI().add(mGUI.getData());
	}

	public void removeGUI(GUITypeAdapter mGUI) {
//		this.dGUIStructure.getGUI().remove(mGUI);
		List<GUIType> tempGUIList = dGUIStructure.getGUI();
		tempGUIList.remove(mGUI.getData());
		this.dGUIStructure.setGUI(tempGUIList);
	}

	public GUITypeAdapter getGUIByTitle(String sTitle) {
		List<GUIType> lGUI = dGUIStructure.getGUI();

		for (GUIType gui : lGUI) {
			GUITypeAdapter guiA = new GUITypeAdapter(gui);
			String sGUITitle = guiA.getTitle();
			if (sTitle.equals(sGUITitle))
				return guiA;
		}

		return null;
	}

	public boolean contains(GUITypeAdapter  obj) {
		
		if (!(obj instanceof GUITypeAdapter))
			return false;

		for (GUIType gui : dGUIStructure.getGUI()) {
			GUITypeAdapter guiA = new GUITypeAdapter(gui);
			if(guiA.equals(obj))
				return true;
		}
		return false;
	}

	/**
	 * @return the data
	 */
	public GUIStructure getData() {
		return dGUIStructure;
	}
	
	public GUITypeAdapter getRoot(){
		List<GUIType> lGUI = dGUIStructure.getGUI();
		
		for(GUIType gui: lGUI){
			GUITypeAdapter guiA = new GUITypeAdapter(gui);
			if(guiA.isRoot())
				return guiA;
		}
		return null;
	}

}
