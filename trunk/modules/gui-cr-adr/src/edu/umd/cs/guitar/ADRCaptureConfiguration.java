package edu.umd.cs.guitar;

import edu.umd.cs.guitar.ripper.GRipperConfiguration;
import edu.umd.cs.guitar.util.Util;

public class ADRCaptureConfiguration extends GRipperConfiguration {
	// GUITAR runtime parameters
	
	static String GUI_FILE = "GUITAR-Default.GUI";

	static String LOG_FILE = Util.getTimeStamp() + ".log";

	static String CONFIG_FILE = //"resources" + File.separator + "config"
		// + File.separator + 
		"configuration.xml";

	static String CUSTOMIZED_EVENT_LIST = null;

	// Application Under Test
	String MAIN_CLASS = null;
	
	// port number for tcp connection to adr-server
	int port;

}
