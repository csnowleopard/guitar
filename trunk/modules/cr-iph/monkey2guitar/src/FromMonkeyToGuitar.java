import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JFileChooser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;




public class FromMonkeyToGuitar {
	private static class UIElement {
		int x, y, height, weight;
		String className, action;

		public UIElement(int x, int y, int weight, int height, String className,
				String action) {
			this.x = x;
			this.y = y;
			this.height = height;
			this.weight = weight;
			this.className = className;
			this.action = action;
		}

		String print(int monkeyID) {
			return className + " #" + monkeyID + " " + x + " " + y + " "
					+ height + " " + weight;
		}
	}
	
	public static void main(String[] args) {
//		JFileChooser jfc=new JFileChooser();
//		jfc.setFileFilter(new EFGFilter());
//		jfc.setMultiSelectionEnabled(false);
//		if(jfc.showDialog(null, "Please choose the EFG File")!=JFileChooser.APPROVE_OPTION){
//			return;
//		}
//		Document efgDoc=null;
//		try {
//			efgDoc = XmlUtility.parse(jfc.getSelectedFile());
//		} catch (ParserConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return;
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return;
//		}
//		EFG efg=new EFG(efgDoc);
//		
//		
//		jfc=new JFileChooser();
//		jfc.setFileFilter(new GUIFilter());
//		jfc.setMultiSelectionEnabled(false);
//		
//		if(jfc.showDialog(null, "Please choose the GUI File")!=JFileChooser.APPROVE_OPTION){
//			return;
//		}
//		Document guiDoc=null;
//		try {
//			guiDoc = XmlUtility.parse(jfc.getSelectedFile());
//		} catch (ParserConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return;
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return;
//		}
//		GUI gui=new GUI(guiDoc,efg.getIDs());
//		Map<String,String> monkeyToGuitarMap=gui.monkeyIDToEventID();
		
		JFileChooser jfc=new JFileChooser();
		jfc.setFileFilter(new MTFilter());
		jfc.setMultiSelectionEnabled(false);
		if(jfc.showDialog(null, "Open Monkey Test File")!=JFileChooser.APPROVE_OPTION){
			return;
		}
		Scanner sc=null;
		try {
			sc=new Scanner(jfc.getSelectedFile());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		Document output=null;
		try {
			output = XmlUtility.getDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Element root = output.createElement("TestCase");
		output.appendChild(root);
//		<Step>
//        <EventId>e101610396</EventId>
//        <ReachingStep>false</ReachingStep>
//    </Step>

		TreeMap<String,Integer> uniqueCommand=new TreeMap<String,Integer>();
		while(sc.hasNextLine()){
			String s=sc.nextLine();
			if(!s.trim().equals("")){
			System.out.println(s);
			String[] ss=s.split(" ");
			String monkeyID="";
			if(ss.length>=2){
				if(ss[0].equals("Button")&&ss[2].equals("Tap")){
					if(ss[1].equals("*")){
						monkeyID=ss[0]+" #1";
					}else{
						monkeyID=ss[0]+" "+ss[1];
					}
				}else if(ss[0].equals("DatePicker")){
					if(ss[1].equals("*")){
						monkeyID=ss[0]+" #1";
					}else{
						monkeyID=ss[0]+" "+ss[1];
					}
				}
				
				Integer eventID=Math.abs((int) ((monkeyID.hashCode()*2)& 0xffffffffL));
				uniqueCommand.put(monkeyID,eventID);
				System.out.println(monkeyID);
				Element step = output.createElement("Step");
				Element id = output.createElement("EventId");
				id.setTextContent("e"+eventID);
				Element rs = output.createElement("ReachingStep");
				rs.setTextContent("false");
				step.appendChild(id);
				step.appendChild(rs);
				root.appendChild(step);
			}
				
			}
		}
		
		jfc=new JFileChooser();
		jfc.setMultiSelectionEnabled(false);
		if(jfc.showDialog(null, "Open Structure File")!=JFileChooser.APPROVE_OPTION){
			return;
		}
		sc=null;
		try {
			sc=new Scanner(jfc.getSelectedFile());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String,UIElement> elementToCoor=new TreeMap<String,UIElement>();
		int xOffset=0;
		int yOffset=0;
		while(sc.hasNextLine()){
			String s=sc.nextLine();
			String[] ss=s.split(" ");
			if(ss[0].equals("UIView")){
				xOffset=Integer.parseInt(ss[1]);
				yOffset=Integer.parseInt(ss[2]);
			}else{
				if(ss[0].contains("Button")){
					elementToCoor.put("Button "+ss[1], new UIElement(new Integer(ss[2])-xOffset,new Integer(ss[3])-yOffset,new Integer(ss[4]),new Integer(ss[5]),ss[0],"TOUCH"));
				}else{
					elementToCoor.put("DataPicker "+ss[1], new UIElement(new Integer(ss[2])-xOffset,new Integer(ss[3])-yOffset,new Integer(ss[4]),new Integer(ss[5]),ss[0],"PICKERW"));
				}
			}
		
		}
		
		jfc=new JFileChooser();
		jfc.setFileFilter(new GuitarTCFilter());
		jfc.setMultiSelectionEnabled(false);
		
		if(jfc.showDialog(null, "Save GUITAR Test File")!=JFileChooser.APPROVE_OPTION){
			return;
		}
		
		File outputFile=jfc.getSelectedFile();
		System.out.println(outputFile.getAbsoluteFile());
		if(!outputFile.getName().toLowerCase().endsWith(".tst")){
			if(outputFile.canWrite()){
				outputFile.renameTo(new File(outputFile.getAbsoluteFile()+".tst"));
			}else{
				outputFile=new File(outputFile.getAbsoluteFile()+".tst");
			}
		}
		System.out.println(outputFile.getAbsoluteFile());
		
		
		try {
			XmlUtility.write(output, outputFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		Document outputEFG=null;
		try {
			outputEFG = XmlUtility.getDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Element efg = outputEFG.createElement("EFG");
		outputEFG.appendChild(efg);
		Element events=outputEFG.createElement("Events");
		efg.appendChild(events);
		for(Map.Entry<String, Integer> entry:uniqueCommand.entrySet()){
			Element event=outputEFG.createElement("Event");
			Element eventID=outputEFG.createElement("EventId");
			eventID.setTextContent("e"+entry.getValue());
			Element widgetId=outputEFG.createElement("WidgetId");
			widgetId.setTextContent("w"+entry.getValue());
			Element type=outputEFG.createElement("Type");
			type.setTextContent("EXPAND");
			Element initial=outputEFG.createElement("Initial");
			initial.setTextContent("true");
			Element action=outputEFG.createElement("Action");
			if(entry.getKey().contains("Button")){
				action.setTextContent("edu.umd.cs.guitar.event.IphTouchEvent");
			}else if(entry.getKey().contains("Picker")){
				action.setTextContent("edu.umd.cs.guitar.event.IphPickerWheelEvent");
			}else{
				action.setTextContent("edu.umd.cs.guitar.event.IphTouchEvent");
			}
			event.appendChild(eventID);
			event.appendChild(widgetId);
			event.appendChild(type);
			event.appendChild(initial);
			event.appendChild(action);
			
			events.appendChild(event);
		}
		
		Element eventGraph=outputEFG.createElement("EventGraph");
		efg.appendChild(eventGraph);
		for(int i=0;i<uniqueCommand.size();i++){
			Element row=outputEFG.createElement("Row");
			eventGraph.appendChild(row);
			for(int j=0;j<uniqueCommand.size();j++){
				Element e=outputEFG.createElement("E");
				e.setTextContent("1");
				row.appendChild(e);
			}
		}
		
		
		jfc=new JFileChooser();
		jfc.setFileFilter(new EFGFilter());
		jfc.setMultiSelectionEnabled(false);

		if(jfc.showDialog(null, "Save GUITAR EFG File")!=JFileChooser.APPROVE_OPTION){
			return;
		}
		
		File outputEFGFile=jfc.getSelectedFile();
		if(!outputEFGFile.getName().toLowerCase().endsWith(".efg")){
			if(outputEFGFile.canWrite()){
				outputEFGFile.renameTo(new File(outputEFGFile.getAbsoluteFile()+".EFG"));
			}else{
				outputEFGFile=new File(outputEFGFile.getAbsoluteFile()+".EFG");
			}
		}
		
		
		
		try {
			XmlUtility.write(outputEFG, outputEFGFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		/*Create GUI File*/
		Document outputGUI=null;
		try {
			outputGUI = XmlUtility.getDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Element guiStructure = outputGUI.createElement("GUIStructure");
		Element gui = outputGUI.createElement("GUI");
		guiStructure.appendChild(gui);
		outputGUI.appendChild(guiStructure);

		gui.appendChild(createWindow(outputGUI));
		Element container = outputGUI.createElement("Container");
		
		gui.appendChild(container);
		for(Map.Entry<String, Integer> entry:uniqueCommand.entrySet()){
			Element contents = outputGUI.createElement("Contents");
			container.appendChild(contents);
			Element newContainer = outputGUI.createElement("Container");
			Element attributes=outputGUI.createElement("Attributes");
			contents.appendChild(newContainer);
			newContainer.appendChild(attributes);
			container=newContainer;
			UIElement uiElement=elementToCoor.get(entry.getKey());
			for(int i=0;i<=11;i++){
				Element property=outputGUI.createElement("Property");
				Element name=outputGUI.createElement("Name");
				Element value=outputGUI.createElement("Value");
				property.appendChild(name);
				property.appendChild(value);
				switch(i){
				case 0:name.setTextContent("ID");value.setTextContent("w"+entry.getValue());break;
				case 1:name.setTextContent("Class");value.setTextContent(uiElement.className);break;
				case 2:name.setTextContent("Type");value.setTextContent(uiElement.className);break;
				case 3:name.setTextContent("X");value.setTextContent(""+uiElement.x);break;
				case 4:name.setTextContent("Y");value.setTextContent(""+uiElement.y);break;
				case 5:name.setTextContent("ReplayableAction");value.setTextContent((entry.getKey().contains("Button")) ? "edu.umd.cs.guitar.event.IphTouchEvent":"edu.umd.cs.guitar.event.IphPickerWheelEvent");break;
				case 6:name.setTextContent("x");value.setTextContent(""+uiElement.x);break;
				case 7:name.setTextContent("y");value.setTextContent(""+uiElement.y);break;
				case 8:name.setTextContent("width");value.setTextContent(""+uiElement.weight);break;
				case 9:name.setTextContent("height");value.setTextContent(""+uiElement.height);break;
				case 10:name.setTextContent("className");value.setTextContent(uiElement.className);break;
				case 11:name.setTextContent("INVOKE");value.setTextContent((entry.getKey().contains("Button")) ? "TOUCH":"PICKER_WHEEL");break;
				}
				attributes.appendChild(property);
				
			}
			
		}
		

		
		
		jfc=new JFileChooser();
		jfc.setFileFilter(new GUIFilter());
		jfc.setMultiSelectionEnabled(false);

		if(jfc.showDialog(null, "Save GUITAR GUI File")!=JFileChooser.APPROVE_OPTION){
			return;
		}
		
		File outputGUIFile=jfc.getSelectedFile();
		if(!outputGUIFile.getName().toLowerCase().endsWith(".gui")){
			if(outputGUIFile.canWrite()){
				outputGUIFile.renameTo(new File(outputGUIFile.getAbsoluteFile()+".GUI"));
			}else{
				outputGUIFile=new File(outputGUIFile.getAbsoluteFile()+".GUI");
			}
		}
		
		
		
		try {
			XmlUtility.write(outputGUI, outputGUIFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	private static Element createWindow(Document outputGUI) {
		Element window=outputGUI.createElement("Window");
		Element attributes=outputGUI.createElement("Attributes");
		window.appendChild(attributes);
		for(int i=0;i<=7;i++){
			Element property=outputGUI.createElement("Property");
			Element name=outputGUI.createElement("Name");
			Element value=outputGUI.createElement("Value");
			property.appendChild(name);
			property.appendChild(value);
			switch(i){
			case 0:name.setTextContent("Title");value.setTextContent("UIWindow");break;
			case 1:name.setTextContent("Modal");value.setTextContent("false");break;
			case 2:name.setTextContent("Rootwindow");value.setTextContent("true");break;
			case 3:name.setTextContent("x");value.setTextContent("0");break;
			case 4:name.setTextContent("y");value.setTextContent("0");break;
			case 5:name.setTextContent("width");value.setTextContent("320");break;
			case 6:name.setTextContent("height");value.setTextContent("480");break;
			case 7:name.setTextContent("className");value.setTextContent("UIWindow");break;
			}
			attributes.appendChild(property);
			
		}
		return window;
	}
}
