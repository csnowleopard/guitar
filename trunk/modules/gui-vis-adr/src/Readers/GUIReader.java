package Readers;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class parses the GUI file and enters the extracted information into the VisualizationData data structure
 * @author Chris Carmel
 *
 */
public class GUIReader {

	/**
	 * Regular expression use to compare against the GUIStructure tag.
	 */
	static Pattern GUIStructureTag	= Pattern.compile("^<GUIStructure>$");

	/**
	 * Regular expression use to compare against the GUI tag.
	 */
	static Pattern GUITag			= Pattern.compile("^<GUI>$");

	/**
	 * Regular expression use to compare against the Container tag.
	 */
	static Pattern containerTag		= Pattern.compile("^<Container>$");

	/**
	 * Regular expression use to compare against the Contents tag.
	 */
	static Pattern contentsTag		= Pattern.compile("^<Contents>$");

	/**
	 * Regular expression use to compare against the Widget tag.
	 */
	static Pattern widgetTag		= Pattern.compile("^<Widget>$");

	/**
	 * Regular expression use to compare against the closing Widget tag.
	 */
	static Pattern closeWidgetTag	= Pattern.compile("^</Widget>$");

	/**
	 * Regular expression use to compare against the Window tag.
	 */
	static Pattern windowTag		= Pattern.compile("^<Window>$");

	/**
	 * Regular expression use to compare against the Attributes tag.
	 */
	static Pattern attributesTag	= Pattern.compile("^<Attributes>$");

	/**
	 * Regular expression use to compare against the Property tag.
	 */
	static Pattern propertyTag		= Pattern.compile("^<Property>$");

	/**
	 * Regular expression use to catch the name of the property from the Name tag.
	 */
	static Pattern nameTag			= Pattern.compile("^<Name>(.+)</Name>$");

	/**
	 * Regular expression use to catch the value of the property from the Value tag.
	 */
	static Pattern valueTag			= Pattern.compile("^<Value>(.+)</Value>$");
	
	
	/**
	 * Parses the GUI file and returns the extracted information in the VisualizationData data structure.
	 * 
	 * @param filepath 		path to the GUI file
	 * @param vd 			VisualizationData structure to be populated
	 * @return 				returns the VisualizationData data structure with the added information from the GUI
	 * @throws IOException
	 * 			if the file cannot be found
	 */
	public static VisualizationData processGUI(String filePath, VisualizationData vd) throws IOException {
		HashMap<String, Widget> widgetsMap = vd.getWidgetsMap();
		ArrayList<Window> windows = new ArrayList<Window>();
		
		FileReader fr = new FileReader(filePath); // open GUI
		BufferedReader br = new BufferedReader(fr);
		Widget currWidget;
		ArrayList<String> widgetIds = new ArrayList<String>();
		Matcher n;

		Window currWindow = null;

		String widgetClass = null;
		String type = null;
		String replayableAction = null;
		String title = null;
		String widgetId = null;

		java.lang.Float x = new java.lang.Float(0.0);
		java.lang.Float y = new java.lang.Float(0.0);
		
		String lineIn, attributeIn;
		Matcher m = null;
		
		while((lineIn = br.readLine()) != null) {
			lineIn = lineIn.trim();
			if (windowTag.matcher(lineIn).matches()) {
				while ((attributeIn = br.readLine()) != null) {
					attributeIn = attributeIn.trim();
					if ((m = nameTag.matcher(attributeIn)).matches()) {
						if (m.group(1).equals("Title")) {
							if ((n = valueTag.matcher(br.readLine().trim())).matches()) {
								currWindow = new Window(n.group(1));
								windows.add(currWindow);
								break;
							}
						}
					}
				}
			} else if (widgetTag.matcher(lineIn).matches()) {
				while ((attributeIn = br.readLine()) != null) {
					attributeIn = attributeIn.trim();
					if ((m = nameTag.matcher(attributeIn)).matches()) {
						if ((n = valueTag.matcher(br.readLine().trim())).matches()) {
							if(m.group(1).equals("Class")){
								widgetClass = n.group(1); 
							} else if(m.group(1).equals("Type")){
								type = n.group(1);
							} else if(m.group(1).equals("X")){
								x = java.lang.Float.parseFloat(n.group(1));
							} else if(m.group(1).equals("Y")){
								y = java.lang.Float.parseFloat(n.group(1));
							} else if(m.group(1).equals("ReplayableAction")){
								replayableAction = n.group(1);
							} else if(m.group(1).equals("Title")){
								title = n.group(1);
							} else if(m.group(1).equals("ID")){
								widgetId = n.group(1);
							}
						}
					} else if (closeWidgetTag.matcher(attributeIn).matches()) {
						if ((currWidget = widgetsMap.get(widgetId)) == null) break;
						
						currWidget.setWidgetAttributes(widgetId, type, null, currWindow, new Point2D.Float(x, y), title, true, widgetClass, replayableAction);
						currWidget.getEvent().setWindow(currWindow);
						
						currWindow.addWidget(currWidget);
						
						widgetIds.add(currWidget.getId());
						break;
					}
				}
			}
		}
		fr.close(); // close GUI
		
		vd.setWindows(windows);
		return vd;
	}
}
