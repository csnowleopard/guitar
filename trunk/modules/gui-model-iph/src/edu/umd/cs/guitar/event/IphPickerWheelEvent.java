package edu.umd.cs.guitar.event;

import java.util.Hashtable;
import java.util.List;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GObject;
import edu.umd.cs.guitar.model.IphCommServer;
import edu.umd.cs.guitar.model.IphComponent;
import edu.umd.cs.guitar.model.data.PropertyType;

public class IphPickerWheelEvent implements GEvent{

	public void perform(GObject gComponent, List<String> parameters,
			Hashtable<String, List<String>> optionalData) {
		System.out.println("Send PICKER_WHEEL message w/ parameters to iphone client.");
		if (!(gComponent instanceof IphComponent)) {
			System.out.println("Error: expected IphComponent but received: " + gComponent.getClass());
			return;
		}
		
		IphComponent comp = (IphComponent) gComponent;
		
		// Get address and UIView name.
		IphCommServer.requestAndHear("INVOKE PICKER_WHEEL " + comp.getClassVal() + " " + comp.getX() + " " + comp.getY());// comp.getAddress());
	}

	public void perform(GObject gComponent,
			Hashtable<String, List<String>> optionalData) {
		System.out.println("Send PICKER_WHEEL message to iphone client.");
		if (!(gComponent instanceof IphComponent)) {
			System.out.println("Error: expected IphComponent but received: " + gComponent.getClass());
			return;
		}
		
		IphComponent comp = (IphComponent) gComponent;
		
		// Get address and UIView name.
		IphCommServer.requestAndHear("INVOKE PICKER_WHEEL " + comp.getClassVal() + " " + comp.getX() + " " + comp.getY()); //comp.getAddress());
	}

	public boolean isSupportedBy(GObject gComponent) {
		if (!(gComponent instanceof IphComponent)) {
			return false;
		}
		
		IphComponent comp = (IphComponent) gComponent;
		
		// Go through the properties and find if one contains
		// INVOKE, TOUCH.
		for (PropertyType property : comp.getGUIProperties()) {
			if (property.getName().equals("INVOKE") &&
					property.getValue().get(0).equals("PICKER_WHEEL")) {
				System.out.println("IphComponent : " + comp.getClassVal()
						+ ", supports PICKER_WHEEL");
				return true;
			}
		}
		return false;
	}

}
