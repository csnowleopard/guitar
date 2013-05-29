/*	
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in all copies or substantial 
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.model;

// import javax.xml.xquery.XQConnection;
// import javax.xml.xquery.XQDataSource;
// import javax.xml.xquery.xqj.mediator.DDXQDataSource;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.qizx.api.Library;
import com.qizx.api.LibraryManager;
import com.qizx.api.LibraryManagerFactory;
import com.qizx.api.QizxException;

/**
 * @author Bao Nguyen
 * 
 */
public class XMLManipulator {

	public static String GUI_DIR = ".\\data\\testcases\\gui";
	public static String GUI_FILE_ROOT = GUI_DIR + "OOo.main.GUI";
	
	
	
	public static String GUI_FILE_ATTACHED = GUI_DIR + "OOo.attach.GUI";
	public static String GUI_FILE_OUT = GUI_DIR + "OOo.com.GUI";
	
	public static String TEST_XML = "OOo.main.GUI";
	
	
	public static String INVOKER = "Indexes and Tables..._36";

	/**
	 * @param args
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws XPathExpressionException 
	 * @throws QizxException 
	 */
	public static void main(String[] args) throws ParserConfigurationException,
			SAXException, IOException, XPathExpressionException, QizxException {

//		test();
		testXQuery();
	}
	
	
	private static void testXQuery() throws QizxException{
		LibraryManagerFactory factory = LibraryManagerFactory.getInstance();
		LibraryManager libManager = factory.openLibraryGroup(new File(GUI_DIR));
//		Library lib = libManager.openLibrary(TEST_XML, /*user*/ null);
		
//	    try {
//            lib.commit();
//        } finally {
//        }
		
	}
	

	/*
 * @throws ParserConfigurationException *
	 * 
	 */
	private static void test() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		
		Document doc = builder.parse(GUI_FILE_ROOT);

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath
				.compile("//GUIStructure/GUI/Window/Attributes/Property [Name/text()='Title'] /Value/text()");
		
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		
		for (int i = 0; i < nodes.getLength(); i++) {
			System.out.println(nodes.item(i).getNodeValue());
		}
	}

	public static void combineGUI(String sMainGUI, String sAttacherGUI,
			String sInvokerID) {

	}

}
