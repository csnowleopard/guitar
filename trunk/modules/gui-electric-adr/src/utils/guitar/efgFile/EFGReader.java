package utils.guitar.efgFile;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dataModels.guitar.efgFile.AdjacencyMatrix;
import dataModels.visualizer.Event;
import dataModels.visualizer.VisualizationData;
import dataModels.visualizer.Widget;


/**
 * This class parses the EFG and enters the extracted information into the VisualizationData data structure
 * @author Chris Carmel
 *
 */
public class EFGReader {

	/**
	 * Regular expression used to compare against the EFG tag.
	 */
	static Pattern efgTag 			= Pattern.compile("^<EFG>$");

	/**
	 * Regular expression used to compare against the Events tag.
	 */
	static Pattern eventsTag 		= Pattern.compile("^<Events>$");

	/**
	 * Regular expression used to compare against the Event tag.
	 */
	static Pattern eventTag 		= Pattern.compile("^<Event>$");

	/**
	 * Regular expression used to compare against the EventId tag.
	 */
	static Pattern eventIdTag 		= Pattern.compile("^<EventId>(.+)</EventId>$");

	/**
	 * Regular expression used to compare against the WidgetId tag.
	 */
	static Pattern widgetIdTag 		= Pattern.compile("^<WidgetId>(.+)</WidgetId>$");

	/**
	 * Regular expression used to catch the type of Event from the Type tag.
	 */
	static Pattern typeTag 			= Pattern.compile("^<Type>(.+)</Type>$");

	/**
	 * Regular expression used to catch the initial status of the Event from the Initial tag.
	 */
	static Pattern initialTag 		= Pattern.compile("^<Initial>(.+)</Initial>$");

	/**
	 * Regular expression used to catch the action of the Event from the Action tag.
	 */
	static Pattern actionTag 		= Pattern.compile("^<Action>(.+)</Action>$");

	/**
	 * Regular expression used to compare against the closing Event tag.
	 */
	static Pattern closeEventTag	= Pattern.compile("^</Event>$");

	/**
	 * Regular expression used to compare against the EventGraph tag.
	 */
	static Pattern eventGraphTag 	= Pattern.compile("^<EventGraph>$");

	/**
	 * Regular expression used to compare against the Row tag.
	 */
	static Pattern rowTag 			= Pattern.compile("^<Row>$");

	/**
	 * Regular expression used to catch the value of the entry of the Event graph from the E tag.
	 */
	static Pattern eTag 			= Pattern.compile("^<E>(.+)</E>$");

	/**
	 * Regular expression used to compare against the closing Row tag.
	 */
	static Pattern closeRowTag		= Pattern.compile("^</Row>$");

	/**
	 * Parses the EFG and returns the extracted information in the VisualizationData data structure
	 * 
	 * @param filepath 		path to the EFG file
	 * @param vd 			VisualizationData structure to be populated
	 * @return 				returns the VisualizationData data structure with the added information from the EFG
	 * @throws IOException
	 * 			if the file cannot be found
	 */
	public static VisualizationData processEFG(String filepath, VisualizationData vd) throws IOException {
		HashMap<String, Event> eventsMap = new HashMap<String, Event>();
		HashMap<String, Widget> widgetsMap = new HashMap<String, Widget>();
		AdjacencyMatrix adjMat = new AdjacencyMatrix(0, new ArrayList<String>());
		
		// Local Variables
		FileReader fr = new FileReader(filepath); // open EFG
		BufferedReader br = new BufferedReader(fr);
		ArrayList<String> eventIds = new ArrayList<String>();
		Event currEvent = null;
		Matcher m;
		String lineIn, attributeIn, entryIn;
		int i = -1, j = -1;

		// Actual EFG reader

		while((lineIn = br.readLine()) != null) {
			lineIn = lineIn.trim();
			if (eventTag.matcher(lineIn).matches()) { // get event
				currEvent = new Event();
				while ((attributeIn = br.readLine()) != null) {
					attributeIn = attributeIn.trim();
					if ((m = eventIdTag.matcher(attributeIn)).matches()) { // get event's eventId
						eventIds.add(m.group(1));
						currEvent.setTitle(m.group(1));
						currEvent.setId(m.group(1));
					} else if ((m = widgetIdTag.matcher(attributeIn)).matches()) { // get event's widgetId
						Widget currWidget = new Widget(m.group(1));
						
						currWidget.setEvent(currEvent);
						currEvent.setWidget(currWidget);
						
						widgetsMap.put(currWidget.getId(), currWidget);
					} else if ((m = typeTag.matcher(attributeIn)).matches()) { // get event's type
						currEvent.setType(m.group(1));
					} else if ((m = initialTag.matcher(attributeIn)).matches()) { // get event's initial
						if (m.group(1).equals("true")) {
							currEvent.setInitial(true);
						} else {
							currEvent.setInitial(false);
						}
					} else if ((m = actionTag.matcher(attributeIn)).matches()) { // get event's action
						currEvent.setAction(m.group(1));
					} else if (closeEventTag.matcher(attributeIn).matches()) {
						eventsMap.put(currEvent.getId(), currEvent);
						break;
					}
				}
			} else if (eventGraphTag.matcher(lineIn).matches()) { // get adjacency matrix
				adjMat = new AdjacencyMatrix(eventsMap.size(), eventIds);
				while ((entryIn = br.readLine()) != null) {
					entryIn = entryIn.trim();
					if (rowTag.matcher(entryIn).matches()) { // get row
						i++;
						j = -1;
					} else if ((m = eTag.matcher(entryIn)).matches()) { // get entry
						j++;
						adjMat.setEntry(i, j, Integer.parseInt(m.group(1)));
					}
				}
			}
		}
		fr.close(); // close EFG

		// Populating events with edges via adjacency matrix

		i = 0;
		j = 0;
		for (String rthRow : adjMat.getEventIds()) {
			for (String cthCol : adjMat.getEventIds()) {
				switch (adjMat.getEntry(i, j)) {
				case 1:
					eventsMap.get(cthCol).addNormalEdgeToSelfFrom(rthRow);
					eventsMap.get(rthRow).addNormalEdgeFromSelfTo(cthCol);
					break;
				case 2:
					eventsMap.get(cthCol).addReachingEdgeToSelfFrom(rthRow);
					eventsMap.get(rthRow).addReachingEdgeFromSelfTo(cthCol);
					break;
				}
				j++;
			}
			i++;
			j = 0;
		}
		
		vd.setEventsMap(eventsMap);
		vd.setWidgetsMap(widgetsMap);
		vd.setAdjMat(adjMat);
		return vd;
	}
}
