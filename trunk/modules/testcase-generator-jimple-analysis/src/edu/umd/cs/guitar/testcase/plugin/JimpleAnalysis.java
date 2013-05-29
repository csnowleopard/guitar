/*	
 *  Copyright (c) 2011. The GREYBOX group at the University of Freiburg, Chair of Software Engineering.
 *  Names of owners of this group may be obtained by sending an e-mail to arlt@informatik.uni-freiburg.de
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

package edu.umd.cs.guitar.testcase.plugin;

import java.io.File;
import java.util.List;

import soot.PackManager;
import soot.Transform;
import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.testcase.JimpleAnalysisConfiguration;
import edu.umd.cs.guitar.testcase.TestCaseGeneratorConfiguration;
import edu.umd.cs.guitar.testcase.plugin.ct.CTBodyTransformer;
import edu.umd.cs.guitar.testcase.plugin.ct.CTSequenceSelector;
import edu.umd.cs.guitar.testcase.plugin.ct.CTSlicer;

/**
 * @author arlt
 */
public class JimpleAnalysis extends GTestCaseGeneratorPlugin {

	@Override
	public TestCaseGeneratorConfiguration getConfiguration() {
		return new JimpleAnalysisConfiguration();
	}

	@Override
	public boolean isValidArgs() {
		if (JimpleAnalysisConfiguration.SCOPE == null)
			return false;
		return true;
	}

	@Override
	public void generate(EFG efg, String outputDir, int nMaxNumber,
			boolean noDuplicateEvent, boolean treatTerminalEventSpecially) {
		try {
			// init
			this.efg = efg;
			initialize();

			// get events
			List<EventType> events = efg.getEvents().getEvent();

			// build class path
			StringBuilder cp = new StringBuilder();
			cp.append(JimpleAnalysisConfiguration.SCOPE + File.pathSeparator);
			if (null != JimpleAnalysisConfiguration.CLASSPATH)
				cp.append(JimpleAnalysisConfiguration.CLASSPATH
						+ File.pathSeparator);

			// add JRE libs to classpath
			cp.append(new File(
					new File(System.getProperty("java.home"), "lib"), "rt.jar")
					.getPath()
					+ File.pathSeparator);
			cp.append(new File(
					new File(System.getProperty("java.home"), "lib"), "jce.jar")
					.getParent()
					+ File.pathSeparator);

			// create body transformer object
			CTBodyTransformer bodyTransformer = new CTBodyTransformer();
			if (null != JimpleAnalysisConfiguration.PACKAGE)
				bodyTransformer.setPackage(JimpleAnalysisConfiguration.PACKAGE);

			// config body transformer
			PackManager.v().getPack("jtp")
					.add(new Transform("jtp.myTransform", bodyTransformer));

			// run body transformer
			soot.Main.main(new String[] { "-output-format", "n",
					"-allow-phantom-refs", "-pp", "-cp", cp.toString(),
					"-process-dir", JimpleAnalysisConfiguration.SCOPE });

			// run slicer
			CTSlicer slicer = new CTSlicer(bodyTransformer);
			slicer.run(events);

			// run sequence selector
			CTSequenceSelector selector = new CTSequenceSelector(slicer, efg,
					succs);
			selector.run(JimpleAnalysisConfiguration.LENGTH, true);

			// print statistics
			// bodyTransformer.printStatistics();
			// slicer.printStatistics();
			// selector.printStatistics();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main method (for debugging purposes)
	 * 
	 * @param args Command-line arguments
	 */
	public static void main(String[] args) {
		// read EFG
		XMLHandler xml = new XMLHandler();
		EFG efg = (EFG) xml.readObjFromFile(args[0], EFG.class);

		// setup analysis
		JimpleAnalysis analysis = new JimpleAnalysis();
		JimpleAnalysisConfiguration.SCOPE = args[1];
		JimpleAnalysisConfiguration.LENGTH = 3;

		// generate test cases
		analysis.generate(efg, args[2], 0, false, false);
	}

}
