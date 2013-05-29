import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.w3c.dom.*;

import edu.umd.cs.guitar.graph.GUIStructure2GraphConverter;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SeleniumXMLParser{

	private ArrayList<String> methods;
	private ArrayList<String> inputs;
	private ArrayList<String> commands;
	private ArrayList<String> values;
	private ArrayList<Boolean> reachingSteps;
	private ArrayList<Event> eventList;
	private String baseURL;

	private Map<String, Map<Component, ArrayList<Event>>> pageToComponentEvents;

	//Constructor.
	public SeleniumXMLParser(){
		methods = new ArrayList<String>();
		inputs = new ArrayList<String>();
		commands = new ArrayList<String>();
		values = new ArrayList<String>();
		reachingSteps = new ArrayList<Boolean>();
		eventList = new ArrayList<Event>();
		pageToComponentEvents = new LinkedHashMap<String, Map<Component, ArrayList<Event>>>();
		baseURL = "";
	}

	//Getters.
	public ArrayList<String> getMethods(){
		return methods;
	}

	public ArrayList<String> getInputs(){
		return inputs;
	}

	public ArrayList<String> getCommands(){
		return commands;
	}

	public ArrayList<String> getValues(){
		return values;
	}

	public ArrayList<Boolean> getReaches(){
		return reachingSteps;
	}

	public ArrayList<Event> getEvents(){
		return eventList;
	}

	public String getBaseURL(){
		return baseURL;
	}

	private void parseXML(File file){
		try{
			//	File file = new File(fileName);
			if (file.exists()){
				//Gets the root XML tag, in this case the <TestCase> tag
				DocumentBuilderFactory fact = 
						DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = fact.newDocumentBuilder();
				Document doc = builder.parse(file.getPath());

				Element testCaseElement = (Element)doc.getElementsByTagName("TestCase").item(0);
				baseURL = testCaseElement.getAttribute("baseURL");

				//Get a NodeList of <Step> elements
				NodeList nl = doc.getElementsByTagName("Step");
				if(nl != null && nl.getLength() > 0) {
					for(int i = 0 ; i < nl.getLength();i++) {

						//get the <Step> element
						Element el = (Element)nl.item(i);

						String value = "";
						String target = "";
						String command = "";
						String reach = "";
						boolean reachingStep;

						//gets the <Step> properties
						if(el.getElementsByTagName("target").item(0).getFirstChild() != null)
							target = el.getElementsByTagName("target").item(0).getFirstChild().getNodeValue();
						if(el.getElementsByTagName("command").item(0).getFirstChild()!=null)
							command = el.getElementsByTagName("command").item(0).getFirstChild().getNodeValue();
						if(el.getElementsByTagName("value").item(0).getFirstChild() != null)
							value = el.getElementsByTagName("value").item(0).getFirstChild().getNodeValue();
						if(el.getElementsByTagName("ReachingStep").item(0).getFirstChild() != null)
							reach = el.getElementsByTagName("ReachingStep").item(0).getFirstChild().getNodeValue();

						reachingStep = Boolean.parseBoolean(reach);

						addInputsAndMethods(target, command, value, reachingStep);
					}
				}

			}
			else{
				System.out.println("File not found!");
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
	}

	//Populates the inputs and methods arraylists.
	private void addInputsAndMethods(String target, String command, String value, boolean reach){
		

		String input = "";
		String method = "";

		int loc = target.indexOf("=")+1; //gets the location of the first '=', used later in conjunction with substring to get the target input
		
		if(command.equals("open") && !baseURL.contains(target)){
			baseURL+=target;
		}
		
		if(target.contains("link=")){
			method = "linkText";
			input = target.substring(loc).trim();
		}else if(target.contains("//")){
			method = "xpath";
			input = target;
		}else if(target.contains("id=") || target.contains("identifier=")){
			method = "id";
			input = target.substring(loc).trim();
		}else if(target.contains("name=")){
			method = "name";
			input = target.substring(loc).trim();
		}else if(target.contains("dom=")){
			method = "dom";
			input = target.substring(loc).trim();
		}else if(target.contains("css=")){
			method = "cssSelector";
			input = target.substring(loc).trim();
		}else if(target.contains("ui=")){
			method = "ui";
			input = target.substring(loc).trim();
		}


		//Method and input go hand in hand
		inputs.add(input);
		methods.add(method);
		commands.add(command);
		values.add(value);
		reachingSteps.add(reach);
	}

	//Processes each elements and prints out all relevant information. 
	public void processElements(){
		FirefoxDriver driver = new FirefoxDriver();
		driver.get(baseURL);
		System.out.println("BaseURL: " + baseURL + "\n");

		Component c = null;

		try{
			// Create file 
			FileWriter fstream = new FileWriter(fname+".log");
			BufferedWriter out = new BufferedWriter(fstream);

			for(int i = 0; i < inputs.size(); i++){

				try{

					String currentURL = driver.getCurrentUrl();
					String by = methods.get(i);
					String input = inputs.get(i); //Target specific
					String command = commands.get(i);
					boolean reach = reachingSteps.get(i);
					String value = values.get(i);

					Event e = new Event(command, (by + "=" + input), value, reach,null);

					System.out.println("By: " + by);
					System.out.println("Input: " + input);

					//Initialize the current web element.
					WebElement wE = null;

					try{

						if(by.equals("className")){
							wE = driver.findElement(By.className(input));
						}else if(by.equals("cssSelector")){
							wE = driver.findElement(By.cssSelector(input));
						}else if(by.equals("id")){
							wE = driver.findElement(By.id(input));
						}else if(by.equals("linkText")){
							wE = driver.findElement(By.linkText(input));
						}else if(by.equals("name")){
							wE = driver.findElement(By.name(input));
						}else if(by.equals("partialLinkText")){
							wE = driver.findElement(By.partialLinkText(input));
						}else if(by.equals("tagName")){
							wE = driver.findElement(By.tagName(input));
						}else if(by.equals("xpath")){
							wE = driver.findElement(By.xpath(input));
						}

					} catch(Exception e1){
						System.out.println("Skipping " + by);
					}

					if(wE != null){

						//Print information and add it to components.
						Point p = wE.getLocation();
						System.out.println("X: " + p.x);
						System.out.println("Y: " + p.y);

						Map<String, String> attributes = new HashMap<String, String>();

						//Consider more tag types?
						if(wE.getTagName().equals("a")){
							System.out.println("href: " + wE.getAttribute("href"));
							System.out.println("name: " + wE.getAttribute("name"));;
							attributes.put("name", wE.getAttribute("name"));
							attributes.put("href", wE.getAttribute("href"));
						}else if(wE.getTagName().equals("input")){
							System.out.println("type: " + wE.getAttribute("type")); //
							System.out.println("name: " + wE.getAttribute("name"));
							System.out.println("value: " + wE.getAttribute("value"));
							attributes.put("type", wE.getAttribute("type"));
							attributes.put("name", wE.getAttribute("name"));
							attributes.put("value", wE.getAttribute("value"));
						} else if(wE.getTagName().equals("button")){
							System.out.println("onclick: " + wE.getAttribute("onclick"));
							attributes.put("onclick", wE.getAttribute("onclick"));
						}else if(wE.getTagName().equals("form")){
							System.out.println("action: " + wE.getAttribute("action"));
							System.out.println("method: " + wE.getAttribute("method"));
							attributes.put("action", wE.getAttribute("action"));
							attributes.put("method", wE.getAttribute("method"));
						}else if(wE.getTagName().equals("select")){
							System.out.println("name: " + wE.getAttribute("name"));
							System.out.println("id: " + wE.getAttribute("id"));
							attributes.put("name", wE.getAttribute("name"));
							attributes.put("id", wE.getAttribute("id"));			
						}else if(wE.getTagName().equals("textarea")){
							System.out.println("name: " + wE.getAttribute("name"));
							System.out.println("id: " + wE.getAttribute("id"));
							System.out.println("rows: " + wE.getAttribute("rows"));
							System.out.println("cols: " + wE.getAttribute("cols"));
							attributes.put("name", wE.getAttribute("name"));
							attributes.put("id", wE.getAttribute("id"));
							attributes.put("rows", wE.getAttribute("rows"));
							attributes.put("cols", wE.getAttribute("cols"));			
						}

						c = new Component(wE.getTagName(), p, attributes);

						System.out.println();

						Map<Component, ArrayList<Event>> componentToEvent = pageToComponentEvents.get(currentURL);

						if (componentToEvent == null){
							componentToEvent = new LinkedHashMap<Component, ArrayList<Event>>();
						}

						e.setComponent(c);

						//assign input value of Event to Component
						if (e.getValue().length() > 0) {
							c.setInputValue(e.getValue());
						}	

						ArrayList<Event> eveList = componentToEvent.get(c);
						String id = c.getId().replace("w", "e");
						//add component's eventID to the event object
						e.setEventID(id);

						if (eveList == null){
							eveList = new ArrayList<Event>();
						}
						eventList.add(e);
						eveList.add(e);

						componentToEvent.put(c, eveList);
						pageToComponentEvents.put(currentURL, componentToEvent);

						//Perform actions that users perform.
						if(command.equals("click") || command.equals("clickAndWait")){
							wE.click();
							//The component and the command, place in data structure
						}else if(command.equals("type")){
							wE.sendKeys(value);
						}
					}

				}catch(org.openqa.selenium.ElementNotVisibleException e){
					out.write(c.toString()+"\n");
					out.write(e.toString()+"\n");
				}
			}
			out.close();
			driver.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	//See contents of the main data structures to visually verify/test
	public void testData(){
		System.out.println("Page to ComponentClass and Events map");
		for (String key : pageToComponentEvents.keySet()){
			Map<Component, ArrayList<Event>> value = pageToComponentEvents.get(key);
			System.out.println("Page: " + key + "\n");
			for (Component c : value.keySet()){
				System.out.println("C: " + c);
				ArrayList<Event> eList = value.get(c);
				if (eList != null) {
					for (Event e : eList){
						System.out.println("E: " + e);
					}
				}
			}
		}
		System.out.println("Event List");
		for (Event e: eventList){
			System.out.println("Event: " + e);
		}
	}

	public void writeGUI(String filename) {
		GUIWriter gw = new GUIWriter();
		for (String url : pageToComponentEvents.keySet()) {
			gw.addGUIType(url, pageToComponentEvents.get(url));
		}
		gw.printGUI(filename);
	}

	public void writeEFG(String guiFile, String efgFile) {
		String args[] = {"-p", "EFGConverter", "-g", guiFile, "-e", efgFile};
		GUIStructure2GraphConverter.main(args);
	}

	public void generateTestCase(String filename){
		DocumentBuilderFactory documentBuilderFactory = 
				DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			Element rootElement = document.createElement("TestCase");
			document.appendChild(rootElement);

			//output test case
			String testFilename = "";

			for (Event e : eventList) {
				if (e.getEventId() != null) {
					if (testFilename.equals("")) {
						testFilename = filename + ".tst";
						System.out.println("setting testfilename " + testFilename);
					}					
					Element stepElement = document.createElement("Step");

					Element eventId = document.createElement("EventId");
					stepElement.appendChild(eventId);
					eventId.setTextContent(e.getEventId());

					Element reaching = document.createElement("ReachingStep");
					stepElement.appendChild(reaching);

					if (eventList.indexOf(e) == eventList.size() -1)
						reaching.setTextContent("true");
					else
						reaching.setTextContent("false");
					rootElement.appendChild(stepElement);
				}
			}

			TransformerFactory transformerFactory = 
					TransformerFactory.newInstance();
			//transformerFactory.setAttribute("indent-number", new Integer(4));
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "5");
			transformer.setOutputProperty(OutputKeys.INDENT,"yes");
			DOMSource source = new DOMSource(document);
			StreamResult result =  new StreamResult(testFilename);
			transformer.transform(source, result);	
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String fname;

	public static void main(String[] args){

		SeleniumXMLParser parser = new SeleniumXMLParser();
		JFileChooser fc = new JFileChooser();
		JFrame frame = new JFrame();
		String fileName;
		File selFile = null;

		//Check this args[0] line
		if (args.length > 0 && args[0] != null){
			fileName = args[0];
			selFile = new File(fileName);			
		} else{
			fc.setDialogTitle("Choose the SelniumIDE-outputted .xml you want to parse:");
			// Show open dialog; this method does not return until the dialog is closed
			fc.showOpenDialog(frame);
			try{
				
				selFile= fc.getSelectedFile();
			} catch(Exception e){
				
				System.out.println("CANNOT PARSE TESTCASE:  SeleniumIDE output .xml for recorded testCase must be chosen");
				System.exit(0);
			}
		}

		String fullPath = null;
		try{
			
			fullPath= selFile.getPath();
		}catch(Exception e){

			System.out.println("CANNOT PARSE TESTCASE:  SeleniumIDE output .xml for recorded testCase must be chosen");
			System.exit(0);
		}
		String path = "";
		if (fullPath.lastIndexOf('/') != -1)
			path = fullPath.substring(0, fullPath.lastIndexOf('/')+1);
		fname = fullPath.substring(0, fullPath.indexOf('.'));

		String GUIfile = fname + ".GUI";
		String EFGfile = fname + ".EFG";

		parser.parseXML(selFile);
		parser.processElements();
		parser.testData();

		parser.generateTestCase(fname);
		parser.writeGUI(GUIfile);
		//temporary fix, this line must be executed last 
		//because gui2efg converter will exit the program
		//-> But is that fixed now?
		parser.writeEFG(GUIfile, EFGfile);
	}
}