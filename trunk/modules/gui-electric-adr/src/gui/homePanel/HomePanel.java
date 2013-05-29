package gui.homePanel;


import gui.MainWindow;
import gui.projectManagement.CreateProject;
import gui.projectManagement.DeleteProject;
import gui.testExecution.TestRunner;
import gui.testGenerator.TestCaseGenerator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import javax.swing.JList;
import java.awt.Insets;
import javax.swing.ListSelectionModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.io.File;
import java.util.Collection;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;


import org.apache.commons.io.FileUtils;

import dataModels.electric.projects.Project;
import dataModels.electric.projects.ProjectListModel;
import dataModels.electric.projects.ProjectManagement;



/**
 * Home Panel on the left side of the ElectricGUITAR screen.
 * It shows the list of projects and gives the user the option to add/edit/delete projects.
 * It also allows you to create/replay tests
 * @author CSCHulze
 *
 */
public class HomePanel extends JPanel {
        public JList list;
        private ProjectManagement projectManagement;
        private MainWindow preliminaryGUI;
        private JButton btnDeleteProject;
        private JButton btnEditProject;
        private JButton btnTestGeneration;
        private JButton btnTestCaseRunner;
		

        /**
         * Create the panel.
         * @param preliminaryGUI 
         */
        public HomePanel(ProjectManagement pm, MainWindow preliminaryGUI) {
                this.projectManagement = pm;
                this.preliminaryGUI = preliminaryGUI;
                
                GridBagLayout gridBagLayout = new GridBagLayout();
                gridBagLayout.columnWidths = new int[]{150, 150, 0};
                gridBagLayout.rowHeights = new int[]{0, 0, 30, 30, 0, 0, 0};
                gridBagLayout.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
                gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
                setLayout(gridBagLayout);
                
                list = new JList(projectManagement.getListModel());
                list.setFont(new Font("Tahoma", Font.PLAIN, 20));
                list.addListSelectionListener(new ListSelectionListener() {
                        public void valueChanged(ListSelectionEvent e) {                                
                                valueChangedHandler(); 
                        }
                });
                
                // Display an icon and a string for each object in the list.
                /**
                 * Implementation of the ListCellRenderer that shows the project information.
                 * It greys out the projects that are not yet ripped.
                 * @author CSCHulze
                 *
                 */
                class MyCellRenderer extends JLabel implements ListCellRenderer {

                    // This is the only method defined by ListCellRenderer.
                    // We just reconfigure the JLabel each time we're called.

                    public Component getListCellRendererComponent(
                      JList list,              // the list
                      Object value,            // value to display
                      int index,               // cell index
                      boolean isSelected,      // is the cell selected
                      boolean cellHasFocus)    // does the cell have focus
                    {
                    	Project project = (Project) value;
                        setText(project.getName());
                        
                        if (isSelected) {
                            setBackground(list.getSelectionBackground());
                            setForeground(list.getSelectionForeground());
                        } else {
                            setBackground(list.getBackground());
                            setForeground(list.getForeground());
                        }
                        
                        /*If it is not ripped, gray it out*/
                        if(!project.isRipped()){
                        	setForeground(Color.GRAY);
                        }
                       
                        setEnabled(list.isEnabled());
                        setFont(list.getFont());
                        setOpaque(true);
                        return this;
                    }
                }
                
                list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                list.setCellRenderer(new MyCellRenderer());
                
                
                GridBagConstraints gbc_list = new GridBagConstraints();
                gbc_list.gridwidth = 2;
                gbc_list.insets = new Insets(0, 0, 5, 0);
                gbc_list.fill = GridBagConstraints.BOTH;
                gbc_list.gridx = 0;
                gbc_list.gridy = 1;
                add(list, gbc_list);
                
                JButton btnNewProject = new JButton("New Project");
                btnNewProject.setFont(new Font("Tahoma", Font.PLAIN, 15));
                btnNewProject.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                                createNewProject();
                        }
                });
                GridBagConstraints gbc_btnNewProject = new GridBagConstraints();
                gbc_btnNewProject.fill = GridBagConstraints.HORIZONTAL;
                gbc_btnNewProject.insets = new Insets(0, 0, 5, 5);
                gbc_btnNewProject.gridx = 0;
                gbc_btnNewProject.gridy = 2;
                add(btnNewProject, gbc_btnNewProject);
                
                btnDeleteProject = new JButton("Delete Project");
                btnDeleteProject.setEnabled(false);
                btnDeleteProject.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent arg0) {
                                deleteProject();
                        }
                });
                btnDeleteProject.setFont(new Font("Tahoma", Font.PLAIN, 15));
                GridBagConstraints gbc_btnDeleteProject = new GridBagConstraints();
                gbc_btnDeleteProject.fill = GridBagConstraints.HORIZONTAL;
                gbc_btnDeleteProject.insets = new Insets(0, 0, 5, 0);
                gbc_btnDeleteProject.gridx = 1;
                gbc_btnDeleteProject.gridy = 2;
                add(btnDeleteProject, gbc_btnDeleteProject);
                
                btnEditProject = new JButton("Edit Project");
                btnEditProject.setEnabled(false);
                btnEditProject.addActionListener(new ActionListener() {
                	public void actionPerformed(ActionEvent arg0) {
                		editProject();
                	}
                });
                btnEditProject.setFont(new Font("Tahoma", Font.PLAIN, 15));
                GridBagConstraints gbc_btnEditProject = new GridBagConstraints();
                gbc_btnEditProject.insets = new Insets(0, 0, 5, 0);
                gbc_btnEditProject.fill = GridBagConstraints.HORIZONTAL;
                gbc_btnEditProject.gridwidth = 2;
                gbc_btnEditProject.gridx = 0;
                gbc_btnEditProject.gridy = 3;
                add(btnEditProject, gbc_btnEditProject);
                
                btnTestGeneration = new JButton("Test Case Generator");
                btnTestGeneration.setEnabled(false);
                btnTestGeneration.addActionListener(new ActionListener() {
                	public void actionPerformed(ActionEvent arg0) {
                		testCaseGenerator();
                	}
                });
                btnTestGeneration.setFont(new Font("Tahoma", Font.PLAIN, 15));
                GridBagConstraints gbc_btnTestGeneration = new GridBagConstraints();
                gbc_btnTestGeneration.insets = new Insets(0, 0, 5, 0);
                gbc_btnTestGeneration.fill = GridBagConstraints.HORIZONTAL;
                gbc_btnTestGeneration.gridwidth = 2;
                gbc_btnTestGeneration.gridx = 0;
                gbc_btnTestGeneration.gridy = 4;
                add(btnTestGeneration, gbc_btnTestGeneration);
                
                btnTestCaseRunner = new JButton("Test Case Runner");
                btnTestCaseRunner.addActionListener(new ActionListener() {
                	public void actionPerformed(ActionEvent arg0) {
                		testCaseRunner();
                	}
                });
                btnTestCaseRunner.setEnabled(false);
                btnTestCaseRunner.setFont(new Font("Tahoma", Font.PLAIN, 15));
                GridBagConstraints gbc_btnTestCaseRunner = new GridBagConstraints();
                gbc_btnTestCaseRunner.gridwidth = 2;
                gbc_btnTestCaseRunner.fill = GridBagConstraints.HORIZONTAL;
                gbc_btnTestCaseRunner.insets = new Insets(0, 0, 0, 5);
                gbc_btnTestCaseRunner.gridx = 0;
                gbc_btnTestCaseRunner.gridy = 5;
                add(btnTestCaseRunner, gbc_btnTestCaseRunner);

        }
        
        /**
         * Loads the test case runner dialog
         * */
        protected void testCaseRunner() {
        	Project project = ((ProjectListModel) projectManagement.getListModel()).getList().get(list.getSelectedIndex());
        	projectManagement.saveTemporary(project);
        	TestRunner testRunner = new TestRunner(projectManagement);
        	testRunner.showDialog();
		}

        /**
         * Loads the test case creator dialog
         * */
		private void testCaseGenerator() {
        	Project project = ((ProjectListModel) projectManagement.getListModel()).getList().get(list.getSelectedIndex());
        	projectManagement.saveTemporary(project);
        	TestCaseGenerator testCaseGenerator = new TestCaseGenerator(projectManagement);
        	testCaseGenerator.showDialog();
		}

		/**
		 * This is invoked whenever the selected item in the list changes
		 * */
        protected void valueChangedHandler() {
        	if(list.getSelectedIndex() == -1){
        		//System.out.println("DEBUG");
        		//System.out.println(getClass().getResource("/resources/logo.jpeg").toString());
        		this.preliminaryGUI.pvcp.pvc.displayHomeCanvas();
        		getBtnDeleteProject().setEnabled(false);
        		getBtnTestCaseCreator().setEnabled(false);
        		getBtnEdit().setEnabled(false);
        		getbtnTestCaseRunner().setEnabled(false);
        		this.preliminaryGUI.makeUnClickable();
        	}
        	else{
        		showScreenshot();
                getBtnDeleteProject().setEnabled(true);
        		getBtnEdit().setEnabled(true);
        		getBtnTestCaseCreator().setEnabled(true);
        		getbtnTestCaseRunner().setEnabled(true);
        		this.preliminaryGUI.makeClickable();
        	}

		}

        /***
         * Shows the earliest screenshot of the project whenever it is selected in the JList
         */
		public void showScreenshot() {
            String imageLocation = "null";
        	if(list.getModel().getElementAt(list.getSelectedIndex()) != null){
        		Project project = (Project) list.getModel().getElementAt(list.getSelectedIndex());
        		imageLocation = "data/" + project.getName() + "/screenshots/";
                
	            try{
	                    File folderSrc = new File(imageLocation);
	            
	                    String[] extensions = {"png"};
	                    Collection<File> iter = FileUtils.listFiles(folderSrc, extensions, false);
	
	                    imageLocation = "null";  // If no images exist, send a broken message to the screenshot updater
	                    long earliestShot = Long.MAX_VALUE;
	                    for (File file : iter) {
	                            if (file.lastModified() < earliestShot){
	                                    earliestShot = file.lastModified();
	                                    imageLocation = file.getAbsolutePath();
	                            }
	                    }
	            }
	            catch (java.lang.NullPointerException n){
	                    imageLocation = "null";
	            }
	            catch (java.lang.IllegalArgumentException n){
	                    imageLocation = "null";
	            }
        	}
            
			this.preliminaryGUI.pvcp.pvc.showScreenShot(imageLocation);
		}


		/**
		 * Loads the edit project dialog
		 * */
		protected void editProject() {
        	Project project = ((ProjectListModel) projectManagement.getListModel()).getList().get(list.getSelectedIndex());
        	projectManagement.saveTemporary(project);
        	CreateProject createProject = new CreateProject(projectManagement, true);
            createProject.showDialog();
		}

		/**
		 * Loads the delete project dialog
		 * */
		protected void deleteProject() {
                DeleteProject deleteProject = new DeleteProject();
                int choice = deleteProject.showDialog();
                
                /*Choice -1 means that the contents on disk are also being deleted*/
                if(choice == -1){
                        projectManagement.removeProject(list.getSelectedIndex(), true);
                }
                /*Choice 0 means that the contents on disk are untouched*/
                else if(choice == 0){
                        projectManagement.removeProject(list.getSelectedIndex(), false);
                }
                
                
        }

		/**
		 * Loads the create project dialog
		 * */
        protected void createNewProject() {
                CreateProject createProject = new CreateProject(projectManagement, false);
                createProject.showDialog();
        }

		public JButton getBtnEdit() {
			return btnEditProject;
		}
        
		 public JButton getBtnDeleteProject() {
             return btnDeleteProject;
		 }
		 
		 public JButton getBtnTestCaseCreator() {
             return btnTestGeneration;
		 }
		 
		public JButton getbtnTestCaseRunner(){
			return btnTestCaseRunner;
		}


}