import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.umd.cs.guitar.event.WebEvent;
import edu.umd.cs.guitar.event.WebSubmit;
import edu.umd.cs.guitar.event.WebTextBox;
import edu.umd.cs.guitar.event.WebToggleCheckbox;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.WebConstants;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.ContentsType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;


@SuppressWarnings("deprecation")
public class GUIWriter {
	private GUIStructure dGUIStructure = new GUIStructure();
	private ObjectFactory factory = new ObjectFactory();
	
	public GUIWriter() {
		this.dGUIStructure = new GUIStructure();
	}
	
	public void addGUIType(String url, Map<Component, ArrayList<Event>> components) {
		GUIType retGUI;
		//System.out.println("Extracting GUI Properties from window ");
		
		retGUI = factory.createGUIType();

		// ---------------------
		// Window
		// ---------------------

		ComponentType window = factory.createComponentType();

		ComponentTypeWrapper windowAdapter = new ComponentTypeWrapper(window);

		// Add properties required by GUITAR
		windowAdapter.addValueByName(GUITARConstants.TITLE_TAG_NAME, ""
				+ url);

		// Modal
		windowAdapter.addValueByName(GUITARConstants.MODAL_TAG_NAME, ""
				+ true);

		// Is Root window
		windowAdapter.addValueByName(GUITARConstants.ROOTWINDOW_TAG_NAME, ""
				+ true); //if first window opened

		window = windowAdapter.getDComponentType();

		AttributesType attributes = window.getAttributes();
		List<PropertyType> lProperties = attributes.getProperty();

		attributes.setProperty(lProperties);
		window.setAttributes(attributes);

		retGUI.setWindow(window);
		
		// Container
		
		ContainerType container = factory.createContainerType();
		ContentsType contents = factory.createContentsType();
		
		for (Component c : components.keySet()) {
			ComponentType retComp = factory.createComponentType();
			Map<String,String> attributeList = c.getAttributes();
			
			List<PropertyType> retList = new ArrayList<PropertyType>();
			
			retList.add(createProperty(GUITARConstants.ID_TAG_NAME, c.getId()));
			//need to add Class and Type?
			retList.add(createProperty(GUITARConstants.X_TAG_NAME, c.getX()));
			retList.add(createProperty(GUITARConstants.Y_TAG_NAME, c.getY()));
			
			//add events here
			ArrayList<Event> events = components.get(c);
			for (Event e : events) {
				String eventType = null;
				String type = attributeList.get("type");
				if (type != null) {
					if (type.equals("checkbox")) 
						eventType = WebToggleCheckbox.class.getName();
					else if (type.equals("submit"))
						eventType = WebSubmit.class.getName();	
				}
				if (eventType == null) {
					if (e.getEventType().equals("type"))
						eventType = WebTextBox.class.getName();
					else
						eventType = WebEvent.class.getName();
				}
				retList.add(createProperty(GUITARConstants.EVENT_TAG_NAME, eventType));
				
				//add event input value
				String inputValue = c.getInputValue();
				
					List<PropertyType> optionalList = new ArrayList<PropertyType>();
					AttributesType optional = factory.createAttributesType();
					if (inputValue != null)
						optionalList.add(createProperty(GUITARConstants.INPUT_VALUE_PROPERTY_TYPE, inputValue));
					else 
						optionalList.add(createProperty(GUITARConstants.INPUT_VALUE_PROPERTY_TYPE, "1"));
					optional.setProperty(optionalList);
					retComp.setOptional(optional);
				
			}
			
			retList.add(createProperty(WebConstants.TITLE_TAG, c.getTag()));
			String value = attributeList.get("value");
			if (value != null)
				retList.add(createProperty(WebConstants.VALUE_TAG, value));
			
			//this should equal the value for 'Target' from sIDE
/*			String tag = c.getTarget();
			if (tag != null)
				retList.add(createProperty(WebConstants.TAGID_TAG, tag));
*/			
			String href = attributeList.get("href");
			if (href != null) 
				retList.add(createProperty(WebConstants.HREF_TAG, href));
			String name = attributeList.get("name");
			if (name != null)
				retList.add(createProperty(WebConstants.NAME_TAG, name));
			
			AttributesType compAttributes = factory.createAttributesType();
			compAttributes.setProperty(retList);
			retComp.setAttributes(compAttributes);
			contents.getWidgetOrContainer().add(retComp);
		}
		container.setContents(contents);
		retGUI.setContainer(container);
		this.dGUIStructure.getGUI().add(retGUI);
	}
	
	private PropertyType createProperty(String name, String value) {
		PropertyType property = factory.createPropertyType(); 
		property.setName(name);
		ArrayList<String>lPropertyValue = new ArrayList<String>();
		lPropertyValue.add(value);
		property.setValue(lPropertyValue);
		
		return property;
	}
	
	public void printGUI(String filename) {
		System.out.println("Writing GUI");
		IO.writeObjToFile(dGUIStructure, filename);
	}
}