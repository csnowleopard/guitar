package gui.ripping;

import gui.projectManagement.CreateProject;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import javax.swing.JProgressBar;


import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import dataModels.electric.projects.Project;
import dataModels.electric.projects.ProjectManagement;




/**
 * This is the panel to rip an application it gives the user the option to start/cancel the ripping process
 * and shows him feedback from the script execution
 * @author CSCHulze
 *
 */
public class RipperPanel extends JPanel implements PropertyChangeListener {

	/**
	 * Project information
	 * */
	private ProjectManagement projectManagement;
	
	/**
	 * The progress bar on the panel
	 * */
	private JProgressBar progressBar;
	
	/**
	 * The button to start the ripping process
	 * */
	private JButton btnRipApplication;
	
	/**
	 * The button to cancel the ripping
	 * */
	private JButton btnCancel;

	/**
	 * The underlying task that handles the interaction with the command line script that does the ripping
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
	 * The underlying task that handles the interaction with the command line script that does the ripping
	 * */
	class Task extends SwingWorker<Void, Void> {
		/*androidGuitarPath is only used for debugging the real version will not make use of it*/
		private String androidGuitarPath = "/home/kerl/android/androidguitar/trunk/AndroidGUITAR/dist/guitar/";
		private Process pr;
		private BufferedReader input;

		/*
		 * Main task. Executed in background thread. Runs the ripping script and updates the 
		 * progress bar and the terminal output window.
		 */
		@Override
		public Void doInBackground() {
			try {
				Project project = projectManagement.getTemporarySave();
				
				/*Creates the process to execute the ripping script*/
				ProcessBuilder pb = new ProcessBuilder("./adr-workflow-electric.sh", project.getPath(), project.getPackageName(), project.getMain());

				//File file = new File(androidGuitarPath);
				//pb.directory(file);
				
				/*Starts the script*/
				pr = pb.start();

				String line = null;

				
				input = new BufferedReader(new InputStreamReader(
						pr.getInputStream()));
				
				int progress = 0;
				
				/*
				 * Read the output of the command line execution of the script and write it 
				 * to the text window.
				 * It will look for certain keywords in the output to determine if a task
				 * has been finished. If a task has been done it will update the progress bar.
				 * */
				while ((line = input.readLine()) != null) {
					textArea.append(line + "\n");
					System.out.println(line);
					if (line.contains("SETUP COMPLETE")) {
						progress++;
						task.setProgress(progress);
					}
					else if(line.contains("EMULATOR STARTED")){
						progress++;
						task.setProgress(progress);
					}
					else if(line.contains("RIPPING COMPLETED")){
						progress++;
						task.setProgress(progress);
					}
					else if(line.contains("FILES CREATED")){
						progress++;
						task.setProgress(progress);
					}
					
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
			btnRipApplication.setEnabled(true);
			setCursor(null); // turn off the wait cursor
			projectManagement.getTemporarySave().setRipped(true);
			projectManagement.createList();
			((CreateProject) getTopLevel()).dispose();
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

	/**
	 * Create the panel.
	 * 
	 * @param edit
	 * @param pm
	 */
	public RipperPanel(ProjectManagement projectManagement) {
		this.projectManagement = projectManagement;

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 10, 190, 273, 142, 142, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 30, 30, 30, 30, 10, 100, 30, 30,
				30 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0,
				0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		setPreferredSize(new Dimension(833, 400));

		icon = new JLabel("");
		
		icon.setIcon(new ImageIcon(getClass().getResource("/resources/logo_small.jpg")));
		GridBagConstraints gbc_icon = new GridBagConstraints();
		gbc_icon.gridheight = 3;
		gbc_icon.insets = new Insets(0, 0, 5, 5);
		gbc_icon.gridx = 1;
		gbc_icon.gridy = 1;
		add(icon, gbc_icon);

		progressBar = new JProgressBar(0, 4);
		progressBar.setFont(new Font("Tahoma", Font.PLAIN, 20));
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setString("");
		progressBar.setForeground(new Color(164, 198, 57));
		progressBar.setOpaque(true);
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.gridwidth = 3;
		gbc_progressBar.fill = GridBagConstraints.BOTH;
		gbc_progressBar.insets = new Insets(0, 0, 5, 5);
		gbc_progressBar.gridx = 2;
		gbc_progressBar.gridy = 1;
		add(progressBar, gbc_progressBar);

		btnRipApplication = new JButton("Rip Application");
		btnRipApplication.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createProject();
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
		gbc_textArea.gridy = 2;
		add(area, gbc_textArea);

		lblStatusMessage = new JLabel(
				"Press \"Rip Application\" to start the ripping process");
		lblStatusMessage.setFont(new Font("Tahoma", Font.PLAIN, 22));

		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 3;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 2;
		gbc_lblNewLabel.gridy = 6;
		add(lblStatusMessage, gbc_lblNewLabel);
		btnRipApplication.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GridBagConstraints gbc_btnRipApplication = new GridBagConstraints();
		gbc_btnRipApplication.insets = new Insets(0, 0, 0, 5);
		gbc_btnRipApplication.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRipApplication.gridx = 3;
		gbc_btnRipApplication.gridy = 7;
		add(btnRipApplication, gbc_btnRipApplication);

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
		gbc_btnCancel.gridy = 7;
		add(btnCancel, gbc_btnCancel);

	}

	/**
	 * Handles the creation of the project
	 */
	protected void createProject() {
		icon.setIcon(new ImageIcon(getClass().getResource("/resources/Android_Busy.jpg")));
		projectManagement.commitTemporary();
		progressBar.setString("0/4 tasks complete");
		lblStatusMessage.setText("Setting up tools");

		btnRipApplication.setEnabled(false);
		btnCancel.setEnabled(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		// Instances of javax.swing.SwingWorker are not reusuable, so
		// we create new instances as needed.
		task = new Task();
		task.addPropertyChangeListener(this);
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
		((CreateProject) getTopLevel()).dispose();
	}

	

	/**
	 * Invoked when task's progress property changes.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);

			progressBar.setString(progress + "/4 tasks complete");

			switch (progress) {
			case 1:
				lblStatusMessage.setText("Starting the Emulator");
				break;
			case 2:
				lblStatusMessage.setText("Starting the Ripping");
				break;
			case 3:
				lblStatusMessage.setText("Creating EFG and GUI files");
				break;
			case 4:
				lblStatusMessage.setText("Done");
				break;

			}
		}
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

	/**
	 * Return the top level component of this item.
	 * @return
	 */
	protected Container getTopLevel() {
		Container container = this;
		while (!(container.getParent() instanceof CreateProject)) {
			container = container.getParent();
		}
		return container.getParent();
	}

}
