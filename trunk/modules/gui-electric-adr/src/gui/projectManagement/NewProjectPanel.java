package gui.projectManagement;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import javax.swing.filechooser.FileNameExtensionFilter;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JLayeredPane;

import javax.swing.ImageIcon;

import utils.electric.ClassFileFilter;

import dataModels.electric.projects.Project;
import dataModels.electric.projects.ProjectManagement;


/**
 * JPanel that handles creation/edit of a new project it is embedded in the CreateProject dialog
 * 
 * @author CSCHulze
 *
 */
public class NewProjectPanel extends JPanel {
	/**
	 * Name of the project
	 */
	private JTextField textProjectName;
	
	/**
	 * Package name of the project
	 */
	private JTextField textPackageName;
	
	/**
	 * Path to the application. This is currently not really used. the application has to be 
	 * in the dist guitar folder. TODO: make it really dynamic.
	 */
	private JTextField textApplicationPath;
	
	/**
	 * Name of the main class of the application
	 */
	private JTextField textApplicationMain;
	
	/**
	 * Information about the existing projects
	 */
	private ProjectManagement projects;
	
	/**
	 * Shows the error message if anything went wrong in the creation process
	 */
	private JLabel lblErrorMsg;
	
	/**
	 * Variable that indicates if it is a new project or an edit of an existing one.
	 */
	private boolean edit;

	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		try {
			
			ProjectManagement projects = new ProjectManagement();
			NewProjectPanel panel = new NewProjectPanel(projects,false);
			panel.setVisible(true);
			JFrame frame = new JFrame();
			frame.setSize(600, 200);
			frame.getContentPane().add(panel);
			
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Initializes the GUI
	 */
	private void initialize(){
		setPreferredSize(new Dimension(734, 405));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{10, 140, 10, 100, 151, 134, 117, 0, 10};
		gridBagLayout.rowHeights = new int[]{30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		lblErrorMsg = new JLabel("Project Already Exists");
		lblErrorMsg.setForeground(Color.RED);
		lblErrorMsg.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblErrorMsg.setVisible(false);
		
		
		GridBagConstraints gbc_lblErrorMsg = new GridBagConstraints();
		gbc_lblErrorMsg.gridwidth = 2;
		gbc_lblErrorMsg.insets = new Insets(0, 0, 5, 5);
		gbc_lblErrorMsg.gridx = 4;
		gbc_lblErrorMsg.gridy = 1;
		add(lblErrorMsg, gbc_lblErrorMsg);
		
		JLabel logo = new JLabel("");
		logo.setIcon(new ImageIcon(getClass().getResource("/resources/logo_small.jpg")));
		GridBagConstraints gbc_logo = new GridBagConstraints();
		gbc_logo.gridheight = 9;
		gbc_logo.insets = new Insets(0, 0, 5, 5);
		gbc_logo.gridx = 1;
		gbc_logo.gridy = 0;
		add(logo, gbc_logo);
		
		JLabel lblProjectName = new JLabel("Project Name");
		lblProjectName.setHorizontalAlignment(SwingConstants.LEFT);
		lblProjectName.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_lblProjectName = new GridBagConstraints();
		gbc_lblProjectName.fill = GridBagConstraints.VERTICAL;
		gbc_lblProjectName.anchor = GridBagConstraints.WEST;
		gbc_lblProjectName.insets = new Insets(0, 0, 5, 5);
		gbc_lblProjectName.gridx = 3;
		gbc_lblProjectName.gridy = 2;
		add(lblProjectName, gbc_lblProjectName);
		
		textProjectName = new JTextField();
		
		if(edit){
			textProjectName.setText(projects.getTemporarySave().getName());
		}
		GridBagConstraints gbc_textProjectName = new GridBagConstraints();
		gbc_textProjectName.gridwidth = 2;
		gbc_textProjectName.insets = new Insets(0, 0, 5, 5);
		gbc_textProjectName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textProjectName.gridx = 4;
		gbc_textProjectName.gridy = 2;
		add(textProjectName, gbc_textProjectName);
		textProjectName.setColumns(10);
		
		JLabel lblPackageName = new JLabel("Package Name");
		lblPackageName.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_lblPackageName = new GridBagConstraints();
		gbc_lblPackageName.anchor = GridBagConstraints.WEST;
		gbc_lblPackageName.insets = new Insets(0, 0, 5, 5);
		gbc_lblPackageName.gridx = 3;
		gbc_lblPackageName.gridy = 4;
		add(lblPackageName, gbc_lblPackageName);
		
		textPackageName = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		if(edit){
			textPackageName.setText(projects.getTemporarySave().getPackageName());
		}
		gbc_textField.gridwidth = 2;
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 4;
		gbc_textField.gridy = 4;
		add(textPackageName, gbc_textField);
		textPackageName.setColumns(10);
		
		JLabel lblApplicationPath = new JLabel("Application Path");
		lblApplicationPath.setHorizontalAlignment(SwingConstants.LEFT);
		lblApplicationPath.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_lblApplicationPath = new GridBagConstraints();
		gbc_lblApplicationPath.fill = GridBagConstraints.VERTICAL;
		gbc_lblApplicationPath.anchor = GridBagConstraints.WEST;
		gbc_lblApplicationPath.insets = new Insets(0, 0, 5, 5);
		gbc_lblApplicationPath.gridx = 3;
		gbc_lblApplicationPath.gridy = 6;
		add(lblApplicationPath, gbc_lblApplicationPath);
		
		textApplicationPath = new JTextField();
		textApplicationPath.setEditable(false);
		if(edit){
			textApplicationPath.setText(projects.getTemporarySave().getPath());
		}
		GridBagConstraints gbc_textApplicationPath = new GridBagConstraints();
		gbc_textApplicationPath.gridwidth = 2;
		gbc_textApplicationPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_textApplicationPath.insets = new Insets(0, 0, 5, 5);
		gbc_textApplicationPath.gridx = 4;
		gbc_textApplicationPath.gridy = 6;
		add(textApplicationPath, gbc_textApplicationPath);
		textApplicationPath.setColumns(10);
		
		JButton btnApplicationPath = new JButton("Add Path");
		btnApplicationPath.setToolTipText("Choose the path to the application");
		btnApplicationPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addPath();
			}
		});
		btnApplicationPath.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_btnApplicationPath = new GridBagConstraints();
		gbc_btnApplicationPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnApplicationPath.insets = new Insets(0, 0, 5, 5);
		gbc_btnApplicationPath.gridx = 6;
		gbc_btnApplicationPath.gridy = 6;
		add(btnApplicationPath, gbc_btnApplicationPath);
		
		JLabel lblApplicationMain = new JLabel("Application Main");
		lblApplicationMain.setHorizontalAlignment(SwingConstants.LEFT);
		lblApplicationMain.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_lblApplicationMain = new GridBagConstraints();
		gbc_lblApplicationMain.fill = GridBagConstraints.VERTICAL;
		gbc_lblApplicationMain.anchor = GridBagConstraints.WEST;
		gbc_lblApplicationMain.insets = new Insets(0, 0, 5, 5);
		gbc_lblApplicationMain.gridx = 3;
		gbc_lblApplicationMain.gridy = 8;
		add(lblApplicationMain, gbc_lblApplicationMain);
		
		textApplicationMain = new JTextField();
		if(edit){
			textApplicationMain.setText(projects.getTemporarySave().getMain());
		}
		GridBagConstraints gbc_textApplicationMain = new GridBagConstraints();
		gbc_textApplicationMain.gridwidth = 2;
		gbc_textApplicationMain.fill = GridBagConstraints.HORIZONTAL;
		gbc_textApplicationMain.insets = new Insets(0, 0, 5, 5);
		gbc_textApplicationMain.gridx = 4;
		gbc_textApplicationMain.gridy = 8;
		add(textApplicationMain, gbc_textApplicationMain);
		textApplicationMain.setColumns(10);
		
		JButton btnAddMain = new JButton("Add Main");
		btnAddMain.setToolTipText("Choose the main class of the application");
		btnAddMain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addMainClass();
			}
		});
		btnAddMain.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_btnAddMain = new GridBagConstraints();
		gbc_btnAddMain.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAddMain.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddMain.gridx = 6;
		gbc_btnAddMain.gridy = 8;
		add(btnAddMain, gbc_btnAddMain);
		
		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.gridwidth = 2;
		gbc_separator.insets = new Insets(0, 0, 5, 5);
		gbc_separator.gridx = 3;
		gbc_separator.gridy = 9;
		add(separator, gbc_separator);
		
		JButton btnCreateProject = new JButton("Create Project");
		btnCreateProject.setToolTipText("Create the project and start the ripping process");
		btnCreateProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createProject();
			}
		});
		btnCreateProject.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_btnCreateProject = new GridBagConstraints();
		gbc_btnCreateProject.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnCreateProject.insets = new Insets(0, 0, 5, 5);
		gbc_btnCreateProject.gridx = 4;
		gbc_btnCreateProject.gridy = 10;
		add(btnCreateProject, gbc_btnCreateProject);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				((CreateProject)getTopLevel()).dispose();
			}
		});
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnCancel.insets = new Insets(0, 0, 5, 5);
		gbc_btnCancel.gridx = 5;
		gbc_btnCancel.gridy = 10;
		add(btnCancel, gbc_btnCancel);
	}

	/**
	 * Create the panel.
	 * @param preliminaryGUI 
	 */
	public NewProjectPanel(ProjectManagement projects, boolean edit) {
		this.projects = projects;
		this.edit = edit;
		initialize();

	}

	/**
	 * Opens a window that lets you choose the path to the application
	 */
	protected void addPath() {
		JFileChooser chooser = new JFileChooser();
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setDialogTitle("Select Application Path");
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setAcceptAllFileFilterUsed(false);

	    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	textApplicationPath.setText(chooser.getSelectedFile().getPath());
	    } 
	}

	/**
	 * Checks if the entered information is correct. If yes it will switch to the ripper view.
	 * If no it will show an appropriate error message.
	 */
	protected void createProject() {
		if(textProjectName.getText().trim().isEmpty()){
			lblErrorMsg.setText("Project name is empty");
			lblErrorMsg.setVisible(true);
			return;
		}
		if(textPackageName.getText().trim().isEmpty()){
			lblErrorMsg.setText("Package naem is empty");
			lblErrorMsg.setVisible(true);
			return;
		}
		if(textApplicationPath.getText().trim().isEmpty()){
			lblErrorMsg.setText("Project path is empty");
			lblErrorMsg.setVisible(true);
			return;
		}
		if(textApplicationMain.getText().trim().isEmpty()){
			lblErrorMsg.setText("Project main class is empty");
			lblErrorMsg.setVisible(true);
			return;
		}
		if(projects.getTemporarySave() != null && textProjectName.getText().trim().equalsIgnoreCase(projects.getTemporarySave().getName())){
			
		}
		else if(projects.projectsExists(textProjectName.getText().trim())){
			lblErrorMsg.setText("Project name already exists");
			lblErrorMsg.setVisible(true);
			return;
		}
		
		/*Workaround until scripts are changed, at the moment it can only rip applications from the aut directory*/
		String path = textApplicationPath.getText().trim();
		if(path.contains("/")){
			path = path.substring(path.lastIndexOf("/"));
			path = path.replace("/", "");
		}
		
		if(edit){
			Project project = projects.getTemporarySave();
			project.setName(textProjectName.getText().trim());
			project.setPackageName(textPackageName.getText().trim());
			project.setPath(path);
			project.setMain(textApplicationMain.getText().trim());
		}
		else{
			Project project = new Project(textProjectName.getText().trim(), path, textPackageName.getText().trim(), textApplicationMain.getText().trim(), "FALSE");
			projects.saveTemporary(project);
		}
		((CreateProject)getTopLevel()).switchToRipper();
		
	}

	/**
	 * Opens a file chooser GUI that lets you choose the main class of the application
	 */
	protected void addMainClass() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select Main Class");
	    chooser.setFileFilter( new ClassFileFilter());
	    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	textApplicationMain.setText(chooser.getSelectedFile().getName());
		} 
	}
	
	/**
	 * Get the top level element. This is used to close the window where this panel is embedded in.
	 * @return
	 */
	protected Container getTopLevel(){
		Container container = this;
		while ( !(container.getParent() instanceof CreateProject) ) {
			container = container.getParent();
		}
		return container.getParent();
	}

}
