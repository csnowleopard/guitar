import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class Viz extends JFrame implements ActionListener {

	JButton generate = new JButton("Generate Visualization");
	JScrollPane jsp;
	JTextArea terminal;

	JFileChooser jfc;

	String guiURL;
	String efgURL;
	String outputDirectory;

	File outputDir;
	File dir1;
	File dir2;

	BufferedReader bis;
	BufferedWriter bos;

	public Viz() {
		super("VizGuitar");
		setSize(500,500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container pane = getContentPane();
		pane.setLayout(new FlowLayout());

		if(isWindows()) {
		terminal = new JTextArea(25,60);
		} else {
		terminal = new JTextArea(20,40);
		}

		terminal.setFont(new Font("Courier New", Font.PLAIN, 12));
		terminal.setForeground(Color.BLACK);

		jsp = new JScrollPane(terminal);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		pane.add(jsp);

		terminal.setEditable(false);

		pane.add(generate);
		generate.addActionListener(this);

		setResizable(false);
		setVisible(true);
	}

	public Viz(boolean suppressed) {
		//Nothing!
	}
	
	private void append(String s) {
		terminal.append(s);
		scrollToEnd();
	}

	private void scrollToEnd() {
		int x;
		terminal.selectAll();
		x = terminal.getSelectionEnd();
		terminal.select(x, x);
	}

	public static void main(String [] args) {
		if(args.length == 0) {
			Viz v = new Viz();
		} else if(args.length != 2) {
			System.out.println("Usage: viz.jar [-s] [efg/gui directory]");
		} else if(args[0].equals("-s")){
			Viz vS = new Viz(true);
			vS.runSuppressed(args[1]);
		} else {
			System.out.println("Usage: viz.jar [-s] [efg/gui directory]");
		}
	}

	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("win") >= 0);
	}

	public static boolean isMac() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("mac") >= 0);
	}

	public static boolean isUnix() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
	}

	private void launch() {
		File f1;
		File f2 = new File(outputDir.getAbsolutePath() + "/index.html");
		if(!f2.exists()) {
			append("ERROR! Could not find index.html\n");
			return;
		}

		if(isWindows()) {
			f1 = new File("batch/runWin.bat");
			if(!f1.exists()) {
				append("ERROR! Could not find runWin.bat\n");
				return;
			}

			try {
				append("Launching index.html via runWin.bat\n");
				Process p = Runtime.getRuntime().exec("cmd.exe /C " + f1.getAbsolutePath() + " " + f2.getAbsolutePath());
				append("\tSuccess!\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(isUnix()) {
			f1 = new File("batch/runNix.sh");
			if(!f1.exists()) {
				append("Error! Could not find runNix.sh\n");
				return;
			}
			try {
				append("Launching index.html via runNix.sh\n");
				System.out.println("bash -x " + f1.getAbsolutePath() + " " + f2.getAbsolutePath());
				Runtime.getRuntime().exec("bash -x " + f1.getAbsolutePath() + " " + f2.getAbsolutePath());
				append("Success!\n");
			} catch(IOException e) {
				e.printStackTrace();
			}
		} else if(isMac()) {
			f1 = new File("batch/runOSX.sh");
			if(!f1.exists()) {
				append("Error! Could not find runOSX.sh\n");
				return;
			}
			try {
				append("Launching index.html via runOSX.sh\n");
				Process p = Runtime.getRuntime().exec("bash " + f1.getAbsolutePath() + " " + f2.getAbsolutePath());
				append("Success!\n");
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == generate) {
			jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			jfc.setDialogTitle("Select GUI/EFG Location");
			int selection = jfc.showOpenDialog(this);
			if(selection == JFileChooser.APPROVE_OPTION) {
				outputDir = jfc.getSelectedFile();
				append("Searching " + jfc.getSelectedFile().getAbsolutePath() + " for EFG and GUI\n");
				File[] list = jfc.getSelectedFile().listFiles();
				boolean[] found = {false, false};
				for(File f : list) {
					String s = f.getAbsolutePath();
					String ext = s.substring(s.length()-3, s.length());
					if(ext.equalsIgnoreCase("efg")) {
						append("Found EFG: " + f.getAbsolutePath() + "\n");
						efgURL = f.getAbsolutePath();
						found[0] = true;
					}
					if(ext.equalsIgnoreCase("gui")) {
						append("Found GUI: " + f.getAbsolutePath() + "\n");
						guiURL = f.getAbsolutePath();
						found[1] = true;
					}
				}

				if(!(found[0] && found[1])) {
					append("Could not find EFG and GUI file.\n");
					return;
				}

				dir1 = new File(jfc.getSelectedFile().getAbsolutePath() + "/JS_sheets/");
				dir2 = new File(jfc.getSelectedFile().getAbsolutePath() + "/style_sheets/");



				if(!dir1.exists()) {
					dir1.mkdir();
				}
				if(!dir2.exists()) {
					dir2.mkdir();
				}

				append("Converting EFG/GUI to JSON\n");
				String[] args = new String[3];
				args[0] = guiURL;
				args[1] = efgURL;
				args[2] = dir1.getAbsolutePath();
				Converter.main(args);
				append("Success!\n");
				try {
					append("\nGenerating:\n");
					
					append("\tdefault.js\n");
					writeDefaultJS();
					
					append("\tdefault.css\n");
					writeStyleSheet();
									
					append("\tindex.html\n");
					writeIndex();
				} catch(IOException ioe) {
					ioe.printStackTrace();
					append("File generation failed!\n");
				}

				int launchOption = JOptionPane.showConfirmDialog(this, "Successfully generated visualizer! Would you like to launch it now?");
				if(launchOption == JOptionPane.YES_OPTION) {
					launch();
				}
				
			} else {
				append("File location selection cancelled by user\n");
			}
		}
	}	

	private void writeStyleSheet() throws IOException {
		bis = new BufferedReader(new FileReader("GUI/style_sheets/default.css"));
		try {
			bos = new BufferedWriter(new FileWriter(dir2.getAbsolutePath() + "/default.css"));
			String line;
			while((line = bis.readLine()) != null) {
				bos.write(line + "\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			bis.close();
			bos.close();
		}
	}

	private void writeIndex() throws IOException {
		bis = new BufferedReader(new FileReader("GUI/index.html"));
		try {
			bos = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath() + "/index.html"));
			String line;
			while((line = bis.readLine()) != null) {
				bos.write(line + "\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			bis.close();
			bos.close();
		}
	}

	private void writeDefaultJS() throws IOException {
		bis = new BufferedReader(new FileReader("GUI/JS_sheets/default.js"));
		try {
			bos = new BufferedWriter(new FileWriter(dir1.getAbsolutePath() + "/default.js"));
			String line;
			while((line = bis.readLine()) != null) {
				bos.write(line + "\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			bis.close();
			bos.close();
		}
	}
	
	private void runSuppressed(String outDir) {
		outputDir = new File(outDir);
		System.out.println("Searching " + outputDir.getAbsolutePath() + " for EFG and GUI");
		File[] list = outputDir.listFiles();
		boolean[] found = {false, false};
		for(File f : list) {
			String s = f.getAbsolutePath();
			String ext = s.substring(s.length()-3, s.length());
			if(ext.equalsIgnoreCase("efg")) {
				System.out.println("Found EFG: " + f.getAbsolutePath());
				efgURL = f.getAbsolutePath();
				found[0] = true;
			}
			if(ext.equalsIgnoreCase("gui")) {
				System.out.println("Found GUI: " + f.getAbsolutePath());
				guiURL = f.getAbsolutePath();
				found[1] = true;
			}
		}

		if(!(found[0] && found[1])) {
			System.out.println("Could not find EFG and GUI file.");
			return;
		}

		dir1 = new File(outputDir.getAbsolutePath() + "/JS_sheets/");
		dir2 = new File(outputDir.getAbsolutePath() + "/style_sheets/");



		if(!dir1.exists()) {
			dir1.mkdir();
		}
		if(!dir2.exists()) {
			dir2.mkdir();
		}

		System.out.println("Converting EFG/GUI to JSON");
		String[] args = new String[3];
		args[0] = guiURL;
		args[1] = efgURL;
		args[2] = dir1.getAbsolutePath();
		Converter.main(args);
		System.out.println("Success!");
		try {
			System.out.println("Generating:");
			
			System.out.println("\tdefault.js");
			writeDefaultJS();
			
			System.out.println("\tdefault.css");
			writeStyleSheet();
							
			System.out.println("\tindex.html");
			writeIndex();
		} catch(IOException ioe) {
			System.out.println("File generation failed!");
			return;
		}

		System.out.println("Successfully generated visualizer!");
	}
}
