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
package edu.umd.cs.guitar.model.wrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.umd.cs.guitar.model.GHashcodeGenerator;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * 
 * Wrapper class to process GUIStructure.
 * 
 * <p>
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 * 
 */
public class GUIStructureWrapper {
	GUIStructure dGUIStructure;
	List<GUITypeWrapper> lGUI;

	/**
     * 
     */
	public void parseData() {
		lGUI = new ArrayList<GUITypeWrapper>();

		Set<GUITypeWrapper> listRootWindows = getRootWindows();

		for (GUITypeWrapper rootWin : listRootWindows) {
			if (!lGUI.contains(rootWin))
				lGUI.add(rootWin);
			rootWin.parseData(dGUIStructure, this);
		}

	}

	/**
	 * @param data
	 */
	public GUIStructureWrapper(GUIStructure data) {
		super();
		this.dGUIStructure = data;
	}

	public void addGUI(GUIType dGUI) {
		this.dGUIStructure.getGUI().add(dGUI);
	}

	public void addGUI(GUITypeWrapper mGUI) {
		this.dGUIStructure.getGUI().add(mGUI.getData());
	}

	public void removeGUI(GUITypeWrapper mGUI) {
		List<GUIType> tempGUIList = new ArrayList<GUIType>();
		tempGUIList.addAll(dGUIStructure.getGUI());
		for (GUIType gui : tempGUIList) {
			GUITypeWrapper guiA = new GUITypeWrapper(gui);
			String sGUITitle = guiA.getTitle();
			if (mGUI.getTitle().equals(sGUITitle))
				dGUIStructure.getGUI().remove(gui);
		}
	}

	public GUITypeWrapper getGUIByTitle(String sTitle) {
		List<GUIType> lGUI = dGUIStructure.getGUI();

		for (GUIType gui : lGUI) {
			GUITypeWrapper guiA = new GUITypeWrapper(gui);
			String sGUITitle = guiA.getTitle();
			if (sTitle.equals(sGUITitle))
				return guiA;
		}

		return null;
	}

	public boolean contains(GUIType window) {
		return contains(new GUITypeWrapper(window));
	}

	public boolean contains(GUITypeWrapper obj) {

		if (!(obj instanceof GUITypeWrapper))
			return false;
		for (GUIType gui : dGUIStructure.getGUI()) {
			GUITypeWrapper guiA = new GUITypeWrapper(gui);
			if (guiA.equals(obj))
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

	/**
	 * Get root window from GUIStructure
	 * 
	 * @return
	 */
	public GUITypeWrapper getRoot() {
		Set<GUITypeWrapper> guiList = getRootWindows();
		if (guiList == null)
			return null;

		if (guiList.size() == 0)
			;

		return (GUITypeWrapper) (guiList.toArray())[0];
	}

	/**
	 * 
	 * Find a component in the GUI using its ID
	 * 
	 * <p>
	 * 
	 * @param ID
	 * @return
	 */
	public ComponentTypeWrapper getComponentFromID(String ID) {
		ComponentTypeWrapper retComp = null;

		List<GUITypeWrapper> lGUIType = lGUI;
		if (lGUIType == null) {
			// parseData();
			lGUIType = new ArrayList<GUITypeWrapper>();
			for (GUIType gui : this.dGUIStructure.getGUI()) {
				lGUIType.add(new GUITypeWrapper(gui));
			}
		}

		for (GUITypeWrapper wGUI : lGUIType) {
			retComp = wGUI.getChildByID(ID);
			if (retComp != null)
				break;
		}
		return retComp;
	}

	/**
	 * Find parent of a specified component.
	 * 
	 * Given the ID of a specific component, find its containing GUIType
	 * 
	 * @param ID
	 *            String ID of component
	 * @return GUITypeWrapper for parent of specified component on success null
	 *         on failure.
	 */
	public GUITypeWrapper getParentFromID(String ID) {
		List<GUITypeWrapper> lGUIType = lGUI;

		if (lGUIType == null) {
			lGUIType = new ArrayList<GUITypeWrapper>();

			for (GUIType gui : this.dGUIStructure.getGUI()) {
				lGUIType.add(new GUITypeWrapper(gui));
			}
		}

		for (GUITypeWrapper wGUI : lGUIType) {
			if (wGUI.getChildByID(ID) != null) {
				return wGUI;
			}
		}

		return null;
	}

	/**
	 * @param signature
	 * @param name
	 * @param values
	 */
	public void addValueBySignature(AttributesType signature, String name,
			Set<String> values) {
		for (GUIType dGUI : dGUIStructure.getGUI()) {
			GUITypeWrapper wGUI = new GUITypeWrapper(dGUI);
			wGUI.addValueBySignature(signature, name, values);
		}

	}

	public void addValueBySignature(AttributesType signature, String name,
			String value) {

		Set<String> values = new HashSet<String>();
		values.add(value);
		addValueBySignature(signature, name, values);

	}

	public void updateValueBySignature(AttributesType signature, String name,
			String value) {

		Set<String> values = new HashSet<String>();
		values.add(value);
		updateValueBySignature(signature, name, values);

	}

	/**
	 * @param signature
	 * @param name
	 * @param values
	 */
	public void updateValueBySignature(AttributesType signature, String name,
			Set<String> values) {
		for (GUIType dGUI : dGUIStructure.getGUI()) {
			GUITypeWrapper wGUI = new GUITypeWrapper(dGUI);
			wGUI.updateValueBySignature(signature, name, values);
		}
	}

	// /**
	// * Add a postfix to widget ID
	// *
	// * @param sPostfix
	// */
	// public void updateID(String sPostfix) {
	// for (GUIType dGUI : dGUIStructure.getGUI()) {
	// GUITypeWrapper wGUI = new GUITypeWrapper(dGUI);
	// wGUI.updateID(sPostfix);
	// }
	// }

	/**
	 * Update widget ID
	 * 
	 * @param sPostfix
	 */
	public void updateID() {
		for (GUIType dGUI : dGUIStructure.getGUI()) {
			GUITypeWrapper wGUI = new GUITypeWrapper(dGUI);
			wGUI.updateID();
		}
	}

	/**
	 * Generate ID for widgets based on a hashcode generator
	 * 
	 * @param hashcodeGenerator
	 */
	public void generateID(GHashcodeGenerator hashcodeGenerator) {
		for (GUIType dGUI : dGUIStructure.getGUI()) {
			GUITypeWrapper wGUI = new GUITypeWrapper(dGUI);
			wGUI.generateID(hashcodeGenerator);
		}
	}

	public ComponentTypeWrapper getComponentBySignature(AttributesType signature) {

		ComponentTypeWrapper result = null;

		for (GUIType dGUI : dGUIStructure.getGUI()) {
			GUITypeWrapper wGUI = new GUITypeWrapper(dGUI);
			result = wGUI.getComponentBySignature(signature);

			if (result != null)
				return result;
		}

		return result;

	}

	public ComponentTypeWrapper getComponentBySignature(String windowTitle,
			AttributesType signature) {

		ComponentTypeWrapper result = null;

		for (GUIType dGUI : dGUIStructure.getGUI()) {
			GUITypeWrapper wGUI = new GUITypeWrapper(dGUI);
			if (windowTitle.equals(wGUI.getTitle())) {
				result = wGUI.getComponentBySignature(signature);
				if (result != null)
					return result;
			}
		}

		return result;

	}

	public void addValueByName(String sTitle, String sName, String sValue) {
		for (GUIType dGUI : dGUIStructure.getGUI()) {
			GUITypeWrapper wGUI = new GUITypeWrapper(dGUI);
			wGUI.addValueByName(sTitle, sName, sValue);
		}
	}

	/**
	 * Get the list of root windows
	 * 
	 * @return
	 */
	private Set<GUITypeWrapper> getRootWindows() {

		Set<GUITypeWrapper> rootWindows = new HashSet<GUITypeWrapper>();

		for (GUIType dGUI : dGUIStructure.getGUI()) {
			GUITypeWrapper wGUI = new GUITypeWrapper(dGUI);
			if (wGUI.isRoot())
				rootWindows.add(wGUI);
		}
		return rootWindows;

	}

	/**
	 * Get the list of root windows
	 * 
	 * @return
	 */
	public Set<GUIType> getRootGUI() {

		Set<GUIType> rootGUI = new HashSet<GUIType>();

		for (GUIType dGUI : dGUIStructure.getGUI()) {
			GUITypeWrapper wGUI = new GUITypeWrapper(dGUI);
			if (wGUI.isRoot())
				rootGUI.add(dGUI);
		}
		return rootGUI;

	}

	/**
	 * @return
	 */
	public List<GUITypeWrapper> getGUIs() {
		return this.lGUI;
	}

	/**
	 * Find the maximum widget ID. This method is used to generate ID for a new
	 * added widget
	 * <p>
	 * 
	 * @return
	 */
	public int getMaxID() {
		int max = 0;

		for (GUIType dGUI : dGUIStructure.getGUI()) {
			GUITypeWrapper wGUI = new GUITypeWrapper(dGUI);
			int iLocalMax = wGUI.getMaxID();
			if (max < iLocalMax)
				max = iLocalMax;
		}
		return max;
	}

	/**
	 * Compare "this" GUISTructure with "other" in 'obj' and return a
	 * GUIStructure containing differing elements.
	 * 
	 * @param obj
	 *            The GUIStructure to compare with.
	 * @return Returns a List<Object> containing two elements. The first is a
	 *         boolean. false=full match, true=mismatch The second is a
	 *         GUIStructure containing differences
	 */
	public List<Object> compare(Object obj) {
		int i;
		boolean retStatus = false;
		List<Object> retList = new ArrayList<Object>();

		assert (dGUIStructure != null);
		assert (!(dGUIStructure == null && obj == null));

		GUIStructure retGUIStructure = new GUIStructure();
		GUITARLog.log.debug("BEGIN GUIStruct");

		GUIStructure oGUIStructure = (GUIStructure) obj;
		if (oGUIStructure == null) {
			GUITARLog.log.debug("END GUIStruct");

			retGUIStructure.setGUI(dGUIStructure.getGUI());
			retList.add(true);
			retList.add(retGUIStructure);
			return retList;
		}

		List<GUIType> thisGUITypeList = dGUIStructure.getGUI();
		List<GUIType> oGUITypeList = ((GUIStructure) obj).getGUI();

		List<GUIType> retGUIType = new ArrayList<GUIType>();
		for (i = 0; i < Math.max(thisGUITypeList.size(), oGUITypeList.size()); i++) {
			List<Object> diffList;
			if (i < thisGUITypeList.size()) {
				diffList = (new GUITypeWrapper(thisGUITypeList.get(i)))
						.compare(i < oGUITypeList.size() ? oGUITypeList.get(i)
								: null);
			} else {
				// Switch to other since "this" is now null
				diffList = (new GUITypeWrapper(oGUITypeList.get(i)))
						.compare(null);
			}
			retStatus = retStatus || (Boolean) diffList.get(0);
			retGUIType.add((GUIType) diffList.get(1));
		}

		GUITARLog.log.debug("DONE GUIStruct");

		// Construct and return List
		retGUIStructure.setGUI(retGUIType);
		retList.add(retStatus);
		retList.add(retGUIStructure);

		return retList;
	} // End of function
	
	public List<ComponentType> getAllComponents() {
		List<ComponentType> allComponents = new ArrayList<ComponentType>();
		for(GUIType gui: this.dGUIStructure.getGUI()){
			GUITypeWrapper wGui = new GUITypeWrapper(gui);
			List<ComponentType> allGUIComponent = wGui.getAllComponents(); 
			allComponents.addAll(allGUIComponent);
		}
		return allComponents;
	}

	

} // End of class
