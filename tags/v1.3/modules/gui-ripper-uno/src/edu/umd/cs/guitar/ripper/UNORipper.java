/*  
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *  the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in all copies or substantial 
 *  portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *  LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *  EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */

package edu.umd.cs.guitar.ripper;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.kohsuke.args4j.CmdLineException;

import edu.umd.cs.guitar.model.GIDGenerator;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.OODefaultIDGeneratorSimple;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentListType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.Configuration;
import edu.umd.cs.guitar.model.data.FullComponentType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.LogWidget;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.ripper.filter.GComponentFilter;
import edu.umd.cs.guitar.ripper.filter.IgnoreSignExpandFilter;
import edu.umd.cs.guitar.util.DefaultFactory;
import edu.umd.cs.guitar.util.GUITARLog;
import edu.umd.cs.guitar.util.OOConstants;

/**
 * 
 * Executing class for UNORipper
 * 
 * <p>
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 * @author <a href="mailto:wikum@cs.umd.edu"> Wikum Dinalankara </a>
 */
public class UNORipper {

	UNORipperConfiguration CONFIG;

	/**
	 * @param CONFIG
	 */
	public UNORipper(UNORipperConfiguration CONFIG) {
		super();
		this.CONFIG = CONFIG;
	}

	// Logger logger;

	/**
	 * Execute the uno ripper
	 * 
	 * <p>
	 * 
	 * @throws CmdLineException
	 * 
	 */
	Ripper ripper;

	public void execute() throws CmdLineException {

		if (CONFIG.help) {
			throw new CmdLineException("");
		}

		System.setProperty(GUITARLog.LOGFILE_NAME_SYSTEM_PROPERTY,
				UNORipperConfiguration.LOG_FILE);

		long nStartTime = System.currentTimeMillis();
		ripper = new Ripper(GUITARLog.log);

		// -------------------------
		// Setup configuration
		// -------------------------

		try {
			System.out.println("UNORipper: Setting up environment");
			setupEnv();
			System.out.println("UNORipper: running execute()");
			ripper.execute();
			System.out.println("UNORipper: finished execute()");
		} catch (Exception e) {
			GUITARLog.log.error("UNORipper: ", e);
			System.exit(1);
		}

		System.out.println("UNORipper: writing result");
		GUIStructure dGUIStructure = ripper.getResult();
		IO.writeObjToFile(dGUIStructure, UNORipperConfiguration.GUI_FILE);

		GUITARLog.log.info("-----------------------------");
		GUITARLog.log.info("OUTPUT SUMARY: ");
		GUITARLog.log.info("Number of Windows: "
				+ dGUIStructure.getGUI().size());
		GUITARLog.log.info("GUI file:" + UNORipperConfiguration.GUI_FILE);
		// IO.writeObjToFile(logWidget, UNORipperConfiguration.LOG_WIDGET_FILE);

		// ------------------
		// Elapsed time:
		long nEndTime = System.currentTimeMillis();
		long nDuration = nEndTime - nStartTime;
		DateFormat df = new SimpleDateFormat("HH : mm : ss: SS");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		GUITARLog.log.info("Ripping Elapsed: " + df.format(nDuration));
		GUITARLog.log.info("Log file: " + UNORipperConfiguration.LOG_FILE);
	}

	/**
	 * Setup
	 */
	private void setupEnv() {
		
//		OOConstants.DELAY = CONFIG.DELAY;

		// Try to find absolute path first then relative path

		Configuration conf = null;

		try {
			conf = (Configuration) IO.readObjFromFile(
					UNORipperConfiguration.CONFIG_FILE, Configuration.class);

			if (conf == null) {
				InputStream in = getClass()
						.getClassLoader()
						.getResourceAsStream(UNORipperConfiguration.CONFIG_FILE);
				conf = (Configuration) IO.readObjFromFile(in,
						Configuration.class);
			}

		} catch (Exception e) {
			GUITARLog.log.error("No configuration file. Using an empty one...");
			// return;
		}

		if (conf == null) {
			DefaultFactory df = new DefaultFactory();
			conf = df.createDefaultConfiguration();
		} else {
			System.out.println("Reading config ... " + conf.toString());
		}

		List<FullComponentType> cTerminalList = conf.getTerminalComponents()
				.getFullComponent();

		for (FullComponentType cTermWidget : cTerminalList) {
			ComponentType component = cTermWidget.getComponent();
			AttributesType attributes = component.getAttributes();
			if (attributes != null)
				OOConstants.sTerminalWidgetSignature
						.add(new AttributesTypeWrapper(component
								.getAttributes()));
		}

		UNORipperMonitor uMonitor = new UNORipperMonitor(CONFIG);
		uMonitor.setPort(UNORipperConfiguration.PORT);

		List<FullComponentType> lIgnoredComps = new ArrayList<FullComponentType>();

		ComponentListType ignoredComponentList = conf.getIgnoredComponents();

		if (ignoredComponentList != null)
			for (FullComponentType fullComp : ignoredComponentList
					.getFullComponent()) {
				ComponentType comp = fullComp.getComponent();

				// TODO: Shortcut here
				if (comp == null) {
					ComponentType win = fullComp.getWindow();
					ComponentTypeWrapper winAdapter = new ComponentTypeWrapper(
							win);
					String sWindowTitle = winAdapter
							.getFirstValueByName(GUITARConstants.TITLE_TAG_NAME);
					if (sWindowTitle != null)
						OOConstants.sIgnoredWins.add(sWindowTitle);

				} else
					lIgnoredComps.add(fullComp);
			}

		// --------------------------
		// Ignore components xml
		GComponentFilter jIgnoreExpand = new IgnoreSignExpandFilter(
				lIgnoredComps);
		ripper.addComponentFilter(jIgnoreExpand);

		System.out.println("*** UNORipper: IgnoreSignExpandFilter *** ");
		System.out.println("*** Size*** "
				+ conf.getIgnoredComponents().getFullComponent().size());
		System.out.println("UNORipperConfiguration.CONFIG_FILE: "
				+ UNORipperConfiguration.CONFIG_FILE);

		// // --------------------------
		//		
		// GComponentFilter cmIgnoredRipFilter =
		// UNOIgnoreRipFilter.getInstance();
		// GComponentFilter cmIgnoredExpandFilter = UNOIgnoreExpandFilter
		// .getInstance();
		// GComponentFilter cmTerminalFilter = UNOTerminalFilter.getInstance();
		// GComponentFilter cmTabFilter = UNOTabFilter.getInstance();
		// GComponentFilter cmTreeFilter = UNOTreeFilter.getInstance();
		//
		// ripper.addComponentFilter(cmIgnoredRipFilter);
		// ripper.addComponentFilter(cmIgnoredExpandFilter);
		// ripper.addComponentFilter(cmTerminalFilter);
		// ripper.addComponentFilter(cmTabFilter);
		// ripper.addComponentFilter(cmTreeFilter);

		// Set up Monitor
		ripper.setMonitor(uMonitor);

		// Set up IDGenerator

		GIDGenerator uIDGenerator = OODefaultIDGeneratorSimple.getInstance();
		ripper.setIDGenerator(uIDGenerator);

	}

}
