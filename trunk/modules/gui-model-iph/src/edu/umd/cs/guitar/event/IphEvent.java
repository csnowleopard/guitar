package edu.umd.cs.guitar.event;

import java.util.Hashtable;
import java.util.List;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GObject;

public class IphEvent implements GEvent{

	public void perform(GObject gComponent, List<String> parameters,
			Hashtable<String, List<String>> optionalData) {
		// TODO Auto-generated method stub
		
	}

	public void perform(GObject gComponent,
			Hashtable<String, List<String>> optionalData) {
		// TODO Auto-generated method stub
		
	}

	public boolean isSupportedBy(GObject gComponent) {
		// TODO Auto-generated method stub
		return false;
	}

}
