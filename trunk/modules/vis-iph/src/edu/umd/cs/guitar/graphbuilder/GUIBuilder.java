package edu.umd.cs.guitar.graphbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import edu.umd.cs.guitar.gen.ComponentType;
import edu.umd.cs.guitar.gen.ContainerType;
import edu.umd.cs.guitar.gen.GUIStructure;
import edu.umd.cs.guitar.gen.GUIType;
import edu.umd.cs.guitar.gen.PropertyType;
import edu.umd.cs.guitar.gui.InputException;
import edu.umd.cs.guitar.helper.iGUITARHelper;
import edu.umd.cs.guitar.parser.XMLParser;

/**
 * This class is only used for testing purpose
 * @author Muhammad Ashraf Ishak
 *
 */
public class GUIBuilder {

	private GUIStructure gui;
	// It holds the list of all widgets, regardless of view
	private HashMap<String, ComponentType> widgetList = null;
	// Mapping:  View Title => Map of {Widget ID, ComponentType}
	private HashMap<String, HashMap<String, ComponentType>> multipleView = new HashMap<String, HashMap<String, ComponentType>>();
	
	/**
	 * Constructor needs the file name of GUI structure
	 * @param guiFileName
	 * @throws InputException 
	 */
	public GUIBuilder(String guiFileName) throws InputException{
		// Initialize XML parser
		XMLParser parser = new XMLParser();
		parser.setGuiFileName(guiFileName); //put GUI Structure file here
		
		// Generate GUI Structure object from parser
        GUIStructure guiStruc = parser.getGUIStructure();
        this.gui = guiStruc;
        if(guiStruc != null){
        	if (widgetList == null){
            	widgetList = new HashMap<String, ComponentType>();
        	}
        	for (GUIType guiOne: guiStruc.getGUI()){
        		String viewTitle = iGUITARHelper.getProperty(guiOne.getWindow().getAttributes().getProperty(), "Title");
        		if (multipleView.get(viewTitle) == null){
            		multipleView.put(viewTitle, new HashMap<String, ComponentType>());
        		}
        		ContainerType container = guiOne.getContainer();
    	    	getWidgetsX(container.getContents().getWidgetOrContainer(),viewTitle,0,0,0);

        	}	    	
        }else{
        	throw new InputException("Error: Unable to process from GUI file!\nThe program will now exit.");
        }
	}
	
	public GUIBuilder(XMLParser parser){
		// Generate GUI Structure object from parser
        GUIStructure guiStruc = parser.getGUIStructure();
        this.gui = guiStruc;
        if(guiStruc != null){
        	if (widgetList == null){
            	widgetList = new HashMap<String, ComponentType>();
        	}
        	for (GUIType guiOne: guiStruc.getGUI()){
        		String viewTitle = iGUITARHelper.getProperty(guiOne.getWindow().getAttributes().getProperty(), "Title");
        		if (multipleView.get(viewTitle) == null){
            		multipleView.put(viewTitle, new HashMap<String, ComponentType>());
        		}
        		ContainerType container = guiOne.getContainer();
    	    	getWidgetsX(container.getContents().getWidgetOrContainer(),viewTitle,0,0,0);

        	}	    	
        }
	}
	
	
	private void getWidgetsX(List<ComponentType> componentList, String viewTitle, int px, int py, int tabnum){ 
		if (componentList.size() == 0){
			return;
		} else {
			
			for (ComponentType comp: componentList){
				int currCoordinateX = Integer.parseInt
										(iGUITARHelper.getProperty(comp.getAttributes().getProperty(), "x"))
									  + px;
				int currCoordinateY = Integer.parseInt
										(iGUITARHelper.getProperty(comp.getAttributes().getProperty(), "y"))
									  + py;
				String id = iGUITARHelper.getProperty(comp.getAttributes().getProperty(), "ID");
				if (!multipleView.get(viewTitle).containsKey(id)){
					PropertyType propX = new PropertyType();
					propX.setName("x_absolute");
					propX.getValue().add(String.valueOf(currCoordinateX));
					PropertyType propY = new PropertyType();
					propY.setName("y_absolute");
					propY.getValue().add(String.valueOf(currCoordinateY));
					comp.getAttributes().getProperty().add(propX);
					comp.getAttributes().getProperty().add(propY);
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < tabnum; i++){
						sb.append("\t");
					}
					widgetList.put(id, comp);
					HashMap<String, ComponentType> temp = multipleView.get(viewTitle);
					temp.put(id, comp);
					multipleView.put(viewTitle, temp);
				}
				if (comp instanceof ContainerType){
					getWidgetsX(((ContainerType) comp).getContents().getWidgetOrContainer(),viewTitle,
								currCoordinateX, currCoordinateY, tabnum + 1);
				}
			}
		}
	}
	
	/**
	 * Get the View title associated with the Widget with the WidgetID (with assumption <br />
	 * that all widgetID is unique)
	 * @param widgetID
	 * @return
	 */
	public String getViewTitle (String widgetID){
		HashMap <String, ArrayList<ComponentType>> temp = getAllComponentList();
		for (String view: temp.keySet()){
			for (ComponentType c: temp.get(view)){
				if (iGUITARHelper.getProperty(c.getAttributes().getProperty(), "ID").equals(widgetID)){
					return view;
				}
			}
		}
		return null;
	}
	/**
	 * Retrieve list of ComponentType based on the View title
	 * @param viewTitle View title from the "Title" property of a WindowType
	 * @return
	 */
	public ArrayList<ComponentType> getComponentList (String viewTitle){
		ArrayList <ComponentType> temp = new ArrayList<ComponentType>();
		for (String id: multipleView.get(viewTitle).keySet()){
			temp.add(multipleView.get(viewTitle).get(id));
		}
		return temp;
	}
	
	/**
	 * Retrieve list of all ComponentType which are mapped to their respective View
	 * @return
	 */
	public HashMap <String, ArrayList<ComponentType>> getAllComponentList (){
		HashMap <String, ArrayList<ComponentType>> result = new HashMap <String, ArrayList<ComponentType>>();
		for (String view: multipleView.keySet()){
			result.put(view, getComponentList(view));
		}
		return result;
	}
	
	/**
	 * Retrieve ComponentType from the collection of GUI Structure
	 * @param viewTitle
	 * @param widgetID
	 * @return
	 */
	public ComponentType getComponent (String viewTitle, String widgetID){
		return multipleView.get(viewTitle).get(widgetID);
	}
	
	/**
	 * Get list of all widgets (after absolute coordinate calculation) <br />
	 * regardless of the view
	 * @return
	 */
	public HashMap<String, ComponentType> getWidgetList() {
		return widgetList;
	}
	

	public void setGui(GUIStructure gui) {
		this.gui = gui;
	}

	public GUIStructure getGui() {
		return gui;
	}

	

	public void setWidgetList(HashMap<String, ComponentType> widgetList) {
		this.widgetList = widgetList;
	}
	
	

	// FOR TESTING PURPOSE ONLY
	public void printGUI(GUIStructure gui){
		for (GUIType g: gui.getGUI()){
			ArrayList<ComponentType> test = new ArrayList<ComponentType>();
			test.add(g.getWindow());
			printGUIX(test,0);
			printGUIX(g.getContainer().getContents().getWidgetOrContainer(),0);
		}
	}
	
	// FOR TESTING PURPOSE ONLY
	public void printGUIX(List<ComponentType> componentList, int num){
		if (componentList.size() == 0){
			return;
		} else {
			for (ComponentType comp: componentList){
				String x = iGUITARHelper.getProperty(comp.getAttributes().getProperty(), "x_absolute");
				String y = iGUITARHelper.getProperty(comp.getAttributes().getProperty(), "y_absolute");
				String width = iGUITARHelper.getProperty(comp.getAttributes().getProperty(), "width");
				String height = iGUITARHelper.getProperty(comp.getAttributes().getProperty(), "height");
				for (PropertyType prop: comp.getAttributes().getProperty()){
					String name = prop.getName();
					if (name.equals("ID")){
						String value = prop.getValue().get(0);
						String tab = "";
						for (int i = 0; i < num; i++){
							tab += "\t";
						}
						System.out.println(tab+value+" ["+x+","+y+"] - ("+height+"x"+width+")");
					}
				}
				if (comp instanceof ContainerType){
					printGUIX(((ContainerType) comp).getContents().getWidgetOrContainer(), num + 1);
				}
			}
		}
	}

}
