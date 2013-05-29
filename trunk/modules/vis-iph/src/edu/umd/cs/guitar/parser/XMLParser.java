package edu.umd.cs.guitar.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.umd.cs.guitar.gen.*;

/**
 * This class parses XML file to Java objects
 * 
 * @author Muhammad Ashraf Ishak
 *
 */
public class XMLParser {

	private String guiFileName; 
	private String efgFileName;
	private String testCaseDirName;
	private File efgFile;
	private File guiFile;
	private File testCaseDir;
	
	public XMLParser(){
		this.guiFile = null;
		this.efgFile = null;
		this.testCaseDir = null;
		this.guiFileName = null;
		this.efgFileName = null;
		this.testCaseDirName = null;
	}
	
	public XMLParser(String gui, String efg, String testCase){
		guiFileName = gui;
		efgFileName = efg;
		testCaseDirName = testCase;
		if(gui != null){
			guiFile = new File(gui);
		}else{
			guiFile = null;
		}
		if(efg != null){
			efgFile = new File(efg);
		}else{
			efgFile = null;
		}
		if(testCase != null){
			testCaseDir = new File(testCase);
		}else{
			testCaseDir = null;
		}
	}
	
	public XMLParser (File gui, File efg, File testCase){
		guiFile = gui;
		efgFile = efg;
		testCaseDir = testCase;
		if(gui != null){
			guiFileName = gui.getName();
		}else{
			guiFileName = null;
		}
		if(efg != null){
			efgFileName = efg.getName();
		}else{
			efgFileName = null;
		}
		if(testCase != null){
			testCaseDirName = testCase.getName();
		}else{
			testCaseDirName = null;
		}
	}
	
	/**
	 * Get mapping of {test case file name => test case instance}
	 * @return
	 */
	public HashMap<String, TestCase> getTestCases() {
		HashMap<String, TestCase> testCases = null;
		
		if(testCaseDir == null && testCaseDirName != null){
			testCaseDir = new File(testCaseDirName);
		}
		if(testCaseDir != null){
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				testCases = new HashMap<String, TestCase>();
				testCases = parseTestcaseDOM(testCaseDir, dBuilder);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
		return testCases;
	}
	
	public  EFG getEFG(){
		if(efgFile == null && efgFileName != null){
			efgFile = new File(efgFileName);
		}
		if(efgFile != null){
			return (EFG)parse(EFG.class, efgFile);
		}else{
			return null;
		}
	}
	
	public GUIStructure getGUIStructure(){
		if(guiFile == null && guiFileName != null){
			guiFile = new File(guiFileName);
		}
		if(guiFile != null){
			return (GUIStructure)parse(GUIStructure.class, guiFile);
		}else{
			return null;
		}
	}

	
	
	/**
	 * Mapping of {First step event ID => List of test cases instance}
	 * @return
	 */
	public HashMap <String, ArrayList<TestCase>> getTestCasesByParent (){
		HashMap<String, ArrayList<TestCase>> testCases = null;
		
		if(testCaseDir == null && testCaseDirName != null){
			testCaseDir = new File(testCaseDirName);
		}
		
		if(testCaseDir != null){
			for (File tc: testCaseDir.listFiles()){
				if(tc.getName().endsWith(".tst")){
					if (testCases == null){
						testCases = new HashMap<String, ArrayList<TestCase>>();
					}
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					try {
						DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
						TestCase p = parseOneTestcaseDOM(tc, dBuilder);
						StepType step1 = p.getStep().get(0);
						StepType step2 = p.getStep().get(1);
						if (testCases.get(step1.getEventId()) == null){
							testCases.put(step1.getEventId(), new ArrayList<TestCase>());
						}
						if (!step1.getEventId().equals(step2.getEventId()) || !testCases.get(step1.getEventId()).contains(p)){
							testCases.get(step1.getEventId()).add(p);
						}
					} catch (ParserConfigurationException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return testCases;
	}
	
	/**
	 * Parses XML file into Java object
	 * @param clazz 
	 * @param file XML file
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Object parse(Class clazz, File file){
		Object result = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			result = jaxbUnmarshaller.unmarshal(file);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	private TestCase parseOneTestcaseDOM (File testcaseFile, DocumentBuilder dBuilder){
		TestCase testcase = null;
		try {
			Document doc = dBuilder.parse(testcaseFile);
			doc.getDocumentElement().normalize();
			testcase = new TestCase();
			Node root = doc.getElementsByTagName("TestCase").item(0);
			NodeList steps = root.getChildNodes();
			for (int i = 0; i < steps.getLength(); i++){
				Node step = steps.item(i);
				if (step.getFirstChild() != null){
					NodeList inside = step.getChildNodes();
					StepType stepType = new StepType();
					for (int j = 0; j < inside.getLength(); j++){
						Node ins = inside.item(j);
						String value = ins.getTextContent();
						if (value != null){
							if (ins.getNodeName().equals("EventId")){
								stepType.setEventId(value);
							} else if (ins.getNodeName().equals("ReachingStep")){
								stepType.setReachingStep(Boolean.parseBoolean(value));
							}
						}
					}
					testcase.getStep().add(stepType);
				}
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return testcase;
	}
	
	private HashMap<String,TestCase> parseTestcaseDOM (File testcaseDir, DocumentBuilder dBuilder){
		HashMap<String, TestCase> result = new HashMap<String, TestCase>();
		for (File f: testcaseDir.listFiles()){
			if(f.getName().endsWith(".tst") && !f.isDirectory()){
				TestCase tc = parseOneTestcaseDOM (f, dBuilder);
				result.put(f.getName(), tc);
			}
		}
		return result;
		
	}
	
	
	public void setGuiFileName(String guiFileName) {
		this.guiFileName = guiFileName;
		if(guiFileName != null){
			guiFile = new File(guiFileName);
		}else{
			guiFile = null;
		}
	}

	public void setEfgFileName(String efgFileName) {
		this.efgFileName = efgFileName;
		if(efgFileName != null){
			efgFile = new File(efgFileName);
		}else{
			efgFile = null;
		}
	}

	
	public void setEFGFile(File efgFile){
		this.efgFile = efgFile;
		if(efgFile != null){
			efgFileName = efgFile.getName();
		}else{
			efgFileName = null;
		}
	}
	
	public File getEFGFile(){
		return efgFile;
	}
	
	public void setGuiFile(File guiFile){
		this.guiFile = guiFile;
		if(guiFile != null){
			guiFileName = guiFile.getName();
		}else{
			guiFileName = null;
		}
	}
	
	public File getGUIFile(){
		return guiFile;
	}
	
	public void setTestCaseDir(File testCaseDir){
		this.testCaseDir = testCaseDir;
		if(testCaseDir != null){
			testCaseDirName = testCaseDir.getName();
		}else{
			testCaseDirName = null;
		}
	}
	
	public File getTestCaseFile(){
		return testCaseDir;
	}

	
}
