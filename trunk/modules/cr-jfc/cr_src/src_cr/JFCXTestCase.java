import java.awt.Component;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import javax.swing.event.*;
import java.awt.Frame ;
import java.awt.Window ;
import java.awt.Container ;
import edu.umd.cs.guitar.model.* ;
import edu.umd.cs.guitar.model.data.* ;
import java.io.*;

import edu.umd.cs.guitar.graph.plugin.EFGConverter ;
import edu.umd.cs.guitar.graph.plugin.GraphConverter;

//test comment

public class JFCXTestCase{
	//holds hashes, in order, of components clicked
	private ArrayList<String> components = new ArrayList<String>();
	private boolean recording = false;
	
	public void start(){
		components = new ArrayList<String>();
		recording = true;
	}
	
	public void stop(){
		recording = false;
	}

  public void reset(){
    components = new ArrayList<String>();
    recording = false;
  }

	public ArrayList<String> getOrderedComponents(){
		return new ArrayList<String>(components);
	}

  public void addComponent(Component component){
    components.add(generateHash(component));
  }

	public String generateHash(Component component){
		//Frame frame = (Frame) SwingUtilities.getRoot(component) ;
		// JFK: This was causing problems with JDialog objects etc that are
		//      windows but not frames
		//      Luckily Frames are Windows, so we're still awesome
		Window frame = (Window) SwingUtilities.getRoot(component) ;
		JFCXWindow window = new JFCXWindow(frame) ;

		JFCXComponent jfcXComponent = new JFCXComponent(component, window) ;
		ComponentType componentType = jfcXComponent.extractProperties() ;
		JFCDefaultIDGeneratorCustom customGenerator = JFCDefaultIDGeneratorCustom.getInstance() ;
		return customGenerator.getEntireHashcode(componentType);
	}

	public void writeFiles(GUIStructure gui, String filename) throws InstantiationException {
		//0. add IDs to GUIStructure
		JFCDefaultIDGeneratorCustom.getInstance().generateID(gui) ;

		//1. write GUI file
		IO.writeObjToFile(gui, filename+".GUI") ;

		//2. assemble EFG
		//EFG efg = (EFG)(new EFGConverter()).generate(gui) ;

		try{
    			Runtime rt = Runtime.getRuntime();
			System.out.println("../../../dist/guitar/gui2efg.sh -g " + filename + ".GUI" + " -e " + filename + ".EFG");
			Process proc = rt.exec("../../../dist/guitar/gui2efg.sh -g " + filename + ".GUI" + " -e "  + filename + ".EFG");
			InputStream ips = proc.getInputStream();
			proc.waitFor();
		} catch (Exception e){
			e.printStackTrace();
		}

		//3. write EFG
		//IO.writeObjToFile(efg, filename+".EFG") ;

		//4. generate test case
		String tst = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" ;
		tst += "\n<TestCase>\n" ;
		for(String cID : components){
			tst += "\t<Step>\n" ;
			tst += "\t\t<EventId>e" + cID + "</EventId>\n" ;
			tst += "\t\t<ReachingStep>false</ReachingStep>\n" ;
			tst += "\t</Step>\n" ;
		}

		tst += "\n</TestCase>\n" ;
		
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(filename+".TST")) ;
			out.write(tst) ;
			out.close() ;
		}catch (IOException e){
			System.out.println("We had an exception!") ;
		}
	}
}
