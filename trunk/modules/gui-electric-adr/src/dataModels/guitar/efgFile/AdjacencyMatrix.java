package dataModels.guitar.efgFile;
import java.util.ArrayList;
import java.util.HashMap;

import dataModels.visualizer.Event;
import dataModels.visualizer.Widget;

/**
 * This is a datastructure representation of the EFG's Adjacency Matrix.
 * 
 * @author Chris Carmel
 * 
 */
public class AdjacencyMatrix {
	
	/**
	 * ArrayList of ID strings of the events in the matrix.
	 */
	private ArrayList<String> eventIds;
	
	/**
	 * Two-dimensional integer array representing the values of the matrix.
	 */
	private int[][] matrix;
	
	/**
	 * Integer representing the number of columns and rows of the matrix.
	 */
	private int size;
	
	/**
	 * Constructs and initializes an AdjacencyMatrix of the specified size and specified event IDs.
	 * @param size		the size of the matrix
	 * @param eventIds	the array of event IDs contained in the matrix
	 */
	public AdjacencyMatrix(int size, ArrayList<String> eventIds) {
		this.eventIds = eventIds;
		this.matrix = new int[size][size];
		this.size = size;
	}
	
	/**
	 * Sets the entry of the <code>matrix</code> at the specified location to the incoming value.
	 * @param row		the row at which to enter the value	
	 * @param column	the column at which to enter the value
	 * @param value	the incoming value to be entered
	 */
	public void setEntry(int row, int column, int value) {
		matrix[row][column] = value;
	}
	
	/**
	 * Sets the <code>eventIds</code> to the incoming value.
	 * @param eventIds	the event ID strings <code>ArrayList</code> to be entered
	 */
	public void setEventIds(ArrayList<String> eventIds) {
		this.eventIds = eventIds;
	}
	
	/**
	 * Returns the entry at the specified location.
	 * @param row		row of the entry requested
	 * @param column	column of the entry requested
	 * @return			the <code>int</code> value at the specified location
	 */
	public int getEntry(int row, int column) {
		return matrix[row][column];
	}
	
	/**
	 * Returns the <code>eventIds</code> of this AdjacencyMatrix.
	 * @return		the ArrayList of event ID strings
	 */
	public ArrayList<String> getEventIds() {
		return eventIds;
	}
	
	/**
	 * Tests if two events are connected by a transition in the adjacency matrix
	 * @param fromEventId	the <code>eventId</code> of a particular starting <code>Event</code>
	 * @param toEventId		the <code>eventId</code> of a particular ending <code>Event</code>
	 * @return <code>true</code> if the two events are connected by an edge, <code>false</code> if they are not
	 */
	public boolean reaches(String fromEventId, String toEventId) {
		int toEventIndex = eventIds.indexOf(toEventId);
		int fromEventIndex = eventIds.indexOf(fromEventId);
		
		if (matrix[toEventIndex][fromEventIndex] == 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns a string version of the current state of this AdjacencyMatrix.
	 * @param eventsMap		a HashMap of Events
	 * @param widgetsMap	a HashMap of Widgets
	 * @return				a string version of this AdjacencyMatrix's current state
	 */
	public String toString(HashMap<String, Event> eventsMap, HashMap<String, Widget> widgetsMap) {
		String bar = "\t\t\t\t\t\t+", s = "\t";
		
		for (int i=0; i<size; i++) {
			bar += "---+";
		}
		
		s += bar;
		for (int i=0; i<size; i++) {
			String type = eventsMap.get(eventIds.get(i)).getType();
			if (type.equals("TERMINAL")) type += "\t";
			boolean initial = eventsMap.get(eventIds.get(i)).isInitial();
			s += "\n\t" + eventsMap.get(eventIds.get(i)).getTitle() + "\t" + type + "\t" + initial + "\t|";
			for (int j=0; j<size; j++) {
				if (((float) i / 2) == (i /2)) {
					s += " " + 	matrix[i][j] + " |";
				} else {
					s += " " + 	matrix[i][j] + "  ";
				}
			}
			s += "\n\t" + bar;
		}
		
		return s;
	}
	
}
