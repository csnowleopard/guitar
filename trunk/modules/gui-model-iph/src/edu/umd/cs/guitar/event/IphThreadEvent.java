package edu.umd.cs.guitar.event;

import java.util.Hashtable;
import java.util.List;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GObject;

public class IphThreadEvent extends GThreadEvent{

	@Override
	public boolean isSupportedBy(GObject gComponent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void performImpl(GObject gComponent,
			Hashtable<String, List<String>> optionalData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void performImpl(GObject gComponent, Object parameters,
			Hashtable<String, List<String>> optionalData) {
		// TODO Auto-generated method stub
		
	}

}
