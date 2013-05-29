package edu.umd.cs.guitar.ripper;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.ComponentListType;
import edu.umd.cs.guitar.model.data.Configuration;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.LogWidget;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.ripper.ADRRipperMonitor;
import edu.umd.cs.guitar.util.GUITARLog;

public class ADRRipperMain {

	// TODO we may need our own configuration later
	ADRRipperConfiguration CONFIG = null;

	Ripper ripper;

	public ADRRipperMain(ADRRipperConfiguration config) {
		super();
		this.CONFIG = config;
	}

	public void execute() {
		Configuration conf = null;
		
		System.setProperty(GUITARLog.LOGFILE_NAME_SYSTEM_PROPERTY,
				ADRRipperConfiguration.LOG_FILE);
		
		try {
			conf = (Configuration) IO.readObjFromFile(
					ADRRipperConfiguration.CONFIG_FILE, Configuration.class);

			if (conf == null) {
				InputStream in = getClass()
						.getClassLoader()
						.getResourceAsStream(ADRRipperConfiguration.CONFIG_FILE);
				conf = (Configuration) IO.readObjFromFile(in,
						Configuration.class);
			}

		} catch (Exception e) {
			GUITARLog.log.error("No configuration file. Using an empty one...");
			// return;
		}

		long nStartTime = System.currentTimeMillis();
		ripper = new Ripper(GUITARLog.log);
		
		GRipperMonitor ADRMonitor = new ADRRipperMonitor(CONFIG);
		ripper.setMonitor(ADRMonitor);
		
		ripper.execute();
		
		GUIStructure dGUIStructure = ripper.getResult();
		IO.writeObjToFile(dGUIStructure, ADRRipperConfiguration.GUI_FILE);

		GUITARLog.log.info("-----------------------------");
		GUITARLog.log.info("OUTPUT SUMARY: ");
		GUITARLog.log.info("Number of Windows: "
				+ dGUIStructure.getGUI().size());
		ComponentListType lOpenWins = ripper.getlOpenWindowComps();
		ComponentListType lCloseWins = ripper.getlCloseWindowComp();
		ObjectFactory factory = new ObjectFactory();

		LogWidget logWidget = factory.createLogWidget();
		logWidget.setOpenWindow(lOpenWins);
		logWidget.setCloseWindow(lCloseWins);


		// IO.writeObjToFile(logWidget, JFCRipperConfiguration.LOG_WIDGET_FILE);
		long nEndTime = System.currentTimeMillis();
		long nDuration = nEndTime - nStartTime;
		DateFormat df = new SimpleDateFormat("HH : mm : ss: SS");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		GUITARLog.log.info("Ripping Elapsed: " + df.format(nDuration));
		GUITARLog.log.info("Log file: " + ADRRipperConfiguration.LOG_FILE);
	}

	public static void main(String[] argv) {
		 ADRRipperConfiguration configuration = new ADRRipperConfiguration();
	     CmdLineParser parser = new CmdLineParser(configuration);
	     final ADRRipperMain ripperMain = new ADRRipperMain(configuration);
		
		try {
			parser.parseArgument(argv);
			ripperMain.execute();
		} catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
		System.exit(0);
	}
}