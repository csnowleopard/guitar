package gui.projectManagement;


import gui.ripping.RipperPanel;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import dataModels.electric.projects.ProjectManagement;




/**
 * Top level container class of the project creation.
 * holds the project creation/edit panel and the ripper panel
 * @author CSCHulze
 *
 */
public class CreateProject extends JDialog {

	private ProjectManagement projectManagement;
	private JPanel contentPane;

	/**
	 * Create the frame.
	 * @param edit 
	 * @param pm 
	 */
	public CreateProject(ProjectManagement projectManagement, boolean edit) {
		this.projectManagement = projectManagement;
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		
		contentPane = new NewProjectPanel(projectManagement, edit);
		//contentPane.setSize(projectManagement.)
		Dimension dimension = contentPane.getPreferredSize();
		
		setBounds(100, 100, dimension.width, dimension.height);
		
		setContentPane(contentPane);
	}
	
	/**
	 * This command allows switching to the ripping view
	 */
	public void switchToRipper(){
		contentPane.setVisible(false);
		contentPane = new RipperPanel(projectManagement);
		setContentPane(contentPane);
		Dimension dimension = contentPane.getPreferredSize();
		
		setBounds(100, 100, dimension.width, dimension.height);
		contentPane.setVisible(true);
	}

	public void showDialog() {
		setVisible(true);
	}

}
