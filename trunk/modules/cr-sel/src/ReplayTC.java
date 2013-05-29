import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ReplayTC {
	
	public static void main(String args[]){
		String replayerArgs[]= {"edu.umd.cs.guitar.replayer.WebPluginInfo",
								"-g",
								".GUI to be chosen",
								"-e",
								".EFG to be chosen",
								"-t",
								".tst to be chosen",
								"-d",
								"1000"};
		
		JFileChooser guiFileChooser= new JFileChooser();
		JFrame guiFileChooserFrame=  new JFrame();
		
		JFileChooser efgFileChooser=  new JFileChooser();
		JFrame efgFileChooserFrame= new JFrame();
		
		JFileChooser tcFileChooser=  new JFileChooser();
		JFrame tcFileChooserFrame= new JFrame();
		File tcFile= null;
		String tcFileName;
		
		guiFileChooser.setDialogTitle("Choose .GUI file made for the TestCase you want to replay");
		guiFileChooser.showOpenDialog(guiFileChooserFrame);
		try{
			
			replayerArgs[2]= guiFileChooser.getSelectedFile().getPath();
		} catch(Exception e){
			
			System.out.println("CANNOT REPLAY:  .GUI file must be chosen");
			System.exit(0);
		}
		
		efgFileChooser.setDialogTitle("Choose .EFG file made for the TestCase you want to replay");
		efgFileChooser.showOpenDialog(efgFileChooserFrame);
		try{
			
			replayerArgs[4]= efgFileChooser.getSelectedFile().getPath();
		} catch(Exception e){
			
			System.out.println("CANNOT REPLAY:  .EFG file must be chosen");
			System.exit(0);
		}
		
		tcFileChooser.setDialogTitle("Choose .tst made for the TestCase you want to replay");
		tcFileChooser.showOpenDialog(tcFileChooserFrame);
		try{
			
			replayerArgs[6]= tcFileChooser.getSelectedFile().getPath();
		} catch(Exception e){
			
			System.out.println("CANNOT REPLAY:  .tst TestCase file must be chosen");
			System.exit(0);
		}

		try {

			edu.umd.cs.guitar.replayer.Launcher.main(replayerArgs);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}
