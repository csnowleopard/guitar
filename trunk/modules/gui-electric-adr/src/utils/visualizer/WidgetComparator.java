package utils.visualizer;

import java.util.Comparator;

import dataModels.visualizer.Widget;

/**
 * This class is a comparator that makes use of the Widget's compareTo method.
 * 
 * @author Chris Carmel
 *
 */
public class WidgetComparator implements Comparator<Widget> {

	/**
	 * Compares thisWidget to thatWidget using the Widget's compareTo method.
	 */
	public int compare(Widget thisWidget, Widget thatWidget) {
		return thisWidget.compareTo(thatWidget);
	}
	
}
