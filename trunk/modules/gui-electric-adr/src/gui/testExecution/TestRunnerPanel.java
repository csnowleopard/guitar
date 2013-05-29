package gui.testExecution;

import javax.swing.JPanel;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.text.DefaultCaret;

import javax.swing.JTextField;

import dataModels.electric.projects.Project;
import dataModels.electric.projects.ProjectManagement;


/**
 * This is the panel to run test cases, it runs the test execution script
 * and shows feedback from the script execution
 * @author CSCHulze
 *
 */
public class TestRunnerPanel extends JPanel {
	
	/**
	 * Project information
	 * */
	private ProjectManagement projectManagement;
	
	/**
	 * The button to start the test execution
	 * */
	private JButton btnRunTests;
	
	/**
	 * The button to cancel the test execution
	 * */
	private JButton btnCancel;

	/**
	 * The underlying task that handles the interaction with the command line script that handles the
	 * test execution
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
	 * The underlying task that handles the interaction with the command line script that does the test
	 * execution
	 * */
	class Task extends SwingWorker<Void, Void> {
		/*androidGuitarPath is only used for debugging the real version will not make use of it*/
		private String androidGuitarPath = "/home/kerl/android/androidguitar/trunk/AndroidGUITAR/dist/guitar/";
		private Process pr;
		private BufferedReader input;

		/*
		 * Main task. Executed in background thread. Runs the test runner script and updates the 
		 * terminal output window.
		 */
		@Override
		public Void doInBackground() {
			try {
				Project project = projectManagement.getTemporarySave();
			
				/*Creates the process to execute the test execution script*/
				ProcessBuilder pb = new ProcessBuilder("./adr-workflow-replay.sh", project.getPath(), project.getPackageName(), project.getMain());
				//ProcessBuilder pb = new ProcessBuilder("./adr-workflow-electric.sh");//, project.getName() + " " + project.getPath() + " " + project.getPackageName() + " " + project.getMain());
				
				/*Starts the script*/
				pr = pb.start();
				
				String line = null;

				input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				
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
			((TestRunner) getTopLevel()).dispose();
		}

		/*
		 * Allows you to kill the task from the outside.
		 * */
		public void killTask() {
			try {
				input.close();
				pr.destroy();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			
		}

		
	}

	public TestRunnerPanel(ProjectManagement projectManagement) {
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

		btnRunTests = new JButton("Run Tests");
		btnRunTests.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				executeTests();
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
				"Press \"Run Tests\" to start the test runner process");
		lblStatusMessage.setFont(new Font("Tahoma", Font.PLAIN, 22));

		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 3;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 2;
		gbc_lblNewLabel.gridy = 5;
		add(lblStatusMessage, gbc_lblNewLabel);
		btnRunTests.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_btnCreateTestCases = new GridBagConstraints();
		gbc_btnCreateTestCases.insets = new Insets(0, 0, 0, 5);
		gbc_btnCreateTestCases.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnCreateTestCases.gridx = 3;
		gbc_btnCreateTestCases.gridy = 6;
		add(btnRunTests, gbc_btnCreateTestCases);

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
	 * Handles the execution of the tests. After the corresponding button has been pressed.
	 */
	protected void executeTests() {
		icon.setIcon(new ImageIcon(getClass().getResource("/resources/Android_Busy.jpg")));
		//projectManagement.commitTemporary();
		btnRunTests.setEnabled(false);
		btnCancel.setEnabled(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		lblStatusMessage.setText("Running Tests");
		// Instances of javax.swing.SwingWorker are not reusuable, so
		// we create new instances as needed.
		task = new Task();
		task.execute();
		// preliminaryGUI.resetHome();
	}

	/**
	 * Cancels the script execution
	 */
	private void cancelScripts() {
		task.killTask();
		task.cancel(true);
		killAndroid();
		setCursor(null);
		
		//((TestRunner) getTopLevel()).dispose();
	}
	
	/**
	 * Return the top level component of this item.
	 * @return
	 */
	protected Container getTopLevel() {
		Container container = this;
		while (!(container.getParent() instanceof TestRunner)) {
			container = container.getParent();
		}
		return container.getParent();
	}
	
	/**
	 * Kill the emulator
	 */
	private void killAndroid() {
		String androidGuitarPath = "/home/kerl/android/androidguitar/trunk/AndroidGUITAR/dist/guitar/";
		ProcessBuilder pb = new ProcessBuilder("./adr-kill.sh");
		
		try {
			//File file = new File(androidGuitarPath);
			//System.out.println("FILE: " + file.exists());
			//pb.directory(file);
			Process pr = pb.start();
			BufferedReader input = null;
			String line = null;
			
			input = new BufferedReader(new InputStreamReader(
					pr.getInputStream()));

			while ((line = input.readLine()) != null) {
				System.out.println(line);			
			}
			input.close();
			pr.destroy();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		
		
	}

}
