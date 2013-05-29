package edu.umd.cs.guitar.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * <b>GUIBuilder</b> is the main class used to startup the application. It accepts both user input from command line and if nothing is entered on the 
 * command line it will start up a input dialog requesting input from the user. After it receives correct input it starts a new
 * GraphBuilder object based on the input.
 * 
 * @author Sigmund Gorski
 * @version 1.0
 */
public class GUIBuilder extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	/** Text fields representing the directory required as user input.*/
	private JTextField workdir;
	/** Buttons for the various required user actions.*/
	private JButton selectWorkDir, loadEFG, loadTestCase, cancel;
	/** The various panels used to organize the information.*/
	private JPanel p1, p4;
	/** A frame for this panel.*/
	private static JFrame frame;
	
	/**
	 * The constructor that addes the GUI components to the panel.
	 */
	public GUIBuilder(){
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		p1 = new JPanel(new FlowLayout());
		p4 = new JPanel(new FlowLayout());
		
		workdir = new JTextField(41);
		workdir.setEditable(true);
		selectWorkDir = new JButton("Select Working Dir...");
		selectWorkDir.addActionListener(this);
		loadEFG = new JButton("Open EFG Verifier");
		loadEFG.addActionListener(this);
		loadTestCase = new JButton("Open Test Case Verifier");
		loadTestCase.addActionListener(this);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		
		
		p1.add(selectWorkDir);
		p1.add(workdir);
		
		p4.add(loadEFG);
		p4.add(loadTestCase);
		p4.add(cancel);
		
		add(p1);
		add(p4);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(selectWorkDir)){
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File("."));
		    fc.setDialogTitle("Select Working Dir");
		    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    fc.setAcceptAllFileFilterUsed(false);
			int ret = fc.showDialog(GUIBuilder.this,"Open");
			if(ret == JFileChooser.APPROVE_OPTION){
				workdir.setText(fc.getSelectedFile().getAbsolutePath());
			}
		}else if(e.getSource().equals(loadEFG) || e.getSource().equals(loadTestCase)){
			File dir = new File(workdir.getText());
			if(dir.exists() && !workdir.getText().equals("")){
				if(e.getSource().equals(loadEFG)){
					new GraphBuilder(workdir.getText(),"efg");
				}else{
					new GraphBuilder(workdir.getText(),"test");
				}
				frame.setVisible(false);
				frame.dispose();
			}else{
				JOptionPane.showMessageDialog(null, "Please select a valid working directory!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}else if(e.getSource().equals(cancel)){
			System.exit(0);
		}
    }
	
	/**
	 * A static method used to initialize the frame and create a new GUIBuilder panel for the user input information.
	 */
	public static void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("Select Application Input");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //Add content to the window.
        frame.add(new GUIBuilder());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
	
	/**
	 * This is the main method for running the visualization application. If no input is given off of the command line it will startup a new frame asking for user input.
	 * If input is given it will check for valid input and if wrong startup a new frame asking for user input. If the input is correct it skips the initial input dialog
	 * request from the user and starts the application.
	 * 
	 * @param args Takes in a Working Directory and "efg" for EFG Viewer and "test" for Test Case viewer.
	 */
	public static void main(String[] args) {
		if(args.length >= 2){
			File dir = new File(args[0]);
			if(dir.exists() && !args.equals("")){
				if(args[1].equals("efg") || args[1].equals("test")){
					new GraphBuilder(args[0],args[1]);
				}else{
					System.out.println("Please enter either \"efg\" or \"test\"!");
				}
			}else{
				System.out.println("Please provide a valid working directory!");
			}
		}else{
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					createAndShowGUI();
				}
			});
		}
    }
}
