package edu.umd.cs.guitar.replayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.model.ADRApplication;
import edu.umd.cs.guitar.model.ADRConstants;
import edu.umd.cs.guitar.model.ADRWindow;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.util.GUITARLog;

public class ADRReplayerMonitor extends GReplayerMonitor {

	String MAIN_CLASS;
	int port;
	//Set<String> visitedWindowSet;

	public ADRReplayerMonitor(String main_class, int port) {
		super();
		MAIN_CLASS = main_class;
		this.port = port;
	}

	@Override
	public void connectToApplication() {
		application = new ADRApplication(MAIN_CLASS, port);
		application.connect();
	}

	@Override
	public void setUp() {
		// TODO Auto-generated method stub		
	}

	@Override
	public void cleanUp() {
		((ADRApplication)application).disconnect();
	}

	@Override
	public GWindow getWindow(String sWindowTitle) {
		GWindow retGXWindow = null;
		Set<GWindow> windows = null;
		String list[] = sWindowTitle.split("\\.");

	//	if (visitedWindowSet.contains(sWindowTitle)) {
			windows = ((ADRApplication)application).gotoWindow(sWindowTitle, list[list.length-1]);
		//}

		if (windows == null)
			windows = application.getAllWindow();

		for (GWindow aWindow : windows) {
			if (aWindow.getTitle().equals(sWindowTitle)) {
				retGXWindow = aWindow;
				break;
			}
		}
		delay(ADRReplayerConfiguration.DELAY);

//		visitedWindowSet.add(sWindowTitle);
		return retGXWindow;
	}

	@Override
	public GEvent getAction(String actionName) {
		GEvent retAction = null;
		try {
			Class<?> c = Class.forName(actionName);
			Object action = c.newInstance();

			retAction = (GEvent) action;

		} catch (Exception e) {
			GUITARLog.log.error("Error in getting action", e);
		}

		return retAction;
	}

	@Override
	public Object getArguments(String action) {
		return null;
	}

	@Override
	public List<PropertyType> selectIDProperties(ComponentType comp) {
		if (comp == null)
			return new ArrayList<PropertyType>();

		List<PropertyType> retIDProperties = new ArrayList<PropertyType>();

		AttributesType attributes = comp.getAttributes();
		List<PropertyType> lProperties = attributes.getProperty();
		for (PropertyType p : lProperties) {
			if (ADRConstants.ID_PROPERTIES.contains(p.getName()))
				retIDProperties.add(p);
		}
		return retIDProperties;
	}

	@Override
	public void delay(int delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}