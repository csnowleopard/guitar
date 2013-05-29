package dataModels.electric.projects;

import dataModels.visualizer.VisualizationData;

/**
 * Data model for a project in Android Electric Guitar. It stores all necessary information to rip an application.
 * Visualize its EFG and GUI files, create and run tests.
 * @author CSCHulze
 *
 */
public class Project {

	/**
	 * Name of the project
	 */
	private String name;
	
	/**
	 * Path to the application under test
	 */
	private String path;
	
	/**
	 * Package name of the application under test
	 */
	private String packageName;
	
	/**
	 * Main file of the application under test
	 */
	private String main;
	
	/**
	 * Flag if the application under test has been ripped or not
	 */
	private boolean ripped;
	
	private VisualizationData visualizationData;
	
	
	public Project() {
		
	}
	
	public Project(String name, String path, String packageName, String main, String ripped) {
		this.name = name;
		this.path = path;
		this.packageName = packageName;
		this.main = main;
		if(ripped.equalsIgnoreCase("TRUE")){
			this.ripped = true;
		}
		else{
			this.ripped = false;
		}
	}
	
	public VisualizationData getVisualizationData(){
		visualizationData = new VisualizationData(path);
		return visualizationData;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String getMain() {
		return main;
	}
	
	public void setMain(String main) {
		this.main = main;
	}

	public boolean isRipped() {
		return ripped;
	}
	
	public void setRipped(boolean ripped) {
		this.ripped = ripped;
	}
	
	
}
