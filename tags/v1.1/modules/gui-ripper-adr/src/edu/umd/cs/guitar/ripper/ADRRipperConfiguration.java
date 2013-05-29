package edu.umd.cs.guitar.ripper;

import org.kohsuke.args4j.Option;

import edu.umd.cs.guitar.util.Util;

public class ADRRipperConfiguration extends GRipperConfiguration {
	// GUITAR runtime parameters
	@Option(name = "-g", usage = "destination GUI file path", aliases = "--gui-file")
	static String GUI_FILE = "GUITAR-Default.GUI";

	@Option(name = "-l", usage = "log file name ", aliases = "--log-file")
	static String LOG_FILE = Util.getTimeStamp() + ".log";

	@Option(name = "-cf", usage = "configure file for the ripper defining terminal, ignored components and ignored windows", aliases = "--configure-file")
	static String CONFIG_FILE = //"resources" + File.separator + "config"
		// + File.separator + 
		"configuration.xml";

	@Option(name = "-ce", usage = "customized event list (usually aut-specific events)", aliases = "--event-list")
	static String CUSTOMIZED_EVENT_LIST = null;

	// Application Under Test
	@Option(name = "-c", usage = "<REQUIRED> main class name for the Application Under Test ", aliases = "--main-class", required = true)
	String MAIN_CLASS = null;
	
	// port number for tcp connection to adr-server
	@Option(name = "-pt", usage = "<REQUIRED> port number for tcp connection to adr-server", aliases = "--port", required = true)
	int port;

}
