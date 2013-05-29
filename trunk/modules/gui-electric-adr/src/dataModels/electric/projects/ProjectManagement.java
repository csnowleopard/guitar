package dataModels.electric.projects;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;

import org.apache.commons.io.FileUtils;

import dataModels.visualizer.VisualizationData;

/**
 * This class handles the project management of ElectricGUITAR. It loads the project data from the projectFile and
 * stores this in a ProjectListModel. It also saves the project data back to the projectFile.
 * @author CSCHulze
 *
 */
public class ProjectManagement {
	ProjectListModel projects; 
	private String projectFile = "project.txt";
	private Project temporary;
	private static final String tab = "\t";
	private static final String nl = "\n";
	
	/**
	 * Initializes the class and loads the projects
	 */
	public ProjectManagement(){
		projects = new ProjectListModel(readProjectList());
	}
	
	/**
	 * Reads the project file and creates a list with projects
	 * @return
	 */
	public List<Project> readProjectList(){
		List<Project> projects = new ArrayList<Project>();
		File file = new File(projectFile);
		BufferedReader reader = null;
		try {
			file.createNewFile();
			
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = reader.readLine()) != null){
				String[] projectInformation = line.split(tab);
				if(projectInformation.length != 5){
					continue;
				}
				Project project = new Project(projectInformation[0], projectInformation[1], projectInformation[2], projectInformation[3], projectInformation[4]);
				projects.add(project);
			}
			
			reader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return projects;
	}
	
	/**
	 * Checks wethere a project already exists
	 * @param name
	 * @return
	 */
	public boolean projectsExists(String name){
		File file = new File(projectFile);
		BufferedReader reader = null;
		try {
			file.createNewFile();
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = reader.readLine()) != null){
				String[] projectInformation = line.split(tab);
				if(projectInformation.length != 3){
					continue;
				}
				if(projectInformation[0].equalsIgnoreCase(name)){
					reader.close();
					return true;
				}
				
			}
			
			reader.close();
			return false;
			
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		
	}
	
	/**
	 * Returns the list model
	 * @return
	 */
	public ListModel getListModel(){
		return (ListModel) projects;
	}
	
	/**
	 * Adds a project to the list and saves it to the file.
	 * @param project
	 */
	public void addProject(Project project) {
		File file = new File(projectFile);
		BufferedWriter writer = null;
		
		try {
			file.createNewFile();
			
			String isRipped;
			
			if(project.isRipped()){
				isRipped = "TRUE";
			}
			else{
				isRipped = "FALSE";
			}
			
			String line = project.getName() + tab + project.getPath() + tab + project.getPackageName() + tab + project.getMain() + tab + isRipped + nl;
			writer = new BufferedWriter(new FileWriter(file, true));
			writer.append(line);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		projects.add(project);
		
	}
	
	/**
	 * Removes a project
	 * @param index
	 * @param removeFromDisk
	 * @return
	 */
	public boolean removeProject(int index, boolean removeFromDisk){
		String name = ((Project) projects.getElementAt(index)).getName();
		projects.remove(index);
		createList();
		
		/**
		 * If true it will also delete all project files on the disc
		 */
		if(removeFromDisk){
			removeFromDisk(name);
		}
		
		return true;
	}

	/**
	 * Deletes the contents of a project from the disk
	 * @param name
	 */
	private void removeFromDisk(String name) {
		File projectFolder = new File("data/" + name + "/");
		try {
			FileUtils.forceDelete(projectFolder);
		} catch (IOException e) {
			System.out.println("Could not delete project folder");
		}
	}

	
	public boolean removeProject(Project project){
		projects.remove(project);
		createList();
		return true;
		
	}
	
	/**
	 * Writes the list to the disk
	 */
	public void createList() {
		File file = new File(projectFile);
		BufferedWriter writer = null;
		
		try {
			file.createNewFile();
			
			writer = new BufferedWriter(new FileWriter(file, false));
			
			String isRipped;
			
			for (Project project : projects.getList()) {
				if(project.isRipped()){
					isRipped = "TRUE";
				}
				else{
					isRipped = "FALSE";
				}
				
				String line = project.getName() + tab + project.getPath() + tab + project.getPackageName() + tab + project.getMain() + tab + isRipped + nl;
				writer.append(line);
			}
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stores the temporary information of a Project before it is written to disk
	 * @param project
	 */
	public void saveTemporary(Project project) {
		this.temporary = project;
	}

	public Project getTemporarySave() {
		return temporary;
	}

	/**
	 * Writes the temporary project information to the disc and adds it to the file
	 */
	public void commitTemporary() {
		if(getProject(temporary.getName()) == null){
			addProject(temporary);
		}
	}

	public Project getProject(String name) {
		return projects.getProject(name);
	}
	
}
