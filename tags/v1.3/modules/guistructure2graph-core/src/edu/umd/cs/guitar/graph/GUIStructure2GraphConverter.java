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
package edu.umd.cs.guitar.graph;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import edu.umd.cs.guitar.graph.plugin.GraphConverter;
import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Entry point to convert GUI structure to different kind of graphs
 * 
 * @author <a href="mailto:charlie.biger@gmail.com"> Charlie BIGER </a>
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * @version 1.0
 */
public class GUIStructure2GraphConverter {

	@Option(name = "-g", usage = "GUI file", aliases = "--gui-file", required = true)
	public String GUI_FILE = "GUITAR-Default.GUI";

	@Option(name = "-e", usage = "EFG file ", aliases = "--efg-file", required = false)
	public String EFG_FILE = "GUITAR-Default.EFG";

	@Option(name = "-p", usage = "Plugin name", aliases = "--plugin", required = true)
	public String PLUGIN;

	@Option(name = "-m", usage = "Map file", aliases = "--map-file", required = false)
	public String MAP_FILE;

	// These parameters are only used for debuggin
	// do not visible to the users
	static String GRAPHVIZ_FILE = "";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		setupLog();

		GUIStructure2GraphConverter converter = new GUIStructure2GraphConverter();
		CmdLineParser parser = new CmdLineParser(converter);

		try {
			parser.parseArgument(args);
			converter.execute();
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println();
			System.err.println("Usage: java [JVM options] "
					+ GUIStructure2GraphConverter.class.getName()
					+ " [converter options] \n");
			System.err.println("where [converter options] include:");
			System.err.println();
			parser.printUsage(System.err);
		}
		System.exit(0);

	}

	/**
	 * 
	 */
	private void execute() {
		// TODO: Make a configuration for parameters

		XMLHandler xmlHandler = new XMLHandler();

		String converterFullName = GraphConverter.class.getPackage().getName()
				+ "." + PLUGIN;

		Class<?> converterClass;
		Object graph = null;
		GUIStructure gui = null;
		try {
			converterClass = Class.forName(converterFullName);
			GraphConverter plugin = (GraphConverter) converterClass
					.newInstance();

			gui = (GUIStructure) (xmlHandler.readObjFromFile(GUI_FILE,
					GUIStructure.class));
			graph = plugin.generate(gui);
			xmlHandler.writeObjToFile(graph, EFG_FILE);

		} catch (ClassNotFoundException e) {
			System.out
					.println("The converter can not be found. Please make ensure that the converter name is correct and the corresponding .jar file can be reached.");
		} catch (Exception e) {
			GUITARLog.log.info("Unknown ERROR");
			e.printStackTrace();
		}

		// Convert map file
		if (gui != null && graph != null)
			if (MAP_FILE != null && (graph instanceof EFG)) {
				EFG efg = (EFG) graph;
				GUIMapCreator gui2map = new GUIMapCreator();
				GUIMap map = gui2map.getGUIMap(gui, efg);
				xmlHandler.writeObjToFile(map, MAP_FILE);
			}

		StringBuffer buff = new StringBuffer();

		GUITARLog.log.info("===========================================");

		GUITARLog.log.info("GUIStructure2GraphConverter");
		GUITARLog.log.info("\tPlugin: \t" + PLUGIN);
		GUITARLog.log.info("\tInput GUI: \t" + GUI_FILE);
		GUITARLog.log.info("\tOutput EFG: \t" + EFG_FILE);
		if (MAP_FILE != null) {
			GUITARLog.log.info("\tOutput MAP: \t" + MAP_FILE);
		}

		GUITARLog.log.info("===========================================");

		GUITARLog.log.info(buff);
	}

	/**
     * 
     */
	private static void setupLog() {
		System.setProperty(GUITARLog.LOGFILE_NAME_SYSTEM_PROPERTY,
				GUIStructure2GraphConverter.class.getSimpleName() + ".log");

		// GUITARLog.log =
		// Logger.getLogger(GUIStructure2GraphConverter.class
		// .getSimpleName());
		//
		// final File logFile = new File("GUIStructure2GraphConverter.log");
		// final String LOG_PATTERN = "%m%n";
		// final PatternLayout pl = new PatternLayout(LOG_PATTERN);
		//
		// final FileAppender rfp = new RollingFileAppender(pl, logFile
		// .getCanonicalPath(), true);
		//
		// final ConsoleAppender cp = new ConsoleAppender(pl);
		//
		// GUITARLog.log.addAppender(rfp);
		// GUITARLog.log.addAppender(cp);

	}
}
