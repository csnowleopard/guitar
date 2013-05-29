import java.util.ArrayList;
import java.util.Map;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.WebDefaultIDGenerator;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.ContentsType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;

/*
 * So-far unsuccessful attempt to create GUI from SeleniumIDE output.
 * 	The brick wall that was ran into is the fact that child-nesting relationship of
 * 	components recorded by SeleniumIDE is lost as components are only recoreded
 * 	1-after-another  sequentially without nesting.  ComponentTypes need to know that 
 * 	nesting
 * 
 * Adapted from {edu.umd.cs.guitar.ripper.Ripper,
 * 				 edu.umd.cs.guitar.model.GWindow,
 * 				 edu.umd.cs.guitar.model.WebWindow,
 * 				 edu.umd.cs.guitar.ripper.filter.WebExpandFilter}
 */
@SuppressWarnings("deprecation")
public class GUIfileOps {	
	
	/*
	 * 
	 */
	public static void writeGUItoFile(GUIStructure guiStructure, String filename) {
		
		IO.writeObjToFile(guiStructure, filename);
	}
	
	/*
	 * 
	 */
	public static GUIStructure loadGUIStructure(Map<String, Map<Component, ArrayList<Event>>> pageToComponentEvents){		
		GUIStructure newGUIStructure= new GUIStructure();
		
		// Load GUITypes and their components into newGUIStructure
		for(String pageURL: pageToComponentEvents.keySet()){
			GUIType pageGUIType= initializeGUIType(pageURL);
			ComponentType pageBodyComponentType= loadBodyComponentType(pageToComponentEvents);
			
			if(pageBodyComponentType!=null){	// Then there is a body element in the webpage
				
				pageGUIType.getContainer().getContents().getWidgetOrContainer().add(pageBodyComponentType);	
			}
			
			newGUIStructure.getGUI().add(pageGUIType);
		}
		
		// Generate IDs for widgets in guiStructure
		WebDefaultIDGenerator.getInstance().generateID(newGUIStructure);
		
		return newGUIStructure;
	}
	
	/*
	 * Returns a new GUIType initialized to pageURL, and loaded with common basic data.
	 */
	public static GUIType initializeGUIType(String pageURL){
		ComponentType pageWindowComponentType = new ComponentType();
		ComponentTypeWrapper windowAdapterComponentTypeWrapper= new ComponentTypeWrapper(pageWindowComponentType);	
		
		GUIType pageGUIType= new GUIType();
		
		ContainerType pageTopContainer= new ContainerType();
		
		/* Add properties required by GUITAR.
		 *	Must be done before setting pageGUIType with pageWindowComponentType 
		 *	so that these properties are in that pageWindowComponentType by that time.
		 */		
		windowAdapterComponentTypeWrapper.addValueByName(GUITARConstants.TITLE_TAG_NAME, "" + pageURL);
		windowAdapterComponentTypeWrapper.addValueByName(GUITARConstants.MODAL_TAG_NAME, "" + true);	// Apparently always true in a WebWindow
		windowAdapterComponentTypeWrapper.addValueByName(GUITARConstants.ROOTWINDOW_TAG_NAME, "" + false);	// According to GWindow, true would have told Guitar ripping to start from this window, but no ripping going on here. 
		
		pageWindowComponentType= windowAdapterComponentTypeWrapper.getDComponentType();

		pageGUIType.setWindow(pageWindowComponentType);
		
		pageTopContainer.setContents(new ContentsType());
		pageGUIType.setContainer(pageTopContainer);
		
		return pageGUIType;
	}
	
	/*
	 * 
	 */
	public static ComponentType loadBodyComponentType(Map<String, Map<Component, ArrayList<Event>>> pageToComponentEvents){
		ComponentType newComponentType= null;
		
		
		
		return newComponentType;
	}
}
