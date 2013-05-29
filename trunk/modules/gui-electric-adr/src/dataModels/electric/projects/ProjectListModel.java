package dataModels.electric.projects;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * This implements a list model that is used in the JList of the HomePanel of Electric Guitar to 
 * show all existing projects.
 * @author CSCHulze
 *
 */
public class ProjectListModel implements ListModel{
	
	/**
	 * List of data listeners that are subscribed to this List Model
	 */
	private List<ListDataListener> listeners;
	
	/**
	 * List containing all the projects
	 */
	private List<Project> projects;

	ProjectListModel(List<Project> projects){
		this.projects = projects;
		listeners = new ArrayList<ListDataListener>();
	}
	
	/**
	 * This function adds a new project to the underlying project list and alerts all listeners of this List model
	 * that there was a change in the list
	 * @param project New project that gets added to the list
	 * @return returns true if the project was successfully added to the list, false otherwise
	 */
	public boolean add(Project project){
		boolean result = false;
		/*Add project to the list*/
		result = projects.add(project);
		
		/*If addition to the list was successful alert all listeners of the change*/
		if(result){
			ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, projects.size()-1, projects.size()-1);
			updateListenersAdd(event);
		}
		
		return result;
		
	}
	
	/**
	 * This function removes a project from the underlying project list and alerts all listeners of this List model
	 * that there was a change in the list
	 * @param project Project that should be removed from the list
	 * @return returns true if the project was successfully removed from the list, false otherwise
	 */
	public boolean remove(Project project) {
		int index = projects.indexOf(project);
		/*Remove project from the list*/
		boolean result = projects.remove(project);
		
		/*If removal from the list was successful alert all listeners of the change*/
		if(result){
			ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index);
			updateListenersRemove(event);
		}
		
		return result;
	}
	
	/**
	 * This function removes a project from the underlying project list and alerts all listeners of this List model
	 * that there was a change in the list
	 * @param index List index that should be removed
	 * @return returns true if the project was successfully removed from the list, false otherwise
	 */
	public boolean remove(int index){
		boolean result = false;
		/*Remove given index from the list*/
		if(projects.remove(index) != null){
			result = true;
		}
		
		/*If removal from the list was successful alert all listeners of the change*/
		if(result){
			ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index);
			updateListenersRemove(event);
		}
		
		return result;
		
	}
	
	/**
	 * 
	 * @param event ListDataEvent Tells the listeners what entry on the list was changed
	 */
	private void updateListenersRemove(ListDataEvent event) {
		for (ListDataListener listener : listeners) {	
			listener.intervalRemoved(event);
		}
	}

	/**
	 * 
	 * @param event ListDataEvent Tells the listeners what entry on the list was changed
	 */
	private void updateListenersAdd(ListDataEvent event) {
		for (ListDataListener listener : listeners) {	
			listener.intervalAdded(event);
		}
	}

	@Override
	public int getSize() {
		return projects.size();
	}

	@Override
	public Object getElementAt(int index) {
		try{
		return projects.get(index);
		}
		catch(Exception e){
			return null;
		}
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

	public List<Project> getList(){
		return projects;
	}
	
	public Project getProject(String name){
		for (Project project : projects) {
			if(project.getName().equalsIgnoreCase(name)){
				return project;
			}
		}
		return null;
	}
	

}
