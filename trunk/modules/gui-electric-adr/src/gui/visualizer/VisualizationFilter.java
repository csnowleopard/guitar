package gui.visualizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import dataModels.visualizer.Event;
import dataModels.visualizer.Widget;



/**
 * This class allows easy filtering of UI components to show in android GUI.
 * @author asif
 *
 */
public class VisualizationFilter {

	/**
	 * This method removes views from the widgetsMap and eventsMap as specified in
	 * the removeViews ArrayList. This method should be called from the driver.
	 * Remove views should have the name of the types of views you want to be removed.
	 * For example: "android.widget.Button" to remove the buttons.
	 * @param widgetsMap
	 * @param eventsMap
	 * @param removeViews
	 */
	public static void filterViews(HashMap<String, Widget> widgetsMap, HashMap<String, Event> eventsMap, ArrayList<String> removeViews) {
		
		if (removeViews == null || widgetsMap == null || eventsMap == null)
			return;
		
		HashMap<String, Widget> widgetsMapClone = (HashMap<String, Widget>) widgetsMap.clone();
		HashMap<String, Event> eventsMapClone = (HashMap<String, Event>) eventsMap.clone();
		
		/*
		 * Following loop iterates through the keys of widgetsMap and checks to see if the view is
		 * listed in removeVies, if it is, it looks for it in eventsMap and removes it from eventsMap and widgetsMap.
		 */
//		for (String widgetKey : widgetsMapClone.keySet()) {
//			if (removeViews.contains(widgetsMap.get(widgetKey).getWidgetClass())) {
//				for (String eventKey : eventsMapClone.keySet()) {
//					if (eventsMap.get(eventKey) != null && eventsMap.get(eventKey).getWidgetId().equals(widgetKey)) {
//						eventsMap.remove(eventKey);
//					}
//				}
//				widgetsMap.remove(widgetKey);
//			}
//		}
		for (String widgetKey : widgetsMapClone.keySet()) {
			if (removeViews.contains(widgetsMap.get(widgetKey).getWidgetClass())) {
				for (String eventKey : eventsMapClone.keySet()) {
					if (eventsMap.get(eventKey) != null && eventsMap.get(eventKey).getWidget().getId().equals(widgetKey)) {
						eventsMap.remove(eventKey);
					}
				}
				widgetsMap.remove(widgetKey);
			}
		}
	}
}
