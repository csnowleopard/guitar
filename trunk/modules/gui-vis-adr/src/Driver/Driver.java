package Driver;

import java.util.ArrayList;

import Readers.*;
import Visualization.*;

/**
 * This class is the driver for the EFG Visualizer.
 * 
 * To run the Visualizer, execute this file.
 * 
 * The first argument from the command line when executing must be the title
 * of a ripped Android application. The Visualizer is expecting the files 
 * associated with that ripped application to be in the proper place, which is
 * done automatically in the Ripper.
 * 
 * Note to self, "UNRESTRICTED_FOCUS" is represented as "UNRESTRICED_FOCUS".
 * (Missing the second T) Need to fix this typo in the Ripper.
 * 
 * @author Andrew Guthrie
 * @author Chris Carmel
 * @author Asif Chowdhury
 *
 */
public class Driver {
	

	// Note to self, Unrestricted Focus is represented as unrestriced focus. need to fix this typo.

	/**
	 * Constructs and displays the Visualizer GUI.
	 * 
	 * @param args		The first argument from the command line when executing must be the title
	 * 					of a ripped Android application.
	 */
	public static void main(String[] args) {
		String applicationTitle = "TippyTipper"; // pretending this is the system.in variable
		if (args.length > 0)
			applicationTitle = args[0];
		
		VisualizationData vd = new VisualizationData(applicationTitle);
		
		if (false) {
			ArrayList<String> removeViews = new ArrayList<String>();
			removeViews.add("android.widget.Button");
			VisualizationFilter.filterViews(vd.getWidgetsMap(), vd.getEventsMap(), removeViews);
		}

		PreliminaryGUI pgui = new PreliminaryGUI(vd);
		pgui.setVisible(true);
	}

}