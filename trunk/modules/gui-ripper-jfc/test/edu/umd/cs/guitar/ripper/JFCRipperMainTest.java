package edu.umd.cs.guitar.ripper;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import edu.umd.cs.guitar.demo.Project;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.ripper.plugin.GRipperAfter;
import edu.umd.cs.guitar.ripper.plugin.GRipperAfterComponent;
import edu.umd.cs.guitar.ripper.plugin.GRipperBefore;
import edu.umd.cs.guitar.ripper.plugin.GRipperBeforeComponnent;
import edu.umd.cs.guitar.ripper.plugin.JFCRipperComponentIDCollector;

public class JFCRipperMainTest {

	
	// Argouml	
	private static final String TEST_RESOURCE_DIR = "/media/Data/Ore_no_documen" +
			"ts/Research/Repository/guitar-sf-svn-trunk/modules/gui-ripper-jfc/test-resources";
	private static final String LOG_DIR = TEST_RESOURCE_DIR + File.separator
			+ "log";
	private static final String CONFIGURATION_XML_FILE = "configuration-ignore-menu.xml";
	private static final String GUI_FILE = "Guitar.GUI";

	private static final String ARGOUML_TEST_RESOURCE_DIR = TEST_RESOURCE_DIR
			+ File.separator + "au";
	private static final String ARGOUML_CONFIGURATION_XML_FILE = ARGOUML_TEST_RESOURCE_DIR
			+ File.separator + CONFIGURATION_XML_FILE;
	private static final String ARGOUML_GUI_FILE = ARGOUML_TEST_RESOURCE_DIR
			+ File.separator + GUI_FILE;
	private static final String ARGOUML_MAIN_CLASS = "org.argouml.application.Main";

	
	// Radio Button
	private static final String RD_MAIN_CLASS = Project.class.getName();
	private static final String RD_TEST_RESOURCE_DIR = TEST_RESOURCE_DIR
			+ File.separator + "rd";

	private static final String RD_CONFIGURATION_XML_FILE = RD_TEST_RESOURCE_DIR 
			+ File.separator + "configuration.xml";
	
	private static final String RD_GUI_FILE = RD_TEST_RESOURCE_DIR
			+ File.separator + GUI_FILE;
	@Before
	public void setUp() {
		System.setProperty("log4j.properties", LOG_DIR + File.separator
				+ "guitar-clean.glc");
		PropertyConfigurator.configure(LOG_DIR + File.separator
				+ "guitar-clean.glc");
	}

	@Test
	public void testRipArgoUml() {
				JFCRipperMain.main(new String[] { "-c", ARGOUML_MAIN_CLASS, "-cf",
				ARGOUML_CONFIGURATION_XML_FILE, "-g", ARGOUML_GUI_FILE, "-d",
				"2000" });
	}
	
	@Test
	public void testRandomWalkRadio() {
		System.setProperty(JFCRipperComponentIDCollector.RIPPER_COMP_ID_LOG_FILE_FLAG, "ripped_component.log");
		
				JFCRipperMain.main(new String[] { 
						"-c", RD_MAIN_CLASS, 
						"-cf",RD_CONFIGURATION_XML_FILE, 
						"-g", RD_GUI_FILE, 
						"-d", "2000", 
						"-i", "1000", 
						"-rw", "20",
						"-p", JFCRipperComponentIDCollector.class.getName()
						});
	}
	
	@Test
	public void testRandomWalkArgoUML() {
		System.setProperty(JFCRipperComponentIDCollector.RIPPER_COMP_ID_LOG_FILE_FLAG, "ripped_component.log");
		
				JFCRipperMain.main(new String[] { 
						"-c", ARGOUML_MAIN_CLASS, 
						"-cf",ARGOUML_CONFIGURATION_XML_FILE, 
						"-g", ARGOUML_GUI_FILE, 
						"-d", "2000", 
						"-i", "2000", 
//						"-rw", "100",
						"-p", JFCRipperComponentIDCollector.class.getName()
						});
	}
}
