import java.awt.*;
import java.awt.event.*;
import java.io.File;
//import java.io.BufferedStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.text.* ;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import java.net.MalformedURLException;
import java.net.URL ;
import edu.umd.cs.guitar.model.data.* ;
import edu.umd.cs.guitar.model.wrapper.* ;
import java.util.HashMap;

import java.io.*;

public class CaptureReplay extends JPanel {
 
	private static final long serialVersionUID = 1L;
	
	protected JTextPane updateBox;
	protected JTextPane testBox;
	private JLabel appName;
	private Process process;
	private CaptureAsYouGo guiCapture;
	private JFCXTestCase efgtstCapture;
    protected static JFrame frame;
    private ArrayList<JCheckBox> tests;
    private JButton recordButton;
    private JButton stopButton;
    private JCheckBox recordOnStart;
    private String mainClass;
    private String programPath;
    
    public CaptureReplay() {
    	
    	//Define layout of overall window, create space, then components.
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 3, 0, 0));
        createMainPanel();  
        
        //Define classes used to collect and save test data.
        efgtstCapture = new JFCXTestCase();
        guiCapture = new CaptureAsYouGo(new MouseListener() {
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {
			//System.out.println("Tracking Things That Shouldn't Be Clicked") ;
			//efgtstCapture.addComponent((Component)e.getSource()) ;
		}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
	});

		guiCapture.addComponentInspector(new CaptureAsYouGo.ComponentInspector(){
			private InputMethodListener inputMethodCapture = new InputMethodListener() {
				public void caretPositionChanged(InputMethodEvent event) {
					System.out.println("CaretPositionChanged") ;
				}
				public void inputMethodTextChanged(InputMethodEvent e) {
					System.out.println("TextChanged") ;
              		efgtstCapture.addComponent((Component)e.getSource()) ;
				}
			} ;

			private ActionListener actionCapture = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("TextActionPerformed") ;
              		efgtstCapture.addComponent((Component)e.getSource()) ;
				}
			} ;

			public boolean inspect(Component component) {
				boolean add = component instanceof JTextField ;
				if(add) {
					((JTextField)component).addActionListener(this.actionCapture) ;
					return add ;
				}
				add = component instanceof JTextComponent ;
				if(add) {
					((JTextComponent)component).addInputMethodListener(this.inputMethodCapture) ;
				}
				return add ;
			}
			public String getReplayableActionType() {
				return "edu.umd.cs.guitar.event.JFCEditableTextHandler" ;
			}
		}) ;
        
        guiCapture.addComponentInspector(new CaptureAsYouGo.ComponentInspector(){
          private ActionListener actionCapture = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              System.out.println("ActionPerformed") ;
              efgtstCapture.addComponent((Component)e.getSource()) ;
            }
          } ;
          public boolean inspect(Component component) {
            boolean add = component instanceof AbstractButton && !(component instanceof JMenu) ;
            if(add) ((AbstractButton)component).addActionListener(this.actionCapture) ;
            return add;
          }
		  public String getReplayableActionType() {
		  	return "edu.umd.cs.guitar.event.JFCActionHandler" ;
		  }
		 /* public void inspectComponent(Component component, ComponentTypeWrapper componentType) {
		  	if(component instanceof AbstractButton && !(component instanceof JMenu)) {
				((AbstractButton)component).addActionListener(this.actionCapture) ;
				componentType.addAttribute("ReplayableAction", "edu.umd.cs.guitar.JFCActionHandler") ;
			}
		  } */
        }) ;
        
        guiCapture.addComponentInspector(new CaptureAsYouGo.ComponentInspector(){
          private ActionListener actionCapture = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              System.out.println("ActionPerformed") ;
              efgtstCapture.addComponent((Component)e.getSource()) ;
            }
          } ;
          public boolean inspect(Component component) {
            boolean add = component instanceof Button ;
            if(add) ((Button)component).addActionListener(this.actionCapture) ;
            return add;
          }
		  public String getReplayableActionType() {
		  	return "edu.umd.cs.guitar.event.JFCActionHandler" ;
		  }
        }) ;
        
        guiCapture.addComponentInspector(new CaptureAsYouGo.ComponentInspector(){
          private MenuListener menuCapture = new MenuListener() {
            public void menuCanceled(MenuEvent e) { }
	    public void menuDeselected(MenuEvent e) { }
	    public void menuSelected(MenuEvent e) {
              System.out.println("MenuSelected") ;
              efgtstCapture.addComponent((Component)e.getSource()) ;
            }
          } ;
          public boolean inspect(Component component) {
            boolean add = component instanceof JMenu ;
            if(add) ((JMenu)component).addMenuListener(this.menuCapture) ;
            return add;
          }
		  public String getReplayableActionType() {
		  	return "edu.umd.cs.guitar.event.JFCSelectionHandler" ;
		  }
        }) ;
        
        //Define list of test name for replaying.
        tests = new ArrayList<JCheckBox>();
    }
    
    //Main function.
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    //Create the primary panel with sub panels.
    private void createMainPanel() {
    	
    	 //Create the panels in each tab.
        JPanel capturePanel = createCapturePanel();
        JPanel replayPanel = createReplayPanel();
       
        //Create the tabs and add the new panels to them.
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Capture", capturePanel);
        tabbedPane.addTab("Replay", replayPanel);
        
        //Create the buttons that always show to select app.
        JPanel topButtons = new JPanel();
        JPanel topButtons2 = new JPanel();
        JPanel topButtonsVertical = new JPanel();
    	topButtons.setLayout(new BoxLayout(topButtons, BoxLayout.X_AXIS));
    	topButtons2.setLayout(new BoxLayout(topButtons2, BoxLayout.X_AXIS));
    	topButtons.setAlignmentX(LEFT_ALIGNMENT);
    	topButtons2.setAlignmentX(LEFT_ALIGNMENT);
    	topButtonsVertical.setLayout(new BoxLayout(topButtonsVertical, BoxLayout.Y_AXIS));
        JButton selectApp = new JButton("Select Application");
        JLabel selectedApp = new JLabel("Selected App: ");
        appName = new JLabel();
        topButtons.add(selectApp);
    	topButtons.add(Box.createRigidArea(new Dimension(10, 0)));
    	topButtons.add(selectedApp);
    	topButtons.add(appName);
    	
    	selectApp.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				displayAppFrame();
			}
		});
    	
    	//Create checkbox for recording on start.
    	recordOnStart = new JCheckBox("Start recording when app is loaded.");
    	topButtons2.add(recordOnStart);
    	topButtonsVertical.add(topButtons);
    	topButtonsVertical.add(topButtons2);
        
    	//Add the buttons and tab structure.
    	add(topButtonsVertical, BorderLayout.NORTH);
    	add(tabbedPane, BorderLayout.CENTER);
    }
    
    //Create the display inside the "Capture" tab.
    private JPanel createCapturePanel() {
    	
    	//Create the overall panel with a space between each component.
    	JPanel panel = new JPanel(new BorderLayout(20, 20));
    	
    	//Create container for the bottom buttons.
    	JPanel bottomButtons = new JPanel();
    	bottomButtons.setLayout(new BoxLayout(bottomButtons, BoxLayout.X_AXIS));
    	
    	//Create the bottom buttons and top label.
    	recordButton = new JButton("Record");
    	recordButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			record();
    		}
    	});
    	stopButton = new JButton("Stop");
    	stopButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			stopRecord();
    		}
    	});
    	JButton save = new JButton("Save Test");
    	save.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			saveTest();
    		}
    	});
    	JButton close = new JButton("Close");
    	close.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			closeProgram();
    		}
    	});
    	JLabel updateLabel = new JLabel("Recent Actions:");
    	
    	//Create the update box and make it scrollable.
    	updateBox = new JTextPane();
    	updateBox.setText("Welcome to the JFC Capture/Replay Tool!");
    	JScrollPane scroll = new JScrollPane(updateBox);
    	updateBox.setEditable(false);
    	
    	//Add the update label and box.
    	panel.add(updateLabel, BorderLayout.PAGE_START);
    	panel.add(scroll, BorderLayout.CENTER);
    	
    	//Add the buttons to bottom container and add that to overall.
    	bottomButtons.add(recordButton);
    	bottomButtons.add(Box.createRigidArea(new Dimension(10, 0)));
    	bottomButtons.add(stopButton);
    	bottomButtons.add(Box.createRigidArea(new Dimension(10, 0)));
    	bottomButtons.add(save);
    	bottomButtons.add(Box.createRigidArea(new Dimension(10, 0)));
    	bottomButtons.add(close);
    	panel.add(bottomButtons, BorderLayout.PAGE_END);
    	stopButton.setEnabled(false);
    	
    	//Add space in between edge of tab page.
    	panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    	
    	return panel;
    }
 
    //Create the display inside the "Replay" tab.
    private JPanel createReplayPanel() {
    	
    	//Create the overall panel with a space between each component.
    	JPanel panel = new JPanel(new BorderLayout(20, 20));
    	
    	//Create container for the bottom buttons.
    	JPanel bottomButtons = new JPanel();
    	bottomButtons.setLayout(new BoxLayout(bottomButtons, BoxLayout.X_AXIS));
    	
    	//Create the buttons and label.
    	JButton run = new JButton("Run");
    	run.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			runTests();
    		}
    	});
    	JButton close = new JButton("Close");
    	close.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			closeProgram();
    		}
    	});
    	JButton add = new JButton("Add");
    	add.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			addTest();
    		}
    	});
    	JButton remove = new JButton("Remove");
    	remove.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			removeTest();
    		}
    	});
    	JLabel testLabel = new JLabel("Tests:");
    	
    	//Create the test box and make it scrollable.
    	testBox = new JTextPane();
    	testBox.setLayout(new BoxLayout(testBox, BoxLayout.Y_AXIS));
    	testBox.setEditable(false);
    	JScrollPane scroll = new JScrollPane(testBox);
    	
    	//Add the test label and box.
    	panel.add(testLabel, BorderLayout.PAGE_START);
    	panel.add(scroll, BorderLayout.CENTER);
    	
    	//Add the buttons to bottom container and add that to overall.
    	bottomButtons.add(add);
    	bottomButtons.add(Box.createRigidArea(new Dimension(10, 0)));
    	bottomButtons.add(remove);
    	bottomButtons.add(Box.createRigidArea(new Dimension(10, 0)));
    	bottomButtons.add(run);
    	bottomButtons.add(Box.createRigidArea(new Dimension(10, 0)));
    	bottomButtons.add(close);
    	panel.add(bottomButtons, BorderLayout.PAGE_END);
    	
    	//Add space in between edge of tab page.
    	panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    	
    	return panel;
    }
    
    //Create the GUI and show it.
    private static void createAndShowGUI() {
    	
        //Create and set up the window.
        frame = new JFrame("Capture Replay");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        JComponent newContentPane = new CaptureReplay();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        frame.setPreferredSize(new Dimension(400, 400));
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    //Update the box that shows current actions.
    public void updateGUI(String message) {
    	StyledDocument text = updateBox.getStyledDocument();
    	SimpleAttributeSet currentWords = new SimpleAttributeSet();
    	StyleConstants.setBold(currentWords, true);
    	SimpleAttributeSet pastWords = new SimpleAttributeSet();
    	StyleConstants.setBold(pastWords, false);
    	
    	
    	try {
    		text.setCharacterAttributes(0, text.getLength(), pastWords, true);
			text.insertString(0, message+"\n", currentWords);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			System.err.println("Bad location exception while styling updateBox.");
			e.printStackTrace();
		}
    	
    	updateBox.setCaretPosition(0);
    }
    
    //Choose and run the app to test.
    private void displayAppFrame() {
    	
    	//Set up the frame for the file chooser.
    	final JFrame appframe = new JFrame("Select Application");
        appframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container contentPane = appframe.getContentPane();
        JFileChooser fileChooser = new JFileChooser(".");

        
		//Ask user what type of executable the program is in, display chooser accordingly. 
        int isJar = JOptionPane.showOptionDialog(null, "Is the program a .jar file?", "Question", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		if(isJar == JOptionPane.YES_OPTION) {
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		} else if (isJar == JOptionPane.NO_OPTION) {
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		} else {
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		}
		contentPane.add(fileChooser, BorderLayout.CENTER);
		
		//Make a new action listener for the file chooser.
        ActionListener actionListener = new ActionListener() {
        	
            public void actionPerformed(ActionEvent actionEvent) {
            	
            	//Get the information to check if the file chosen is valid.
            	JFileChooser theFileChooser = (JFileChooser) actionEvent.getSource();
            	String command = actionEvent.getActionCommand();

            	//If the user cancels selecting a program.
            	if(command.equals(JFileChooser.CANCEL_SELECTION)) {
            		appframe.setVisible(false);
            		appframe.dispose();
            		return;
            	}
            	
            	//If the file chosen is valid.
            	if(command.equals(JFileChooser.APPROVE_SELECTION)) {
            		//Retrieve the selected file and ask for the main class name.
            		File f = theFileChooser.getSelectedFile() ;
            		String className = JOptionPane.showInputDialog("Please enter the main class name: ");
            		mainClass = className;
            		programPath = f.getAbsolutePath();
            		
            		//Obtain the file URL.
            		URL[]folderURL = new URL[1] ;
            		String fileURL = null;
            		String programName = "/";
            		try {
						folderURL[0] = f.toURI().toURL();
						fileURL = new String(folderURL[0].toString());
					} catch (MalformedURLException e1) {
						System.err.println("File URL not correct.");
					}
            		
					//check if OS is *nix, if so, add leading /
					String os = System.getProperty("os.name").toLowerCase();
					if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0){
						fileURL = "/" + fileURL;
					}
				
            		//Run the selected application.
            		try {
            			//Begin capturing the programs GUI.
						System.out.println("Before Capture: #Frames="+Frame.getFrames().length) ;
            			guiCapture.beginCapture();
            			if(recordOnStart.isSelected()) {
            				record();
            			}
						//Load the program through the CaptureAsYouGo
						guiCapture.loadApplication(className, folderURL, new String[]{}) ;
						// this.toFront() ;
						CaptureReplay.frame.toFront() ;
            			//Update the label that shows what program is running.
            			programName = fileURL.substring(fileURL.lastIndexOf('/'));
            			if(programName.length() == 1) {
            				programName = "Main Class - "+className;
            			}
            			appName.setText(programName);
            		} catch (IOException e) {
            			e.printStackTrace();
            			System.out.println("TEST 1");
            		} catch (Exception e) {
            			e.printStackTrace();
            			System.out.println("TEST 2");
            		}

            		//Make the file chooser disappear.
            		appframe.setVisible(false) ;
            		appframe.dispose() ;
            	}	
            }
        };
          
        //Add the action listener created above to file chooser, display it. 
        fileChooser.addActionListener(actionListener);
        appframe.pack();
        appframe.setVisible(true);
    	
    }
 
    //Record a test case.
    private void record() {
    	this.guiCapture.enableRipping() ;

    	//Update the text pane buttons.
    	updateGUI("Started Recording.");
    	recordButton.setEnabled(false);
    	stopButton.setEnabled(true);
    	
    	//Clear the old test case and begin recording new EFG and TST files.
    	efgtstCapture.reset();
    	efgtstCapture.start();
    }
	
    //Stop recording a test case.
    private void stopRecord() {
    	
    	//Update the text pane.
    	updateGUI("Stopped Recording.");
    	recordButton.setEnabled(true);
    	stopButton.setEnabled(false);
    	
    	//Stop recording user actions.
    	efgtstCapture.stop();

		//Stop ripping new GUIs
		this.guiCapture.disableRipping() ;
    }

    //Save a test case.
    private void saveTest() {
		this.stopRecord() ;
    
    	JFileChooser fileChooser = new JFileChooser(".");

		//Only let you select directories and add chooser to pane. 
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES) ;

		//Make a new action listener for the file chooser.
        ActionListener actionListener = new ActionListener() {
        	
            public void actionPerformed(ActionEvent actionEvent) {
            	
            	//Get the information to check if the file chosen is valid.
            	JFileChooser theFileChooser = (JFileChooser) actionEvent.getSource();
            	String command = actionEvent.getActionCommand();

            	//If the user cancels selecting a program.
            	if(command.equals(JFileChooser.CANCEL_SELECTION)) {
            		return;
            	}
            	
            	//If the file chosen is valid.
            	if(command.equals(JFileChooser.APPROVE_SELECTION)) {
            		//Retrieve the requested file name and location.
            		File f = theFileChooser.getSelectedFile() ;
            		
            		//Obtain the requested URL.
            		String fileURL = f.getAbsolutePath();
            		
            		//This all makes a folder named by user with three files with the same name in it.
            		
            		//Make a folder named as user requested.
            		f.mkdir();
            		
            		//Concatenate first part of .gui, .efg, .tst file names.
            		fileURL = fileURL.concat(fileURL.substring(fileURL.lastIndexOf('/')));

            		//Create the three files in the folder.
            		File guiFile = new File(fileURL + ".GUI");
            		File efgFile = new File(fileURL + ".EFG");
            		File tstFile = new File(fileURL + ".TST");

			//Write a file to save the program details
			String programDetails = "path="+programPath + "\n";
			programDetails += "main="+mainClass;

			try{
				FileWriter fstream = new FileWriter(fileURL + ".PRG");
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(programDetails);
				out.close();
			}catch (Exception e){//Catch exception if any
				System.err.println(e.getMessage());
			}

            		try {
            			guiFile.createNewFile();
            			efgFile.createNewFile();
            			tstFile.createNewFile();
            		} catch (IOException e) {
            			JOptionPane.showMessageDialog(null, "Could not create files.");
            		}
            		
            		//Write the three files in the created directory.
            		try {
            			efgtstCapture.writeFiles(guiCapture.getStructure(), fileURL);
            		} catch (InstantiationException e) {
            			JOptionPane.showMessageDialog(null, "Could not save to files.");
            		}
            		updateGUI("Saved Test: "+fileURL);
            	}	
            }
        };
          
        //Add the action listener created above to file chooser, display it. 
        fileChooser.addActionListener(actionListener);
        fileChooser.showSaveDialog(this);
    }
    
    //Close the program.
    private void closeProgram() {
    	guiCapture.stopCapture();
    	System.exit(0);
    }
    
    //Run selected test cases.
    private void runTests() {
    	
    	for(int i = 0; i <tests.size(); i++) {
    		//If box for test is checked, run it.
    		if(tests.get(i).isSelected()) {
    			
    			//Get the URLs of all of the required files.
    			String folderURL = tests.get(i).getText();
    			String testURL = folderURL.concat(folderURL.substring(folderURL.lastIndexOf('/')));
    			String efgFile = testURL+".EFG";
    			String guiFile = testURL+".GUI";
    			String tstFile = testURL+".TST";
			String prgFile = testURL+".PRG";

			//attempt to read in file with program's parameters
			try{
				FileInputStream fstream = new FileInputStream(prgFile);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				HashMap<String, String> prgParams = new HashMap<String, String>();
				String strLine;
				while ((strLine = br.readLine()) != null){
					//add found parameters into prgParams as <key, value>
					String[] matches = strLine.split("=");
					prgParams.put(matches[0], matches[1]);
				}
				
				if(prgParams.containsKey("path") && prgParams.containsKey("main")){
					programPath = prgParams.get("path");
					mainClass = prgParams.get("main");
				}

				in.close();
			}catch (Exception e){
				System.err.println(e.getMessage());
			}

    			System.out.println("We hit Run");
    			
    			//Run the replayer using the three test files.
    			System.out.println("../../../dist/guitar/jfc-replayer.sh -cp "+programPath+" -c "+mainClass+
				" -g "+guiFile+" -e "+efgFile+" -t "+tstFile);
			try{
	    			Runtime rt = Runtime.getRuntime();
				Process proc = rt.exec("../../../dist/guitar/jfc-replayer.sh -cp "+programPath+" -c "+mainClass+
    					" -g "+guiFile+" -e "+efgFile+" -t "+tstFile);

				//InputStream ips = proc.getInputStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null)
  					System.out.println(inputLine);
				in.close();

				
				

			} catch (Exception e){
				e.printStackTrace();
			}
    		}
    	}
    }
    
    //Add a test case to testBox and tests array.
    private void addTest() {
    	
    	//Set up the frame for the file chooser.
    	final JFrame appframe = new JFrame("Select Application");
        appframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container contentPane = appframe.getContentPane();
        JFileChooser fileChooser = new JFileChooser(".");
        
		//Only let you select directories and add chooser to pane. 
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY) ;
		contentPane.add(fileChooser, BorderLayout.CENTER);
		
		//Make a new action listener for the file chooser.
        ActionListener actionListener = new ActionListener() {
        	
            public void actionPerformed(ActionEvent actionEvent) {
            	
            	//Get the information to check if the file chosen is valid.
            	JFileChooser theFileChooser = (JFileChooser) actionEvent.getSource();
            	String command = actionEvent.getActionCommand();

            	//If the user cancels selecting a program.
            	if(command.equals(JFileChooser.CANCEL_SELECTION)) {
            		appframe.setVisible(false);
            		appframe.dispose();
            		return;
            	}
            	
            	//If the file chosen is valid.
            	if(command.equals(JFileChooser.APPROVE_SELECTION)) {
            		//Retrieve the selected file and ask for the main class name.
            		File f = theFileChooser.getSelectedFile() ;
            		
            		//Obtain the file URL.
            		String fileURL = null;
            		fileURL = f.getAbsolutePath();
            		
            		//Add a checkbox to the testing check pane.
            		JCheckBox newTest = new JCheckBox(fileURL, true);
            		testBox.setEditable(true);
            		testBox.add(newTest);
            		testBox.repaint();
            		testBox.setEditable(false);
            		//Add the test to the list of tests.
            		tests.add(newTest);
					
            		//Make the file chooser disappear.
            		appframe.setVisible(false) ;
            		appframe.dispose() ;
            	}	
            }
        };
          
        //Add the action listener created above to file chooser, display it. 
        fileChooser.addActionListener(actionListener);
        appframe.pack();
        appframe.setVisible(true);
    	
    }
    
    //Remove a test case from testBox and tests array.
    private void removeTest() {
    	
    	//Make sure they want to remove the checked tests.
    	String message = "Are you sure you would like to remove the selected tests?";
    	int decision = JOptionPane.showConfirmDialog(null, message, "Remove", JOptionPane.OK_CANCEL_OPTION);
    	
    	//If they definitely do want to remove.
    	if(decision == JOptionPane.OK_OPTION) {
    		
    		//Make array list copy, so as to avoid index errors.
    		ArrayList<JCheckBox> newTests = (ArrayList<JCheckBox>) tests.clone();
    		int removed = 0;
    		
    		for(int i = 0; i < tests.size(); i++) {
    			if(tests.get(i).isSelected()) {
    				//Remove from the tests copy.
    				newTests.remove(i-removed);
    				//Remove from the GUI box.
    				testBox.setEditable(true);
            		testBox.remove(tests.get(i));
            		testBox.repaint();
            		testBox.setEditable(false);
    			}
    		}
    		
    		//Update the tests with the newly made list.
    		tests = newTests;
    	}
    }
    
  
}



