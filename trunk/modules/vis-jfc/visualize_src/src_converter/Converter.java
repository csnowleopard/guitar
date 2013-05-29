import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class Converter {
	HashMap<String,ComponentNode> nodeMap;
	Document dom;
	ArrayList<ComponentNode> startingNodes;
	ArrayList<ComponentNode> allNodes;
	ArrayList<WidgetEvent> events;
	int[][] adj;
	String outputDir;

	//Arg 0 is the GUI file and Arg 1 is EFG file
	public static void main(String [] args) {
		if(args.length != 3) {
			System.out.println("Usage: Converter.jar [GUI] [EFG] [OUTPUT_DIRECTORY]");
			return;
		}
		new Converter(args[0], args[1], args[2]);
	}

	public Converter(String GUIfile, String EFGfile, String outputDirectory) {
		nodeMap = new HashMap<String, ComponentNode>();
		startingNodes = new ArrayList<ComponentNode>();
		allNodes = new ArrayList<ComponentNode>();
		events = new ArrayList<WidgetEvent>();

		outputDir = outputDirectory;
		
		parseGUI(GUIfile);
		parseEFG(EFGfile);
		//printAdj();
		generateGraph();
		JSonGenerator JSonFile = new JSonGenerator();
		//System.out.println(allNodes.size());
		/*
		for(ComponentNode n: allNodes)
		{
			JSonFile.add(n);
		}
		JSonFile.genJSONString(allNodes);
		System.out.println("JSON String" + JSonFile.getJSONString());
*/
		File f = new File(outputDirectory + "/json_object.js");
		if(!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		FileWriter fw;
		try {
			fw = new FileWriter(f);
			PrintWriter p = new PrintWriter(fw);
			//String s = JSonFile.getJSONString();
			p.println("var myJSONObject = " + JSonFile.genJSONString(allNodes));
			p.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//for(int i = 0; i < allNodes.size(); i++) {
		//	System.out.println("Interaction Type: " + allNodes.get(i)._type);
		//}
		
		//Uncomment this to run the graph simulation.
		//runSimulation();
		
		//Uncomment this to export an object file
		//Peter put this in with the function it calls...just disregard it.
		//exportGraphFile();
		
	}

	public void exportGraphFile() {
		File f = new File("src/data.graph");
		if(f.exists()) {
			f.delete();
		}
		try {
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(allNodes);
			oos.writeObject(startingNodes);
			
			oos.close();
			fos.close();
	
			JOptionPane.showMessageDialog(null, "Graph Data File Generated!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void runSimulation() {
		System.out.println("Size: "+ allNodes.size());
		System.out.println("Available Starting Nodes: (-9 to Exit)");
		for(ComponentNode c : startingNodes) {
				System.out.println(c.eventIndex + ": " + c._title);
		}
		int choice = -1;
		Scanner in = new Scanner(System.in);
		choice = in.nextInt();
		while(choice != -9) {
			System.out.println("Available Nodes: (-9 to Exit)");
			for(ComponentNode c : allNodes.get(choice).relations) {
					System.out.println(c.eventIndex + ": " + c._title);
			}
			choice = in.nextInt();
		}
	}

	private void printAdj() {
		for(int i = 0; i < adj.length; i++) {
			for(int j = 0; j < adj[i].length; j++) {
				System.out.print(adj[i][j] + " ");
			}
			System.out.println();
		}
	}

	private void generateGraph() {
		for(int i = 0; i < events.size(); i ++) {
			WidgetEvent we = events.get(i);
			String wId = we._wId;
			ComponentNode cn = nodeMap.get(wId);
			if(cn != null) {
				cn.eventIndex = we.eventIndex;

				allNodes.add(cn);

				ComponentNode c;
				for(int j = 0; j < events.size(); j++) {
					if(j != i) {
						//System.out.println(i+"");

						if(adj[i][j] > 0) {
							c = nodeMap.get((events.get(j)._wId));
							if(adj[i][j] == 2) {
								c.isRoot = false;
							}
							if(cn != null) {
								cn.relations.add(c);
							}
						}
					}
				}
			}
		}
		for(ComponentNode c : allNodes) {
			if(c.isRoot)
				startingNodes.add(c);
		}
	}

	public void parseEFG(String filePath) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(filePath);

			//root element
			Element docEle = dom.getDocumentElement();

			NodeList nl = docEle.getElementsByTagName("Events");
			//System.out.println(nl.item(0).getNodeName());
			parseEvents((Element)(nl.item(0)));

			adj = new int[events.size()][events.size()];

			NodeList nl2 = docEle.getElementsByTagName("EventGraph");
			//System.out.println(nl2.item(0).getNodeName());
			parseAdj(((Element)nl2.item(0)));

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void parseAdj(Element el) {
		NodeList nl = el.getElementsByTagName("Row");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0; i < nl.getLength(); i++) {
				parseRow(i, (Element)(nl.item(i)));
			}
		}
	}

	private void parseRow(int row, Element el) {
		NodeList nl = el.getElementsByTagName("E");
		int val;
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0; i < nl.getLength(); i++) {
				val = Integer.parseInt(nl.item(i).getTextContent());
				adj[row][i] = val;
			}
		}
	}

	private void parseEvents(Element el) {
		NodeList nl = el.getElementsByTagName("Event");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0; i < nl.getLength(); i++) {
				//System.out.println(nl.item(i).getNodeName());
				if(nl.item(i).getNodeName().equals("Event")) {
					WidgetEvent we = readWidgetEvent((Element)(nl.item(i)));
					we.eventIndex = i;
					events.add(we);
				}
			}
		}
	}

	private WidgetEvent readWidgetEvent(Element el) {
		WidgetEvent we = new WidgetEvent();
		we._wId = getTextValue(el, "WidgetId");

		return we;
	}

	public void parseGUI(String filePath) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(filePath);

			//root element
			Element docEle = dom.getDocumentElement();

			parseElement(docEle);			

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	boolean rootWindowFound = false;
	boolean isRootWindow = false;

	public void parseElement(Node el) {
		//System.out.println(el.getNodeName());
		
		
		
		NodeList nl = el.getChildNodes();
		Node item;
		String val;
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				item = nl.item(i);

				if(item.getNodeName().equals("Widget")) {
					addWidget(item);
				} else if(item.getNodeName().equals("Container")) {
					addContainer(item);
				} else if(item.getNodeName().equals("Name")) {
					val = item.getTextContent();
					if(val.equals("Rootwindow")) {
						rootWindowFound = true;
					}
				} else if(item.getNodeName().equals("Value") && rootWindowFound == true) {
					rootWindowFound = false;
					val = item.getTextContent();
					if(val.equals("true")) {
						//System.out.println("FOUND ROOT WINDOW!");
						isRootWindow = true;
					} else {
						isRootWindow = false;
					}
				} 
				parseElement(item);
			}
		}
	}

	private void addContainer(Node widget) {

		NodeList attrib = ((Element)widget).getElementsByTagName("Attributes");
		Node attribNode = attrib.item(0);

		NodeList props = attribNode.getChildNodes();

		ComponentNode n = new ComponentNode();
		Node prop;
		String propName = "";
		String propVal = "";

		n.nodeType = "container";

		if(props != null && props.getLength() > 0) {
			for(int i = 0; i < props.getLength(); i++) {
				prop = props.item(i);
				
				if(prop.getNodeName().equals("Property")) {
					propName = getTextValue((Element) prop, "Name");
					//System.out.println(propName);
					if(propName.equals("ID")) {
						propVal = getTextValue((Element)prop,"Value");
						n._wId = propVal;
					} else if(propName.equals("Class")) {
						propVal = getTextValue((Element)prop,"Value");
						n._class = propVal;
					} else if(propName.equals("Type")) {
						propVal = getTextValue((Element)prop,"Value");
						n._type = propVal;
					} else if(propName.equals("Title")) {
						propVal = getTextValue((Element)prop,"Value");
						if(propVal!=null &&propVal.contains("Pos(")) propVal = n._class;
							n._title = propVal;
					} else if(propName.equals("Text")) {
						propVal = getTextValue((Element)prop,"Value");
						n._text = propVal;
					}else if(propName.equals("Icon")) {
						propVal = getTextValue((Element)prop,"Value");
						if(n._title==null)
						n._title = "Icon:"+ propVal + " Class: "+ n._class;
					}else if(propName.equals("X")) {
						propVal = getTextValue((Element)prop,"Value");
						n._x = Integer.parseInt(propVal);
					}else if(propName.equals("Y")) {
						propVal = getTextValue((Element)prop,"Value");
						n._y = Integer.parseInt(propVal);
					}
				}
			}
		}
		//System.out.println("Adding Widget [" + n._wId + "]");
		n.isRoot = isRootWindow;
		nodeMap.put(n._wId, n);
	}

	private void addWidget(Node widget) {

		NodeList attrib = ((Element)widget).getElementsByTagName("Attributes");
		Node attribNode = attrib.item(0);

		NodeList props = attribNode.getChildNodes();

		ComponentNode n = new ComponentNode();
		Node prop;
		String propName = "";
		String propVal = "";

		n.nodeType = "widget";

		if(props != null && props.getLength() > 0) {
			for(int i = 0; i < props.getLength(); i++) {
				prop = props.item(i);
				if(prop == null)
					continue;
				if(prop.getNodeName().equals("Property")) {
					propName = getTextValue((Element)prop, "Name");

					if(propName.equals("ID")) {
						propVal = getTextValue((Element)prop,"Value");
						n._wId = propVal;
					} else if(propName.equals("Class")) {
						propVal = getTextValue((Element)prop,"Value");
						n._class = propVal;
					} else if(propName.equals("Type")) {
						propVal = getTextValue((Element)prop,"Value");
						n._type = propVal;
					} else if(propName.equals("Title")) {
						propVal = getTextValue((Element)prop,"Value");
					if(propVal!=null&&propVal.contains("Pos(")) propVal = "Class: "+n._class;
						n._title =  propVal;
					} else if(propName.equals("Text")) {
						propVal = getTextValue((Element)prop,"Value");
						n._text = propVal;
					}else if(propName.equals("Icon")) {
						propVal = getTextValue((Element)prop,"Value");
						if(n._title==null)
						n._title = "Icon:"+ propVal + " Class: "+ n._class;
					}else if(propName.equals("X")) {
						propVal = getTextValue((Element)prop,"Value");
						n._x = Integer.parseInt(propVal);
					}else if(propName.equals("Y")) {
						propVal = getTextValue((Element)prop,"Value");
						n._y = Integer.parseInt(propVal);
					}
				}
			}
			if(n._title==null)
			{
				n._title="Class: "+n._class;
			}
		}
		//System.out.println("Adding Widget [" + n._wId + "]");
		n.isRoot = isRootWindow;
		nodeMap.put(n._wId, n);
	}

	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0 && nl.item(0).getFirstChild()!=null) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}
}
