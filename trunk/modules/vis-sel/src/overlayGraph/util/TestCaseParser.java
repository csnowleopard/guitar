package overlayGraph.util;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.util.*;

public class TestCaseParser {

	/*
	 * This class goes through the test case folder, parses all the testcase.xml files and compiles them
	 * all into the testcases hashmap.  
	 */

	//stores data in the form <testcase, list of eventIds>. You can access using the getTestCases() method
	private HashMap<String,ArrayList<String>> testcases;

	//keeps track of the current test case
	private String currTestCase;

	//for parsing
	Document dom;

	//takes in the test case folder path
	public TestCaseParser(String tcFolderPath){

		testcases = new HashMap<String,ArrayList<String>>();

		//get all the test case files in the TC directory
		File directory = new File(tcFolderPath);
		File [] files = directory.listFiles();

		for(int i = 0; i < files.length; i++) {
			if(!files[i].getName().contains(".tst"))
				continue;
			currTestCase = files[i].getName();
			run(files[i].getPath());
		}

	}

	public void run(String testcase) {

		//parse the xml file and get the dom object
		parseXmlFile(testcase);

		parseDocument();

	}

	private void parseXmlFile(String testcase){

		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(testcase);

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void parseDocument(){
		//get the root elememt
		Element docEle = dom.getDocumentElement();

		//get a nodelist of <Step> elements
		NodeList nl = docEle.getElementsByTagName("Step");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {

				//get the EventId element
				Element el = (Element)nl.item(i);

				//gets the eventId
				String eid = el.getElementsByTagName("EventId").item(0).getFirstChild().getNodeValue();

				//adds the eventId 
				if(testcases.containsKey(currTestCase)){
					testcases.get(currTestCase).add(eid);
				}else{
					testcases.put(currTestCase, new ArrayList<String>());
					testcases.get(currTestCase).add(eid);
				}
			}
		}
	}

	public HashMap<String,ArrayList<String>> getTestCases(){
		return testcases;
	}
}
