package gui.testExecution;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JPanel;

import dataModels.electric.projects.ProjectManagement;



/**
 * This Dialog holds the panel for the test generation process
 * @author CSCHulze
 *
 */
public class TestRunner extends JDialog {
	
	private JPanel contentPane;

	public TestRunner(ProjectManagement projectManagement) {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		
		contentPane = new TestRunnerPanel(projectManagement);
		//contentPane.setSize(projectManagement.)
		Dimension dimension = contentPane.getPreferredSize();
		
		setBounds(100, 100, dimension.width, dimension.height);
		
		setContentPane(contentPane);
	}

	public void showDialog() {
		setVisible(true);
	}
	
}