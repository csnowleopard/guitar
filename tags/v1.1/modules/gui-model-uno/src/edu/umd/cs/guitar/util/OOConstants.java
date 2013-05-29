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
package edu.umd.cs.guitar.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import edu.umd.cs.guitar.event.OOActionHandler;
import edu.umd.cs.guitar.event.OOEditableTextHandler;
import edu.umd.cs.guitar.event.OOSelectionHandler;
import edu.umd.cs.guitar.event.OOValueHandler;
import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;

// import java.awt.List;

/**
 * 
 * Store constants shared between GUITAR components
 * 
 * @author Bao Nguyen
 * 
 */
public interface OOConstants {

	// ------------------------------
	// Ignored components
	// ------------------------------

	// Commented
	
	public static  String IGNORED_DIR = "data" + File.separator + "ignore" + File.separator;
	
	public static String IGNORED_WIDGET_FILE = "ignore_widget.ign";

	public static String IGNORED_WINDOW_FILE = "ignore_window.ign";

	public static String TERMINAL_WIDGET_FILE = "terminal_widget.ign";

	public static String IGNORED_TAB_FILE = "ignore_tab.ign";

	public static String IGNORED_TREE_NODE_FILE = "ignore_tree_node.ign";
	 
	public static List<String> sIgnoreWidgetList = OOUtil.getListFromFile(
			IGNORED_DIR + IGNORED_WIDGET_FILE, false);

	public static List<String> sIgnoreWindowList = OOUtil.getListFromFile(
			IGNORED_DIR + IGNORED_WINDOW_FILE, false);

	public static List<String> sTerminalWidgetList = OOUtil.getListFromFile(
			IGNORED_DIR + TERMINAL_WIDGET_FILE, true);

	public static List<String> sIgnoreTabList = OOUtil.getListFromFile(IGNORED_DIR
			+ IGNORED_TAB_FILE, false);

	public static List<String> sIgnoreTreeNodeList = OOUtil.getListFromFile(
			IGNORED_DIR + IGNORED_TREE_NODE_FILE, false);
	
	public static String GUI_DIR = "data"+File.separator+"gui"+File.separator;
	
	public static List<AttributesTypeWrapper> sTerminalWidgetSignature = new LinkedList<AttributesTypeWrapper>();
	public static List<String> sIgnoredWins = new ArrayList<String>();

	public static List<String> ID_PROPERTIES = Arrays.asList("Title", "Class", "Icon", "Index");
	// ------------------------------
	// GUITAR Event names for OOo
	// ------------------------------
	
	public static String ACTION_EVENT = OOActionHandler.class.getName();
	public static String TEXT_EVENT = OOEditableTextHandler.class.getName();
	public static String SELECT_EVENT = OOSelectionHandler.class.getName();
	public static String VALUE_EVENT = OOValueHandler.class.getName();
	
	public static final  int DELAY = 500;
	public static final  int OO_REPLAYER_TIMEOUT= 20000;
	
	
	
}
