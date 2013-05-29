package driver;

import gui.MainWindow;


/**
 * This class is the driver for Android Electric Guitar
 * 
 * To run the Visualizer, execute this file.
 * 
 * 
 * @author Andrew Guthrie
 * @author Chris Carmel
 * @author Asif Chowdhury
 *
 */
public class Driver {

	/**
	 * Constructs and displays the Android Electric GUITAR GUI.
	 * 
	 * @param args		no arguments requires
	 */
	public static void main(String[] args) {
		MainWindow electricGuitar = new MainWindow();
		electricGuitar.setVisible(true);
		electricGuitar.getjsp().setDividerLocation((float)0.20);
	}

}