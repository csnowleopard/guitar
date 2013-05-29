package dataModels.visualizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JCheckBox;

import utils.guitar.efgFile.EFGReader;
import utils.guitar.guiFile.GUIReader;
import utils.guitar.testCase.TSTReader;


import dataModels.guitar.efgFile.AdjacencyMatrix;
import dataModels.guitar.testCase.TestCase;


import edu.umd.cs.piccolo.PLayer;
import gui.visualizer.PVisualizationCanvas;


/**
 * Data structure that holds all the data necessary for the visualization tool.
 * 
 * @author Chris Carmel
 *
 */
public class VisualizationData {

	/**
	 * Name of the ripped application, it will become part of the data directory and indicate
	 * where the necessary files for the visualization reside.
	 */
	private String applicationTitle;

	/**
	 * Directory where the EFG and GUI files reside.
	 */
	private String dataDir;

	/**
	 * Directory where the screenshots of the ripped application are stored.
	 */
	private String screenshotsDir;

	/**
	 * Directory where the testcases of the ripped application are stored.
	 */
	private String testCasesDir;

	/**
	 * HashMap that contains all the widgets extracted from the GUI file.
	 */
	private HashMap<String, Widget> widgetsMap;

	/**
	 * HashMap that contains all the events extracted from the EFG file.
	 */
	private HashMap<String, Event> eventsMap;

	/**
	 * List of all windows of the ripped application.
	 */
	private ArrayList<Window> windows;

	/**
	 * Adjacency matrix of the EFG that holds information about edges between Events.
	 */
	private AdjacencyMatrix adjMat;

	/**
	 * PVisualizationCanvas that displays the EFG Visualization.
	 */
	private PVisualizationCanvas pvc;
	
	/**
	 * PLayer representing the current layer of edges that is being used on the PVisualizationCanvas.
	 */
	private PLayer currEdgeLayer = null;
	
	/**
	 * ArrayList of TestCasees for the ripped application.
	 */
	private ArrayList<TestCase> testCases;
	
	/**
	 * Constructs an empty VisualizationData structure.
	 */
	public VisualizationData() {
		this.applicationTitle = null;
		this.dataDir = null;
		this.screenshotsDir = null;
		this.testCasesDir = null;
		this.widgetsMap = null;
		this.eventsMap = null;
		this.windows = null;
		this.adjMat = null;
		this.pvc = new PVisualizationCanvas(this);
		this.testCases = null;
	}

	/**
	 * Constructs and populates a VisualizationData structure for the given application.
	 * 
	 * @param applicationTitle		the title of the given application	
	 */
	public VisualizationData(String applicationTitle) {
		this.applicationTitle = applicationTitle;
//		this.dataDir = "../../../data/" + applicationTitle + "/";
		this.dataDir = "data/" + applicationTitle + "/";
		this.screenshotsDir = dataDir + "screenshots/";
		this.testCasesDir = dataDir + "testcases/";
		this.widgetsMap = null;
		this.eventsMap = null;
		this.windows = null;
		this.adjMat = null;
		this.testCases = new ArrayList<TestCase>();

		try {
			processData(dataDir + applicationTitle + ".EFG", 
						dataDir + applicationTitle + ".GUI", testCasesDir);
		} catch (IOException e) {
			displayOptions();
		}
		this.pvc = new PVisualizationCanvas(this);
	}
	
	/**
	 * Constructs and populates a VisualizationData structure for the given application.
	 * 
	 * @param applicationTitle		the title of the given application	
	 */
	public VisualizationData(String applicationTitle, String directory) {
		this.applicationTitle = applicationTitle;
//		this.dataDir = "../../../data/" + applicationTitle + "/";
		this.dataDir = directory + applicationTitle + "/";
		this.screenshotsDir = this.dataDir + "screenshots/";
		this.testCasesDir = this.dataDir + "testcases/";
		this.widgetsMap = null;
		this.eventsMap = null;
		this.windows = null;
		this.adjMat = null;
		this.testCases = new ArrayList<TestCase>();

		try {
			processData(dataDir + applicationTitle + ".EFG", 
						dataDir + applicationTitle + ".GUI", testCasesDir);
		} catch (IOException e) {
			displayOptions();
		}
		this.pvc = new PVisualizationCanvas(this);
	}

	/**
	 * Constructs and partially populates a VisualizationData structure.
	 * 
	 * This constructor only populates the widgetsMap, eventsMap, windows, and 
	 * adjMat attributes of the VisualizationData.
	 * 
	 * @param efgFile		path to the EFG of the given application
	 * @param guiFile		path to the GUI of the given application
	 */
	/*public VisualizationData(String efgFile, String guiFile) {
		this.applicationTitle = null;
		this.dataDir = null;
		this.screenshotsDir = null;
		this.testCasesDir = null;
		this.widgetsMap = null;
		this.eventsMap = null;
		this.windows = null;
		this.adjMat = null;
		this.testCases = null;

		try {
			processData(efgFile, guiFile);
		} catch (IOException e) {
			displayOptions();
		}
		
		this.pvc = null;
	}*/

	/**
	 * Constructs and populates a VisualizationData structure.
	 * 
	 * This constructor is for constructing the VisualizationData structure
	 * and populating it with already existing attributes.
	 * 
	 * @param widgetsMap			HashMap to set the VisualizationData's widgetsMap to
	 * @param eventsMap				HashMap to set the VisualizationData's eventsMap to
	 * @param windows				ArrayList to set the VisualizationData's eventsMap to
	 * @param adjMat				AdjacencyMatrix to set the VisualizationData's adjMat to
	 * @param applicationTitle		title of the application to set the VisualizationData's to
	 * @param dataDir				location of the data directory to set the VisualizationData's dataDir to
	 * @param screenshotsDir		location of the screenshots directory to set the VisualizationData's screenshotsDir to
	 * @param pvc					PVisualizationCanvas to se the VisualizationData's pvc to
	 */
	public VisualizationData(HashMap<String, Widget> widgetsMap,
			HashMap<String, Event> eventsMap, ArrayList<Window> windows,
			AdjacencyMatrix adjMat, String applicationTitle, String dataDir,
			String screenshotsDir, PVisualizationCanvas pvc) {
		this.applicationTitle = applicationTitle;
		this.dataDir = dataDir;
		this.screenshotsDir = screenshotsDir;
		this.widgetsMap = widgetsMap;
		this.eventsMap = eventsMap;
		this.windows = windows;
		this.adjMat = adjMat;
		this.pvc = pvc;
	}
	
	/**
	 * Processes the .EFG and .GUI files.
	 * @param efg	relative path to the .EFG file for the app
	 * @param gui	relative path to the .GUI file for the app
	 * @throws IOException
	 */
	private void processData(String efg, String gui) throws IOException{
			EFGReader.processEFG(efg, this);
			GUIReader.processGUI(gui, this);
	}
	
	/**
	 * Process .EFG, .GUI, and .TST files.
	 * @param efg	relative path to the .EFG file for the app
	 * @param gui	relative path to the .GUI file for the app
	 * @param tst	relative path to the .TST directory for the app
	 * @throws IOException
	 */
	private void processData(String efg, String gui, String tst) throws IOException{
			processData(efg, gui);
			TSTReader.processTSTDirectory(tst, this);
	}
	
	/**
	 * This function gives the user the option to run the visualization for one of the
	 * apps included with the visualizer jar.
	 */
	private void displayOptions() {
		/*HashMap<Integer, String> appNames = new HashMap<Integer, String>();
		
		appNames.put(1, "TippyTipper");
		appNames.put(2, "ContactManager");
		appNames.put(3, "ToDoManager");
		appNames.put(4, "HelloAUT");
		
		System.out.println("\nYou must run the ripper on the application before you can run the visualizer." 
							+ "\nIf you wish to see visualization for " 
							+ "the following prepackaged apps " 
							+ "please enter the number associated with the name of the application.\n");
		for (Integer i : appNames.keySet()) {
			System.out.println(i + ": " + appNames.get(i));
		}
		System.out.println("\nEnter number:");*/
		
		try {
			/*BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			int i;
			try {
				i = Integer.parseInt(br.readLine());
			} catch (NumberFormatException e) {
				i = 1;
			}*/
			
			//this.applicationTitle = appNames.get(i);
			//this.applicationTitle = "a";
			this.dataDir = "data/" + applicationTitle + "/";
			this.screenshotsDir = dataDir + "screenshots/";
			this.testCasesDir = dataDir + "testcases/";
			
			EFGReader.processEFG(dataDir + applicationTitle + ".EFG", this);
			GUIReader.processGUI(dataDir + applicationTitle + ".GUI", this);
			TSTReader.processTSTDirectory(testCasesDir, this);
			
			this.pvc = new PVisualizationCanvas(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the title of this VisualizationData's application.
	 * 
	 * @return	the title of this VisualizationData's application
	 */
	public String getApplicationTitle() {
		return applicationTitle;
	}

	/**
	 * Returns the location of the data directory of this VisualizationData's application.
	 * 
	 * @return		the location of the data directory of this VisualizationData's application
	 */
	public String getDataDirectory() {
		return dataDir;
	}

	/**
	 * Returns the location of the screenshots directory of this VisualizationData's application.
	 * 
	 * @return		the location of the screenshots directory of this VisualizationData's application
	 */
	public String getScreenshotsDirectory() {
		return screenshotsDir;
	}
	
	/**
	 * Returns the location of the testcases directory of this VisualizationData's application.
	 * 
	 * @return		the location of the testcases directory of this VisualizationData's application
	 */
	public String getUserTestCaseDirectory() {
		return testCasesDir;
	}	

	/**
	 * Returns the HashMap of Widgets in this VisualizationData's application.
	 * 
	 * @return		the HashMap of Widgets in this VisualizationData's application
	 */
	public HashMap<String, Widget> getWidgetsMap() {
		return widgetsMap;
	}

	/**
	 * Returns the HashMap of Events in this VisualizationData's application.
	 * 
	 * @return		the HashMap of Events in this VisualizationData's application 
	 */
	public HashMap<String, Event> getEventsMap() {
		return eventsMap;
	}

	/**
	 * Returns the ArrayList of Windows in this VisualizationData's application.
	 * 
	 * @return		the ArrayList of Windows in this VisualizationData's application
	 */
	public ArrayList<Window> getWindows() {
		return windows;
	}

	/**
	 * Returns the AdjacencyMatrix of this VisualizationData's application.
	 * 
	 * @return		the AdjacencyMatrix of this VisualizationData's application
	 */
	public AdjacencyMatrix getAdjMat() {
		return adjMat;
	}
	
	/**
	 * Returns this VisualizationData's PVisualizationCanvas.
	 * 
	 * @return		this VisualizationData's PVisualizationCanvas
	 */
	public PVisualizationCanvas getPVisualizationCanvas() {
		return pvc;
	}
	
	/**
	 * Returns this VisualizationData's TestCase ArrayList.
	 * 
	 * @return		this VisualizationData's TestCase ArrayList
	 */
	public ArrayList<TestCase> getTestCases() {
		return testCases;
	}
	
	/**
	 * Returns this VisualizationData's currentEdgeLayer.
	 * 
	 * @return		this VisualizationData's currentEdgeLayer
	 */
	public PLayer getCurrentEdgeLayer() {
		return currEdgeLayer;
	}

	/**
	 * Sets this VisualizationData's application title to the incoming value.
	 * 
	 * @param applicationTitle		value to set this VisualizationData's application title to
	 */
	public void setApplicationTitle(String applicationTitle) {
		this.applicationTitle = applicationTitle;
	}

	/**
	 * Sets this VisualizationData's data directory location to the incoming value.
	 * 
	 * @param dataDir		value to set this VisualizationData's data directory location to
	 */
	public void setDataDirectory(String dataDir) {
		this.dataDir = dataDir;
	}

	/**
	 * Sets this VisualizationData's screenshots directory location to the incoming value.
	 * 
	 * @param screenshotsDir		value to set this VisualizationData's screenshots directory location to
	 */
	public void setScreenshotsDirectory(String screenshotsDir) {
		this.screenshotsDir = screenshotsDir;
	}
	
	/**
	 * Sets this VisualizationData's testcases directory location to the incoming value.
	 * 
	 * @param screenshotsDir		value to set this VisualizationData's testcases directory location to
	 */
	public void setTestCasesDirectory(String testCasesDir) {
		this.testCasesDir = testCasesDir;
	}
	
	/**
	 * Sets this VisualizationData's Widget HashMap to the incoming value.
	 * 
	 * @param widgetsMap		value to set this VisualizationData's Widget HashMap to
	 */
	public void setWidgetsMap(HashMap<String, Widget> widgetsMap) {
		this.widgetsMap = widgetsMap;
	}
	
	/**
	 * Sets this VisualizationData's Event HashMap to the incoming value.
	 * 
	 * @param eventsMap			value to set this VisualizationData's Event HashMap to
	 */
	public void setEventsMap(HashMap<String, Event> eventsMap) {
		this.eventsMap = eventsMap;
	}

	/**
	 * Sets this VisualizationData's Window ArrayList to the incoming value.
	 *  
	 * @param windows			value to set this VisualizationData's Window ArrayList to
	 */
	public void setWindows(ArrayList<Window> windows) {
		this.windows = windows;
	}

	/**
	 * Sets this VisualizationData's adjacency matrix to the incoming value.
	 * 
	 * @param adjMat		value to set this VisualizationData's adjacency matrix to
	 */
	public void setAdjMat(AdjacencyMatrix adjMat) {
		this.adjMat = adjMat;
	}
	
	/**
	 * Sets this VisualizationData's PVisualizationCanvas to the incoming value.
	 * 
	 * @param pvc		value to set this VisualizationData's PVisualizationCanvas to
	 */
	public void setPVisualizationCanvas(PVisualizationCanvas pvc) {
		this.pvc = pvc;
	}
	
	/**
	 * Sets this VisualizationData's TestCase ArrayList to the incoming value.
	 * 
	 * @param pvc		value to set this VisualizationData's TestCase ArrayList to
	 */
	public void setPVisualizationCanvas(ArrayList<TestCase> testCases) {
		this.testCases = testCases;
	}
	
	/**
	 * Sets this VisualizationData's currentEdgeLayer value to the incoming value.
	 * 
	 * @param currEdgeLayer		value to set this VisualizationData's currentEdgeLayer to
	 */
	public void setCurrentEdgeLayer(PLayer currEdgeLayer) {
		this.currEdgeLayer = currEdgeLayer;
	}
}
