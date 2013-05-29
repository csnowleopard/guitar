package gui.testGenerator;

import gui.projectManagement.CreateProject;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.text.DefaultCaret;

import javax.swing.JTextField;

import dataModels.electric.projects.Project;
import dataModels.electric.projects.ProjectManagement;


/**
 * This is the panel to create test cases, it runs the test case generation script
 * and shows feedback from the script execution
 * @author CSCHulze
 *
 */
public class TestGeneratorPanel extends JPanel {
	
	/**
	 * Project information
	 * */
	private ProjectManagement projectManagement;
	
	/**
	 * The button to start the test creation
	 * */
	private JButton btnCreateTestCases;
	
	/**
	 * The button to cancel the test execution
	 * */
	private JButton btnCancel;

	/**
	 * The underlying task that handles the interaction with the command line script that handles the
	 * test creation
	 * */
	private Task task;
	
	/**
	 * The label showing the status message
	 * */
	private JLabel lblStatusMessage;
	
	/**
	 * The icon showing the android guitar logo
	 * */
	private JLabel icon;
	
	/**
	 * The text area that shows the feedback from the command line execution of the ripper script
	 * */
	private JTextArea textArea;
	
	/**
	 * The field where the user can choose the n-waycoverage
	 * */
	private JTextField textCoverageCriterion;
	
	/**
	 * The label of the coverage criterion text field
	 * */
	private JLabel lblGraphCoverageType;
	
	/**
	 * The underlying task that handles the interaction with the command line script that does the test
	 * creation
	 * */
	class Task extends SwingWorker<Void, Void> {
		/*androidGuitarPath is only used for debugging the real version will not make use of it*/
		private String androidGuitarPath = "/home/kerl/android/androidguitar/trunk/AndroidGUITAR/dist/guitar/";

		/*
		 * Main task. Executed in background thread. Runs the test creation script and updates the 
		 * terminal output window.
		 */
		@Override
		public Void doInBackground() {
			try {
				Project project = projectManagement.getTemporarySave();
			
				/*Creates the process to execute the test execution script*/
				ProcessBuilder pb = new ProcessBuilder("./adr-creator.sh", project.getPath(), textCoverageCriterion.getText());
				//ProcessBuilder pb = new ProcessBuilder("./adr-workflow-electric.sh");//, project.getName() + " " + project.getPath() + " " + project.getPackageName() + " " + project.getMain());
				
				/*Starts the script*/
				Process pr = pb.start();
				
				BufferedReader input = null;
				String line = null;

				input = new BufferedReader(new InputStreamReader(
						pr.getInputStream()));
				
				/*
				 * Read the output of the command line execution of the script and print it to
				 * the text window.
				 * */
				while ((line = input.readLine()) != null) {
					textArea.append(line + "\n");
					System.out.println(line);
				}
				
				/*Close input and destroy process*/
				input.close();
				pr.destroy();
				
			} catch (Exception e) {
				//e.printStackTrace();
				return null;
			}
			return null;
		}

		/*
		 * Executed in event dispatching thread
		 * Tear down code of the task.
		 */
		@Override
		public void done() {
			btnCancel.setEnabled(true);
			setCursor(null); // turn off the wait cursor
			((CreateProject) getTopLevel()).dispose();
		}

		
	}

	public TestGeneratorPanel(ProjectManagement projectManagement) {
		this.projectManagement = projectManagement;

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 10, 190, 273, 142, 142, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 30, 30, 30, 10, 100, 30, 30,
				30 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		setPreferredSize(new Dimension(880, 400));

		icon = new JLabel("");
		
		icon.setIcon(new ImageIcon(getClass().getResource("/resources/logo_small.jpg")));
		GridBagConstraints gbc_icon = new GridBagConstraints();
		gbc_icon.gridheight = 3;
		gbc_icon.insets = new Insets(0, 0, 5, 5);
		gbc_icon.gridx = 1;
		gbc_icon.gridy = 1;
		add(icon, gbc_icon);

		btnCreateTestCases = new JButton("Create Tests");
		btnCreateTestCases.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createTests();
			}
		});

		textArea = new JTextArea();
		textArea.setForeground(new Color(255, 255, 255));
		textArea.setBackground(new Color(0, 0, 0));
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
		textArea.setEditable(false);
		
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JScrollPane area = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.gridheight = 4;
		gbc_textArea.gridwidth = 3;
		gbc_textArea.insets = new Insets(0, 0, 5, 5);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 2;
		gbc_textArea.gridy = 1;
		add(area, gbc_textArea);

		lblStatusMessage = new JLabel(
				"Press \"Create Tests\" to start the test case generation process");
		lblStatusMessage.setFont(new Font("Tahoma", Font.PLAIN, 22));

		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 3;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 2;
		gbc_lblNewLabel.gridy = 5;
		add(lblStatusMessage, gbc_lblNewLabel);
		
		lblGraphCoverageType = new JLabel("Graph Coverage Type");
		GridBagConstraints gbc_lblGraphCoverageType = new GridBagConstraints();
		gbc_lblGraphCoverageType.insets = new Insets(0, 0, 0, 5);
		gbc_lblGraphCoverageType.anchor = GridBagConstraints.EAST;
		gbc_lblGraphCoverageType.gridx = 1;
		gbc_lblGraphCoverageType.gridy = 6;
		add(lblGraphCoverageType, gbc_lblGraphCoverageType);
		
		textCoverageCriterion = new JTextField();
		textCoverageCriterion.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textCoverageCriterion.setText("2");
		GridBagConstraints gbc_textCoverageCriterion = new GridBagConstraints();
		gbc_textCoverageCriterion.insets = new Insets(0, 0, 0, 5);
		gbc_textCoverageCriterion.fill = GridBagConstraints.BOTH;
		gbc_textCoverageCriterion.gridx = 2;
		gbc_textCoverageCriterion.gridy = 6;
		add(textCoverageCriterion, gbc_textCoverageCriterion);
		textCoverageCriterion.setColumns(10);
		btnCreateTestCases.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_btnCreateTestCases = new GridBagConstraints();
		gbc_btnCreateTestCases.insets = new Insets(0, 0, 0, 5);
		gbc_btnCreateTestCases.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnCreateTestCases.gridx = 3;
		gbc_btnCreateTestCases.gridy = 6;
		add(btnCreateTestCases, gbc_btnCreateTestCases);

		btnCancel = new JButton("Cancel");
		btnCancel.setEnabled(false);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cancelScripts();
			}

		});
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnCancel.gridx = 4;
		gbc_btnCancel.gridy = 6;
		add(btnCancel, gbc_btnCancel);

	}
	
	/**
	 * Handles the creation of the task that executes the test generation script
	 * after the corresponding button has been pressed.
	 */
	protected void createTests() {
		icon.setIcon(new ImageIcon(getClass().getResource("/resources/Android_Busy.jpg")));
		projectManagement.commitTemporary();
		btnCreateTestCases.setEnabled(false);
		btnCancel.setEnabled(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		// Instances of javax.swing.SwingWorker are not reusuable, so
		// we create new instances as needed.
		lblStatusMessage.setText("Creating Tests");
		task = new Task();
		task.execute();
		// preliminaryGUI.resetHome();
	}

	/**
	 * Cancels the script execution
	 */
	private void cancelScripts() {
		task.cancel(true);
		setCursor(null);
		
		((TestCaseGenerator) getTopLevel()).dispose();
	}
	
	/**
	 * Return the top level component of this item.
	 * @return
	 */
	protected Container getTopLevel() {
		Container container = this;
		while (!(container.getParent() instanceof TestCaseGenerator)) {
			container = container.getParent();
		}
		return container.getParent();
	}

}
