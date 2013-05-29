package edu.umd.cs.guitar;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentListType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.Configuration;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.ContentsType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.LogWidget;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.ripper.ADRRipperConfiguration;
import edu.umd.cs.guitar.ripper.ADRRipperMonitor;
import edu.umd.cs.guitar.ripper.GRipperMonitor;
import edu.umd.cs.guitar.util.GUITARLog;
import edu.umd.cs.guitar.model.ADRIDGenerator;

/**
 * Main class responsible for capturing of test cases and generating a GUI file
 * @author Andrew Guthrie
 *
 */

public class ADRCaptureMain {

	// TODO we may need our own configuration later
	ADRRipperConfiguration CONFIG = null;

	ADRCaptureTool ripper;

	ArrayList<GComponent> testCaseComponent;
	
	GUIStructure guiFile;
	
	public ADRCaptureMain(ADRRipperConfiguration config) {
		super();
		this.CONFIG = config;
		testCaseComponent  = new ArrayList<GComponent>();
	}

	public void execute(GComponent gcomp) {
//		Configuration conf = null;
//
//		System.setProperty(GUITARLog.LOGFILE_NAME_SYSTEM_PROPERTY,
//				ADRCaptureConfiguration.LOG_FILE);
//
//		try {
//			conf = (Configuration) IO.readObjFromFile(
//					ADRCaptureConfiguration.CONFIG_FILE, Configuration.class);
//
//			if (conf == null) {
//				InputStream in = getClass()
//						.getClassLoader()
//						.getResourceAsStream(ADRCaptureConfiguration.CONFIG_FILE);
//				conf = (Configuration) IO.readObjFromFile(in,
//						Configuration.class);
//			}
//
//		} catch (Exception e) {
//			GUITARLog.log.error("No configuration file. Using an empty one...");
//		}

		ripper = new ADRCaptureTool();
		ripper.setIDGenerator(new ADRIDGenerator());

		GRipperMonitor ADRMonitor = new ADRRipperMonitor(CONFIG);
		ripper.setMonitor(ADRMonitor);

		if (CONFIG.USE_IMAGE) {
			ripper.setUseImage();
		}
		
		ripper.execute(gcomp);
		System.out.println("Component Ripped");
		GUIStructure dGUIStructure = ripper.getResults();
		guiFile = union(guiFile, dGUIStructure);
		
		testCaseComponent.add(gcomp);
		//TODO Create method which will take in two GUIStructure objects (which are really just 
		//xml files, and return the union of them
		
		//Will keep this IO call in for now until we have a way to terminate capture mode

		ComponentListType lOpenWins = ripper.getlOpenWindowComps();
		ComponentListType lCloseWins = ripper.getlCloseWindowComp();
		ObjectFactory factory = new ObjectFactory();

		LogWidget logWidget = factory.createLogWidget();
		logWidget.setOpenWindow(lOpenWins);
		logWidget.setCloseWindow(lCloseWins);

	}
	
	public ArrayList<GComponent> terminate(){
		//Write this to file
		if(guiFile == null){
			System.out.println("GUI File is Null");
		}
		System.out.println(testCaseComponent.size());
		IO.writeObjToFile(guiFile, ADRCaptureConfiguration.GUI_FILE);
		System.out.println("GUI File written");
		testCaseComponent.clear();
		guiFile = null;
		return testCaseComponent;
	}
//
//	public static void main(String[] argv) {
//		ADRRipperConfiguration configuration = new ADRRipperConfiguration();
//		final ADRCaptureMain ripperMain = new ADRCaptureMain(configuration);
//		ripperMain.execute();
//		System.exit(0);
//	}
	
	public GUIStructure union(GUIStructure a, GUIStructure b){
		if(a == null){
			return b;
		} else if(b == null){
			return a;
		}
		GUIStructure comb = new GUIStructure();
		List<GUIType> a_gui = a.getGUI(); 
		List<GUIType> b_gui = b.getGUI();
		List<GUIType> temp_gui = a_gui;
		//Performing Union on the GUI attributes
		for(int i = 0; i < b_gui.size(); i++){
			boolean containsGUI = false;
			for(int j = 0; j < temp_gui.size(); i++){
				if(sameAttributes(b_gui.get(i).getWindow().getAttributes(), temp_gui.get(j).getWindow().getAttributes())){
					containsGUI = true;
					//Here, we will perform union on the Container
					ContainerType a_cont = temp_gui.get(j).getContainer();
					ContainerType b_cont = temp_gui.get(i).getContainer();
					ContainerType temp_cont = containerUnion(a_cont,b_cont);
					temp_gui.get(j).setContainer(temp_cont);
				}
			}
			if(!containsGUI){
				temp_gui.add(b_gui.get(i));
			}
		}
		
		comb.setGUI(temp_gui);
		return comb;
	}
	
	
	/**
	 * Recursive method which computes the union of two ContainerTypes
	 * @param a_cont
	 * @param b_cont
	 * @return
	 */
	private ContainerType containerUnion(ContainerType a_cont, ContainerType b_cont) {
		if(a_cont.getAttributes() == null){
			if(b_cont.getAttributes() == null){
				//Good... attributes are equal. Happens at the top level
			} else {
				//Shouldn't be reaching here....
				return null;
			}
		} else {
			if(b_cont.getAttributes() == null){
				//Shouldn't be reaching here....
				return null;
			} else {
				if(!sameAttributes(a_cont.getAttributes(), b_cont.getAttributes())){
					//Not the same containers then, no point in unioning two different containers
					return null;
				}
			}
		}
		ContainerType temp_cont = a_cont;
		temp_cont.setContents(contentsUnion(a_cont.getContents(), b_cont.getContents()));
		return temp_cont;
	}

	/**
	 * Performs union operation on object of type ContentsType
	 * @param a_cont
	 * @param b_cont
	 * @return
	 */
	private ContentsType contentsUnion(ContentsType a_cont, ContentsType b_cont){
		List<ComponentType> temp_cont = a_cont.getWidgetOrContainer();
		for(int i = 0; i < b_cont.getWidgetOrContainer().size(); i++){
			temp_cont.add(b_cont.getWidgetOrContainer().get(i));
		}
		ContentsType temp = new ContentsType();
		temp.setWidgetOrContainer(temp_cont);
		return temp;
	}
	
	private static boolean sameAttributes(AttributesType a, AttributesType b){
		List<PropertyType> a_prop = a.getProperty();
		List<PropertyType> b_prop = b.getProperty();
		if(a_prop.size() != b_prop.size()){
			return false;
		}
		for(int i = 0; i < a_prop.size(); i++){
			if(!(a_prop.get(i).getName().equals(b_prop.get(i).getName()) && a_prop.get(i).getValue().equals(b_prop.get(i).getValue()))){
				return false;
			}
		}
		return true;
	}
}

